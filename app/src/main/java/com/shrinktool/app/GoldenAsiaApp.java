package com.shrinktool.app;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.base.CrashHandler;
import com.shrinktool.base.net.NetStateHelper;
import com.shrinktool.base.thread.ThreadPool;
import com.shrinktool.user.UserCentre;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Alashi on 2015/12/22.
 */
public class GoldenAsiaApp extends Application{

    private static GoldenAsiaApp sApp;

    private ThreadPool threadPool;
    private NetStateHelper netStateHelper;
    private UserCentre userCentre;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        //运行时，出现Crash，将log写到sd卡；
        CrashHandler.getInstance().init(this);

        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
            AnalyticsConfig.setChannel("debug");
        }
        //禁止默认的页面统计方式，以便能统计到Fragment的使用情况
        MobclickAgent.openActivityDurationTrack(false);

        threadPool = new ThreadPool();
        netStateHelper = new NetStateHelper(this);
        netStateHelper.resume();

        //String baseUrl = "http://ai.w956.com";//正式服
        //String baseUrl = "http://tf.jinyazhou88.org";//测试服
        userCentre = new UserCentre(this, BuildConfig.BASE_URL);

        configImageLoader();
    }

    /**
     * 配置ImageLoader
     */
    private void configImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation") DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions
                (options).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType
                        .LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public static GoldenAsiaApp getInstance() {
        return sApp;
    }

    public static ThreadPool getThreadPool() {
        return getInstance().threadPool;
    }

    public static NetStateHelper getNetStateHelper() {
        return getInstance().netStateHelper;
    }

    public static UserCentre getUserCentre() {
        return getInstance().userCentre;
    }
}
