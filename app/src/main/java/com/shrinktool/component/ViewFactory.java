package com.shrinktool.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shrinktool.R;


/**
 * Created on 2016/01/07.
 *
 * @author ACE
 * @功能描述: ImageView创建工厂
 */
public class ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @param context
     * @param url
     * @param title
     * @return
     */
    public static View getImageView(Context context, String url, String title) {
        View v = LayoutInflater.from(context).inflate(R.layout.cycleviewpager_textview, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.banner_image);
        ImageLoader.getInstance().displayImage(url, imageView);
        TextView titleTv = (TextView) v.findViewById(R.id.banner_title);
        titleTv.setText(title);
        return v;
    }

    /**
     * 获取一个简单的视图
     *
     * @param text
     * @return
     */
    public static View getImageView(Context context, String text) {
        View v = LayoutInflater.from(context).inflate(R.layout.cycleviewpager_textview, null);

        TextView textTv = (TextView) v.findViewById(R.id.text);
        TextView titleTv = (TextView) v.findViewById(R.id.title);
        textTv.setText(text);
        titleTv.setVisibility(View.GONE);
        return v;
    }
}
