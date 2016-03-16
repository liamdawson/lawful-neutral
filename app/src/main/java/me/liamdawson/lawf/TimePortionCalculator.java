package me.liamdawson.lawf;

import android.util.Log;

import java.io.Console;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Calculates portions of a given time unit from the given GregorianCalendar
 */
public class TimePortionCalculator {

    GregorianCalendar mTime;

    public TimePortionCalculator(GregorianCalendar time) {
        mTime = time;
    }

    public float getHourPortion() {
        return getHourPortion(false);
    }

    public float getHourPortion(boolean halfDay) {
        return getHours(halfDay) / (TimeUnit.DAYS.toHours(1) / (halfDay ? 2 : 1));
    }

    private float getHours(boolean halfDay) {
        return mTime.get(halfDay ? GregorianCalendar.HOUR : GregorianCalendar.HOUR_OF_DAY) +
                getMinutePortion();
    }

    public float getMinutePortion() {
        return getMinutePortion(false);
    }

    public float getMinutePortion(boolean wholeOnly) {
        return getMinutes(wholeOnly) / TimeUnit.HOURS.toMinutes(1);
    }

    private float getMinutes(boolean wholeOnly) {
        return mTime.get(GregorianCalendar.MINUTE) +
                (wholeOnly ? 0 : getSecondPortion());
    }

    public float getSecondPortion() {
        return getSeconds() / TimeUnit.MINUTES.toSeconds(1);
    }

    private float getSeconds() {
        float portion = mTime.get(GregorianCalendar.SECOND);
        portion += (mTime.get(GregorianCalendar.MILLISECOND) / (float)TimeUnit.SECONDS.toMillis(1));
        return portion;
    }

}
