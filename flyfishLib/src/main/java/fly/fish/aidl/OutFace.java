package fly.fish.aidl;


import java.io.File;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import fly.fish.asdk.BuildConfig;
import fly.fish.asdk.LoginActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.asdk.MyCrashHandler;
import fly.fish.asdk.SkipActivity;
import fly.fish.config.Configs;
import fly.fish.config.StatusCode;
import fly.fish.dialog.CloseAccountCallBack;
import fly.fish.impl.ExitCallBack;
import fly.fish.impl.GetCertificationInfoCallback;
import fly.fish.open.impl.CommonCallback;
import fly.fish.open.impl.SimpleCallback;
import fly.fish.othersdk.JGSHaretools;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

public class OutFace {
    private static String d = "fly.fish.aidl.IMyTaskBinder";
    private String DOWN_ACTION = MyApplication.getAppContext().getPackageName() + ".fly.fish.aidl.MyRemoteService.MYBROADCAST";

    public static final String TAG = "asdk_log";
    public static final String SDK_VERSION_NAME = BuildConfig.SDK_VERSION_NAME;
    public static final int SDK_VERSION_CODE = BuildConfig.SDK_VERSION_CODE;

    public static Activity mActivity;
    private static volatile OutFace mInstance;

    private Intent mIntent;
    private IMyTaskBinder ibinder = null;
    private ITestListener listener = null;

    private String Publisher;
    private String cpid = null;
    private String gameid = null;
    private String gameKey = null;
    private String gamename = null;

    private MyBroadCast broadcast;
    private boolean isRegitered = false;

    private Intent service;

    /**
     * @Deprecated 从8.0.0版本开始移除
     * Use {@link #getInstance()}
     */
    @Deprecated
    public static OutFace getInstance(Context context) {
        return getInstance();
    }

    public static OutFace getInstance() {
        if (mInstance == null) {
            synchronized (OutFace.class) {
                if (mInstance == null) {
                    mInstance = new OutFace();
                }
            }
        }
        return mInstance;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void outActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
        MLog.i(TAG, "outActivityResult: ");
        SkipActivity.onActivityResult(act, requestCode, resultCode, data);
    }

    public void outOnCreate(Activity activity) {
        MLog.i(TAG, "outOnCreate: ");
        mActivity = activity;
        SkipActivity.initSDK(activity);
    }

    public void outOnCreate(Activity activity, Bundle savedInstanceState) {
        MLog.i(TAG, "outOnCreate: savedInstanceState");
        mActivity = activity;
        SkipActivity.initSDK(activity, savedInstanceState);
    }

    public void outOnStart(Activity activity) {
        MLog.i(TAG, "outOnStart: ");
        SkipActivity.onStart(activity);
    }

    public void outOnResume(Activity act) {
        MLog.i(TAG, "outOnResume: ");
        SkipActivity.onResume(act);
    }

    public void outOnPause(Activity act) {
        MLog.i(TAG, "outOnPause: ");
        SkipActivity.onPause(act);
    }

    public void outOnStop(Activity act) {
        MLog.i(TAG, "outOnStop: ");
        SkipActivity.onStop(act);
    }

    public void onSaveInstanceState(Activity act, Bundle outState) {
        MLog.i(TAG, "onSaveInstanceState: ");
        SkipActivity.onSaveInstanceState(act, outState);
    }

    public void outDestroy(Activity activity) {
        MLog.i(TAG, "outDestroy: ");
        SkipActivity.onDestroy(activity);
        quit(activity);
    }

    /**
     * 旧的角色上报接口
     * @deprecated use {@link #uploadData(String)}
     * @param abc
     */
    @Deprecated
    public void outInGame(String abc) {
        uploadData(abc);
    }

    public void uploadData(String roleData) {
        MLog.i(TAG, "uploadData: "+roleData);
        SkipActivity.uploadData(roleData);
    }

    // 社区
    public void outForum(Activity activity) {
        MLog.i(TAG, "outForum: ");
        SkipActivity.callPerformFeatureBBS(activity);
    }

    public String getDeviceId(Context context) {
        return  PhoneTool.getIMEI(context);
    }

    public void outNewIntent(Activity act, Intent intent) {
        MLog.i(TAG, "outNewIntent: ");
        SkipActivity.onNewIntent(act, intent);
    }

    public void outRestart(Activity act) {
        MLog.i(TAG, "outRestart: ");
        SkipActivity.onRestart(act);
    }

