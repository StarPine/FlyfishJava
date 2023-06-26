package fly.fish.aidl;

import java.util.ArrayList;
import java.util.List;

import org.keplerproject.luajava.LuaState;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;
import fly.fish.asdk.AsdkActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.HttpUtils;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

public class MyServiceHandler extends Handler {
    /** 我的服务 */
    public MyRemoteService service;
    /** 我的应用 */
    public MyApplication app;
    private List<String> name = null;
    private List<String> url = null;
    public String tag = "MyServiceHandler";

    public MyServiceHandler(Looper looper, MyRemoteService service) {
        super(looper);
        this.service = service;
        this.app = MyApplication.getAppContext();
        name = new ArrayList<String>();
        url = new ArrayList<String>();
    }

    /**
     * 处理消息
     */
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case 0:// 升级来也
               // 解析头
            app.parseData();
            // 加载文件（远程服务重新加载）
            LuaTools.loadUpdate();
            // 检查更新，请求url
            checkup();

            break;

        case 1:// 订单查询
            String str = msg.obj.toString();
            String urls = str.split("\\|")[0];
            String params = str.split("\\|")[1];
            MLog.s(" query url -------> " + urls);
            String s = HttpUtils.postMethod(urls, params, "utf-8");
            MLog.s(" query result -------> " + s);
            String data = "1";
            String sum = "1";
            String chargeType = "";
            if (s.contains("error") || s.contains("exception")) {
                if (service.getIlistener() != null) {
                    try {

                        service.getIlistener().queryback(data, "null", "null",
                                service.getGameArgs().getSelf());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                synchronized (app.getmLuaState()) {
                    // check_charge_result.lua
                    app.getmLuaState().getField(LuaState.LUA_GLOBALSINDEX,
                            "querybackjson");
                    app.getmLuaState().pushString(s);
                    LuaTools.dbcall(app.getmLuaState(), 1, 3);// 代表一个参数，3个返回值
                    data = app.getmLuaState().toString(-3); // 状态
                    sum = app.getmLuaState().toString(-2);
                    chargeType = app.getmLuaState().toString(-1);
                }

                if (service.getIlistener() != null) {
                    try {
                        service.getIlistener().queryback(data, sum, chargeType,
                                service.getGameArgs().getSelf());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            break;
        case 2:// 更新头文件
            app.initLuaState();

            // 解析头
            app.parseData();

            // 加载文件
            LuaTools.loadUpdate();

            // 检查更新，请求url
            checkup();

            break;

        case 3:
            checkup();// 没拿到地址，重新请求
            break;
        case 200:
            try {
                service.getIlistener().loginback("", "", "1", "");
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        case 201:
            if (msg.obj != null) {
                Toast.makeText(MyApplication.context, msg.obj.toString(),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyApplication.context, "网络数据异常",
                        Toast.LENGTH_SHORT).show();
            }

            break;
        default:
            break;
        }
    }

    /**
     * 检查更新，请求url
     */
    public void checkup() {
        name.clear();
        url.clear();

        // 检查是否要更新
        synchronized (app.getmLuaState()) {
            String str = "success";
            app.getmLuaState().getGlobal("update");
            int index = app.getmLuaState().getTop();
            app.getmLuaState().getField(index, "checkup");
            LuaTools.dbcall(app.getmLuaState(), 0, 1);
            // 取检查结果
            int re = app.getmLuaState().toInteger(-1);

            app.getmLuaState().getGlobal("update");
            index = app.getmLuaState().getTop();
            app.getmLuaState().getField(index, "getaddr");
            LuaTools.dbcall(app.getmLuaState(), 0, 1);

            app.getmLuaState().getGlobal("update");
            index = app.getmLuaState().getTop();
            app.getmLuaState().getField(index, "geturls");
            LuaTools.dbcall(app.getmLuaState(), 0, 9);

            String othersdkextdata1, othersdkextdata2, othersdkextdata3, othersdkextdata4, othersdkextdata5;

            // 取检查结果（请求的url）
            service.re4 = app.getmLuaState().toString(-6);// BBS
            service.re1 = app.getmLuaState().toString(-7);// 回调
            service.re2 = app.getmLuaState().toString(-8);// 充值
            service.re3 = app.getmLuaState().toString(-9);// 帐号

            // 扩展参数
            othersdkextdata1 = app.getmLuaState().toString(-5);
            othersdkextdata2 = app.getmLuaState().toString(-4);
            othersdkextdata3 = app.getmLuaState().toString(-3);
            othersdkextdata4 = app.getmLuaState().toString(-2);
            othersdkextdata5 = app.getmLuaState().toString(-1);

            MLog.a(tag,
                    "==========================getaddr by java==========================");
            MLog.a(tag, "alipay_cb_url = " + service.re1);
            MLog.a(tag, "payserver = " + service.re2);
            MLog.a(tag, "accountserver = " + service.re3);
            MLog.a(tag, "bbs_url = " + service.re4);

            /**
             * 没拿到地址，重新请求
             */
            if (service.re1 == null || service.re1.equals("")) {
                service.getGameArgs().setInit(false);
                try {
                    ASDKReport.getInstance().startSDKReport(MyApplication.context, EventManager.SDK_EVENT_INIT_FAIL);
                    service.getIlistener().initback("2");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                // sendEmptyMessage(3);
            } else {

                SharedPreferences sharedPreferences = MyApplication.context
                        .getSharedPreferences("user_info", 0);
                sharedPreferences.edit()
                        .putString("gamekey", service.app.curKey).commit();
                MLog.a(tag, "gamekey" + "======init========"
                        + service.app.curKey);
                sharedPreferences.edit()
                        .putString("accountserver", service.re3).commit();
                MLog.a(tag, "accountserver" + "=====init========="
                        + service.re3);
                sharedPreferences.edit().putString("payserver", service.re2)
                        .commit();
                MLog.a(tag, "payserver" + "=======init=======" + service.re2);
                sharedPreferences.edit().putString("notify_url", service.re1)
                        .commit();
                MLog.a(tag, "notify_url" + "=======init=======" + service.re1);
                sharedPreferences.edit().putString(service.app.curKey, str)
                        .commit();
                MLog.a(tag, service.app.curKey + "====init==========" + str);
                sharedPreferences.edit()
                        .putString("othersdkextdata1", othersdkextdata1)
                        .commit();
                MLog.a(tag, "othersdkextdata1" + "=======init======="
                        + othersdkextdata1);
                sharedPreferences.edit()
                        .putString("othersdkextdata2", othersdkextdata2)
                        .commit();
                MLog.a(tag, "othersdkextdata2" + "=======init======="
                        + othersdkextdata2);
                sharedPreferences.edit()
                        .putString("othersdkextdata3", othersdkextdata3)
                        .commit();
                MLog.a(tag, "othersdkextdata3" + "======init========"
                        + othersdkextdata3);
                sharedPreferences.edit()
                        .putString("othersdkextdata4", othersdkextdata4)
                        .commit();
                MLog.a(tag, "othersdkextdata4" + "=======init======="
                        + othersdkextdata4);
                sharedPreferences.edit()
                        .putString("othersdkextdata5", othersdkextdata5)
                        .commit();
                MLog.a(tag, "othersdkextdata5" + "======init========"
                        + othersdkextdata5);

                if (re == 2 || re == 3) {// BV3或SV2升级
                    // 用作更新检测类

                    service.getGameArgs().setInit(true);

                    Intent itn = new Intent(service, AsdkActivity.class);
                    itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bu = new Bundle();
                    bu.putParcelable("gameargs", service.getGameArgs());
                    bu.putInt("result", re);
                    itn.putExtras(bu);
                    service.startActivity(itn);

                } else if (re == 4) {// 不需要更新
                    MLog.s("servicehandler---success----re ==不需要更新");
                    service.getGameArgs().setInit(true);

                    if (service.getIlistener() != null) {
                        try {
                            MLog.s("servicehandler---success----re == 4");
                            ASDKReport.getInstance().startSDKReport(MyApplication.context, EventManager.SDK_EVENT_INIT_SUCCESS);
                            service.getIlistener().initback("0");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MLog.s("servicehandler---failure-----re == 4");
                    }
                } else if (re == 5) {// 游戏ID不正确(就是更新服上没有)

                    service.getGameArgs().setInit(false);

                    if (service.getIlistener() != null) {
                        try {
                            MLog.s("servicehandler---success----re == 5");
                            ASDKReport.getInstance().startSDKReport(MyApplication.context, EventManager.SDK_EVENT_INIT_FAIL);
                            service.getIlistener().initback("1");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MLog.s("servicehandler---failure-----re == 5");
                    }
                    str = "wrongid";
                } else {// 网络异常，不处理re==0 or re==1

                    app.getGameArgs().setInit(false);
                    if (service.getIlistener() != null) {
                        try {
                            MLog.s("servicehandler---success----else");
                            ASDKReport.getInstance().startSDKReport(MyApplication.context, EventManager.SDK_EVENT_INIT_FAIL);
                            service.getIlistener().initback("2");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MLog.s("servicehandler---failure-----else");
                    }
                    str = "neterror";

                }
            }
        }
    }
}
