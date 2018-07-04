package com.shrinktool.Analyzer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shrinktool.component.Utils;

import java.util.ArrayList;

/**
 * 用于显示走势图
 * Created by Alashi on 2016/7/13.
 */
public class CodeFormView extends View {
    private static final String TAG = "CodeFormView";
    private static final boolean DEBUG_DRAW = false;
    private Paint linePaint;//x,y轴的线
    private Paint textPaint;
    private Paint debugPaint;
    private PaintFlagsDrawFilter paintFlagsDrawFilter;// 毛边过滤
    private ArrayList<RowInfo> rowInfos = new ArrayList<>();

    public CodeFormView(Context context) {
        super(context);
        init();
    }

    public CodeFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.GRAY);

        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);

        debugPaint = new Paint();
        debugPaint.setColor(Color.RED);

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    public void setRowInfo(ArrayList<RowInfo> rowInfo) {
        this.rowInfos = rowInfo;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (rowInfos == null) {
            setMeasuredDimension(0, 0);
            return;
        }

        int unitOffsetX = 0;
        int unitOffsetY = 0;
        for (RowInfo rowInfo: rowInfos) {
            int maxRight = 0;
            int maxBottom = 0;
            int uW = rowInfo.unitW;
            int uH = rowInfo.unitH;
            for (UnitInfo info: rowInfo.infos) {
                //int left = unitOffsetX + info.x * uW;
                //int top = info.y * uH;
                int right = unitOffsetX + (info.x + info.w) * uW;
                int bottom = (info.y + info.h) * uH;
                maxRight = Math.max(maxRight, right);
                maxBottom = Math.max(maxBottom, bottom);
            }
            unitOffsetX = maxRight;
            unitOffsetY = Math.max(unitOffsetY, maxBottom);
        }
        //Log.d(TAG, "onMeasure: w,h = " + unitOffsetX + ", " + unitOffsetY);
        setMeasuredDimension(unitOffsetX, unitOffsetY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis();
        super.onDraw(canvas);
        canvas.setDrawFilter(paintFlagsDrawFilter);

        int unitOffset = 0;
        for (RowInfo rowInfo: rowInfos) {
            int maxRight = 0;
            int maxBottom = 0;
            int uW = rowInfo.unitW;
            int uH = rowInfo.unitH;
            for (UnitInfo info: rowInfo.infos) {
                //矩形
                int left = unitOffset + info.x * uW;
                int top = info.y * uH;
                int right = unitOffset + (info.x + info.w) * uW;
                int bottom = (info.y + info.h) * uH;

                maxBottom = Math.max(maxBottom, bottom);
                maxRight = Math.max(maxRight, right);

                linePaint.setColor(Color.GRAY);
                canvas.drawRect(left, top, right, bottom, linePaint);

                linePaint.setColor(info.bgColor);
                canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, linePaint);

                //绘制文字
                if (info.unitTextDrawer != null) {
                    info.unitTextDrawer.onDraw(canvas, rowInfo, info, unitOffset);
                } else {
                    textPaint.setColor(info.textColor);
                    textPaint.setTextSize(info.textSize);
                    float x = unitOffset + info.x * uW + (info.w * uW - textPaint.measureText(info.text)) / 2;
                    float y = info.y * uH + info.h * uH * 0.5f
                            + textPaint.getTextSize() * 0.4f;
                    //Log.d(TAG, "onDraw: text: " + x + " , " + y + " -> " + info);
                    canvas.drawText(info.text, x, y, textPaint);
                    //drawDebug(canvas, left, top, right, bottom);
                    if (info.smallText != null) {
                        x += textPaint.measureText(info.text);
                        y -= textPaint.getTextSize() * 0.4f;
                        textPaint.setTextSize((float) (textPaint.getTextSize() * 0.8));
                        canvas.drawText(info.smallText, x, y, textPaint);
                    }
                }
            }

            if (rowInfo.supportHighlight) {
                UnitInfo lastInfo = null;
                for (UnitInfo info: rowInfo.infos) {
                    if (info.highlight) {
                        if (lastInfo == null) {
                            lastInfo = info;
                        } else {
                            //绘制斜线
                            int startX = unitOffset + lastInfo.x * uW + lastInfo.w * uW / 2;
                            int startY = lastInfo.y * uH + lastInfo.h * uH / 2;
                            int stopX = unitOffset + info.x * uW + info.w * uW / 2;
                            int stopY = info.y * uH + info.h * uH / 2;
                            linePaint.setStrokeWidth(2);
                            linePaint.setColor(Color.RED);
                            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
                            linePaint.reset();

                            //圆球的背景
                            linePaint.setColor(rowInfo.highlightColor);
                            int radius = lastInfo.h * uH / 2 - 2;
                            canvas.drawCircle(startX, startY, radius, linePaint);
                            canvas.drawCircle(stopX, stopY, radius, linePaint);

                            //绘制文字，上一个球的
                            textPaint.setColor(lastInfo.textColor);
                            textPaint.setTextSize(lastInfo.textSize);
                            float x = unitOffset + lastInfo.x * uW + (lastInfo.w * uW - textPaint.measureText(lastInfo.text)) / 2;
                            float y = lastInfo.y * uH + lastInfo.h * uH * 0.5f
                                    + textPaint.getTextSize() * 0.4f;
                            //Log.d(TAG, "onDraw: text: " + x + " , " + y + " -> " + info);
                            canvas.drawText(lastInfo.text, x, y, textPaint);

                            //绘制文字，当前一个球的
                            textPaint.setColor(info.textColor);
                            textPaint.setTextSize(info.textSize);
                            x = unitOffset + info.x * uW + (info.w * uW - textPaint.measureText(info.text)) / 2;
                            y = info.y * uH + info.h * uH * 0.5f
                                    + textPaint.getTextSize() * 0.4f;
                            //Log.d(TAG, "onDraw: text: " + x + " , " + y + " -> " + info);
                            canvas.drawText(info.text, x, y, textPaint);

                            lastInfo = info;
                        }
                    }
                }
            }

            if (rowInfo.leftLine) {
                linePaint.setStrokeWidth(3);
                linePaint.setColor(Color.BLACK);
                canvas.drawLine(unitOffset, 0, unitOffset, maxBottom, linePaint);
            }
            if (rowInfo.rightLine) {
                linePaint.setStrokeWidth(3);
                linePaint.setColor(Color.BLACK);
                canvas.drawLine(maxRight, 0, maxRight, maxBottom, linePaint);
            }

            if (DEBUG_DRAW) {
                Utils.drawDebug(canvas, unitOffset, 0, maxRight, maxBottom, debugPaint);
            }

            unitOffset = maxRight;
        }
        Log.i(TAG, "onDraw: use time is : " + (System.currentTimeMillis() - time));
    }

    public static class RowInfo {
        int unitW;
        int unitH;//一个小格表示的像素大小
        boolean supportHighlight = false;
        int highlightColor = Color.parseColor("#1ABC9C");
        boolean leftLine = true;//加粗左边线
        boolean rightLine = true;//加粗右边线

        ArrayList<UnitInfo> infos = new ArrayList<>();

        public UnitInfo addUnitInfo(String text, int x, int y, int w, int h,
                                int bgColor, int textColor, int textSize, boolean highlight) {
            UnitInfo unitInfo = new UnitInfo();
            unitInfo.h = h;
            unitInfo.w = w;
            unitInfo.y = y;
            unitInfo.x = x;
            unitInfo.bgColor = bgColor;
            unitInfo.textColor = textColor;
            unitInfo.textSize = textSize;
            unitInfo.text = text;
            unitInfo.highlight = highlight;

            infos.add(unitInfo);
            return unitInfo;
        }
    }

    /** 用于定制单元格内的文字的绘制 */
    public interface UnitTextDrawer {
        void onDraw(Canvas canvas, RowInfo rowInfo, UnitInfo info, int unitOffset);
    }

    public static class UnitInfo{
        String text; //显示的文字
        int textSize = 48; //字体大小
        int textColor = Color.BLACK; //字体颜色
        int bgColor = Color.WHITE; //背景颜色
        int x = 0; //X坐标
        int y = 0; //Y坐标
        int w = 1; //占比
        int h = 1; //占比
        boolean highlight = false; //高亮，开奖号码位，高亮、连线显示
        String smallText;//右上角的小字内容
        UnitTextDrawer unitTextDrawer;

        @Override
        public String toString() {
            return "UnitInfo{" +
                    "text='" + text + '\'' +
                    ", x=" + x +
                    ", y=" + y +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }
}
