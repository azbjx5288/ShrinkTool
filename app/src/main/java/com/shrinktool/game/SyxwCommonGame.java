package com.shrinktool.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.data.Method;
import com.shrinktool.view.PickNumber;

/**
 * 11选5时，一般性的玩法，均由这个类处理，除“定单双, SDDDS”
 */
public class SyxwCommonGame extends Game {

    public SyxwCommonGame(Method method) {
        super(method);
    }

    @Override
    public void onInflate() {
        try {
            java.lang.reflect.Method function = getClass().getMethod(method.getName(), Game.class);
            function.invoke(null, this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(topLayout.getContext(), "不支持的类型", Toast.LENGTH_LONG).show();
        }
    }

    private static View createDefaultPickLayout(ViewGroup container) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.pick_column2, null, false);
    }

    private static void createPicklayout(Game game, String[] name) {
        View[] views = new View[name.length];
        for (int i = 0; i < name.length; i++) {
            View view = createDefaultPickLayout(game.getTopLayout());
            game.addPickNumber(new PickNumber(view, name[i]));
            views[i] = view;
        }

        ViewGroup topLayout = game.getTopLayout();
        for (View view : views) {
            topLayout.addView(view);
        }
    }
    /*
    //三星直选
    public static void SXZX(Game game) {
        createPicklayout(game, new String[]{"百位", "十位", "个位"});
    }

    //四星直选
    public static void SIXZX(Game game) {
        createPicklayout(game, new String[]{"千位", "百位", "十位", "个位"});
    }

    //五星直选
    public static void WXZX(Game game) {
        createPicklayout(game, new String[]{"万位", "千位", "百位", "十位", "个位"});
    }

    //前二直选
    public static void SDQEZX(Game game) {
        createPicklayout(game, new String[]{"第一位", "第二位"});
    }

    //前三直选
    public static void SDQSZX(Game game) {
        createPicklayout(game, new String[]{"第一位", "第二位", "第三位"});
    }
    */
    //任选5中5
    public static void SDRX5(Game game) {
        createPicklayout(game, new String[]{"任选五"});
    }
}
