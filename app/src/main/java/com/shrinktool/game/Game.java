package com.shrinktool.game;

import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.shrinktool.data.Method;
import com.shrinktool.view.NumberGroupView;
import com.shrinktool.view.PickNumber;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 某一种彩种下的一种玩法：提供选号界面中间的选号区布局配置，计算注数，格式化输出选号Code的String
 * Created by Alashi on 2016/2/16.
 */
public abstract class Game implements NumberGroupView.OnChooseItemClickListener {
    protected ViewGroup topLayout;
    protected OnSelectedListener onSelectedListener;
    protected Method method;
    protected ArrayList<PickNumber> pickNumbers = new ArrayList<>();
    private int singleNum;
    private boolean isDup;

    public Game(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public final void inflate(ViewGroup container) {
        topLayout = container;
        onInflate();
    }

    public final void setOnSelectedListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }

    protected final void notifyListener() {
        if (onSelectedListener != null) {
            onSelectedListener.onChanged(this);
        }
    }

    public final void destroy() {
        topLayout.removeAllViews();
        onSelectedListener = null;
        onDestroy();
    }

    public void onDestroy(){
        pickNumbers.clear();
    }

    public final void addPickNumber(PickNumber pickNumber) {
        pickNumbers.add(pickNumber);
        pickNumber.setChooseItemClickListener(this);
    }

    public String getWebViewCode() {
        JsonArray jsonArray = new JsonArray();
        for (PickNumber pickNumber : pickNumbers) {
            jsonArray.add(transform(pickNumber.getCheckedNumber(), false, true));
        }
        return jsonArray.toString();
    }

    public String getSubmitCodes() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, size = pickNumbers.size(); i < size; i++) {
            builder.append(transform(pickNumbers.get(i).getCheckedNumber(), false, false));
            if (i != size - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public ViewGroup getTopLayout() {
        return topLayout;
    }

    public int getSingleNum() {
        return singleNum;
    }

    public boolean isDup() {
        return isDup;
    }

    public void setNumState(int singleNum, boolean isDup) {
        this.singleNum = singleNum;
        this.isDup = isDup;
    }

    public abstract void onInflate();

    /** 设置遗漏数据，子类可能需要重写此方法 */
    public void setMiss(boolean showMiss, int[] miss){
        int size = pickNumbers.size();
        if (size == 0) {
            return;
        }

        int step = pickNumbers.get(0).getNumberGroupView().getNumberCount();
        if (miss == null || miss.length != step * size) {
            for (int i = 0; i < size; i++) {
                pickNumbers.get(i).getNumberGroupView().setMiss(showMiss, null);
            }
        } else {
            try {
                for (int i = 0; i < size; i++) {
                    pickNumbers.get(i).getNumberGroupView()
                            .setMiss(showMiss, Arrays.copyOfRange(miss, i * step, (i + 1) * step));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("Game", "setMiss: ArrayIndexOutOfBoundsException");
                for (int i = 0; i < size; i++) {
                    pickNumbers.get(i).getNumberGroupView().setMiss(showMiss, null);
                }
            }
        }
    }

    /**
     * 提示调用
     */
    public void onCustomDialog(String msg) {
       PromptManager.showCustomDialog(topLayout.getContext(), msg);
    }

    @Override
    public void onChooseItemClick(boolean maxCount) {
        notifyListener();
    }

    /** 将Int的list转换成字符串，如list[06, 07] 转成string[06_07]
     * @param list int型数组
     * @param numberStyle 数字显示风格，true: 6, false: 06
     * @param emptyStyle 数组空时的显示风格， true: ""，false: "-"*/
    protected static String transform(ArrayList<Integer> list, boolean numberStyle, boolean emptyStyle) {
        StringBuilder builder = new StringBuilder();
        if (list.size() > 0) {
            for (int i = 0, size = list.size(); i < size; i++) {
                builder.append(String.format(numberStyle ? "%d" : "%02d", list.get(i)));
                if (!numberStyle && i != size - 1) {
                    builder.append("_");
                }
            }
        } else {
            builder.append(emptyStyle? "": "-");
        }
        return builder.toString();
    }

    public void reset() {
        for (PickNumber pickNumber : pickNumbers) {
            pickNumber.getNumberGroupView().setCheckNumber(null);
        }
        notifyListener();
    }
}
