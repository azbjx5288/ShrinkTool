package com.shrinktool.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.shrinktool.R;
import com.shrinktool.component.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 用于显示彩票选择时的数字栏
 * Created by Alashi on 2016/1/13.
 */
public class NumberGroupView extends View {
    private static final String TAG = NumberGroupView.class.getSimpleName();

    private static final boolean DEBUG_DRAW = false;
    private Paint debugPaint;

    private TextPaint uncheckedPaint;
    private TextPaint checkedPaint;
    private Drawable checkedDrawable;
    private Drawable uncheckedDrawable;
    private int itemSize;
    private int horizontalGap;
    private int verticalGap;
    /** true 数字显示成“1”，false 数字显示成“01” null 显示文字 */
    private Boolean numberStyle;
    /** true 默认为单选 false 为多选*/
    private boolean chooseMode;
    private boolean readOnly;
    private int maxNumber;
    private int minNumber;
    private String[] displayText;
    private int column;
    private int maxChooseCount;
    private DisplayMethod method = DisplayMethod.SINGLE;

    /** 是否显示遗漏 */
    private boolean showMiss;
    /** 遗漏数据 */
    private int[] miss;// = new int[]{25,1,40,14,0,5,10,6,9,2};
    private int highlightMiss1;
    private int highlightMiss2;
    private float missTextSize;
    private TextPaint missPaint;

    private SparseBooleanArray checkedArray;
    private GestureDetector gestureDetector;

    private OnChooseItemClickListener chooseItemListener;

