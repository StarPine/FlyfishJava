package fly.fish.tools;

import android.util.Log;

public class MLog {
    private static final String TAG = "ASDk";
    private static boolean isLog = false;
    private static boolean debug = false;

    public static void enableLogger(boolean enable) {
        MLog.isLog = enable;
    }

    public static void setDebug(boolean debug) {
        MLog.debug = debug;
    }

    public static void d(String tag, String msg) {
        if (isaBoolean()) {
            Log.i(tag, msg);
        }
    }

    private static boolean isaBoolean() {
        return debug || isLog;
    }

    public static void i(String tag, String msg) {
        if (isaBoolean()) {
            Log.i(tag, msg);
        }
    }

    public static void a(String str) {
        if (isaBoolean()) {
            Log.i(TAG, str);
        }
    }

    public static void a(String tag, String str) {
        if (isaBoolean()) {
            Log.i(tag, str);
        }
    }

    public static void s(String str) {
        if (isaBoolean()) {
            Log.i(TAG, str);
        }
    }

    public static void e(String tag, String str) {
        Log.e(tag, str);
    }

    public static void b(Object str) {
        if (isaBoolean()) {
            System.out.println("NONONO " + str.toString());
        }
    }

    public static void err(Object str) {
        if (isaBoolean()) {
            System.err.println(str.toString());
        }
    }
}
