package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.component.Utils;
import com.shrinktool.data.MultipartEntity;
import com.shrinktool.data.MultipartRequest;

import butterknife.OnClick;

/**
 * 测试专用
 * Created by Alashi on 2016/12/14.
 */
public class TestF extends BaseFragment {
    private static final String TAG = "TestF";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test, container, false);
    }

    @OnClick(R.id.setHeadimg)
    public void onButton(View view){
        testOk();
    }

    //测试上传文件---ok
    private void testOk(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        MultipartRequest multipartRequest = new MultipartRequest(
                "http://tf.jinyazhou88.org/index.jsp?c=user&a=uploadHeadimg",
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    //调用成功
                },
                error -> {
                    //调用失败
                    Log.d(TAG, "onErrorResponse: " + error);
                });
        multipartRequest.addHeader("User-Agent", "Android App");
        multipartRequest.addHeader("Cookie", GoldenAsiaApp.getUserCentre().getSession());

        // 通过MultipartEntity来设置参数
        MultipartEntity multi = multipartRequest.getMultiPartEntity();
        //multi.addStringPart("location", "模拟的地理位置");
        //multi.addStringPart("type", "0");
        String path = "/sdcard/1/111.png";
        byte[] image = Utils.getBitmap(path, 200, 200);
        multi.addBinaryPart("headimg", image, "image/png", null, "??.png");

        queue.add(multipartRequest);
    }
}
