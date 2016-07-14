package com.jlcsoftware.helpers;

import android.util.Log;

import com.jlcsoftware.sampleapp.BuildConfig;

/**
 * Created by Jeff on 09-Jul-16.
 * <p/>
 * A simple Logger placeholder
 * <p/>
 * In "real" production code, this class could do interesting things or be removed via ProGuard
 * We could send logs to Crashlytics (http://www.crashlytics.com), our server, email them or other exciting things!!!
 *
 * the TAG is an interesting policy issue... Do we use a Static or use the Class name (which changes under ProGuard)
 */
public class MyLogger {
    public static final int DEBUG = Log.DEBUG;
    public static final int ERROR = Log.ERROR;
    public static final int INFO = Log.INFO;
    public static final int VERBOSE = Log.VERBOSE;
    public static final int WARN = Log.WARN;
    public static final int WTF = 0xdead;

    private static void initialize() {
        // TODO: Do stuff to initialize
    }

    /**
     * Prints the Log Data provided
     *
     * @param level     Log level of the data being logged. Verbose, Error, etc.
     * @param tag       Tag for for the log data. Can be used to organize log statements.
     * @param msg       The actual message to be logged. The actual message to be logged.
     * @param throwable If an exception was thrown, this can be sent along for the logging facilities
     *                  to extract and print useful information.
     */

    public static void Log(int level, String tag, String msg, Throwable throwable) {

        if (BuildConfig.DEBUG) { // Android says that deployed application should not contain logging code.
            initialize();
            switch (level) {
                case DEBUG:
                    Log.d(tag, msg, throwable); // throwable == null returns ""
                    return;
                case ERROR:
                    Log.e(tag, msg, throwable);
                    return;
                case INFO:
                    Log.i(tag, msg, throwable);
                    return;
                case VERBOSE:
                    Log.v(tag, msg, throwable);
                    return;
                case WARN:
                    Log.w(tag, msg, throwable);
                    return;
                case WTF:
                    Log.wtf(tag, msg, throwable);
                    return;
            }
        }
    }

    public static void Log(int level, String tag, String msg) {
        Log(level, tag, msg, null);
    }


    /**
     * Prints a message at VERBOSE priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void v(String tag, String msg, Throwable tr) {
        Log(VERBOSE, tag, msg, tr);
    }

    /**
     * Prints a message at VERBOSE priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     */
    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }


    /**
     * Prints a message at DEBUG priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void d(String tag, String msg, Throwable tr) {
        Log(DEBUG, tag, msg, tr);
    }

    /**
     * Prints a message at DEBUG priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     */
    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    /**
     * Prints a message at INFO priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void i(String tag, String msg, Throwable tr) {
        Log(INFO, tag, msg, tr);
    }

    /**
     * Prints a message at INFO priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     */
    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    /**
     * Prints a message at WARN priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void w(String tag, String msg, Throwable tr) {
        Log(WARN, tag, msg, tr);
    }

    /**
     * Prints a message at WARN priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     */
    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    /**
     * Prints a message at WARN priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void w(String tag, Throwable tr) {
        w(tag, null, tr);
    }

    /**
     * Prints a message at ERROR priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void e(String tag, String msg, Throwable tr) {
        Log(ERROR, tag, msg, tr);
    }

    /**
     * Prints a message at ERROR priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     */
    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    /**
     * Prints a message at WTF priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void wtf(String tag, String msg, Throwable tr) {
        Log(WTF, tag, msg, tr);
    }

    /**
     * Prints a message at ASSERT priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param msg The actual message to be logged.
     */
    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    /**
     * Prints a message at WTF priority.
     *
     * @param tag Tag for for the log data. Can be used to organize log statements.
     * @param tr  If an exception was thrown, this can be sent along for the logging facilities
     *            to extract and print useful information.
     */
    public static void wtf(String tag, Throwable tr) {
        wtf(tag, null, tr);
    }

}
