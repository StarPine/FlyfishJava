package fly.fish.asdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import fly.fish.aidl.CallBackListener;
import fly.fish.aidl.MyRemoteService;
import fly.fish.dialog.CloseAccountCallBack;
import fly.fish.othersdk.AImageCapturer;
import fly.fish.othersdk.ExitCallBack;
//import fly.fish.othersdk.ForADresult;
import fly.fish.othersdk.GetCertificationInfoCallback;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;

public class SkipActivity extends Activity {
    private static Class<?> clazz = null;
    private static String Publisher = FilesTool.getPublisherStringContent()
            .split("_")[0];
    private static HashMap<String, String> map = new HashMap<String, String>();
    private static String gu_str = "fly.fish.othersdk.";
    public static String userInfo = "";
    static {
        map.put("asdk", gu_str + "Asdk");
        map.put("cmgamesdk", gu_str + "CMCame");
        map.put("mmsdk", gu_str + "MobileSDK");
        map.put("cmgamedjsdk", gu_str + "CMGameDJ");
        map.put("youkusdk", gu_str + "YOUKUSDK");
        map.put("lenovo2sdk", gu_str + "LenovoSDK");
        map.put("jodo2sdk", gu_str + "JODOSDK2");
        map.put("jodo3sdk", gu_str + "JODOSDK2");
        map.put("qihoo2sdk", gu_str + "QiHooSDK3");
        map.put("meizusdk", gu_str + "MeiZuSDK2");
        map.put("kuaiwansdk", gu_str + "KuaiWanSDK");
        map.put("oppo2sdk", gu_str + "OPPOSDK200");
        map.put("qqsdk", gu_str + "QQSDK2");
        map.put("kuwosdk", gu_str + "KuWoSDK");
        map.put("baidu4sdk", gu_str + "BDSDK");
        map.put("sky2sdk", gu_str + "Sky2SDK");
        map.put("lbyxsdk", gu_str + "LiebaoSDK");
        map.put("tt2sdk", gu_str + "TTSDK");
        map.put("youlongsdk", gu_str + "YLongSDK21");
        map.put("youlong2sdk", gu_str + "YLongSDK30");
        map.put("kaopu2sdk", gu_str + "KaopuSDK514");
        map.put("xwansdk", gu_str + "XwanSDK");
        map.put("guopansdk", gu_str + "GuoPanSDK");
        map.put("jufengsdk", gu_str + "JuFengSDK");
        map.put("xwqqsdk", gu_str + "Xwan2SDK");
        map.put("cmqqsdk", gu_str + "CMQQSDK");
        map.put("ucsdk", gu_str + "UCSDK610");
        map.put("cmqq2sdk", gu_str + "CMQQSDK2");
        map.put("mogesdk", gu_str + "MogeSDK");
        map.put("bkmhsdk", gu_str + "TongbulvSDK");
        map.put("hulusdk", gu_str + "HuluxiaSDK");
        map.put("hulu3sdk", gu_str + "HuluxiaSDK3");
        map.put("wandou2sdk", gu_str + "wandoujia");
        map.put("downjoy2sdk", gu_str + "DownJoySDK");
        map.put("sohusdk", gu_str + "SoHuSDK");
        map.put("migamesdk", gu_str + "MIGAMESDK");
        map.put("amsdk", gu_str + "AMSDK");
        map.put("am2sdk", gu_str + "AMSDK");
        map.put("yywansdk", gu_str + "YAYAWANSDK");
        map.put("ywydsdk", gu_str + "YWYDSDK");
        map.put("qilesdk", gu_str + "QILESDK");
        map.put("mzwsdk", gu_str + "MZWSDK301");
        map.put("anzhi2sdk", gu_str + "AnzhiSDK420");
        map.put("huaweisdk", gu_str + "HuaWeiSDK253302");
        map.put("huawei2sdk", gu_str + "HuaWeiSDK253302");
        map.put("yyh3sdk", gu_str + "YYHSDK700");
        map.put("youxiskysdk", gu_str + "YXSkySDK");
        map.put("kudongsdk", gu_str + "MOGOOSDK");
        map.put("pipawsdk", gu_str + "PipaSDK");
        map.put("kupad4sdk", gu_str + "KUPADSDK212");
        map.put("pjsdk", gu_str + "PaoJiaoSDK353");
        map.put("gfansdk", gu_str + "GFANSDK43");
        map.put("vivo3sdk", gu_str + "VivoSDK220");
        map.put("jolosdk", gu_str + "JoLoSDK");
        map.put("mumayisdk", gu_str + "MumayiSDK");
        map.put("kugou2sdk", gu_str + "KUGOUSDK2");
        map.put("umisdk", gu_str + "UMISDK");
        map.put("ouwansdk", gu_str + "OUWANSDK");
        map.put("oupengsdk", gu_str + "OperaSDK");
        map.put("itoolssdk", gu_str + "IToolsSDK");
        map.put("letvsdk", gu_str + "LeTVSDK227");
        map.put("ewansdk", gu_str + "EwanSDK");
        map.put("foxsdk", gu_str + "HuliSDK");
        map.put("ccsdk", gu_str + "CcSDK");
        map.put("baofengsdk", gu_str + "BaoFengSDK");
        map.put("tiantuosdk", gu_str + "TianTuoSDK");
        map.put("qikesdk", gu_str + "QikeSDK");
        map.put("egame3sdk", gu_str + "Egame");
        map.put("sogousdk", gu_str + "SoGouGameSDK");
        map.put("m4399sdk", gu_str + "M4399SDK");
        map.put("pptvsdk", gu_str + "PPTVSDK");
        map.put("sysdk", gu_str + "Sy07073SDK");
        map.put("nduosdk", gu_str + "NDuoSDK");
        map.put("ppssdk", gu_str + "PPSGameSDK");
        map.put("pps2sdk", gu_str + "PPSGameSDK");
        map.put("xyzssdk", gu_str + "XYSDK");
        map.put("hmsdk", gu_str + "HaiMaSDK");
        map.put("woniusdk", gu_str + "SnailSDK");
        map.put("xmwsdk", gu_str + "XmwSDK");
        map.put("youkugamesdk", gu_str + "YKSDK");
        map.put("shoumengsdk", gu_str + "ShouMengSDK246");
        map.put("meitusdk", gu_str + "MeiTuSDK313");
        map.put("m37sdk", gu_str + "SQSYSDK");
        map.put("lewansdk", gu_str + "LeWanSDK");
        map.put("pyw2sdk", gu_str + "PYWSDK2");
        map.put("morefunsdk", gu_str + "MoreFunSDK");
        map.put("yxfansdk", gu_str + "YXfanSDK");
        map.put("yxfan4sdk", gu_str + "YXfanSDK3");
        map.put("yxfan5sdk", gu_str + "YXfanSDK3");
        map.put("anliesdk", gu_str + "AnlieSDK");
        map.put("xinjisdk", gu_str + "XinJiSDK");
        map.put("yihuansdk", gu_str + "YiHuanSDK");
        map.put("cainiaosdk", gu_str + "CNWSDK");
        map.put("aqqzgsdk", gu_str + "QQzgSDK");
        map.put("pptv2sdk", gu_str + "PPTVSDK100");
        map.put("xinlang2sdk", gu_str + "SinaSDK");
        map.put("mtxxsdk", gu_str + "meituxiuxiuSDk");
        map.put("mengyousdk", gu_str + "MengyouSDK");
        map.put("wangyisdk", gu_str + "wangyiSDK");
        map.put("douyu2sdk", gu_str + "DouYuSDK200");
        map.put("pandasdk", gu_str + "Pandasdk");
        map.put("yinliansdk", gu_str + "YinlianSDK");
        map.put("kuaishousdk", gu_str + "kuaishousdk");
        map.put("tianxingsdk", gu_str + "DhjtSDK");
        map.put("zhiniusdk", gu_str + "ZhiNiuSDK");
        map.put("dachensdk", gu_str + "DachenSDK");
        map.put("yuewensdk", gu_str + "YueWenSDK");
        map.put("jiuyaosdk", gu_str + "JiuYaoSDK");
        map.put("xiaoniusdk", gu_str + "XiaoniuSDK");
        map.put("xiaoqi2sdk", gu_str + "XiaoQiSDK");
        map.put("lhhbtsdk", gu_str + "LhhBTSDK");
        map.put("swsdk", gu_str + "ShunWangSDK");
        map.put("sw2sdk", gu_str + "ShunWang2SDK");
        map.put("duomengsdk", gu_str + "Duomengsdk");
        map.put("aqqzgsdk", gu_str + "AQQSDK");
        map.put("asamsungsdk", gu_str + "ASamsungSDK");
        map.put("samsung3sdk", gu_str + "Samsung4SDK");
        map.put("amigamesdk", gu_str + "AMIGAMESDK");
        map.put("papagamesdk", gu_str + "PaPaGameSDK");
        map.put("bilisdk", gu_str + "BilbilSDK");
        map.put("huawei3sdk", gu_str + "HuaWeiSDK401300");
        map.put("ahuawei3sdk", gu_str + "AHuaWeiSDK401300");
        map.put("huawei4sdk", gu_str + "HuaWeiSDK_old401300");
        map.put("ggsdk", gu_str + "GGSDK");
        map.put("quicksdk", gu_str + "quickSDK");
        map.put("m233sdk", gu_str + "M233SDK");
        map.put("kukusdk", gu_str + "KUKUSDK");
        map.put("sy233sdk", gu_str + "SY233SDK");
        map.put("xtsdk", gu_str + "XTSDK");
        map.put("manbasdk", gu_str + "ManBaSDK");
        map.put("yuewansdk", gu_str + "YWSDK");
       // map.put("bsgamesdk", gu_str + "BSGameSDK");
        map.put("hysdk", gu_str + "HYSDK");
        map.put("chaotusdk", gu_str + "ChaoTuSDK");
        //map.put("papagamesdk", gu_str + "wufunSDK");
        map.put("baidu5sdk", gu_str + "BDSDK105");
        map.put("dybsdk", gu_str + "DiYiBoSDK");
        map.put("appasdk", gu_str + "Asdk");
        map.put("ypasdk", gu_str + "Asdk");
        map.put("nubiasdk", gu_str + "NubiaSDK");
        map.put("cabbagesdk", gu_str + "CabbageSDK");
        map.put("qutongsdk", gu_str + "QutongSDK");
        map.put("tbsdk", gu_str + "TongBuTuiSDK");
        map.put("yingpaisdk", gu_str + "YingpaiSDK");
        map.put("tanwansdk", gu_str + "TanwanSDK");
        map.put("youaisdk", gu_str + "YouaiSDK");
        map.put("yougusdk", gu_str + "YouGuBTSDK");
        map.put("aidousdk", gu_str + "AiDouSDK");
        map.put("orangersdk", gu_str + "ChengChengSDK");
        map.put("asyxsdk", gu_str + "ASYXSDK");
        map.put("changyusdk", gu_str + "ChangYuSDK");
        map.put("daqiansdk", gu_str + "DaQianSDK");
        map.put("dddsdk", gu_str + "DddSDK");
        map.put("ddsdk", gu_str + "DDyxSDK");
        map.put("firesdk", gu_str + "FireTypeSDK");
        map.put("fuyusdk", gu_str + "FuYuSDK");
        map.put("skyyusdk", gu_str + "GZ_TianYuSDK");
        map.put("m178ggsdk", gu_str + "HuoSuSDK");
        map.put("jianwansdk", gu_str + "JianWanSDK");
        map.put("kuaikansdk", gu_str + "KKMHSDK");
        map.put("landiesdk", gu_str + "LanDieSDK");
        map.put("landie2sdk", gu_str + "LanDieSDK1511");
        map.put("lhh2sdk", gu_str + "LeHHSDK");
        map.put("lexinsdk", gu_str + "LeXinSDK");
        map.put("sy233sdk", gu_str + "M233SDK241");
        map.put("m7477sdk", gu_str + "M7477SDK");
        map.put("toutiaosdk", gu_str + "TouTiaoLianYunSDK");// 頭條聯運
        map.put("m7723sdk", gu_str + "M7723SDK");// 7233sdk
        map.put("zhongchuansdk", gu_str + "ZhongChuanSDK");// 中传sdk
        map.put("m6533sdk", gu_str + "M6533SDK");// 6533sdk
        map.put("xinji2sdk", gu_str + "XinJisSDKU14");// 心迹SDK
        map.put("quick2sdk", gu_str + "Quick2SDK");// quickSDK
        map.put("kuaipansdk", gu_str + "KuaiPanSDK");// 快盘
        map.put("vivodjsdk", gu_str + "VivoDJSDK");// vivo单机
        map.put("dangwansdk", gu_str + "DangWanSDK");
        map.put("jiuqusdk", gu_str + "JiuQuSDk");
        map.put("binghusdk", gu_str + "BingHuSDK");
        map.put("wangyisdk", gu_str + "WangYiSDK");
        map.put("mangosdk", gu_str + "MangGuoSDK");
        map.put("bsgamesdk", gu_str + "SharkGameSDK");
        map.put("hulu4sdk", gu_str + "HuluxiaSDK4");
        map.put("guopansdk2", gu_str + "GuoPanSDK");
        map.put("guopansdk3", gu_str + "GuoPanSDK");
        map.put("yougusdk2", gu_str + "YouGuBTSDK");
        map.put("quick2sdk2", gu_str + "Quick2SDK");// quickSDK  kuaishousdk
        map.put("kuaishousdk", gu_str + "KuaiShouSDK");
        map.put("mgsdk", gu_str + "MGSDK");
        map.put("kupad5sdk", gu_str + "KUPAD2112");
        //map.put("xinji2sdk", gu_str + "Asdk");
        map.put("cdlsdk", gu_str + "Cdlsdk");
        map.put("huyasdk", gu_str + "HuYasdk");
        map.put("samsung5sdk", gu_str + "Samsung5SDK");
        map.put("samsung6sdk", gu_str + "Samsung6SDK");
        map.put("leidian2sdk", gu_str + "LeiDianSDK");
        map.put("xindongsdk", gu_str + "XinDongSDK");
        map.put("punkdjsdk", gu_str + "PunkDJSDK");
        map.put("daqinsdk", gu_str + "DaQinSDK");
        map.put("daqin2sdk", gu_str + "DaQinSDK");
        map.put("amdjsdk", gu_str + "AMDJSDK");
        map.put("m1699sdk", gu_str + "M1699SDK");
        map.put("guaimaosdk", gu_str + "GUAIMAOSDK");
        map.put("quick2sdk3", gu_str + "Quick2SDK");// quickSDK
        map.put("xiaoqi3sdk", gu_str + "XiaoQiSDK");
        map.put("yiyuansdk", gu_str + "YiYuanSDK");
        map.put("tianssdk", gu_str + "TianShengSDK");
        map.put("yofun2sdk", gu_str + "YoFunSDK");
        map.put("eusdk", gu_str + "NEWEUSDK");
        map.put("yuewensdk2", gu_str + "YueWenSDK");
        map.put("wufanabsdk", gu_str + "wufunABSDK");
        map.put("chitusdk", gu_str + "ChiTuSDK");
        map.put("huosdk", gu_str + "HUOSDK");
        map.put("daxisdk", gu_str + "DXhdSDK");
        map.put("quick3sdk", gu_str + "Quick3SDK");
        map.put("douyinsdk", gu_str + "DouYinLianYunSDK");
        map.put("wufanjhsdk", gu_str + "WUFANJHSDK");
        map.put("m7477sdk", gu_str + "M7477SDK");
    }

