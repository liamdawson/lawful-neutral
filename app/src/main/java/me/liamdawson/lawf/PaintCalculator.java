package me.liamdawson.lawf;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Handles the paints for watch components
 */
public class PaintCalculator {
    // Colour scheme adapted from:
    // https://color.adobe.com/Midnight-Blue-Monochromatic-color-theme-6782714/

    private boolean ambientMode;
    private boolean lowBitAmbientMode;
    private boolean shouldAntiAlias;

    int hourHandColor,
            minuteHandColor,
            secondHandColor,
            backgroundColor,
            demarcationColor,
            textColor;

    Paint hourHandMain,
            minuteHandMain,
            secondHandMain,
            hourHandEcho,
            minuteHandEcho,
            secondHandEcho,
            textMain,
            textSecondary,
            demarcationMain,
            demarcationSecondary,
            background;

    // Prepare everything beforehand, so paints are only calculated when constructed
    public PaintCalculator(boolean isAmbient, boolean lowBitAmbient) {
        ambientMode = isAmbient;
        lowBitAmbientMode = lowBitAmbient;
        shouldAntiAlias = !(lowBitAmbientMode && ambientMode);

        hourHandColor = ambientMode ? Color.WHITE : Color.rgb(0, 74, 144);
        minuteHandColor = ambientMode ? Color.WHITE : Color.rgb(0, 97, 191);
        secondHandColor = Color.rgb(0, 117, 229);
        backgroundColor = ambientMode ? Color.BLACK : Color.rgb(0, 33, 64);
        demarcationColor = backgroundColor;
        textColor = ambientMode ? Color.WHITE : Color.rgb(0, 130, 255);

        hourHandMain = arcPaint(hourHandColor, 255, 8);
        minuteHandMain = arcPaint(minuteHandColor, 255, 8);
        secondHandMain = arcPaint(secondHandColor, 255, 4);

        hourHandEcho = arcPaint(hourHandColor, 64, 8);
        minuteHandEcho = arcPaint(minuteHandColor, 64, 8);
        secondHandEcho = arcPaint(secondHandColor, 64, 4);

        textMain = textPaint(textColor, 255, 24);
        textSecondary = textPaint(textColor, 192, 16);

        demarcationMain = arcPaint(demarcationColor, 255, 3);
        demarcationSecondary = arcPaint(demarcationColor, 128, 1);

        background = backgroundPaint(backgroundColor);
    }

    private Paint arcPaint(int color, int alpha, int thickness) {
        Paint p = new Paint();
        p.setColor(color);
        p.setAlpha(alpha);
        p.setStrokeWidth(thickness);
        p.setAntiAlias(shouldAntiAlias);
        p.setStyle(Paint.Style.STROKE);

        return p;
    }

    private Paint textPaint(int color, int alpha, int size) {
        Paint p = new Paint();
        p.setColor(color);
        p.setAlpha(ambientMode ? 255 : alpha);
        p.setAntiAlias(shouldAntiAlias);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(size);

        return p;
    }

    private Paint backgroundPaint(int color) {
        Paint p = new Paint();
        p.setColor(color);
        p.setAlpha(255);
        p.setStyle(Paint.Style.FILL);

        return p;
    }

    public Paint getHourHandPaint() {
        return hourHandMain;
    }

    public Paint getMinuteHandPaint() {
        return minuteHandMain;
    }

    public Paint getSecondHandPaint() {
        return secondHandMain;
    }

    public Paint getHourEchoPaint() {
        return hourHandEcho;
    }

    public Paint getMinuteEchoPaint() {
        return minuteHandEcho;
    }

    public Paint getSecondEchoPaint() {
        return secondHandEcho;
    }

    public Paint getBackgroundPaint() {
        return background;
    }

    public Paint getMinorTickPaint() {
        return demarcationSecondary;
    }

    public Paint getMajorTickPaint() {
        return demarcationMain;
    }

    public Paint getTextPaint() {
        return textMain;
    }

    public Paint getSecondaryTextPaint() {
        return textSecondary;
    }

}
