package com.example.smartattendence.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic {

    private int mRectcolor = Color.GREEN;
    private float mStrokeWidth = 4.0f;
    private Paint mRectPaint;
    private GraphicOverlay graphicOverlay;
    private Rect rect;

    public RectOverlay(GraphicOverlay graphicOverlay, Rect rect) {
        super(graphicOverlay);

        mRectPaint = new Paint();
        mRectPaint.setColor(mRectcolor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(mStrokeWidth);

        this.graphicOverlay = graphicOverlay;
        this.rect = rect;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {

        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.top = translateX(rectF.top);
        rectF.bottom = translateX(rectF.bottom);

        canvas.drawRect(rectF,mRectPaint);
    }
}
