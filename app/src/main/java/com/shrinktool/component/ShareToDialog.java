package com.shrinktool.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinktool.R;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * “分享到”对话框
 * Created by Alashi on 2016/9/12.
 */
public class ShareToDialog {
    private static final String TAG = "ShareToDialog";
    private Dialog dialog;
    private Activity activity;
    private ArrayList<DataItem> dataList = new ArrayList<>();
    private String title;
    private String summary;
    private String url;

    private static final String MSG_SUMMARY = "最近我在使用这个APP看走势，还能用手机选择号码进行过滤，用了一段时间，感觉可管用了，你也去下一个吧！";
    private static final String MSG_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.shrinktool";
    private static final String MSG_IMAGE = "http://fd.w956.com/images/double.png";

    public ShareToDialog(Activity activity) {
        this.activity = activity;
        title = activity.getString(R.string.app_name);
        summary = MSG_SUMMARY;
        url = MSG_URL;
        dataList.add(new DataItem(0, R.drawable.share_pyq, "微信朋友圈"));
        dataList.add(new DataItem(1, R.drawable.share_wx, "微信好友"));
        dataList.add(new DataItem(2, R.drawable.share_qzone, "QQ空间"));
        dataList.add(new DataItem(3, R.drawable.share_qq, "QQ好友"));
        //dataList.add(new DataItem(4, R.drawable.jia, "新浪微博"));
        //dataList.add(new DataItem(5, R.drawable.jia, "其他"));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private void shareTo(int id) {
        //Toast.makeText(ShareToDialog.this.activity, " " + id, Toast.LENGTH_SHORT).show();
        switch (id) {
            case 5:
                shareToOther();
                break;
            case 0:
                wechatShare(1);
                break;
            case 1:
                wechatShare(0);
                break;
            case 2:
                shareToQzone();
                break;
            case 3:
                shareToQQ();
                break;
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
            return;
        }

        dialog = new Dialog(activity, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.share_to_dialog_layout);
        GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
        dialog.findViewById(R.id.cancel).setOnClickListener(view1 -> dialog.dismiss());
        gridView.setAdapter(new MyAdapter());
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            shareTo(dataList.get(i).id);
            dialog.dismiss();
        });

        //获得当前窗体
        Window window = dialog.getWindow();

        //重新设置
        WindowManager.LayoutParams lp = window.getAttributes();
        window .setGravity(Gravity.BOTTOM);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT; // 宽度
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT; // 高度

        //(当Window的Attributes改变时系统会调用此函数)
        window .setAttributes(lp);
        dialog.show();
    }

    private class DataItem{
        int id;
        int logoId;
        String name;

        public DataItem(int id, int logoId, String name) {
            this.id = id;
            this.logoId = logoId;
            this.name = name;
        }
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_to_item, viewGroup, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DataItem dataItem = dataList.get(index);
            holder.name.setText(dataItem.name);
            holder.logo.setImageResource(dataItem.logoId);

            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.imageView)
        ImageView logo;
        @Bind(R.id.name)
        TextView name;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }

    private void shareToOther() {
        /*String smsBody = MSG_SUMMARY + MSG_URL;
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        activity.startActivity(sendIntent);*/

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); // 纯文本
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, summary + url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private void shareToQQ() {
        Tencent tencent = Tencent.createInstance("1105757316", activity.getApplicationContext());
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  summary);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, MSG_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  activity.getString(R.string.app_name));
        tencent.shareToQQ(activity, params, new ShareListener());
    }

    private void shareToQzone () {
        Tencent tencent = Tencent.createInstance("1105757316", activity.getApplicationContext());

        Bundle params = new Bundle();
        //分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(MSG_IMAGE);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList);
        tencent.shareToQzone(activity, params, new ShareListener());
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag){
        IWXAPI wxApi = WXAPIFactory.createWXAPI(activity, "wx9f419ba4a6b0b0a3");
        wxApi.registerApp("wx9f419ba4a6b0b0a3");

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = summary;
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private class ShareListener implements IUiListener {

        @Override
        public void onCancel() {
            Toast.makeText(activity, "分享取消", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object arg0) {
            Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError arg0) {
            Toast.makeText(activity, "分享出错", Toast.LENGTH_SHORT).show();
        }

    }
}
