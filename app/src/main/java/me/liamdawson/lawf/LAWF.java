/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.liamdawson.lawf;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't shown. On
 * devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient mode.
 */
public class LAWF extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<LAWF.Engine> mWeakReference;

        public EngineHandler(LAWF.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            LAWF.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);

        boolean ambientMode;
        boolean lowBitAmbient;
        GregorianCalendar time;
        PaintCalculator paintCalculator;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(LAWF.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            paintCalculator = new PaintCalculator(ambientMode, lowBitAmbient);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);

            paintCalculator = new PaintCalculator(ambientMode, lowBitAmbient);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (ambientMode != inAmbientMode) {
                ambientMode = inAmbientMode;
                paintCalculator = new PaintCalculator(ambientMode, lowBitAmbient);
                invalidate();
            }

            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            // Relatively constant, but used repeatedly
            float centerX = bounds.width() / 2f;
            float centerY = bounds.height() / 2f;
            float radius = Math.min(centerX, centerY);

            // Time elements
            time = new GregorianCalendar();
            Date timeForString = time.getTime();

            TimePortionCalculator timePortionCalculator = new TimePortionCalculator(time);

            // Bounding boxes for time arcs
            RectF hourBounds = new RectF(centerX - 0.75f*radius, centerY - 0.75f*radius, centerX + 0.75f*radius, centerY + 0.75f*radius);
            RectF minuteBounds = new RectF(centerX - 0.65f*radius, centerY - 0.65f*radius, centerX + 0.65f*radius, centerY + 0.65f*radius);
            RectF secondBounds = new RectF(centerX - 0.55f*radius, centerY - 0.55f*radius, centerX + 0.55f*radius, centerY + 0.55f*radius);

            // Show the 'reflected' arcs beneath if not in ambient mode
            float arcExtent = ambientMode ? 180 : 360;

            // Start drawing

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paintCalculator.getBackgroundPaint());

            // Draw 'echo' arcs first
            canvas.drawArc(hourBounds, 180, arcExtent, false, paintCalculator.getHourEchoPaint());
            canvas.drawArc(minuteBounds, 180, arcExtent, false, paintCalculator.getMinuteEchoPaint());
            if(!ambientMode) {
                canvas.drawArc(secondBounds, 180, arcExtent, false, paintCalculator.getSecondEchoPaint());
            }

            // Draw brighter arcs to indicate portion of time passed

            canvas.drawArc(hourBounds, 180, 180 * timePortionCalculator.getHourPortion(), false, paintCalculator.getHourHandPaint());
            canvas.drawArc(minuteBounds, 180, 180 * timePortionCalculator.getMinutePortion(), false, paintCalculator.getMinuteHandPaint());
            if(!ambientMode) {
                canvas.drawArc(secondBounds, 180, 180 * timePortionCalculator.getSecondPortion(), false, paintCalculator.getSecondHandPaint());
            }

            // Draw the lines to segment the second and minute arcs
            for(int i = 0; i <= 60; i++) {

                float drawingDegrees = (3 * i + 270) % 360; // Angle for trigonometric functions
                float drawingRadians = (drawingDegrees / 360) * (2*(float)Math.PI);

                // Use 70% of the radius, otherwise segments the hour arc into 24m blocks
                float xPos = (float)Math.sin(drawingRadians) * 0.7f*radius;
                float yPos = (float)Math.cos(drawingRadians) * 0.7f*radius;

                canvas.drawLine(centerX, centerY, centerX + xPos, centerY - yPos, (i % 5 == 0) ? paintCalculator.getMajorTickPaint() : paintCalculator.getMinorTickPaint());
            }

            // Draw the lines to segment the hour arc into half-hours
            for(int i = 0; i <= 48; i++) {

                float drawingDegrees = (3.75f * i + 90) % 360; // Angle for trigonometric functions
                float drawingRadians = (drawingDegrees / 360) * (2*(float)Math.PI);

                // Calculate the points of the line:
                // use 70% -> 80% of the radius to bisect the hour arc each time
                float startingX = (float)Math.sin(drawingRadians) * 0.7f*radius;
                float endingX = (float)Math.sin(drawingRadians) * 0.8f*radius;
                float startingY = (float)Math.cos(drawingRadians) * 0.7f*radius;
                float endingY = (float)Math.cos(drawingRadians) * 0.8f*radius;

                canvas.drawLine(centerX + startingX, centerY + startingY, centerX + endingX, centerY + endingY, (i % 2 == 0) ? paintCalculator.getMajorTickPaint() : paintCalculator.getMinorTickPaint());
            }

            canvas.drawText(DateFormat.getTimeInstance(DateFormat.SHORT).format(timeForString),
                    centerX,
                    centerY - 22,
                    paintCalculator.getTextPaint()
            );

            canvas.drawText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(timeForString),
                    centerX,
                    centerY,
                    paintCalculator.getSecondaryTextPaint()
            );
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            updateTimer();
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}
