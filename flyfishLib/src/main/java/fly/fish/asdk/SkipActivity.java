package fly.fish.asdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import fly.fish.aidl.CallBackListener;
import fly.fish.aidl.MyRemoteService;
import fly.fish.dialog.CloseAccountCallBack;
import fly.fish.impl.AImageCapturer;
import fly.fish.impl.ExitCallBack;
import fly.fish.impl.GetCertificationInfoCallback;
import fly.fish.open.impl.CommonCallback;
import fly.fish.open.impl.SimpleCallback;
import fly.fish.tools.FilesTool;
import fly.fish.tools.ReflectUtils;

public class SkipActivity {

    private static String channelClassName;


    // ----------------以下为application调用的方法----------------
    public static void APPAttachBaseContext(MyApplication app, Context base) {
        channelClassName = FilesTool.getChannelOpenClassName();
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "applicationAttachBaseContext", Application.class, Context.class)
                .invoke(app, base);
    }

    public static void APPOnCreate(MyApplication app) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "applicationOnCreate", Application.class)
                .invoke(app);
        ReflectUtils.getInstance()
                .getMethod("fly.fish.othersdk.ADMergePlatform", "initAD", Application.class)
                .invoke(app);
    }

    public static void applicationOnConfigurationChanged(MyApplication app, Configuration newConfig) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "applicationOnConfigurationChanged", Application.class, Configuration.class)
                .invoke(app, newConfig);
    }

    public static void onTerminate(MyApplication app) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "applicationOnTerminate", Application.class)
                .invoke(app);
    }
    // ----------------以上为application调用的方法----------------

    // 登录
    public static void loginSDK(Activity act, Intent mIntent) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "loginSDK", Activity.class, Intent.class)
                .invoke(act, mIntent);
    }

    //更新
    public static void update(Activity activity) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "update", Activity.class)
                .invoke(activity);
    }

    public static String getOrderExtdata() {
        Object invoke = ReflectUtils.getInstance()
                .getMethod(channelClassName, "getOrderExtdata")
                .invoke();
        return (String) invoke;
    }

    // 支付
    private static void paySDK(Activity act, Intent mIntent, String order, String paynotifyurl, String extdata1, String extdata2, String extdata3) {
        // 1 渠道 其他默认
        String payCode = MyApplication.context.getSharedPreferences("user_info", 0).getString("othersdkextdata5", "");
        if (payCode.equals("1")) {
            mIntent.setClass(act, ChargeActivity.class);
            act.startActivity(mIntent);
        } else {
            ReflectUtils.getInstance()
                    .getMethod(channelClassName, "paySDK", Activity.class, Intent.class,
                            String.class, String.class, String.class, String.class, String.class)
                    .invoke(act, mIntent, order, paynotifyurl, extdata1,
                            extdata2, extdata3);
        }

    }

    public static void myReceive(Activity mActivity, Intent mIntent, Intent intent) {

        String order = intent.getStringExtra("orderid");
        String paynotifyurl = intent.getStringExtra("paynotifyurl");
        String extdata1 = intent.getStringExtra("extdata1");
        String extdata2 = intent.getStringExtra("extdata2");
        String extdata3 = intent.getStringExtra("extdata3");
        Boolean finish = intent.getBooleanExtra("isFinish", false);

        if (finish) {
            if (intent.getStringExtra("msg") != null) {
                Toast.makeText(MyApplication.context, intent.getStringExtra("msg"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyApplication.context, "网络异常", Toast.LENGTH_LONG).show();
            }
            payFailed(mActivity, mIntent);
        } else {
            paySDK(mActivity, mIntent, order, paynotifyurl, extdata1, extdata2, extdata3);
        }
    }

    public static void CloseAccountWithUserInfo(String userdata) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "CloseAccountWithUserInfo", String.class)
                .invoke(userdata);
    }

    private static void payFailed(Activity act, Intent intent) {
        intent.setClass(act, MyRemoteService.class);
        Bundle locBundle = intent.getExtras();
        locBundle.putString("flag", "pay");
        locBundle.putString("msg", locBundle.getString("desc"));
        locBundle.putString("sum", locBundle.getString("account"));
        locBundle.putString("chargetype", "pay");
        locBundle.putString("custominfo", locBundle.getString("callBackData"));
        locBundle.putString("customorderid", locBundle.getString("merchantsOrder"));
        locBundle.putString("status", "1");
        intent.putExtras(locBundle);
        act.startService(intent);
    }

    // 初始化1
    public static void initSDK(Activity activity) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "initSDK", Activity.class)
                .invoke(activity);
    }

    public static void initSDK(Activity activity, Bundle savedInstanceState) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "initSDK", Activity.class, Bundle.class)
                .invoke(activity, savedInstanceState);
    }

    public static void onSaveInstanceState(Activity activity, Bundle savedInstanceState) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onSaveInstanceState", Activity.class, Bundle.class)
                .invoke(activity, savedInstanceState);
    }

    // 初始化2
    public static void initLaunch(Activity activity, boolean isLandscape, CallBackListener callback) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "InitLaunch", Activity.class, boolean.class, CallBackListener.class)
                .invoke(activity, isLandscape, callback);
    }

    // ----------------以下为生命周期方法----------------
    public static void onNewIntent(Activity act, Intent intent) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onNewIntent", Activity.class, Intent.class)
                .invoke(act, intent);
    }

    public static void onResume(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onResume", Activity.class)
                .invoke(act);
    }

    public static void onPause(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onPause", Activity.class)
                .invoke(act);
    }

    public static void onStop(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onStop", Activity.class)
                .invoke(act);
    }

    public static void onDestroy(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onDestroy", Activity.class)
                .invoke(act);
    }

    public static void onStart(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onStart", Activity.class)
                .invoke(act);
    }

    public static void onRestart(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onRestart", Activity.class)
                .invoke(act);
    }

    public static void onActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onActivityResult", Activity.class, int.class, int.class, Intent.class)
                .invoke(act, requestCode, resultCode, data);
    }

    public static void onBackPressed(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onBackPressed", Activity.class)
                .invoke(act);
    }

    public static void onConfigurationChanged(Configuration newConfig) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onConfigurationChanged", Configuration.class)
                .invoke(newConfig);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onRequestPermissionsResult", Activity.class, int.class, String[].class, int[].class)
                .invoke(activity, requestCode, permissions, grantResults);
    }

    public static void onWindowFocusChanged(boolean hasFocus) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "onWindowFocusChanged", boolean.class)
                .invoke(hasFocus);
    }

    // ----------------以上为生命周期方法----------------

    // 退出
    public static void exit(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "exit", Activity.class)
                .invoke(act);
    }

    // 有回调的退出
    public static void outQuitCallBack(Activity act, ExitCallBack exitcallback) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "doSdkQuit", Activity.class,ExitCallBack.class)
                .invoke(act, exitcallback);
    }

    // 注销
    public static void logout(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "logout", Activity.class)
                .invoke(act);
    }

    // 应用宝的设置截图监听
    public static void setScreenCapturer(AImageCapturer capturer) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "setScreenCapturer", AImageCapturer.class)
                .invoke(capturer);
    }

    public static void sdkopenForumPage() {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "openForumPage")
                .invoke();
    }

    // 调用论坛接口
    public static void callPerformFeatureBBS(Activity act) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "callPerformFeatureBBS", Activity.class)
                .invoke(act);
    }

    // oppo判断是否从应用中心启动
    public static Object isFromGameCenter(Activity act) {
        return ReflectUtils.getInstance()
                .getMethod(channelClassName, "isFromGameCenter", Activity.class)
                .invoke(act);
    }

    // 上报角色信息
    public static void uploadData(String userdata) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "submitData", String.class)
                .invoke(userdata);
    }

    // 阿游戏支付跳转
    public static void asdkPay(Activity act, Intent mIntent) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "paySDK", Activity.class, Intent.class,
                        String.class, String.class, String.class, String.class, String.class)
                .invoke(act, mIntent, null, null, null, null, null);
    }

    // 热云登录上报
    public static void reyunsetLogin(String acc) {
        System.out.println("SkipActivity  ------asdkPay-------paySDK-----");
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "reyunsetLogin", String.class)
                .invoke(acc);
    }

    // 热云头条支付上报
    public static void reyunandttsetPay(String desc, String orderid, String type, String sum, boolean issuccess) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "reyunandttsetPay", String.class, String.class,
                        String.class, String.class, boolean.class)
                .invoke(desc, orderid, type, sum, issuccess);
    }

    public static void getCertificateInfo(Activity act, GetCertificationInfoCallback callback) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "getCertificateInfo", Activity.class,
                        GetCertificationInfoCallback.class)
                .invoke(act, callback);
    }

    public static void CloseAccountWithUserInfo(Activity act, String userdata, CloseAccountCallBack exitcallback) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "CloseAccountWithUserInfo", Activity.class, String.class, CloseAccountCallBack.class)
                .invoke(act, userdata, exitcallback);
    }

    public static void setLogiinState(boolean isLoginSuccess, Intent intent) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "setLogiinState",Boolean.class,Intent.class)
                .invoke(isLoginSuccess, intent);
    }

    public static void setExtdata(String extdata1) {
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "setExtdata",String.class)
                .invoke(extdata1);
    }

    public static void commonApi1(Object... objects) {
        Object[] data = objects;
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "commonApi1",Object[].class)
                .invoke((Object) data);
    }

    public static void commonApi2(Object... objects) {
        Object[] data = objects;
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "commonApi2",Object[].class)
                .invoke((Object) data);
    }

    public static Object commonApi3(Context context, Object... objects) {
        Object[] data = objects;
        return ReflectUtils.getInstance()
                .getMethod(channelClassName, "commonApi3", Context.class, Object[].class)
                .invoke(context, (Object) data);
    }

    public static Object commonApi4(Context context, Object... objects) {
        Object[] data = objects;
        return ReflectUtils.getInstance()
                .getMethod(channelClassName, "commonApi4", Context.class, Object[].class)
                .invoke(context, (Object) data);
    }

    public static void commonApi5(Context context, SimpleCallback callback, Object... objects) {
        Object[] data = objects;
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "commonApi5", Context.class, SimpleCallback.class, Object[].class)
                .invoke(context, callback, data);
    }

    public static void commonApi6(Context context, CommonCallback callback, Object... objects) {
        Object[] data = objects;
        ReflectUtils.getInstance()
                .getMethod(channelClassName, "commonApi6", Context.class, CommonCallback.class, Object[].class)
                .invoke(context, callback, (Object) data);
    }

}
