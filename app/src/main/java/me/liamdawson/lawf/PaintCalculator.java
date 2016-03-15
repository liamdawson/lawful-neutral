package me.liamdawson.lawf;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Handles the paints for watch components
 */
public class PaintCalculator {
    // Colour scheme: https://color.adobe.com/warm-color-theme-7755835/?showPublished=true

    private boolean mAmbient;
    private boolean mLowBitAmbient;

    public PaintCalculator(boolean isAmbient, boolean lowBitAmbient) {
        mAmbient = isAmbient;
        mLowBitAmbient = lowBitAmbient;
    }

    public Paint getHourHandPaint() {
        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(6);
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setColor(mAmbient ? Color.WHITE : Color.argb(255, 241, 180, 90));

        return p;
    }

    public Paint getMinuteHandPaint() {
        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(6);
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setColor(mAmbient ? Color.WHITE : Color.argb(255, 243, 135, 39));

        return p;
    }

    public Paint getSecondHandPaint() {
        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(6);
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setColor(mAmbient ? Color.WHITE : Color.argb(255, 215, 79, 21));

        return p;
    }

    private int getBackgroundColorR() {
        return mAmbient ? 0 : 115;
    }

    private int getBackgroundColorG() {
        return mAmbient ? 0 : 42;
    }

    private int getBackgroundColorB() {
        return mAmbient ? 0 : 27;
    }

    private int getTextColorR() {
        return mAmbient ? 255 : 217;
    }

    private int getTextColorG() {
        return mAmbient ? 255 : 174;
    }

    private int getTextColorB() {
        return mAmbient ? 255 : 138;
    }

    public int getBackgroundColor() {
        return Color.argb(255, getBackgroundColorR(), getBackgroundColorG(), getBackgroundColorB());
    }

    public Paint getBackgroundPaint() {
        Paint p = new Paint();

        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setColor(getBackgroundColor());

        return p;
    }

    public Paint getTranslucentBackgroundPaint() {
        Paint p = new Paint();

        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setColor(Color.argb(224, getBackgroundColorR(), getBackgroundColorG(), getBackgroundColorB()));

        return p;
    }

    public Paint getMinorTickPaint() {
        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);
        p.setColor(getBackgroundColor());
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setStrokeWidth(1);

        return p;
    }

    public Paint getMajorTickPaint() {
        Paint p = getMinorTickPaint();
        p.setStrokeWidth(2);

        return p;
    }

    public Paint getTextPaint() {
        Paint p = new Paint();
        p.setColor(Color.argb(255, getTextColorR(), getTextColorG(), getTextColorB()));
        p.setAntiAlias(!(mLowBitAmbient && mAmbient));
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(24);

        return p;
    }

    public Paint getSecondaryTextPaint() {
        Paint p = getTextPaint();
        p.setTextSize(16);
        p.setColor(Color.argb(mAmbient ? 255 : 128, getTextColorR(), getTextColorG(), getTextColorB()));

        return p;
    }

}
