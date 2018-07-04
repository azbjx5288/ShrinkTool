package com.shrinktool.view;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;

import java.util.ArrayList;

/**
 * 对选号列的View的相关操作的封装
 * Created by Alashi on 2016/1/13.
 */
public class PickNumber implements View.OnClickListener {
    private static final String TAG = PickNumber.class.getSimpleName();

    private NumberGroupView numberGroupView;
    private LinearLayout viewGroup;
    private TextView columnTitle;
    private NumberGroupView.OnChooseItemClickListener onChooseItem;

    public PickNumber(View topView, String title) {
        columnTitle = (TextView) topView.findViewById(R.id.pick_column_title);
        columnTitle.setText(title);
        numberGroupView = (NumberGroupView) topView.findViewById(R.id.pick_column_NumberGroupView);
        viewGroup = (LinearLayout) topView.findViewById(R.id.pick_column_control);
        for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
            viewGroup.getChildAt(i).setOnClickListener(this);
        }
    }

    public NumberGroupView getNumberGroupView() {
        return numberGroupView;
    }

    public ArrayList<Integer> getCheckedNumber() {
        return numberGroupView.getCheckedNumber();
    }

    /**
     * 显示文字
     */
    public void setDisplayText(String[] displayText) {
        numberGroupView.setDisplayText(displayText);
    }

    /**
     * 显示方式
     */
    public void setNumberStyle(Boolean flagStyle) {
        numberGroupView.setNumberStyle(flagStyle);
    }

    /**
     * 选择模式
     */
    public void setChooseMode(boolean chooseMode) {
        numberGroupView.setChooseMode(chooseMode);
    }

    /**
     * 背景设置
     */
    public void setUncheckedDrawable(Drawable uncheckedDrawable) {
        numberGroupView.setUncheckedDrawable(uncheckedDrawable);
    }

    public void setCheckedDrawable(Drawable checkedDrawable) {
        numberGroupView.setCheckedDrawable(checkedDrawable);
    }

    public void onRandom(ArrayList<Integer> list) {
        numberGroupView.setCheckNumber(list);
    }

    /**
     * 选择监听
     */
    public void setChooseItemClickListener(NumberGroupView.OnChooseItemClickListener onChooseItem) {
        this.onChooseItem = onChooseItem;
        numberGroupView.setChooseItemListener(onChooseItem);
    }

    @Override
    public void onClick(View v) {
        int max = numberGroupView.getMaxNumber();
        int min = numberGroupView.getMinNumber();
        ArrayList<Integer> list = new ArrayList<>();
        switch (v.getId()) {
            case R.id.pick_column_big:
                for (int i = min + (max - min + 1) / 2; i <= max; i++) {
                    list.add(i);
                }
                break;
            case R.id.pick_column_small:
                for (int i = min, end = min + (max - min + 1) / 2; i < end; i++) {
                    list.add(i);
                }
                break;
            case R.id.pick_column_singular:
                for (int i = min; i <= max; i++) {
                    if (i % 2 != 0) {
                        list.add(i);
                    }
                }
                break;
            case R.id.pick_column_evenNumbers:
                for (int i = min; i <= max; i++) {
                    if (i % 2 == 0) {
                        list.add(i);
                    }
                }
                break;
            case R.id.pick_column_all:
                for (int i = min; i <= max; i++) {
                    list.add(i);
                }
                break;
            case R.id.pick_column_clear:

                break;
            default:
                Log.d(TAG, "onClick: nonsupport view id:" + v.getId());
                return;
        }
        numberGroupView.setCheckNumber(list);
        onChooseItem.onChooseItemClick(false);
    }

    public void setLableText(String text) {
        columnTitle.setText(text);
    }

    public void setColumnAreaHideOrShow(boolean flag) {
        if (flag) {
            viewGroup.setVisibility(View.VISIBLE);
        } else {
            viewGroup.setVisibility(View.GONE);
        }
    }
}