    public void outBackPressed(Activity activity) {
        MLog.i(TAG, "outBackPressed: ");
        SkipActivity.onBackPressed(activity);
    }

    public void outConfigurationChanged(Configuration newConfig) {
        MLog.i(TAG, "outConfigurationChanged: ");
        SkipActivity.onConfigurationChanged(newConfig);
    }

    public void outQuit(Activity act) {
        MLog.i(TAG, "outQuit: ");
        SkipActivity.exit(act);
    }

    public void outQuitCallBack(Activity act, ExitCallBack exitcallback) {
        MLog.i(TAG, "outQuitCallBack: ");
        SkipActivity.outQuitCallBack(act, exitcallback);
    }

    public void outLogout(Activity act) {
        MLog.i(TAG, "outLogout: ");
        SkipActivity.logout(act);
    }

    public void outonWindowFocusChanged(boolean hasFocus) {
        MLog.i(TAG, "outonWindowFocusChanged: ");
        SkipActivity.onWindowFocusChanged(hasFocus);
    }

    public void getCertificateInfo(Activity act, GetCertificationInfoCallback callback) {
        MLog.i(TAG, "getCertificateInfo: ");
        SkipActivity.getCertificateInfo(act, callback);
    }

    public void outInitLaunch(final Activity activity, final boolean isLandscape, final CallBackListener callback) {
        MLog.i(TAG, "outInitLaunch: ");
        mActivity = activity;
        MyCrashHandler mCrashHandler = MyCrashHandler.getInstance();
        mCrashHandler.init(activity.getApplicationContext());
        requestPermission();
        SkipActivity.initLaunch(activity, isLandscape, new CallBackListener() {
            @Override
            public void callback(int code, boolean isHasExitBox) {
                //兼容旧版本初始化
                if (code == 0){
                    if (callback != null)
                        callback.callback(code,isHasExitBox);
                }else {
                    try {
                        if (listener != null)
                            listener.initback(StatusCode.INIT_FAIL);
                    } catch (RemoteException e) {

                    }
                }
            }
        });

    }

    public void setDebug(boolean isDebug) {
        MLog.setDebug(isDebug);
    }

    public void onRequestPermissionsResult(Activity paramActivity, int requestCode, String[] permissions, int[] grantResults) {
        MLog.i(TAG, "onRequestPermissionsResult: ");
        SkipActivity.onRequestPermissionsResult(paramActivity, requestCode, permissions, grantResults);
    }

    /**
     * 连接远程服务
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        /**
         * bindService
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ibinder = IMyTaskBinder.Stub.asInterface(service);
            System.out.println("onServiceConnected init ----> " + ibinder);
            if (ibinder != null) {
                try {
                    ibinder.registerCallBack(listener, gameKey);
                    ibinder.init(cpid, gameid, gameKey, gamename);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * unbindService
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("onServiceDisconnected quit ----> " + ibinder);
            ibinder = null;
        }
    };

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    private OutFace() {
        // 创建广播实现类
        broadcast = new MyBroadCast(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DOWN_ACTION);
        Publisher = FilesTool.getPublisherStringContent();

        System.out.println("registerReceiver");
        MyApplication.getAppContext().registerReceiver(broadcast, filter);
        isRegitered = true;

        initFloatService();
    }

    /***
     * @deprecated use {@link #callBack(String, FlyFishSDK)}
     * @param callback
     * @param key
     */
    @Deprecated
    public void callBack(FlyFishSDK callback,String key) {
        callBack(key,callback);
    }

