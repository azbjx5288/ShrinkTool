package com.shrinktool.component;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.shrinktool.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一般性的静态方法
 * Created by Alashi on 2016/9/2.
 */
public class Utils {

    /** 部分机子的系统状态栏颜色问题 */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void statusColor(Activity activity) {
        if (activity == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Window window = activity.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(activity.getResources().getColor(R.color.app_main));

        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
    }

    public static void drawDebug(Canvas canvas, float l, float t, float r, float b, Paint debugPaint) {
        canvas.drawLine(l, t, r, t, debugPaint);
        canvas.drawLine(l, t, l, b, debugPaint);
        canvas.drawLine(l, t, r, b, debugPaint);//对角线
        canvas.drawLine(r, t, r, b, debugPaint);
        canvas.drawLine(l, b, r, b, debugPaint);
        canvas.drawLine(l, b, r, t, debugPaint);//对角线
    }

    /**
     * 下载Apk, 并设置Apk地址,
     * 默认位置: /storage/sdcard0/Download
     *
     * @param context    上下文
     * @param downLoadUrl 下载地址
     * @param infoName   通知名称
     * @param description  通知描述
     */
    @SuppressWarnings("unused")
    public static void downloadApk(Context context, String downLoadUrl, String description,
            String infoName) {

        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(downLoadUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        request.setTitle(infoName);
        request.setDescription(description);

        //在通知栏显示下载进度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        //设置保存下载apk保存路径
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, infoName + ".apk");
        Uri out = Uri.withAppendedPath(Uri.fromFile(context.getExternalCacheDir()), infoName + ".apk");
        request.setDestinationUri(out);
        request.setMimeType("application/vnd.android.package-archive");

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //进入下载队列
        manager.enqueue(request);
    }

    public static String writeOutFilterResult(Context context, String lotteryName, String text) {
        File dir = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists() || !dir.canWrite()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(" yyyy-MM-dd HH-mm");
        String time = format.format(new Date());
        File file = new File(dir, lotteryName + time + ".txt");
        int index = 1;
        while (file.exists()) {
            file = new File(dir, lotteryName + time + "(" + index + ").txt");
        }
        try {
            FileOutputStream os = new FileOutputStream(file);
            os.write(text.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        index = Environment.getExternalStorageDirectory().getAbsolutePath().length() + 1;
        return file.getAbsolutePath().substring(index);
    }


    public static Bitmap getPathBitmap(String path, int dw, int dh) throws FileNotFoundException {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, op);

        int wRatio = (int) Math.ceil(op.outWidth / (float) dw);
        int hRatio = (int) Math.ceil(op.outHeight / (float) dh);
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, op);
    }

    public static byte[] getBitmap(String path, int dw, int dh) {
        Bitmap bitmap = null;
        try {
            bitmap = getPathBitmap(path, dw, dh);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        bitmap.recycle();
        return baos.toByteArray();
    }

    public static void setDrawableLeft(TextView textView, int drawableId){
        Drawable drawable = textView.getContext().getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }
}
