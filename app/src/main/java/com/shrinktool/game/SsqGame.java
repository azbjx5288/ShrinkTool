package com.shrinktool.game;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.data.Method;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 双色球
 * Created by Alashi on 2016/8/2.
 */
public class SsqGame extends Game {
    private NumberGroupView red;
    private NumberGroupView blue;

    public SsqGame(Method method) {
        super(method);
    }

    @Override
    public void onInflate() {
        ViewGroup layout = (ViewGroup) LayoutInflater.from(topLayout.getContext()).inflate(R.layout.pick_column_ssq, null, false);
        red = (NumberGroupView) layout.findViewById(R.id.pick_column_NumberGroupView_red);
        blue = (NumberGroupView) layout.findViewById(R.id.pick_column_NumberGroupView_blue);
        red.setChooseItemListener(maxCount -> {
            if (maxCount) {
                Toast.makeText(red.getContext(), "最多可以选择21个红球", Toast.LENGTH_SHORT).show();
            } else {
                notifyListener();
            }
        });
        red.setMaxChooseCount(21);
        blue.setChooseItemListener(maxCount -> {
            if (maxCount) {
                Toast.makeText(red.getContext(), "最多可以选择10个蓝球", Toast.LENGTH_SHORT).show();
            } else {
                notifyListener();
            }
        });
        blue.setMaxChooseCount(10);
        topLayout.addView(layout);

        if (BuildConfig.DEBUG) {
            ArrayList<Integer> numRed = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                numRed.add(i);
            }
            red.setCheckNumber(numRed);

            ArrayList<Integer> numBlue = new ArrayList<>();
            numBlue.add(12);
            numBlue.add(13);
            blue.setCheckNumber(numBlue);
        }
    }

    public String getWebViewCode() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(transform(red.getCheckedNumber(), false, true));
        jsonArray.add(transform(blue.getCheckedNumber(), false, true));
        return jsonArray.toString();
    }

    public String getSubmitCodes() {
        return transform(red.getCheckedNumber(), false, false) +
                "," +
                transform(blue.getCheckedNumber(), false, false);
    }

    @Override
    public void setMiss(boolean showMiss, int[] miss) {
        if (miss == null) {
            red.setMiss(showMiss, null);
            red.setMiss(showMiss, null);
        } else {
            red.setMiss(showMiss, Arrays.copyOfRange(miss, 0, 33));
            blue.setMiss(showMiss, Arrays.copyOfRange(miss, 33, miss.length));
        }
    }

    @Override
    public void reset() {
        red.setCheckNumber(null);
        blue.setCheckNumber(null);
        notifyListener();
    }
}
