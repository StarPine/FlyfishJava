package fly.fish.asdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    private static Object object;

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
            if (object == null){
                object = clazz.newInstance();
            }
            method.invoke(object, prams);
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
            String channelClassName = FilesTool.getChannelOpenClassName();
            try {
                clazz = Class.forName(channelClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Method method = getMethod("applicationAttachBaseContext",
                Application.class, Context.class);
        invoke(method, app, base);
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
    public static void reyunandttsetPay(String desc,String orderid, String type,
            String sum, boolean issuccess) {
        Method method = getMethod("reyunandttsetPay", String.class,String.class,
                String.class, String.class, boolean.class);
        invoke(method, desc,orderid, type, sum, issuccess);
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

    public static void setLogiinState(boolean isLoginSuccess, Intent intent) {
        Method method = getMethod("setLogiinState", Boolean.class,
                Intent.class);
        invoke(method, isLoginSuccess, intent);
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
