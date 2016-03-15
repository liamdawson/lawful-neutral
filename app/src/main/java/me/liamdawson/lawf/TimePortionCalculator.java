package me.liamdawson.lawf;

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
        return getMinutes() / TimeUnit.HOURS.toMinutes(1);
    }

    private float getMinutes() {
        return mTime.get(GregorianCalendar.MINUTE) +
                getSecondPortion(false);
    }

    public float getSecondPortion() {
        return getSecondPortion(false);
    }

    public float getSecondPortion(boolean includeMillis) {
        return getSeconds(includeMillis) / TimeUnit.MINUTES.toSeconds(1);
    }

    private float getSeconds(boolean includeMillis) {
        float portion = mTime.get(GregorianCalendar.SECOND);
        if(includeMillis) portion += mTime.get(GregorianCalendar.MILLISECOND) / TimeUnit.SECONDS.toMillis(1);
        return portion;
    }

}
