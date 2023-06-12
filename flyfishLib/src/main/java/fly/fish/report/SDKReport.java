package fly.fish.report;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import fly.fish.aidl.OutFace;
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

    public static class Event{

        //启动游戏
        public static final int START_APPLICATION = 1000000;
        //游戏热更新开始
        public static final int START_GAME_HOT_REFRESH = 1001000;
        //游戏热更新成功
        public static final int GAME_HOT_REFRESH_FINISH = 1002000;
        //游戏加载本地资源开始
        public static final int START_LOAD_LOCAL_RES = 1003000;
        //游戏加载本地资源成功
        public static final int LOAD_LOCAL_RES_FINISH = 1004000;
        //游戏调用SDK初始化
        public static final int INVOKE_SDK_INIT = 1005000;
        //SDK初始化成功
        public static final int SDK_INIT_SUCCESS = 1006000;
        //SDK初始化失败
        public static final int SDK_INIT_FAIL = 1007000;
        //游戏调用SDK登陆页
        public static final int INVOKE_SDK_LOGIN = 1008000;
        //SDK返回登陆成功
        public static final int SDK_LOGIN_SUCCESS = 1009000;
        //SDK返回登陆失败
        public static final int SDK_LOGIN_FAIL = 1010000;
        //游戏选服界面弹出
        public static final int GAME_SERVER_SELECTION_SHOW = 1011000;
        //游戏公告界面弹出
        public static final int GAME_ANNOUNCEMENT_SHOW = 1012000;
        //玩家创角界面弹出
        public static final int GAME_CREATE_ROLE_SHOW = 1013000;
        //玩家创建角色成功
        public static final int GAME_CREATE_ROLE_SUCCESS = 1014000;
        //正式进入游戏
        public static final int ENTER_THE_GAME = 1015000;
    }

    public static Map<String, Object> startReport(Context context) throws JSONException {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String body = null;
                try {
                    String commonParams = createCommonParams(context);
                    String trackParams = createTrackParams(context);

                    Map<String, Object> bodyParams = new TreeMap<>();
                    bodyParams.put(TRACK_ID, Event.START_APPLICATION);
                    bodyParams.put(PUBLIC_PROPERTIES, commonParams);
                    bodyParams.put(TRACK_PROPERTIES, trackParams);
                    body = RequestUtils.createBody(bodyParams);
                    RequestConfig config = new RequestConfig(URL,body);
                    String result = RequestUtils.POST(config);
                    MLog.a("上报结果---------" + result);
                    MLog.a("RequestConfig---------" + config.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return null;
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

    private static String createCommonParams(Context context) throws JSONException {
        return createCommonParams(context, null);
    }

    private static String createCommonParams(Context context, Map<String, String> map) throws JSONException {
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
