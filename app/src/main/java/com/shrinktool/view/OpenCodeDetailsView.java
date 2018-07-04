package com.shrinktool.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 开奖号码详情的列表控件
 * Created by Alashi on 2016/12/21.
 */

public class OpenCodeDetailsView extends View {
    private static final String TAG = "OpenCodeDetailsView";

    private LinkedHashMap<String, String[]> date;

    private int itemWidth;
    private int itemHeight;

    private TextPaint titlePaint;
    private TextPaint contentPaint;
    private int titleBgColor;
    private int contentBgColor;
    private int divideLineColor;
    private float divideLineSize;

    private Paint debugPaint;
    private Paint paint;

    private TextColorProvider colorProvider;

    public interface TextColorProvider{
        int getColor(int xIndex, int yIndex);
    }

    public OpenCodeDetailsView(Context context) {
        super(context);
        init();
    }

    public OpenCodeDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenCodeDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        divideLineSize = density * 1;
        itemHeight = (int) (density * 28);

        titlePaint = new TextPaint();
        titlePaint.setStyle(Paint.Style.FILL);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setColor(Color.parseColor("#979797"));
        titlePaint.setTextSize(44);
        titlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        contentPaint = new TextPaint();
        contentPaint.setStyle(Paint.Style.FILL);
        contentPaint.setTextAlign(Paint.Align.CENTER);
        contentPaint.setColor(Color.parseColor("#979797"));
        contentPaint.setTextSize(44);
        contentPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        paint = new Paint();
        paint.setStrokeWidth(divideLineSize);

        titleBgColor = Color.parseColor("#ececec");
        contentBgColor = Color.parseColor("#ffffff");
        divideLineColor = Color.parseColor("#cdcdcd");

        debugPaint = new Paint();
        debugPaint.setColor(Color.RED);

        //test();
    }

    public void setColorProvider(TextColorProvider colorProvider) {
        this.colorProvider = colorProvider;
    }

    private void test() {
        LinkedHashMap<String, String[]> fd = new LinkedHashMap<>();
        fd.put("奖项", new String[]{"值1", "值2", "值3"});
        fd.put("中奖注数", new String[]{"值x1", "值x2", "值x3"});
        fd.put("中奖注数2", new String[]{"值y1", "值y2", "值y3"});
        setDate(fd);
    }

    public void setDate(LinkedHashMap<String, String[]> date) {
        this.date = date;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (date == null || date.size() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }

        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        itemWidth = specSize / date.size();
        int valueSize = date.values().iterator().next().length;
        int specHeight = itemHeight * (1 + valueSize);
        //Log.d(TAG, "onMeasure: date.size= " + date.size() + " measure to " + specSize + ", " + specHeight);
        setMeasuredDimension(specSize, specHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (date == null) {
            return;
        }

        Paint.FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();
        float x = 0;
        float y;
        float cX = itemWidth /2;
        float cY = (itemHeight - fontMetrics.bottom - fontMetrics.top) / 2;
        int xIndex = -1;
        int yIndex;
        for (Map.Entry<String, String[]> line: date.entrySet()) {
            y = 0;
            xIndex++;
            yIndex = 0;
            paint.setColor(titleBgColor);
            drawBox(canvas, x, y, x + itemWidth, y + itemHeight, titleBgColor);
            canvas.drawText(line.getKey(), x + cX, y + cY, titlePaint);

            paint.setColor(contentBgColor);
            for (String text: line.getValue()) {
                if (text == null) {
                    text = "";
                }
                y += itemHeight;
                yIndex++;
                drawBox(canvas, x, y, x + itemWidth, y + itemHeight, contentBgColor);
                if (colorProvider != null) {
                    contentPaint.setColor(colorProvider.getColor(xIndex, yIndex));
                    canvas.drawText(text, x + cX, y + cY, contentPaint);
                } else {
                    canvas.drawText(text, x + cX, y + cY, contentPaint);
                }
            }
            x += itemWidth;
        }

        drawOutLine(canvas, 1, 1, getWidth() - divideLineSize, getHeight() - divideLineSize);
    }

    private void drawBox(Canvas canvas, float l, float t, float r, float b, int bgColor) {
        paint.setColor(bgColor);
        canvas.drawRect(l, t, r, b, paint);
        drawOutLine(canvas, l, t, r, b);
    }

    private void drawOutLine(Canvas canvas, float l, float t, float r, float b) {
        paint.setColor(divideLineColor);
        canvas.drawLine(l, t, r, t, paint);
        canvas.drawLine(l, t, l, b, paint);
        canvas.drawLine(r, t, r, b, paint);
        canvas.drawLine(l, b, r, b, paint);
    }
}
