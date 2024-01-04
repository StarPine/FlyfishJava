package fly.fish.tools;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import java.io.File;

import fly.fish.config.Configs;

public class MLog {
    private static final String TAG = "ASDk";
    private static boolean isLog = false;
    private static boolean debug = false;

    public static void enableLogger(boolean enable) {
        Log.i(TAG, "enableLogger: "+enable);
        MLog.isLogger = enable;
    }

    public static void setDebug(boolean debug) {
        MLog.debug = debug;
    }

    public static void d(String tag, String msg) {
        if (isEnable()) {
            Log.i(tag, msg);
        }
    }

    private static boolean isEnable() {
        return debug || isLogger ;
    }

    public static void i(String msg) {
        if (isEnable()) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isEnable()) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (isEnable()) {
            Log.i(tag, String.format(format, args));
        }
    }

    public static void a(String str) {
        if (isEnable()) {
            Log.i(TAG, str);
        }
    }

    public static void a(String tag, String str) {
        if (isEnable()) {
            Log.i(tag, str);
        }
    }

    public static void s(String str) {
        if (isEnable()) {
            Log.i(TAG, str);
        }
    }

    public static void e(String tag, String str) {
        Log.e(tag, str);
    }

    public static void b(Object str) {
        if (isEnable()) {
            System.out.println("NONONO " + str.toString());
        }
    }

    public static void err(Object str) {
        if (isEnable()) {
            System.err.println(str.toString());
        }
    }

    public static void setLogModel(Context ctx) {
        if (FilesTool.isPermissionGranted(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            setDebug(enable(ctx));
        } else {
            setDebug(false);
        }
    }

    private static boolean enable(Context ctx) {
        String LogFilesDir = "/ASdkLog";
        File writeFlagFile = new File(FilesTool.getStorageDir(ctx), LogFilesDir);
        if (!writeFlagFile.exists() || !writeFlagFile.isDirectory()) {
            return false;
        }
        File[] files = writeFlagFile.listFiles();
        if (files != null && files.length > 0) {
            try {
                return MD5Util.getMD5String(files[0].getName().substring(0, files[0].getName().length() - 4)).equals("490fadf94938a334b6823235fefb5d6c");
            } catch (Exception e) {

            }
        }
        return false;
    }
}
