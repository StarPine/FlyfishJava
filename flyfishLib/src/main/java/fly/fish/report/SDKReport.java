package fly.fish.report;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import fly.fish.aidl.OutFace;
import fly.fish.tools.PhoneTool;

public class SDKReport {
    //第一层key
    //事件ID
    private static final String TRACK_ID = "TrackId";
    //公共属性
    private static final String PUBLIC_PROPERTIES = "PublicProperties";
    //自定义属性
    private static final String TRACK_PROPERTIES = "TrackProperties";

    //第二层key
    private static final String KEY_TIME = "time";
    private static final String KEY_GAME_ID = "gameid";
    private static final String KEY_ACCOUNT_ID = "accountid";
    private static final String KEY_FLAT = "flat";
    private static final String KEY_PUB = "pub";
    private static final String KEY_GID = "gid";
    private static final String KEY_SDK_BV = "sdkbv";
    private static final String KEY_IP = "ip";
    private static final String KEY_OS = "os";
    private static final String KEY_UA = "ua";
    private static final String KEY_NET = "net";
    private static final String KEY_GAME_BV = "gamebv";
    private static final String KEY_ROLE_ID = "roleid";
    private static final String KEY_SERVER_ID = "serverid";
    private static final String KEY_SERVER_NAME = "servername";
    private static final String KEY_ROLE_LEVEL = "rolelevel";
    private static final String KEY_VIP_LEVEL = "viplevel";

    //自定义key
    private static final String KEY_STR1 = "str1";
    private static final String KEY_STR2 = "str2";
    private static final String KEY_STR3 = "str3";
    private static final String KEY_INT1 = "int1";
    private static final String KEY_INT2 = "int2";
    private static final String KEY_INT3 = "int3";

    public static Map<String, String> getPamars(Context context) throws JSONException {
        return applicationStart(context);
    }

    public static Map<String, String> applicationStart(Context context) throws JSONException {
        String baseParams = createBaseParams(context);
        String trackParams = createTrackParams(context);
        Map<String, String> bodyParams = new TreeMap<>();
        bodyParams.put(TRACK_ID, "1000000");
        bodyParams.put(PUBLIC_PROPERTIES, baseParams);
        bodyParams.put(TRACK_PROPERTIES, trackParams);
        return bodyParams;
    }

    private static String createTrackParams(Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_STR1,"9850c7ada261cc073af8d802bdf03c93");
        jsonObject.put(KEY_STR2,"");
        jsonObject.put(KEY_STR3,"");
        jsonObject.put(KEY_INT1,"6");
        jsonObject.put(KEY_INT2,"");
        jsonObject.put(KEY_INT3,"");
        return jsonObject.toString();
    }

    private static String createBaseParams(Context context) throws JSONException {
        return createBaseParams(context, null);
    }

    private static String createBaseParams(Context context, Map<String, String> map) throws JSONException {
        if (map == null) {
            map = new TreeMap<>();
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject jsonObject = new JSONObject();
        //包体默认参数
        jsonObject.put(KEY_TIME, dateFormat.format(date));
        jsonObject.put(KEY_FLAT, "Android");
        jsonObject.put(KEY_PUB, OutFace.getInstance(context).getPublisher());
        jsonObject.put(KEY_GID, PhoneTool.getIMEI(context));
        jsonObject.put(KEY_SDK_BV, "5.4.7");
        jsonObject.put(KEY_IP, PhoneTool.getIP(context));
        jsonObject.put(KEY_OS, "android" + PhoneTool.getOSVersion());
        jsonObject.put(KEY_UA, PhoneTool.getPT(context));
        jsonObject.put(KEY_NET, PhoneTool.getPhoneNet(context));
        jsonObject.put(KEY_GAME_BV, PhoneTool.getVersionName(context));

        //账号相关参数
        jsonObject.put(KEY_GAME_ID, "100973");
        jsonObject.put(KEY_ACCOUNT_ID, "");

        //游戏服务器相关参数
        jsonObject.put(KEY_ROLE_ID, "");
        jsonObject.put(KEY_SERVER_ID, "");
        jsonObject.put(KEY_SERVER_NAME, "");
        jsonObject.put(KEY_ROLE_LEVEL, "");
        jsonObject.put(KEY_VIP_LEVEL, "");

        return jsonObject.toString();

    }

}
