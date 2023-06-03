package fly.fish.aidl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.keplerproject.luajava.LuaState;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import fly.fish.asdk.MyApplication;
import fly.fish.asdk.SkipActivity;
import fly.fish.beans.GameArgs;
import fly.fish.config.Configs;
import fly.fish.othersdk.Asdk;
import fly.fish.tools.FilesTool;
import fly.fish.tools.HttpUtils;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

/**
 * 服务自己不以处理延时操作 要借助线程才可以 startService 启动的服务
 * 主要用于启动一个服务执行后台任务，不进行通信。停止服务使用stopService bindService 启动的服务
 * 该方法启动的服务要进行通信。停止服务使用unbindService startService 同时也 bindService 启动的服务
 * 停止服务应同时使用stepService与unbindService
 */
public class MyRemoteService extends Service {
    public static final String MY_ACTION = "fuckyou";

    /**
     * 游戏参数集合
     */
    private Map<String, GameArgs> gamemap = null;

    /**
     * 回调接口集合
     */
    private Map<String, ITestListener> callbacks = null;

    /**
     * 我的应用
     */
    public MyApplication app;

    /**
     * 回调接口类
     */
    private ITestListener ilistener;

    /**
     * 接口实现类
     */
    private TestImpl testimpl = null;

    /**
     * 广播处理类
     */
    private MyBroadCast broadcast = null;

    /**
     * 我的消息处理线程
     */
    private HandlerThread ht = null;

    /**
     * 我的消息处理类
     */
    private Handler myhand = null;

    public String re1 = null;
    public String re2 = null;
    public String re3 = null;
    public String re4 = null;
    public String tag = "MyRemoteService";
    /**
     * 应用进程ID
     */
    public int mypid = 0;
    private String publisher = null;

