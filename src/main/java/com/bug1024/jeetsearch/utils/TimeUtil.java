package com.bug1024.jeetsearch.utils;

/**
 * time util
 * @author bug1024
 * @date 2020-06-26
 */
public class TimeUtil {
    private TimeUtil() {}

    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {

        }
    }
}
