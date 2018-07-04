package com.shrinktool.Analyzer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import com.shrinktool.Analyzer.CodeFormView.RowInfo;
import com.shrinktool.Analyzer.CodeFormView.UnitInfo;
import com.shrinktool.Analyzer.CodeFormView.UnitTextDrawer;

/**
 * 用于显示“开奖号码”时，部分高亮，部分淡化: 开奖号码是5个，玩法用后3个
 * Created by Alashi on 2016/9/9.
 */
public class UnitTextSplitDrawer implements UnitTextDrawer {
    private Paint textPaint;
    protected int colorDesalt = Color.parseColor("#D4D4D4");//浅灰色字体

    public UnitTextSplitDrawer() {
        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas canvas, RowInfo rowInfo, UnitInfo info, int unitOffset) {
        textPaint.setTextSize(info.textSize);
        int uW = rowInfo.unitW;
        int uH = rowInfo.unitH;
        float x = unitOffset + info.x * uW + (info.w * uW - textPaint.measureText(info.text)) / 2;
        float y = info.y * uH + info.h * uH * 0.5f
                + textPaint.getTextSize() * 0.4f;

        String desalt = info.text.substring(0, 2);
        textPaint.setColor(colorDesalt);
        canvas.drawText(desalt, x, y, textPaint);

        String general = info.text.substring(2);
        textPaint.setColor(info.textColor);
        canvas.drawText(general, x + textPaint.measureText(desalt), y, textPaint);
    }
}