    private static Method getMethod(String flag, Class<?>... clas) {
        try {
            return clazz.getMethod(flag, clas);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void invoke(Method method, Object... prams) {
        if (method == null) {
            return;
        }
        try {
            method.invoke(null, prams);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 登录
    public static void othLogin(Activity act, Intent mIntent) {
        Method method = getMethod("loginSDK", Activity.class, Intent.class);
        invoke(method, act, mIntent);
    }

    // 支付
    private static void othPay(Activity act, Intent mIntent, String order,
            String paynotifyurl, String extdata1, String extdata2,
            String extdata3) {
    	System.out.println("SkipActivity  ------othPay---------");
        // 1 渠道 其他默认
        String payCode = MyApplication.context.getSharedPreferences(
                "user_info", 0).getString("othersdkextdata5", "");

        if (payCode.equals("1")) {

            mIntent.setClass(act, ChargeActivity.class);
            act.startActivity(mIntent);

        } else {

            Method method = getMethod("paySDK", Activity.class, Intent.class,
                    String.class, String.class, String.class, String.class,
                    String.class);
            invoke(method, act, mIntent, order, paynotifyurl, extdata1,
                    extdata2, extdata3);
        }

    }

    public static void myReceive(Activity mActivity, Intent mIntent,
            Intent intent) {

        String order = intent.getStringExtra("orderid");
        String paynotifyurl = intent.getStringExtra("paynotifyurl");
        String extdata1 = intent.getStringExtra("extdata1");
        String extdata2 = intent.getStringExtra("extdata2");
        String extdata3 = intent.getStringExtra("extdata3");
        Boolean finish = intent.getBooleanExtra("isFinish", false);

        if (finish) {
            if (intent.getStringExtra("msg") != null) {
                Toast.makeText(MyApplication.context,
                        intent.getStringExtra("msg"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyApplication.context, "网络异常", Toast.LENGTH_LONG)
                        .show();
            }
            payFailed(mActivity, mIntent);
        } else {
            othPay(mActivity, mIntent, order, paynotifyurl, extdata1, extdata2,
                    extdata3);
        }
    }
    public static void CloseAccountWithUserInfo(String userdata) {
		// TODO Auto-generated method stub
		Method method = getMethod("CloseAccountWithUserInfo", String.class);
		invoke(method, userdata);
	}
    private static void payFailed(Activity act, Intent intent) {
        intent.setClass(act, MyRemoteService.class);
        Bundle locBundle = intent.getExtras();
        locBundle.putString("flag", "pay");
        locBundle.putString("msg", locBundle.getString("desc"));
        locBundle.putString("sum", locBundle.getString("account"));
        locBundle.putString("chargetype", "pay");
        locBundle.putString("custominfo", locBundle.getString("callBackData"));
        locBundle.putString("customorderid",
                locBundle.getString("merchantsOrder"));
        locBundle.putString("status", "1");
        intent.putExtras(locBundle);
        act.startService(intent);
    };

    // ----------------以下为application调用的方法----------------
    public static void APPAttachBaseContext(MyApplication app, Context base) {
        if (clazz == null) {
            String name = map.get(Publisher);
            MLog.a("Publisher=" + name);
            String name_ = (name == null) ? map.get(Publisher.substring(0,
                    Publisher.length() - 1)) : name;
            try {
                clazz = Class.forName(name_);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Method method = getMethod("applicationAttachBaseContext",
                Application.class, Context.class);
        invoke(method, app, base);
        System.out.println("X7SDK");
    }

    public static void APPOnCreate(MyApplication app) {
        Method method = getMethod("applicationOnCreate", Application.class);
        invoke(method, app);
    }

    public static void APPConfigurationChanged(MyApplication app,
            Configuration newConfig) {
        Method method = getMethod("applicationOnConfigurationChanged",
                Application.class, Configuration.class);
        invoke(method, app, newConfig);
    }

    public static void APPOnTerminate(MyApplication app) {
        Method method = getMethod("applicationOnTerminate", Application.class);
        invoke(method, app);
    }

    // ----------------以上为application调用的方法----------------

    // 初始化1
    public static void othInit(Activity activity) {
        Method method = getMethod("initSDK", Activity.class);
        invoke(method, activity);
    }

    public static void othInit(Activity activity, Bundle savedInstanceState) {
        Method method = getMethod("initSDK", Activity.class, Bundle.class);
        invoke(method, activity, savedInstanceState);
    }

    public static void onSaveInstanceState(Activity activity,
            Bundle savedInstanceState) {
        Method method = getMethod("onSaveInstanceState", Activity.class,
                Bundle.class);
        invoke(method, activity, savedInstanceState);
    }

    // 初始化2
    public static void othInitLaunch(Activity activity, boolean isLandscape,
            CallBackListener callback) {
        Method method = getMethod("InitLaunch", Activity.class, boolean.class,
                CallBackListener.class);
        invoke(method, activity, isLandscape, callback);
        if (method == null) {
            callback.callback(0, false);
        }

    }

    // ----------------以下为生命周期方法----------------
    public static void othNewIntent(Activity act, Intent intent) {
        Method method = getMethod("onNewIntent", Activity.class, Intent.class);
        invoke(method, act, intent);
    }

    public static void othOnResume(Activity act) {
        Method method = getMethod("onResume", Activity.class);
        invoke(method, act);
    }

    public static void othOnPuse(Activity act) {
        Method method = getMethod("onPause", Activity.class);
        invoke(method, act);
    }

    public static void othOnStop(Activity act) {
        Method method = getMethod("onStop", Activity.class);
        invoke(method, act);
    }

    public static void othDestroy(Activity act) {
        Method method = getMethod("onDestroy", Activity.class);
        invoke(method, act);
    }

    public static void othOnStart(Activity act) {
        Method method = getMethod("onStart", Activity.class);
        invoke(method, act);
    }

    public static void othRestart(Activity act) {
        Method method = getMethod("onRestart", Activity.class);
        invoke(method, act);
    }

    public static void othActivityResult(Activity act, int requestCode,
            int resultCode, Intent data, Intent mIntent) {
        Method method = getMethod("onActivityResult", Activity.class,
                int.class, int.class, Intent.class);
        invoke(method, act, requestCode, resultCode, data);
    }

    public static void othBackPressed(Activity act) {
        Method method = getMethod("onBackPressed", Activity.class);
        invoke(method, act);
    }

    public static void othConfigurationChanged(Configuration newConfig) {
        Method method = getMethod("onConfigurationChanged", Configuration.class);
        invoke(method, newConfig); 
    }

    public static void onRequestPermissionsResult(Activity activity,
            int requestCode, String[] permissions, int[] grantResults) {
        Method method = getMethod("onRequestPermissionsResult", Activity.class,
                int.class, String[].class, int[].class);
        invoke(method, activity, requestCode, permissions, grantResults);

    }
    
    public static void othonWindowFocusChanged(boolean hasFocus) {
        Method method = getMethod("onWindowFocusChanged", boolean.class);
        invoke(method, hasFocus); 
    }

    // ----------------以上为生命周期方法----------------

    // 退出
    public static void othQuit(Activity act) {
        Method method = getMethod("exit", Activity.class);
        invoke(method, act);
    }

    // 有回调的退出
    public static void outQuitCallBack(Activity act, ExitCallBack exitcallback) {
        Method method = getMethod("doSdkQuit", Activity.class,
                ExitCallBack.class);
        invoke(method, act, exitcallback);
    }

    // 默认注销回调
    private static void logout(Activity act) {
        Intent locIntent = new Intent();
        locIntent.setClass(act, MyRemoteService.class);
        Bundle locBundle = new Bundle();
        locBundle.putString("flag", "login");
        locBundle.putString("sessionid", "0");
        locBundle.putString("accountid", "0");
        locBundle.putString("status", "2");
        locBundle.putString("custominfo", "");
        locIntent.putExtras(locBundle);
        act.startService(locIntent);
    }

    // 注销
    public static void othLogout(Activity act) {
        Method method = getMethod("logout", Activity.class);
        if (method == null) {
            logout(act);
            return;
        }
        invoke(method, act);
    }

    // 应用宝的设置截图监听
    public static void setScreenCapturer(AImageCapturer capturer) {
        Method method = getMethod("setScreenCapturer", AImageCapturer.class);
        invoke(method, capturer);
    }

    // 调用论坛接口
    public static void callPerformFeatureBBS(Activity act) {
        Method method = getMethod("callPerformFeatureBBS", Activity.class);
        invoke(method, act);
    }

    // oppo判断是否从应用中心启动
    public static Object isFromGameCenter(Activity act) {
        Method method = getMethod("isFromGameCenter", Activity.class);

        try {
            return method.invoke(null, act);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 上报角色信息
    public static void othInGame(String userdata) {
        MLog.a("ASDK", "info------------------>" + userdata);
        userInfo = userdata;

        Method method = getMethod("submitData", String.class);
        invoke(method, userdata);
    }

    // 阿游戏支付跳转
    public static void asdkPay(Activity act, Intent mIntent) {
    	System.out.println("SkipActivity  ------asdkPay-------paySDK-----");
        Method method = getMethod("paySDK", Activity.class, Intent.class);
        invoke(method, act, mIntent);
    }

    // 热云登录上报
    public static void reyunsetLogin(String acc) {
    	System.out.println("SkipActivity  ------asdkPay-------paySDK-----");
        Method method = getMethod("reyunsetLogin", String.class);
        invoke(method, acc);
    }

    // 热云头条支付上报
    public static void reyunandttsetPay(String orderid, String type,
            String sum, boolean issuccess) {
        Method method = getMethod("reyunandttsetPay", String.class,
                String.class, String.class, boolean.class);
        invoke(method, orderid, type, sum, issuccess);
    }

    public static void getCertificateInfo(Activity act,
            GetCertificationInfoCallback callback) {
        Method method = getMethod("getCertificateInfo", Activity.class,
                GetCertificationInfoCallback.class);
        invoke(method, act, callback);
    }
    
    public static void CloseAccountWithUserInfo(Activity act,String userdata,CloseAccountCallBack exitcallback) {
		// TODO Auto-generated method stub
		Method method = getMethod("CloseAccountWithUserInfo",Activity.class, String.class,CloseAccountCallBack.class);
		invoke(method,act, userdata,exitcallback);
	}
    /* public static void Closead(boolean isclose){
        try {
            Method method = getMethod("closead",boolean.class);
            invoke(method, isclose);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void loadad(Activity activity,String adid){
        try {
            Method method = getMethod("loadad",Activity.class,String.class);
            invoke(method, activity,adid);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void  setadback( ForADresult back){
        try {
            Method method = getMethod("setadback", ForADresult.class);
            invoke(method, back );
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
  /* public  static void skishare(Activity activity, String code, File file){

        Method method = getMethod("JGshare",Activity.class, String.class,File.class);
        invoke(method,activity, code,file);
    }*/



}