    /**
     * 1
     */
    @Override
    public void onCreate() {
        MLog.a(tag, "Service ---- onCreate");
        super.onCreate();
        publisher = FilesTool.getPublisherStringContent();
        app = MyApplication.getAppContext();

        if (app != null) {

            gamemap = app.getGamemap();
            callbacks = new HashMap<String, ITestListener>();

            // 创建接口实现类
            testimpl = new TestImpl(this);

            // 创建广播实现类
            broadcast = new MyBroadCast(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(MY_ACTION);
            filter.addDataScheme("package");
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            registerReceiver(broadcast, filter);

            // 创建线程

            // 步骤1：创新HandlerThread的一个对象，并开启这个线程，HandlerThread将通过Looper来处理Handler对列中的消息，也就是如果发现Handler中有消息，将在HandlerThread这个线程中进行处理。
            ht = new HandlerThread("hander_thread");
            // 步骤2：启动这个线程;
            ht.start();
            // 步骤3：创建我的Handler;
            myhand = new MyServiceHandler(ht.getLooper(), this);
        }
    }

    /**
     * 2
     */
    @Override
    public IBinder onBind(Intent intent) {
        MLog.a(tag, "Service ---- onBind");
        return testimpl;
    }

    /**
     * （3）该返回值决定服务被异常kill后是否重启的问题
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        MLog.a(tag, "Service ---- onStartCommand");
        if (intent != null) {
            final Bundle bu = intent.getExtras();
            if (bu != null) {
                String key = bu.getString("key");
                MLog.s(key + " --KEY-- " + callbacks.keySet().size() + " ---- " + bu.getString("flag") + "--" + callbacks.get(key));
                if (key != null) {
                    setIlistener(callbacks.get(key));
                }
                if (bu.getString("flag").equals("init")) {// 处理初始化回调(has key)
                    try {
                        MLog.s("onStartCommand--init");
                        if (ilistener != null) {
                            MLog.s("onStartCommand--初始化回调");
                            ilistener.initback(bu.getString("status"));
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else if (bu.getString("flag").equals("login")) {// 处理登陆回调(has//
                    // key)
                    MLog.s("onStartCommand--login");
                    try {
                        if (ilistener != null) {
                            if(bu.getString("status").equals("0")){
                                Asdk.setLogiinState(true,intent);
                            }else {
                                Asdk.setLogiinState(false,intent);
                            }
                            if (publisher != null && publisher.startsWith("asdk_gamecenter")) {
                                ilistener.loginback(bu.getString("sessionid"), bu.getString("accountid"), bu.getString("status"), bu.getString("phone"));
                            } else {
                                ilistener.loginback(bu.getString("sessionid"), bu.getString("accountid"), bu.getString("status"), bu.getString("custominfo"));
                            }
                            app.getSharedPreferences("user_info", 0).edit().putString("pipaw_payerId", bu.getString("accountid")).putString("pipaw_sessId", bu.getString("sessionid")).commit();

                            // 保存登陆回调数据
                            String accountid = bu.getString("accountid");
                            if (accountid != null && !"".equals(accountid) && !"0".equals(accountid)) {
                                getGameArgs().setAccount_id(bu.getString("accountid"));
                                getGameArgs().setSession_id(bu.getString("sessionid"));
                            }


                            try {
                                if (publisher.startsWith("asdk")) {
                                    Asdk.reyunsetLogin(accountid);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else if (bu.getString("flag").equals("gamelogin")) {
                    final String server = bu.getString("server");
                    final String username = bu.getString("username");
                    final String sessionid = bu.getString("sessionid") == null ? "empty" : bu.getString("sessionid");
                    final String extend = bu.getString("extend") == null ? "" : bu.getString("extend");
                    final String customstring = bu.getString("callBackData");

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            JSONObject param = null;
                            String param_json = "";
                            try {
                                param = new JSONObject();
                                param.put("username", username);
                                param.put("sessionid", sessionid);
                                param.put("extend", extend);
                            } catch (JSONException e2) {
                                e2.printStackTrace();
                            }
                            param_json = param.toString();
                            MLog.a(tag, "param_json:" + param_json);
//							MLog.a("请求地址========"+server);

                            String s = HttpUtils.postMethod(server, param_json, "utf-8");

//							String s1 = HttpUtils.postMethod(server, "{\"username\":\"" + username + "\",\"sessionid\":" + "\"" + sessionid + "\",\"extend\":" + "\"" + extend+ "\"}", "utf-8");
//							MLog.a(tag,s);

                            JSONObject jo1;
                            try {
                                if (s.contains("exception")) {
                                    try {
                                        myhand.sendEmptyMessage(200);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    jo1 = new JSONObject(s);
                                    if ("0".equals(jo1.getString("code"))) {
                                        JSONObject jo2 = jo1.getJSONObject("data").getJSONObject("account");
//										sendBroadcast(new Intent("fly.fish.aidl.MyRemoteService.MYBROADCAST.LOGIN").putExtra("extdata1", jo2.getString("extdata1")).putExtra("extdata2", jo2.getString("extdata2")));
                                        app.getSharedPreferences("user_info", 0).edit().putString("pipaw_payerId", jo2.getString("accountid")).putString("pipaw_sessId", jo2.getString("sessionid")).putBoolean("islogin", true).commit();
                                        try {
                                            ilistener.loginback(jo2.getString("sessionid"), jo2.getString("accountid"), jo1.getString("code"), customstring);
                                            // 保存登陆回调数据
                                            getGameArgs().setAccount_id(jo2.getString("accountid"));
                                            getGameArgs().setSession_id(jo2.getString("sessionid"));
                                            app.getSharedPreferences("user_info", 0).edit().putString("asdk_accountid", jo2.getString("accountid")).commit();
                                            app.getSharedPreferences("user_info", 0).edit().putString("asdk_sessionid", jo2.getString("sessionid")).commit();
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        myhand.sendMessage(new Message().obtain(myhand, 200, jo1.getString("msg")));
                                    }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }).start();

                } else if (bu.getString("flag").equals("pay")) {// 处理充值回调(haskey)
                    try {
                        if (ilistener != null) {
                            MLog.s("onStartCommand--充值回调");
                            ilistener.payback(bu.getString("msg"), bu.getString("status"), bu.getString("sum"), bu.getString("chargetype"), bu.getString("customorderid"), bu.getString("custominfo"));
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (bu.getString("status").equals("0")){
                            SkipActivity.reyunandttsetPay(bu.getString("msg"),bu.getString("customorderid"), bu.getString("chargetype"), bu.getString("sum"),true);
                        }else {
                            SkipActivity.reyunandttsetPay(bu.getString("msg"),bu.getString("customorderid"), bu.getString("chargetype"), bu.getString("sum"),false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (bu.getString("flag").equals("getOrder")) {

                } else if (bu.getString("flag").equals("sec_confirmation")) {// 二次验证
                    final String server = app.getSharedPreferences("user_info", 0).getString(("payserver"), "") + "gameparam=sec_confirmation";
                    final String exorderno = bu.getString("merchantsOrder");
                    final String desc = bu.getString("desc");
                    final String account = bu.getString("account");
                    final String merchantsOrder = exorderno.indexOf("@") == -1 ? exorderno : exorderno.substring(0, exorderno.indexOf("@"));
                    final String callBackData = bu.getString("callBackData");

                    MLog.a(tag, exorderno + "**************************************6" + bu.getString("merchantsOrder"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 6; i++) {
                                String conf_info = HttpUtils.postMethod(server, "{\"exorderno\":\"" + exorderno + "\"}", "utf-8");
                                MLog.a(tag, "------------------------" + conf_info);
                                try {
                                    if (conf_info.contains("exception")) {
                                        try {
                                            myhand.sendEmptyMessage(201);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        MLog.s("onStartCommand--充值回调");
                                        JSONObject order = new JSONObject(conf_info);
                                        if ("0".equals(order.getString("code"))) {
                                            if (ilistener != null) {
                                                if ("1".equals(order.getString("data"))) {
                                                    ilistener.payback(desc, "0", account, "pay", merchantsOrder, callBackData);
                                                    //应用宝热云,广点通支付回调
                                                    if (publisher != null && (publisher.startsWith("qqsdk") || (publisher.startsWith("asdk")))) {
                                                        try {
                                                            SkipActivity.reyunandttsetPay(desc,merchantsOrder, "weixinpay", account,true);
                                                        } catch (Exception e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                        try {
//															GDTSDK.purchase();
                                                        } catch (Exception e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    break;
                                                }
                                                if ("0".equals(order.getString("data"))) {
                                                    ilistener.payback(desc, "1", account, "pay", merchantsOrder, callBackData);
                                                    break;
                                                }
                                                if ("-1".equals(order.getString("data"))) {
                                                    ilistener.payback(desc, "2", account, "pay", merchantsOrder, callBackData);
                                                }
                                            }
                                        } else {
                                            myhand.sendMessage(new Message().obtain(myhand, 201, order.getString("msg")));
                                        }
                                    }
                                    Thread.sleep(1000);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                } else if (bu.getString("flag").equals("qq_confirmation")) {// 应用宝支付确认
                    final String qq_orderid = bu.getString("qq_orderid");
                    final String qq_amt = bu.getString("qq_amt");
                    final String qq_zoneid = bu.getString("qq_zoneid");
                    final String qq_openid = bu.getString("qq_openid");
                    final String qq_openkey = bu.getString("qq_openkey");
                    final String qq_url = bu.getString("qq_url");
                    final String extend = bu.getString("extend");
                    final String payon = (publisher != null && publisher.startsWith("cmqqsdk")) ? bu.getString("payon", "") : "";

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 1; i++) {
                                JSONObject param_js = new JSONObject();
                                try {
                                    param_js.put("orderid", qq_orderid);
                                    param_js.put("amt", qq_amt);
                                    param_js.put("zoneid", qq_zoneid);
                                    param_js.put("openid", qq_openid);
                                    param_js.put("openkey", qq_openkey);
                                    param_js.put("extend", extend);
                                    param_js.put("payon", payon);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                                String param = param_js.toString();
                                final String result = HttpUtils.postMethod(qq_url, param, "utf-8");
                                MLog.a(tag, result);

                                try {
                                    if (result.contains("exception")) {
                                        MLog.a(tag, "-----------pay--exception-------------");
                                    } else {
                                        JSONObject data = new JSONObject(result);
                                        if ("0".equals(data.getString("code"))) {
                                            intent.setClass(MyApplication.context, MyRemoteService.class);
                                            bu.putString("flag", "sec_confirmation");
                                            intent.putExtras(bu);
                                            MyApplication.context.startService(intent);
                                            break;
                                        } else {
                                            MLog.a(tag, "-----------pay--failed-------------");
                                            String desc = bu.getString("desc");
                                            String account = bu.getString("account");
                                            String merchantsOrder = bu.getString("merchantsOrder");
                                            String callBackData = bu.getString("callBackData");
                                            try {
                                                ilistener.payback(desc, "1", account, "pay", merchantsOrder, callBackData);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        }
                                    }
                                    Thread.sleep(8000);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                } else if (bu.getString("flag").equals("get_qqvipurl")) {// 获取qq会员授权页
                    final String url = bu.getString("url");
                    final String extend = bu.getString("extend");
//                    new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            JSONObject param_js = new JSONObject();
//                            try {
//                                param_js.put("extend", extend);
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//
//                            String param = param_js.toString();
//                            final String result = HttpUtils.postMethod(url, param, "utf-8");
//                            MLog.a(tag, result);
//
//                            try {
//                                if (result.contains("exception")) {
//                                    MLog.a(tag, "-----------pay--exception-------------");
//                                } else {
//                                    JSONObject data = new JSONObject(result);
//                                    if ("0".equals(data.getString("code"))) {
//                                        QQSDK2.m_qqvipurlcallback.callback(0, data.getJSONObject("data").getString("qqvipurl"));
//                                    } else {
//                                        QQSDK2.m_qqvipurlcallback.callback(1, data.getString("msg"));
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
                } else if (bu.getString("flag").equals("getCardPayInfo")) {//获取电话卡支付信息
                    final String server = app.getSharedPreferences("user_info", 0).getString(("payserver"), "") + "gameparam=getCardPayInfo";
                    String sum = bu.getString("account");
                    String callbackurl = bu.getString("url");
                    String customorderid = bu.getString("merchantsOrder");
                    String custominfo = bu.getString("callBackData");
                    JSONObject param_js = new JSONObject();
                    try {
                        param_js.put("sum", sum);
                        param_js.put("callbackurl", callbackurl);
                        param_js.put("customorderid", customorderid);
                        param_js.put("custominfo", custominfo);
                        param_js.put("account", getGameArgs().getAccount_id());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    final String param = param_js.toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//							String cardPayInfo = HttpUtils.postMethod(server, param, "utf-8");
//							MLog.a(tag,cardPayInfo);
//							if (cardPayInfo.contains("exception")) {
//								CardRechargeActivity.reqResult(1,"");
//							} else {
//								CardRechargeActivity.reqResult(0,cardPayInfo);
//							}
                        }
                    }).start();
                } else if (bu.getString("flag").equals("update")) {// 更新头文件（SV升级）(has
                    // key)

                    MLog.s("onStartCommand--更新头文件");
                    try {
                        myhand.sendEmptyMessage(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (bu.getString("flag").equals("curkey")) {// 得到当前KEY(has
                    // key)

                    app.curKey = bu.getString("key");
                    MLog.s("onStartCommand--得到当前KEY ---> " + app.curKey);

                } else if (bu.getString("flag").equals("getmypid")) {// 得到当前进程PID(has
                    // not
                    // key)
                    mypid = bu.getInt("mypid");
                    MLog.s("onStartCommand--得到当前进程PID---> " + mypid);

                } else if (bu.getString("flag").equals("gameinfo")) {
                    final String gameinfo = bu.getString("gameinfo");
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            HttpUtils.postMethod(MyApplication.context.getSharedPreferences("user_info", 0).getString("accountserver", "") + "gameparam=usergameinfo", gameinfo, "utf-8");
                        }
                    }).start();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * -2
     */
    @Override
    public boolean onUnbind(Intent intent) {
        MLog.a(tag, "Service ---- onUnbind");
        // return super.onUnbind(intent);

        return true;
    }

    /**
     * -1
     */
    @Override
    public void onDestroy() {
        MLog.a(tag, "Service ---- onDestroy");
        super.onDestroy();
        if (broadcast != null) {
            unregisterReceiver(broadcast);
            broadcast = null;
        }

        gamemap.clear();
        callbacks.clear();

        ht.quit();
//		app.exit(mypid);
    }

    @Override
    public void onRebind(Intent intent) {
        MLog.a(tag, "Service ---- onRebind");
        super.onRebind(intent);
    }

    /**
     * 获得当前游戏实体
     *
     * @return
     */
    public GameArgs getGameArgs() {
        return gamemap.get(app.curKey);
    }

    public ITestListener getIlistener() {
        return ilistener;
    }

    public void setIlistener(ITestListener ilistener) {
        this.ilistener = ilistener;
    }

    /**
     * 广播接口类 可以在其他地方操作广播
     *
     * @author Administrator
     */
    public class MyBroadCast extends BroadcastReceiver {
        /**
         * 主Context
         */
        public Context context;

        public MyBroadCast(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (intent.getAction().equals(MyRemoteService.MY_ACTION)) {

            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
                MLog.a(tag, "服务内有应用被替换" + intent.getPackage());
                if (packageName.equals(MyApplication.context.getPackageName())) {
                    String gamenumber = MyApplication.getAppContext().getGameArgs().getPrefixx();
                    File file = new File(Configs.ASDKROOT + gamenumber);
                    FilesTool.deleteCraFile(file);
                }
            }
        }
    }

    /**
     * 接口服务类
     *
     * @author Administrator
     */
    public class TestImpl extends IMyTaskBinder.Stub {
        /**
         * 服务类
         */
        private MyRemoteService service;
        private String str = "plesea init game args";

        public TestImpl(MyRemoteService service) {
            this.service = service;
        }

        @Override
        public void registerCallBack(ITestListener listener, String key) throws RemoteException {
            MLog.s(key + " registerCallBack ----> " + listener);
            setIlistener(listener);
            callbacks.put(key, ilistener);
        }

        /**
         * 接收游戏参数，走更新协议，置位标志位 其实初始化多遍也无所谓，只是改成员而已
         */
        @Override
        public void init(String cpid, String gameid, String key, String name) throws RemoteException {
            app.curKey = key;
            GameArgs gameargs = new GameArgs();
            if (app.getGamemap().containsKey(key)) {
                gamemap.remove(key);
            }
            setIlistener(callbacks.get(app.curKey));

            gameargs.setCpid(cpid);
            gameargs.setGameno(gameid);
            gameargs.setKey(key);
            gameargs.setName(name);
            gameargs.setPublisher(FilesTool.getPublisherString()[0]);

            MLog.s("service Publisher -------> " + gameargs.getPublisher());
            MLog.s("service cpidid -------> " + cpid);
            MLog.s("service gameid -------> " + gameid);
            MLog.s("service gamekey-------> " + key);
            MLog.s("service gamename------> " + name);

            gamemap.put(app.curKey, gameargs);

            // 更新的时候取的是Application的gameargs
            app.setGameArgs(gameargs);

            // 检测更新,有则更新，无则不更新
            try {
                service.myhand.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyApplication.context.getSharedPreferences("user_info", 0).edit().putBoolean("islogin", false).commit();
        }

        /**
         * 登陆接口
         */
        @Override
        public void login(String self, String key) throws RemoteException {
            // String string = AESSecurity.encryptionResult(par);
            /*
             * app.curKey = key; GameArgs gameargs = getGameArgs();
             *
             * if (gameargs!=null && gameargs.isInit()) {
             *
             * gameargs.setSelf(self); Intent itn = new Intent(service,
             * LoginActivity.class);
             *
             * itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             * itn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); Bundle bu = new
             * Bundle(); bu.putParcelable("gameargs", gameargs);
             *
             * bu.putString("re1", re1); bu.putString("re2", re2);
             * bu.putString("re3", re3);
             *
             * MLog.s("URLURL1 login-----> " + re1 );
             * MLog.s("URLURL2 login-----> " + re2 );
             * MLog.s("URLURL3 login-----> " + re3 );
             *
             * itn.putExtras(bu); startActivity(itn);
             *
             * app.setGameArgs(gameargs);
             *
             * } else { ilistener.loginback(str, str, str, str); }
             */

        }

        /**
         * 充值接口
         */
        @Override
        public void pay(String order, String url, String sum, String desc, String self, String key) throws RemoteException {
            /*
             * app.curKey = key; GameArgs gameargs = getGameArgs(); ilistener =
             * callbacks.get(app.curKey);
             *
             * if (gameargs!=null && gameargs.isInit()) {
             * gameargs.setSelf(self); gameargs.setCustomorderid(order);
             * gameargs.setCallbackurl(url); gameargs.setSum(sum);
             * gameargs.setDesc(desc);
             *
             * Intent itn = new Intent(service, ChargeActivity.class);
             *
             * itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.
             * FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
             *
             * Bundle bu = new Bundle(); bu.putParcelable("gameargs", gameargs);
             *
             * bu.putString("re1", re1); bu.putString("re2", re2);
             * bu.putString("re3", re3);
             *
             * MLog.s(app.curKey + " URLURL1 pay-----> " + re1 );
             * MLog.s(gameargs.getCpid()+" URLURL2 pay-----> " + re2 );
             * MLog.s(gameargs.getGameno()+" URLURL3 pay-----> " + re3 );
             *
             * itn.putExtras(bu); startActivity(itn);
             *
             * app.setGameArgs(gameargs); } else { ilistener.payback(str, str,
             * str,str, str, str); }
             */
        }

        /**
         * 查询接口
         */
        @Override
        public void query(String order, String self, String key) throws RemoteException {
            app.curKey = key;
            GameArgs gameargs = getGameArgs();
            setIlistener(callbacks.get(app.curKey));

            if (gameargs != null && gameargs.isInit()) {

                gameargs.setSelf(self);
                gameargs.setCustomorderid(order);

                String url = "";
                String par = "";
                LuaState myLuaState = MyApplication.getAppContext().getmLuaState();
                synchronized (myLuaState) {
                    FilesTool.loadLuaScript("lua/check_charge_result.lua");
                    myLuaState.getField(LuaState.LUA_GLOBALSINDEX, "queryOrder");
                    myLuaState.pushString(order);// 压入第一个参数
                    LuaTools.dbcall(myLuaState, 1, 2);// 代表两个参数，0个返回值

                    url = myLuaState.toString(-2);
                    par = myLuaState.toString(-1);
                }

                // 检测更新,有则更新，无则不更新
                Message mes = new Message();
                mes.obj = url + "|" + par;
                mes.what = 1;
                service.myhand.sendMessage(mes);

                app.setGameArgs(gameargs);

            } else {
                ilistener.queryback(str, str, str, str);
            }

        }

        @Override
        public void quit() throws RemoteException {
            MLog.s("quit");

            /** 清除已成功初始化标志 **/
            // ContentResolver contentResolver = app.getContentResolver();
            // Uri url = Uri.parse("content://" + ShareContent.PROVIDER_URL +
            // "/config/2");
            // contentResolver.delete(url, "gamekey=" + "\'" + key + "\'",
            // null);

            // android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
