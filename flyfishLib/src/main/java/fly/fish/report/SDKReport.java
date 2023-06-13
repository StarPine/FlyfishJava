package fly.fish.report;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import fly.fish.aidl.OutFace;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

public class SDKReport {

    //上报地址
    private static final String URL = "http://allapi.xinxinjoy.com:8084/outerinterface/track.php?";

    //事件ID
    private static final String TRACK_ID = "TrackId";
    //公共属性
    private static final String PUBLIC_PROPERTIES = "PublicProperties";
    //自定义属性
    private static final String TRACK_PROPERTIES = "TrackProperties";

    //第二层key
    private static final String KEY_TIME = "time";
    private static final String KEY_FLAT = "flat";
    private static final String KEY_PUB = "pub";
    private static final String KEY_IMEI = "imei";
    private static final String KEY_SDK_BV = "sdkbv";
    private static final String KEY_IP = "ip";
    private static final String KEY_OS = "os";
    private static final String KEY_UA = "ua";
    private static final String KEY_NET = "net";
    private static final String KEY_GAME_BV = "gamebv";

    public static final String KEY_GID = "gid";
    public static final String KEY_GAME_ID = "gameid";
    public static final String KEY_ACCOUNT_ID = "accountid";
    public static final String KEY_ROLE_ID = "roleid";
    public static final String KEY_ROLE_NAME = "rolename";
    public static final String KEY_SERVER_ID = "serverid";
    public static final String KEY_SERVER_NAME = "servername";
    public static final String KEY_ROLE_LEVEL = "rolelevel";
    public static final String KEY_VIP_LEVEL = "viplevel";

    //自定义key
    public static final String KEY_STR1 = "str1";
    public static final String KEY_STR2 = "str2";
    public static final String KEY_STR3 = "str3";
    public static final String KEY_INT1 = "int1";
    public static final String KEY_INT2 = "int2";
    public static final String KEY_INT3 = "int3";

    private static volatile SDKReport mInstance;

    private SDKReport() {
    }

