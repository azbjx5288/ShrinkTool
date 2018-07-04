package com.shrinktool.base.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.shrinktool.BuildConfig;
import com.shrinktool.app.GoldenAsiaApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON请求的Volley适配器，返回结果使用Gson格式化为指定对象。
 * <p/>
 * Notice: 大文件上传下载不适用该请求
 */
public final class GsonRequest extends Request<RestResponse> {
    private final static boolean DEBUG = BuildConfig.DEBUG;
    private final static boolean SEND_GZIP = false;

    /**约定开发与测试时期*/
    private static final String SIGN_KEY = "706df0abddf329c2b0077b1d0e35a020";

    private static RetryPolicy sRetryPolicy = new DefaultRetryPolicy(30 * 1000, 0, 0);

    private final RestRequest request;
    private long startAtTime;
    private String cacheKey;

    /**
     * 发起请求
     * @param method        参考{@link Method#POST}、{@link Method#GET}等
     * @param request       请求参数封装对象
     */
    public GsonRequest(int method, RestRequest request) {
        super(method, convertUrl(method, request), request);

        this.request = request;

        setRetryPolicy(sRetryPolicy);
        cacheKey = BuildConfig.VERSION_CODE
                + GoldenAsiaApp.getUserCentre().getUserID()
                + ":" + request.getConfig().version()
                + ":" + super.getCacheKey();
        if (DEBUG) {
            startAtTime = System.currentTimeMillis();
            String urlTag;
            if (method == Request.Method.GET) {
                urlTag = request.getConfig().api();
            } else {
                urlTag = getUrl();
            }
            Formater2.outJsonObject("GsonRequest_REQUEST", "", urlTag, GsonHelper.toJson(request.getCommand()));
        }
    }

    /**
     * 由api拼上服务器host，若是GET模式，将参数拼到url
     */
    private static String convertUrl(int method, RestRequest request) {
        if (method != Method.GET) {
            return GoldenAsiaApp.getUserCentre().getUrl(request.getConfig().api());
        }

        String baseUrl = GoldenAsiaApp.getUserCentre().getUrl(request.getConfig().api());
        StringBuilder encodedParams = new StringBuilder(baseUrl);
        if (!baseUrl.contains("?")) {
            encodedParams.append("?");
        }
        if (encodedParams.charAt(encodedParams.length() - 1) != '?') {
            encodedParams.append('&');
        }

        Map<String, String> params = GsonHelper.convert2Map(GsonHelper.convert2gson(request.getCommand()));
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (null == entry.getValue()) {
                    continue;
                }
                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                encodedParams.append('&');
            }

            encodedParams.append(URLEncoder.encode("sign", "UTF-8"));
            encodedParams.append('=');
            encodedParams.append(URLEncoder.encode(SIGN_KEY, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedParams.toString();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            JsonObject jsonObject = GsonHelper.convert2gson(request.getCommand());
            jsonObject.addProperty("sign", SIGN_KEY);
            if (SEND_GZIP) {
                return Gzip2Utils.compress(jsonObject.toString().getBytes("UTF-8"));
            } else {
                return jsonObject.toString().getBytes("UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getBody();
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=UTF-8";
    }

    @Override
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> map = new HashMap<>();
        String cookie = GoldenAsiaApp.getUserCentre().getSession();
        if (cookie != null) {
            map.put("Cookie", cookie);
        }
        map.put("User-Agent", "Android App");
        if (SEND_GZIP) {
            map.put("Content-Encoding", "gzip");
        }
        return map;
    }

    @Override
    protected void deliverResponse(RestResponse response) {
        request.onResponse(response);
    }

    /**
     * 网络请求结果解析
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<RestResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //json = new String(Base64.decode(json, Base64.DEFAULT), "UTF-8");
            //String json = new String(Gzip2Utils.decompress(response.data));
            if (DEBUG) {
                String urlTag;
                if (getMethod() == Request.Method.GET) {
                    urlTag = request.getConfig().api();
                } else {
                    urlTag = getUrl();
                }
                String name = String.format("call use %dms for %s",
                        System.currentTimeMillis() - startAtTime, urlTag);
                Formater2.outJsonObject("GsonRequest_RESPONSE", "", name, json);
            }

            RestResponse restResponse = GsonHelper.fromJson(json, request.getTypeOfResponse());
            request.onBackgroundResult(response, restResponse);
            return Response.success(restResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Log.d("GsonRequest", e.toString());
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            Log.d("GsonRequest", e.toString());
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            Log.d("GsonRequest", e.toString());
            return Response.error(new ParseError(e));
        }
    }
}