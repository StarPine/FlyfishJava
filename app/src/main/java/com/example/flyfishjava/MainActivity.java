package com.example.flyfishjava;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import fly.fish.aidl.CallBackListener;
import fly.fish.aidl.OutFace;
import fly.fish.tools.MLog;

public class MainActivity extends Activity {

    private OutFace out;
    private boolean isinit;
    private boolean hasExitBox;

//    private String cpid = "100079";
//    private String gameid = "100122";
//    private String gamekey = "12fhd5748sasuh47";
//    private String gamename = "test";
//
//    private String cpid = "100079";
//    private String gameid = "100971";
//    private String gamekey = "0bd7ed3fe56c1393";
//    private String gamename = "szwl";

    private String cpid = "100079";
    private String gameid = "100910";
    private String gamekey = "bfe6f3bfd4415423";
    private String gamename = "pfdmw2";


    //余额，角色id，帮派，VIP等级，服务器名称，角色等级，服务器id，角色名称，阵营（若没有可不传）
    String userinfo = "{\"ingot\":\"1000\",\"playerId\":\"0001040C0000002A\",\"factionName\":\"丐帮\",\"vipLevel\":\"20\",\"serverName\":\"五虎上将\",\"playerLevel\":\"200\",\"serverId\":\"100\",\"playerName\":\"赖瑾萱\",\"campId\":\"3\"}";

    public OutFace.FlyFishSDK callback = new OutFace.FlyFishSDK() {

        @Override
        public void initback(String status) throws RemoteException {
            System.out.println("initback ----> " + status);
            if ("0".equals(status)){
                isinit = true;
                out.login(MainActivity.this, "myself", gamekey);
            }else {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                System.out.println("初始化失败");
            }
        }

        @Override
        public void loginback(String sessionid, String accountid, String status, String customstring) throws RemoteException {
            System.out.println("loginback ----> " + sessionid + " = " + accountid + " = " + status + " = " + customstring);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
            if ("0".equals(status))
                handler.sendEmptyMessage(0);
            else if("2".equals(status)){//帐号注销
                handler.sendEmptyMessage(1);
            }else{
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void payback(String msg, String status, String sum, String chargetype, String customorgerid, String customstring) throws RemoteException {
            System.out.println("payback ----> " + msg + " = " + status + " = " + sum + "=" + chargetype + " = " + customorgerid + " = " + customstring);
        }

        @Override
        public void queryback(String status, String sum, String chargetype, String customstring) throws RemoteException {
            System.out.println("queryback ----> " + status + " = " + sum + "=" + chargetype + " = " + customstring);
        }

    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int main = MainActivity.this.getResources().getIdentifier("main", "layout", MainActivity.this.getPackageName());
            int first = MainActivity.this.getResources().getIdentifier("first", "layout", MainActivity.this.getPackageName());
            switch (msg.what) {
                case 0:
                    out.outInGame(userinfo);//传入角色信息
                    MainActivity.this.setContentView(main);
                    break;
                case 1:
                    MainActivity.this.setContentView(first);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐去标题栏（应用程序的名字）
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        out = OutFace.getInstance(this);
        out.setDebug(true);
        out.outInitLaunch(this, true, new CallBackListener() {

            @Override
            public void callback(int code, boolean isHasExitBox) {
                //code:0成功,1失败;isHasExitBox:true有退出框,false:无退出框
                handler.sendEmptyMessage(1);
                hasExitBox = isHasExitBox;

            }
        });
        //out.onSaveInstanceState(MainActivity.this,savedInstanceState);
        out.outOnCreate(MainActivity.this);
        out.outOnCreate(MainActivity.this,savedInstanceState);
        out.callBack(callback, gamekey);
    }


    public static String getAppKey(Activity context) {
        String key = "";
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            key = appInfo.metaData.getInt("PA_APP_KEY") + "";

        } catch (ClassCastException e) {
            //maybe vender is not the user of 1.0.
            try {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                key = appInfo.metaData.getString("PA_APP_KEY");
            } catch (PackageManager.NameNotFoundException e1) {
//                e1.printStackTrace();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if ("0".equals(key)) {
            try {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                key = appInfo.metaData.getString("PA_APP_KEY");
            } catch (Exception e1) {
//                e1.printStackTrace();
            }
        }

        return key;
    }

    @Override
    protected void onResume() {
        super.onResume();
        out.outOnResume(this);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        out.outRestart(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        out.outOnPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        out.outOnStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        out.outDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        out.outActivityResult(this, requestCode, resultCode, data);
    }

    private ProgressDialog dialog = null;

    /**
     * 登录
     * @param view
     */
    public void login(View view) throws RemoteException {

        String appkey= getAppKey(this);
        MLog.a("appkey---------"+appkey);
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("请求中...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }
        dialog.show();
        if(isinit){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    out.login(MainActivity.this, "myself", gamekey);
                }
            });

        }else{
            out.init(cpid, gameid, gamekey, gamename);
        }
    }

    /**
     * 充值
     * @param view
     */
    public void pay(View view) throws RemoteException {
        if (isValidHits()) {
            if (isinit) {
                //订单号，回调URL，充值金额，商品描述，游戏自定义参数

                out.pay(this, System.currentTimeMillis() + "", "", "0.01","12","1元礼包", "myself", gamekey);
            } else {
                out.init(cpid, gameid, gamekey, gamename);
            }
        }
    }

    protected long lastClickTime;

    // 有效点击事件
    // 3秒内重复点击无效
    protected boolean isValidHits() {
        if (System.currentTimeMillis() - lastClickTime > 3000) {
            lastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(hasExitBox){//调用sdk的退出框
            out.outQuit(this);
        }else{
            //调用游戏自带的退出框
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您确定退出游戏吗？");
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //执行游戏退出
                            out.outQuit(MainActivity.this);
                            MainActivity.this.finish();
                            System.exit(0);
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }
}