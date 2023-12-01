package fly.fish.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import fly.fish.dialog.DialgTool;

/**
 * 从8.0.0开始移除
 */
@Deprecated
public class OthPhone {


    private static Activity activity;


    private static String gamekey = "";
    public static boolean isrequ = false;

    public static void setisreq(boolean isreq) {
        isrequ = isreq;
    }

    public static void Initgetnum(Activity act) {
        activity = act;
        if (isrequ) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }


    }

    public static void setGamekey(String gamekey1) {
        gamekey = gamekey1;
    }

    public static String getIMEI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();
            //android 10以上已经获取不了imei了 用 android id代替
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = Settings.System.getString(
                        activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            }

            return deviceId;
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis() + "a1s2";
        }
    }


    public static String ForAsdkGetnum() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("gameid", gamekey);
            jsonObject.put("flat", "Android");
            jsonObject.put("pub", DialgTool.getpub("AsdkPublisher.txt"));
            jsonObject.put("gid", getIMEI());
            jsonObject.put("sdkbv", "5.4.9");
//            jsonObject.put ("os",android.os.Build.MANUFACTURER);
            jsonObject.put("os", "Android" + android.os.Build.VERSION.RELEASE);

            jsonObject.put("ua", android.os.Build.MODEL);
            jsonObject.put("net", getNetworkOperatorName());


            System.out.println("othphone" + jsonObject.toString());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }

    public static String getNetworkOperatorName() {
        String opeType = "unknown";
        // No sim
        if (!hasSim(activity.getApplication().getApplicationContext())) {
            return opeType;
        }

        TelephonyManager tm = (TelephonyManager) activity.getApplication().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = "中国联通";
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = "中国移动";

        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = "中国电信";
        } else {
            opeType = "unknown";
        }
        return opeType;
    }

    private static boolean hasSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            return false;
        }
        return true;

    }

    /*public static boolean isMobileDataEnabled(Context context) {
        try {
            Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return (Boolean) method.invoke(connectivityManager);
        } catch (Throwable t) {
            return false;
        }
    }*/


}
