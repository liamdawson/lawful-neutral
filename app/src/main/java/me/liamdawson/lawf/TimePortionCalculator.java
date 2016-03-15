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
        return mTime.get(halfDay ? GregorianCalendar.HOUR : GregorianCalendar.HOUR_OF_DAY) +
                mTime.get(GregorianCalendar.MINUTE) / TimeUnit.HOURS.toMinutes(1);
    }

    public float getMinutePortion() {
        return mTime.get(GregorianCalendar.MINUTE) +
                mTime.get(GregorianCalendar.SECOND) / TimeUnit.MINUTES.toSeconds(1);
    }

    public float getSecondPortion() {
        return getSecondPortion(false);
    }

    public float getSecondPortion(boolean includeMillis) {
        float portion = mTime.get(GregorianCalendar.SECOND);
        if(includeMillis) portion += mTime.get(GregorianCalendar.MILLISECOND) / TimeUnit.SECONDS.toMillis(1);
        return portion;
    }

}
