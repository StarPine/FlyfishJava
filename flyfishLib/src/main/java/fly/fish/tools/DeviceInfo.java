package fly.fish.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

import fly.fish.asdk.MyApplication;

public class DeviceInfo {
    public static final String TAG = "DeviceInfo";

    public static String getImei(Context context) {
        String imei = "";
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = mTelephonyMgr.getDeviceId();
        } catch (Exception e) {
            Log.i(TAG, "IMEI获取失败");
        }
        return imei;
    }

    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }

    public static String getOAID() {
        return PhoneTool.getOAID();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String deviceId = uuid.toString().replace("-", "");
        return MD5Util.getMD5String(deviceId);
    }

    public static String getDiyDeviceId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", 0);
        String diyDeviceid = sharedPreferences.getString("diy_deviceid", "");
        if (diyDeviceid.equals("")){
            diyDeviceid = System.currentTimeMillis() + PhoneTool.getRandomCode();
            sharedPreferences.edit().putString("diy_deviceid", diyDeviceid).apply();
        }
        return MD5Util.getMD5String(diyDeviceid);
    }



}