    public NumberGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NumberGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable.NumberGroupView);
        itemSize = attribute.getDimensionPixelSize(R.styleable.NumberGroupView_itemSize, 48);
        verticalGap = attribute.getDimensionPixelSize(R.styleable.NumberGroupView_verticalGap, 16);
        int textColor = attribute.getColor(R.styleable.NumberGroupView_textColor, Color.parseColor("#F34C66"));
        int checkedTextColor = attribute.getColor(R.styleable.NumberGroupView_checkedTextColor,
                Color.parseColor("#FFFFFF"));
        float textSize = attribute.getDimension(R.styleable.NumberGroupView_textSize, 36);
        missTextSize = attribute.getDimension(R.styleable.NumberGroupView_missTextSize, 36);
        checkedDrawable = attribute.getDrawable(R.styleable.NumberGroupView_checkedDrawable);
        uncheckedDrawable = attribute.getDrawable(R.styleable.NumberGroupView_uncheckedDrawable);
        numberStyle = attribute.getBoolean(R.styleable.NumberGroupView_numberStyle, true);
        chooseMode = attribute.getBoolean(R.styleable.NumberGroupView_chooseMode, false);
        maxNumber = attribute.getInt(R.styleable.NumberGroupView_maxNumber, 9);
        minNumber = attribute.getInt(R.styleable.NumberGroupView_minNumber, 0);
        column = attribute.getInt(R.styleable.NumberGroupView_column, 5);
        attribute.recycle();

        uncheckedPaint = new TextPaint();
        uncheckedPaint.setStyle(Paint.Style.FILL);
        uncheckedPaint.setTextAlign(Paint.Align.CENTER);
        uncheckedPaint.setColor(textColor);
        uncheckedPaint.setTextSize(textSize);
        uncheckedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        checkedPaint = new TextPaint();
        checkedPaint.setStyle(Paint.Style.FILL);
        checkedPaint.setTextAlign(Paint.Align.CENTER);
        checkedPaint.setColor(checkedTextColor);
        checkedPaint.setTextSize(textSize);
        checkedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        missPaint = new TextPaint();
        missPaint.setStyle(Paint.Style.FILL);
        missPaint.setTextSize(missTextSize);
        missPaint.setColor(Color.LTGRAY);
        missPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        debugPaint = new Paint();
        debugPaint.setColor(Color.RED);

        checkedArray = new SparseBooleanArray(maxNumber - minNumber + 1);

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (readOnly) {
                    return true;
                }
                calculateClick((int) e.getX(), (int) e.getY());
                //Log.d(TAG, "onSingleTapUp: " + Arrays.deepToString(getCheckedNumber().toArray()));
                return true;
            }

        });

    }

    /**
     * 获取选中的数字
     */
    public ArrayList<Integer> getCheckedNumber() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0, count = maxNumber - minNumber + 1; i < count; i++) {
            if (checkedArray.get(i + minNumber)) {
                list.add(i + minNumber);
            }
        }
        return list;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setMaxChooseCount(int maxChooseCount) {
        this.maxChooseCount = maxChooseCount;
    }

    /**
     * 设置可以选中的数字的范围[min, max]
     */
    public void setNumber(int minNumber, int maxNumber) {
        if (this.minNumber == minNumber && this.maxNumber == maxNumber) {
            return;
        }
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        checkedArray = new SparseBooleanArray(maxNumber - minNumber + 1);
        requestLayout();
    }


    /**
     * 设置需要显示的文字集合
     **/
    public void setDisplayText(String[] displayText) {
        this.displayText = displayText;
    }

    /**
     * 设置数字显示的尺寸大小，单位像素
     */
    public void setItemSize(int itemSize) {
        if (this.itemSize == itemSize) {
            return;
        }
        this.itemSize = itemSize;
        requestLayout();
    }

    /**
     * 设置一行显示多少个数字
     */
    public void setColumn(int column) {
        if (this.column == column) {
            return;
        }
        this.column = column;
        requestLayout();
    }

    /**
     * 设置选中的数字显示的背景图
     */
    public void setCheckedDrawable(Drawable checkedDrawable) {
        this.checkedDrawable = checkedDrawable;
    }

    /**
     * 设置未选中的数字显示的背景图
     */
    public void setUncheckedDrawable(Drawable uncheckedDrawable) {
        this.uncheckedDrawable = uncheckedDrawable;
    }

    /**
     * 行间垂直间隔，单位像素
     */
    public void setVerticalGap(int verticalGap) {
        if (this.verticalGap == verticalGap) {
            return;
        }
        this.verticalGap = verticalGap;
        requestLayout();
    }

    /**
     * 选择模式 true 单选模式 false 多选模式
     */
    public void setChooseMode(boolean chooseMode) {
        this.chooseMode = chooseMode;
    }

    private boolean isRadioStyle() {
        return chooseMode;
    }

    /**
     * 数字显示的风格，true 数字显示成“1”，false 数字显示成“01” null显示文字
     */
    public void setNumberStyle(Boolean numberStyle) {
        this.numberStyle = numberStyle;
    }

    /**
     * 数字显示尺寸
     */
    public void setTextSize(float textSize) {
        uncheckedPaint.setTextSize(textSize);
        checkedPaint.setTextSize(textSize);
    }

    public void setDisplayMethod(DisplayMethod method) {
        this.method = method;
    }

    public OnChooseItemClickListener getChooseItemListener() {
        return chooseItemListener;
    }

    public void setChooseItemListener(OnChooseItemClickListener chooseItemListener) {
        this.chooseItemListener = chooseItemListener;
    }

    /**
     * 设置被选中的数字(0-9)
     */
    public void setCheckNumber(ArrayList<Integer> checkNumber) {
        checkedArray.clear();
        if (checkNumber != null) {
            for (int number : checkNumber) {
                checkedArray.put(number, true);
            }
        }
        invalidate();
        //Log.d(TAG, "setCheckNumber: " + Arrays.deepToString(getCheckedNumber().toArray()));
    }

    /** 设置遗漏数据 */
    public NumberGroupView setMiss(int[] miss) {
        if (miss != null) {
            if (miss.length >= 2) {
                int[] tmpMiss = miss.clone();
                Arrays.sort(tmpMiss);
                highlightMiss1 = tmpMiss[miss.length - 1];
                highlightMiss2 = tmpMiss[miss.length - 2];
            } else {
                highlightMiss1 = highlightMiss2 = miss[0];
            }
        }
        this.miss = miss;
        requestLayout();
        return this;
    }

    /** 设置是否需要显示遗漏，在"遗漏数据"不为空时有效 */
    public NumberGroupView showMiss(boolean showMiss) {
        if (showMiss != this.showMiss) {
            this.showMiss = showMiss;
            requestLayout();
        }
        return this;
    }

    public NumberGroupView setMiss(boolean showMiss, int[] miss) {
        this.showMiss = showMiss;
        return setMiss(miss);
    }

    private void calculateClick(int eventX, int eventY) {
        int x, y;
        Rect rect = new Rect();
        int missGap = showMiss && miss != null ? (int) missTextSize : 0;
        for (int i = 0, count = maxNumber - minNumber + 1; i < count; i++) {
            x = i % column * (itemSize + horizontalGap);
            y = i / column * (itemSize + missGap + verticalGap);
            rect.set(x, y, x + itemSize, y + itemSize + missGap);

            if (rect.contains(eventX, eventY)) {
                if (isRadioStyle()) {
                    Log.d(TAG, checkedArray.toString());
                    checkedArray.clear();
                }

                if (checkedArray.get(i + minNumber)) {
                    checkedArray.delete(i + minNumber);
                    invalidate();
                    notifyListener(false);
                } else {
                    if (maxChooseCount > 0 && checkedArray.size() == maxChooseCount) {
                        notifyListener(true);
                    } else {
                        checkedArray.put(i + minNumber, true);
                        invalidate();
                        notifyListener(false);
                    }
                }
                return;
            }
        }
    }

    private void notifyListener(boolean maxCount) {
        if (chooseItemListener != null) {
            chooseItemListener.onChooseItemClick(maxCount);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        horizontalGap = (specSize - column * itemSize) / (column - 1);
        int itemCount = maxNumber - minNumber + 1;
        int line =  (itemCount) / column + ((itemCount) % column != 0 ? 1 : 0);
        int specHeight = (int) (line * (itemSize + (showMiss && miss != null ? missTextSize : 0)) + (line - 1) * verticalGap);
        setMeasuredDimension(specSize, specHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint textPaint;
        checkedDrawable.setBounds(0, 0, itemSize, itemSize);
        uncheckedDrawable.setBounds(0, 0, itemSize, itemSize);

        Paint.FontMetricsInt fontMetrics = uncheckedPaint.getFontMetricsInt();
        boolean supportMiss = showMiss && miss != null;
        float x, y;
        float offTextY = (itemSize - fontMetrics.bottom - fontMetrics.top) / 2;
        // + uncheckedPaint.getTextSize() * 0.5f - uncheckedPaint.getTextSize() * 0.2f;
        for (int i = 0, count = maxNumber - minNumber + 1; i < count; i++) {
            x = i % column * (itemSize + horizontalGap);
            y = i / column * (itemSize + verticalGap + (supportMiss ? missTextSize : 0));
            canvas.save();
            canvas.translate(x, y);
            if (checkedArray.get(i + minNumber)) {
                checkedDrawable.draw(canvas);
                textPaint = checkedPaint;
            } else {
                uncheckedDrawable.draw(canvas);
                textPaint = uncheckedPaint;
            }

            String text = null;
            if (numberStyle == null) {
                if (displayText.length >= maxNumber)
                    text = moreDisplay(i);
                else {
                    Log.e(TAG, "Less than the array size required to display text");
                    text = "";
                }
            } else {
                text = String.format(numberStyle ? "%d" : "%02d", i + minNumber);
            }
            //float offTextX = (itemSize - textPaint.measureText(text)) / 2;
            float offTextX = itemSize / 2;
            canvas.drawText(text, offTextX, offTextY, textPaint);

            if (supportMiss) {
                if (i < miss.length) {
                    text = String.valueOf(miss[i]);
                } else {
                    text = "?";
                }
                float missY = itemSize + missTextSize;
                offTextX = (itemSize - missPaint.measureText(text)) / 2;
                if (i < miss.length && miss[i] == highlightMiss1 || miss[i] == highlightMiss2) {
                    int tmpColor = missPaint.getColor();
                    missPaint.setColor(Color.RED);
                    canvas.drawText(text, offTextX, missY, missPaint);
                    missPaint.setColor(tmpColor);
                } else {
                    canvas.drawText(text, offTextX, missY, missPaint);
                }
            }

            canvas.restore();
        }

        if (DEBUG_DRAW) {
            int missGap = showMiss && miss != null ? (int) missTextSize : 0;
            for (int i = 0, count = maxNumber - minNumber + 1; i < count; i++) {
                x = i % column * (itemSize + horizontalGap);
                y = i / column * (itemSize + missGap + verticalGap);
                Utils.drawDebug(canvas, x, y, x + itemSize, y + itemSize + missGap, debugPaint);
            }
        }
    }

    private String moreDisplay(int iteration) {
        String text = "";
        switch (method) {
            case SINGLE:
                text = displayText[iteration];
                break;
            case TWIN:
                String twin = "";
                for (int i = 0; i < 2; i++) {
                    twin += displayText[iteration];
                }
                text = twin;
                break;
            case THREE:
                String three = "";
                for (int i = 0; i < 2; i++) {
                    three += displayText[iteration];
                }
                text = three;
                break;
            case JUNKO:
            case MORE:
                break;
            default:
                text = displayText[iteration];
        }
        return text;
    }

    private enum DisplayMethod {
        SINGLE, TWIN, THREE, JUNKO, MORE
    }

    public int getMinNumber() {
        return minNumber;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public int getNumberCount() {
        return maxNumber - minNumber + 1;
    }

    public interface OnChooseItemClickListener {
        /**
         * @param maxCount 是否达到选号个数上限
         */
        void onChooseItemClick(boolean maxCount);
    }
}