    public void callBack(String key, FlyFishSDK callback) {
        this.listener = callback;
        if (ibinder != null) {
            try {
                ibinder.registerCallBack(callback, key);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化接口
     *
     * @param cpid
     * @param gameid
     * @return
     * @throws RemoteException
     */
    public boolean init(String cpid, String gameid, String key, String gamename) {

        MLog.i(TAG, "init: ");
        // 先保存参数
        this.cpid = cpid;
        this.gameid = gameid;
        this.gameKey = key;
        this.gamename = MyApplication.getAppContext().getApplicationInfo().loadLabel(MyApplication.getAppContext().getPackageManager()).toString();

        // 判断是走sdk还是jar包
        if (ibinder == null) {
            if (Publisher.contains("kdygfk")) {
                d = MyApplication.getAppContext().getPackageName() + "." + d;
            }
            service = new Intent(d);
            service.setPackage(MyApplication.getAppContext().getPackageName());
            boolean isbinded = MyApplication.getAppContext().bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
            System.out.println("action------------------------->" + d);
            System.out.println("bindService-------------------->" + isbinded);

        } else {
            try {
                ibinder.init(cpid, gameid, key, gamename);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void outCloseAccountWithUserInfo(Activity act, String userInfo, CloseAccountCallBack exitcallback) {
        MLog.i(TAG, "outCloseAccountWithUserInfo: ");
        SkipActivity.CloseAccountWithUserInfo(act, userInfo, exitcallback);
    }

    public void login(Activity act){
        login(act,"meself",gameKey);
    }

    /**
     * 登陆接口
     *
     * @param callBackData 自定义参数
     * @throws RemoteException
     */
    public void login(Activity act, String callBackData, String key) {
        MLog.i(TAG, "login: ");
        mActivity = act;
        mIntent = new Intent();
        Bundle localBundle = new Bundle();
        localBundle.putString("cpid", cpid);
        localBundle.putString("gameid", gameid);
        localBundle.putString("gamename", gamename);
        localBundle.putString("callBackData", callBackData);
        localBundle.putString("key", gameKey);
        localBundle.putString("pid", Binder.getCallingPid() + "");
        localBundle.putString("mode", "login");

        mIntent.putExtras(localBundle);
        SkipActivity.loginSDK(act, mIntent);
    }

    /**
     * 充值接口
     *
     * @param customOrderId 支付订单
     * @param payCallBackUrl            回调接口
     * @param sum        支付金额
     * @param desc           商品描述
     * @param callBackData   自定义参数
     * @throws RemoteException
     */
    public void pay(final Activity act, final String customOrderId, String payCallBackUrl, final String sum, final String desc, final String callBackData, String key) {
        pay(act, customOrderId, payCallBackUrl, sum, "", desc, callBackData, key);
    }

    /**
     * 充值接口
     *
     * @param customOrderId     支付订单
     * @param payCallBackUrl    回调接口
     * @param sum               支付金额
     * @param feePoint          计费点
     * @param desc              商品描述
     * @param callBackData      自定义参数
     * @throws RemoteException
     */
    public void pay(Activity act, String customOrderId, String payCallBackUrl, String sum, String feePoint, String desc, String callBackData, String key) {
        MLog.i(TAG, "pay: ");
        mActivity = act;
        mIntent = new Intent();
        Bundle localBundle = new Bundle();

        localBundle.putString("cpid", cpid);
        localBundle.putString("gameid", gameid);
        localBundle.putString("gamename", gamename);

        localBundle.putString("merchantsOrder", customOrderId);
        localBundle.putString("url", payCallBackUrl);
        localBundle.putString("account", sum);
        localBundle.putString("feepoint", feePoint);
        localBundle.putString("desc", desc);
        localBundle.putString("callBackData", callBackData);
        localBundle.putString("key", key);
        localBundle.putString("pid", Binder.getCallingPid() + "");
        localBundle.putString("mode", "pay");
        localBundle.putString("flag", "getOrder");
        mIntent.putExtras(localBundle);
        String channelClassName = FilesTool.getChannelOpenClassName();
        if (channelClassName.equals("fly.fish.othersdk.Asdk")){
            SkipActivity.asdkPay(act, this.mIntent);
        } else {
            String orderExtdata = SkipActivity.getOrderExtdata();
            mIntent.putExtra("extdata", orderExtdata);
            mIntent.setClass(act, MyRemoteService.class);
            act.startService(mIntent);
        }

    }

    /**
     * 查询接口
     *
     * @param merchantsOrder 支付订单
     * @param callBackData   自定义参数
     * @throws RemoteException
     */
    @Deprecated
    public void query(String merchantsOrder, String callBackData, String key) {
        MLog.i(TAG, "query: ");
    }

    /**
     * 退出接口
     *
     * @throws RemoteException
     */
    public void quit(Activity activity) {
        MLog.i(TAG, "quit: ");
        if (MyApplication.getAppContext() != null) {
            if (ibinder != null) {
                Intent service = null;
                service = new Intent(d);
                service.setPackage(MyApplication.getAppContext().getPackageName());
                MyApplication.getAppContext().unbindService(serviceConnection);
                MyApplication.getAppContext().stopService(service);
                ibinder = null;
                System.out.println("unbindService");
            }
            if (isRegitered) {
                MyApplication.getAppContext().unregisterReceiver(broadcast);
                System.out.println("unregisterReceiver");
                isRegitered = false;
            }
        }

        cpid = null;
        gameid = null;
        gameKey = null;
        gamename = null;
        mInstance = null;
        System.out.println("---------asdk--exit--end-----------");

    }

    /**
     * 对外接口类
     *
     * @author Administrator
     */
    public static abstract class FlyFishSDK extends ITestListener.Stub {
    }

    /**
     * 广播接口类 可以在其他地方操作广播
     *
     * @author Administrator
     */
    public class MyBroadCast extends BroadcastReceiver {

        public MyBroadCast(OutFace out) {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String actionCode = intent.getAction();
            if (DOWN_ACTION.equals(actionCode)) {
                SkipActivity.myReceive(mActivity, mIntent, intent);
            }
        }

    }

    private boolean isLandScape = false;
    private String packageName = "";
    private IGhostWindowService mBind = null;
    private ServiceConnection ghostServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            if (service != null) {
                try {
                    mBind = IGhostWindowService.Stub.asInterface(service);
                    if (mBind != null) {
                        mBind.initGhostWindow();
                        mBind.showGhostWindow( packageName, isLandScape);
                        mBind.hideGhostWindow();
                        mBind.showChatWindow();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 启动悬浮框服务
     */
    public void initFloatService() {
        getApplicationConfig();
        Intent intent = new Intent();
        intent.setAction("fly.fish.ghostWindowService");
        intent.setPackage("com.zshd.GameCenter");
        MyApplication.getAppContext().bindService(intent, ghostServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取程序配置信息，横竖屏，包名等
     */
    public void getApplicationConfig() {
        Configuration configuration = MyApplication.getAppContext().getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true;
        }
        packageName = MyApplication.getAppContext().getPackageName();
    }

    /**
     * login for float view only --- 只适用于悬浮框登录
     *
     * @param act
     * @param callBackData
     * @param key
     */
    public void loginByContext(Context act, String callBackData, String key) {
        mIntent = new Intent();
        Bundle localBundle = new Bundle();
        localBundle.putString("cpid", cpid);
        localBundle.putString("gameid", gameid);
        localBundle.putString("gamename", gamename);
        localBundle.putString("callBackData", callBackData);
        localBundle.putString("key", key);
        localBundle.putString("pid", Binder.getCallingPid() + "");
        localBundle.putString("mode", "login");

        mIntent.putExtras(localBundle);
        mIntent.setClass(act, LoginActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        act.startActivity(mIntent);
    }

    //跳转小程序
    public void outshare(Activity activity, int code) {
        MLog.i(TAG, "outshare: ");
        JGSHaretools.othshare(activity, code);
    }

    //分享
    public void outJGshare(Activity activity, int code, File file) {
        MLog.i(TAG, "outJGshare: ");
        JGSHaretools.othJgshare(activity, code, file);
    }

    public static boolean getOneLoginCheck() {
        return Configs.isEnableOneKeyLogin;
    }

    //获取审核状态
    public boolean getCheckState() {
        return Configs.isEnableFormalMode;
    }

    public static void requestPermission() {
        if (Configs.isEnableRequestPermission) {
            if (Build.VERSION.SDK_INT < 23) {
                return;
            }

            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                // || ContextCompat.checkSelfPermission(activity,
                // Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

            ) {
                System.out.println("shouldShowRequestPermissionRationale  ----> 000 ");
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
                        // ,Manifest.permission.CAMERA
                }, 1);

            }
        }
    }

    public void othopenForumPage() {
        MLog.i(TAG, "othopenForumPage: ");
        SkipActivity.sdkopenForumPage();
    }

    /**
     * 通用扩展接口-预留
     * @param objects
     */
    public void commonApi1(Object... objects) {
        MLog.i(TAG, "commonApi1: ");
        SkipActivity.commonApi1(objects);
    }

    public void commonApi2(Object... objects) {
        MLog.i(TAG, "commonApi2: ");
        SkipActivity.commonApi2(objects);
    }

    public Object commonApi3(Context context, Object... objects) {
        MLog.i(TAG, "commonApi3: ");
        return SkipActivity.commonApi3(context,objects);
    }

    public Object commonApi4(Context context, Object... objects) {
        MLog.i(TAG, "commonApi4: ");
        return SkipActivity.commonApi4(context,objects);
    }

    public void commonApi5(Context context, SimpleCallback callback, Object... objects) {
        MLog.i(TAG, "commonApi5: ");
        SkipActivity.commonApi5(context,callback,objects);
    }

    public void commonApi6(Context context, CommonCallback callback, Object... objects) {
        MLog.i(TAG, "commonApi6: ");
        SkipActivity.commonApi6(context,callback,objects);
    }

}
