package com.shrinktool.game;

import android.util.Log;
import android.widget.Toast;

import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.data.Method;

/**
 * 不支持的游戏
 * Created by Alashi on 2016/2/17.
 */
public class NonsupportGame extends Game{
    public NonsupportGame(Method method) {
        super(method);
    }

    @Override
    public void onInflate() {
        Log.w("NonsupportGame", "onInflate: " + GsonHelper.toJson(method));
        Toast.makeText(topLayout.getContext(), "不支持的类型", Toast.LENGTH_LONG).show();
    }
}
