package fly.fish.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class ManifestInfo {

    private static final String TAG = "ManifestInfo";

    public static String getMetaString(Context context, String key) {
        try {
            Object obj = getObj(context, key);
            return obj == null ? null : String.valueOf(obj);
        } catch (Exception e) {
            MLog.e(TAG, "getMetaString: error: "+ e);
        }
        return null;
    }

    public static int getMetaInt(Context context, String key, int defValue) {
        try {
            return Integer.parseInt(getObj(context, key).toString());
        } catch (Exception e) {
            MLog.e(TAG, "getMetaInt: error: "+ e);
        }
        return defValue;
    }

    public static long getMetaLong(Context context, String key, long defValue) {
        try {
            return Long.parseLong(getObj(context, key).toString());
        } catch (Exception e) {
            MLog.e(TAG, "getMetaLong: error: "+ e);
        }
        return defValue;
    }

    public static float getMetaFloat(Context context, String key, float defValue) {
        try {
            return Float.parseFloat(getObj(context, key).toString());
        } catch (Exception e) {
            MLog.e(TAG, "getMetaFloat: error: "+ e);
        }
        return defValue;
    }

    public static boolean getMetaBoolean(Context context, String key, boolean defValue) {
        try {
            return getMeta(context).getBoolean(key, defValue);
        } catch (Exception e) {
            MLog.e(TAG, "getMetaBoolean: error: "+ e);
        }
        return defValue;
    }

    public static Object getObj(Context context, String key) {
        try {
            return getMeta(context).get(key);
        } catch (Exception e) {
            MLog.e(TAG, "getObj: error: "+ e);
        }
        return null;
    }

    private static Bundle getMeta(Context context) throws Exception {
        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        return appInfo.metaData;
    }
}