    public static SDKReport getInstance() {
        if (mInstance == null) {
            synchronized (SDKReport.class) {
                if (mInstance == null) {
                    mInstance = new SDKReport();
                }
            }
        }
        return mInstance;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef(value = {Event.ENTER_THE_GAME,
            Event.START_APPLICATION,
            Event.START_GAME_HOT_REFRESH,
            Event.GAME_HOT_REFRESH_FINISH,
            Event.START_LOAD_LOCAL_RES,
            Event.LOAD_LOCAL_RES_FINISH,
            Event.INVOKE_SDK_INIT,
            Event.SDK_INIT_SUCCESS,
            Event.SDK_INIT_FAIL,
            Event.INVOKE_SDK_LOGIN,
            Event.SDK_LOGIN_SUCCESS,
            Event.SDK_LOGIN_FAIL,
            Event.GAME_SERVER_SELECTION_SHOW,
            Event.GAME_ANNOUNCEMENT_SHOW,
            Event.GAME_CREATE_ROLE_SHOW,
            Event.GAME_CREATE_ROLE_SUCCESS,
            Event.NOVICE_GUIDE,
            Event.SDK_PAY_SUCCESS,
            Event.SERVICE_GET_PAY_SUCCESS,
            Event.SERVICE_DISPOSE_PAY_FAIL,
            Event.PLAYER_CURRENCY_GENERATE,
            Event.PLAYER_CURRENCY_CONSUME,
            Event.PLAYER_PROPERTY_GENERATE,
            Event.PLAYER_PROPERTY_CONSUME,
            Event.PLAYER_ROEL_UPDATE,
            Event.PLAYER_VIP_UPDATE,
            Event.PLAYER_MAIN_LINE_TASK,
            Event.PLAYER_MAIN_LEVEL,
            Event.PLAYER_EXIT_GAME,
            Event.ROLE_RENAME,
            Event.SERVER_LAUNCH
    })
    private @interface EventID {
    }

    @Deprecated
    public void startCommonReport(Context context, @EventID int eventId) {
        startCommonReport(context, eventId, null);
    }

    public void startCommonReport(Context context, @EventID int eventId, Map<String, Object> commonMap) {
        startReport(context, eventId, commonMap, null);
    }

    @Deprecated
    public void startReportCustom(Context context, @EventID int eventId, Map<String, Object> customMap) {
        startReportCustom(context, eventId, null, customMap);
    }

    public void startReportCustom(Context context, @EventID int eventId, Map<String, Object> commonMap, Map<String, Object> customMap) {
        startReport(context, eventId, commonMap, customMap);
    }

    private void startReport(Context context, @EventID int eventId, Map<String, Object> commonMap, Map<String, Object> customMap) {
        String commonParams = createCommonParams(context, commonMap);
        String trackParams = map2JsonString(customMap);
        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put(TRACK_ID, eventId);
        bodyMap.put(PUBLIC_PROPERTIES, commonParams);
        if (!TextUtils.isEmpty(trackParams))
            bodyMap.put(TRACK_PROPERTIES, trackParams);
        String body = RequestUtils.createBody(bodyMap);
        request(body);
    }

    private void request(String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestConfig config = new RequestConfig(URL, body);
                    String result = RequestUtils.POST(config);
                    MLog.a("上报结果---------" + result);
                    MLog.a("RequestConfig---------" + config.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String map2JsonString(Map<String, Object> customParams) {
        if (customParams == null || customParams.size() <= 0)return "";
        JSONObject jsonObject = new JSONObject(customParams);
        return jsonObject.toString();
    }

    /**
     * 配置默认参数
     * @param context
     * @param map
     * @return
     */
    private String createCommonParams(Context context, Map<String, Object> map) {
        if (map == null) {
            map = new TreeMap<>();
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String publisher = OutFace.getInstance(context).getPublisher();
        if (TextUtils.isEmpty(publisher)) {
            publisher = FilesTool.getPublisherStringContent();
        }

        //包体默认参数
        map.put(KEY_TIME, dateFormat.format(date));
        map.put(KEY_FLAT, "Android");
        map.put(KEY_PUB, publisher);
        map.put(KEY_IMEI, PhoneTool.getIMEI(context));
        map.put(KEY_SDK_BV, "5.5.0");
        map.put(KEY_IP, PhoneTool.getIP(context));
        map.put(KEY_OS, "android" + PhoneTool.getOSVersion());
        map.put(KEY_UA, PhoneTool.getPT(context));
        map.put(KEY_NET, PhoneTool.getPhoneNet(context));
        map.put(KEY_GAME_BV, PhoneTool.getVersionName(context));

        //账号相关参数
        map.put(KEY_GAME_ID,  map.containsKey(KEY_GAME_ID) ? map.get(KEY_GAME_ID) : "");
        map.put(KEY_GID, map.containsKey(KEY_GID) ? map.get(KEY_GID) : "");
        map.put(KEY_ACCOUNT_ID, map.containsKey(KEY_ACCOUNT_ID) ? map.get(KEY_ACCOUNT_ID) : "");

        //游戏服务器相关参数
        map.put(KEY_ROLE_ID, map.containsKey(KEY_ROLE_ID) ? map.get(KEY_ROLE_ID) : "");
        map.put(KEY_ROLE_NAME, map.containsKey(KEY_ROLE_NAME) ? map.get(KEY_ROLE_NAME) : "");
        map.put(KEY_SERVER_ID, map.containsKey(KEY_SERVER_ID) ? map.get(KEY_SERVER_ID) : "");
        map.put(KEY_SERVER_NAME, map.containsKey(KEY_SERVER_NAME) ? map.get(KEY_SERVER_NAME) : "");
        map.put(KEY_ROLE_LEVEL, map.containsKey(KEY_ROLE_LEVEL) ? map.get(KEY_ROLE_LEVEL) : "");
        map.put(KEY_VIP_LEVEL, map.containsKey(KEY_VIP_LEVEL) ? map.get(KEY_VIP_LEVEL) : "");

        return map2JsonString(map);

    }

}
