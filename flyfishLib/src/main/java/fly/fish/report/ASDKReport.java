package fly.fish.report;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fly.fish.aidl.OutFace;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

public class ASDKReport {

    //上报地址
    private static final String URL = "http://allapi.xinxinjoy.com:8084/outerinterface/track.php?";

    private static final String TRACK_ID = "TrackId";                   //事件ID
    private static final String PUBLIC_PROPERTIES = "PublicProperties"; //公共属性
    private static final String TRACK_PROPERTIES = "TrackProperties";   //自定义属性
    private static final String TAG = "ASDKReport";

    private static String gameId = "";
    private static String gid = "";
    private static String accountId = "";

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

    private static volatile ASDKReport mInstance;

    private ASDKReport() {
    }

    public static ASDKReport getInstance() {
        if (mInstance == null) {
            synchronized (ASDKReport.class) {
                if (mInstance == null) {
                    mInstance = new ASDKReport();
                }
            }
        }
        return mInstance;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef(value = {EventManager.EVENT_ENTER_THE_GAME,
            EventManager.EVENT_START_APPLICATION,
            EventManager.EVENT_START_GAME_HOT_REFRESH,
            EventManager.EVENT_GAME_HOT_REFRESH_FINISH,
            EventManager.EVENT_START_LOAD_LOCAL_RES,
            EventManager.EVENT_LOAD_LOCAL_RES_FINISH,
            EventManager.EVENT_INVOKE_SDK_INIT,
            EventManager.EVENT_SDK_INIT_SUCCESS,
            EventManager.EVENT_SDK_INIT_FAIL,
            EventManager.EVENT_INVOKE_SDK_LOGIN,
            EventManager.EVENT_SDK_LOGIN_SUCCESS,
            EventManager.EVENT_SDK_LOGIN_FAIL,
            EventManager.EVENT_GAME_SERVER_SELECTION_SHOW,
            EventManager.EVENT_GAME_ANNOUNCEMENT_SHOW,
            EventManager.EVENT_GAME_CREATE_ROLE_SHOW,
            EventManager.EVENT_GAME_CREATE_ROLE_SUCCESS,
            EventManager.EVENT_NOVICE_GUIDE,
            EventManager.EVENT_SDK_PAY_SUCCESS,
            EventManager.EVENT_SERVICE_GET_PAY_SUCCESS,
            EventManager.EVENT_SERVICE_DISPOSE_PAY_FAIL,
            EventManager.EVENT_PLAYER_CURRENCY_GENERATE,
            EventManager.EVENT_PLAYER_CURRENCY_CONSUME,
            EventManager.EVENT_PLAYER_PROPERTY_GENERATE,
            EventManager.EVENT_PLAYER_PROPERTY_CONSUME,
            EventManager.EVENT_PLAYER_ROEL_UPDATE,
            EventManager.EVENT_PLAYER_VIP_UPDATE,
            EventManager.EVENT_PLAYER_MAIN_LINE_TASK,
            EventManager.EVENT_PLAYER_MAIN_LEVEL,
            EventManager.EVENT_PLAYER_EXIT_GAME,
            EventManager.EVENT_ROLE_RENAME,
            EventManager.EVENT_SERVER_LAUNCH
    })
    private @interface EventID {
    }

    @Deprecated
    public void startReportCommon(Context context, @EventID int eventId) {
        startReportCommon(context, eventId, null);
    }

    public void startReportCommon(Context context, @EventID int eventId, Map<String, Object> commonMap) {
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
        MLog.a(TAG, "上报事件ID：" + eventId);
        String commonParams = createCommonParams(context, commonMap);
        String trackParams = map2JsonString(customMap);
        Map<String, Object> bodyMap = new HashMap<>();
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
                    MLog.a(TAG, "上报结果---------" + result);
                    MLog.a(TAG, "RequestConfig---------" + config.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String map2JsonString(Map<String, Object> customParams) {
        if (customParams == null || customParams.size() <= 0) return "";
        JSONObject jsonObject = new JSONObject(customParams);
        return jsonObject.toString();
    }

    /**
     * 配置默认参数
     *
     * @param context
     * @param map
     * @return
     */
    private String createCommonParams(Context context, Map<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String publisher = OutFace.getInstance(context).getPublisher();
        if (TextUtils.isEmpty(publisher)) {
            publisher = FilesTool.getPublisherStringContent();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("asdk", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        //包体默认参数
        map.put(KEY_TIME, dateFormat.format(date));
        map.put(KEY_FLAT, "Android");
        map.put(KEY_PUB, publisher);
        map.put(KEY_IMEI, isFirstRun ? PhoneTool.getRandomCode() : PhoneTool.getIMEI(context));
        map.put(KEY_SDK_BV, "5.5.0");
        map.put(KEY_IP, PhoneTool.getIP(context));
        map.put(KEY_OS, "android" + PhoneTool.getOSVersion());
        map.put(KEY_UA, PhoneTool.getPT(context));
        map.put(KEY_NET, PhoneTool.getNetworkOperatorName(context));
        map.put(KEY_GAME_BV, PhoneTool.getVersionName(context));

        //账号相关参数
        if (map.containsKey(KEY_GAME_ID) && !TextUtils.isEmpty((String) map.get(KEY_GAME_ID))) {
            gameId = (String) map.get(KEY_GAME_ID);
        }
        map.put(KEY_GAME_ID, gameId);

        if (map.containsKey(KEY_GID) && !TextUtils.isEmpty((String) map.get(KEY_GID))) {
            gid = (String) map.get(KEY_GID);
        }
        map.put(KEY_GID, gid);

        if (map.containsKey(KEY_ACCOUNT_ID) && !TextUtils.isEmpty((String) map.get(KEY_ACCOUNT_ID))) {
            accountId = (String) map.get(KEY_ACCOUNT_ID);
        }
        map.put(KEY_ACCOUNT_ID, accountId);

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
