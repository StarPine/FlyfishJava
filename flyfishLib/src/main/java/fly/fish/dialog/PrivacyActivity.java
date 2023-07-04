package fly.fish.dialog;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fly.fish.asdk.MyApplication;
import fly.fish.othersdk.OaidHelper;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;


public class PrivacyActivity extends Activity {

    String state = "";
    String qx_url = "";
    String ys_url = "";
    String yh_url = "";
    String oaidKey = "";
    private boolean isAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getConfig(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("asdk", MODE_PRIVATE);
            boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            if (isFirstRun && state.equals("1")) { // 第一次则跳转到引导页面
                showDialog(editor);

            } else if (!isFirstRun) { // 如果是第二次启动则直接跳转到主页面
                MLog.a("非第一次安装-----------");
                startGameActivity();

            } else if (state.equals("0")) { // 如果是第二次启动则直接跳转到主页面
                MLog.a("协议关闭-----------");
                startGameActivity();

            }
        });



    }

    private void getConfig(HttpCallback httpCallback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String asdkPublisher = DialgTool.getpub("AsdkPublisher.txt");
                String address = DialgTool.getpub("address.txt");
                MLog.a("--------pub------" + asdkPublisher);
                MLog.a("--------url------" + address);
                String json = DialgTool.getWebMethod(address + asdkPublisher);
                MLog.a("--------json------" + json);

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    state = jsonObject.getString("state");
                    qx_url = jsonObject.getString("qxurl");
                    ys_url = jsonObject.getString("ysurl");
                    yh_url = jsonObject.getString("yhurl");
                    oaidKey = jsonObject.getString("oakey");
                    MLog.a("--------请求完成------qx=" + qx_url + ";ys_url=" + ys_url + ";yh_url=" + yh_url);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (httpCallback != null) httpCallback.success();
                    }
                });
            }
        }).start();
    }

    public interface HttpCallback {
        void success();
    }

    private void showDialog(SharedPreferences.Editor editor) {
        final PrivacyDialog privacyDialog = new PrivacyDialog(PrivacyActivity.this, yh_url, ys_url, qx_url,
                getResources().getIdentifier("MyDialog", "style", getPackageName()));

        privacyDialog.setCancelable(false);// 点击返回键或者空白处不消失
        privacyDialog.setClickListener(new PrivacyDialog.ClickInterface() {
            @Override
            public void doCofirm() {
                privacyDialog.dismiss();
                editor.putBoolean("isFirstRun", false);
                editor.commit();
                isAgree = true;
                startGameActivity();
                MLog.a("同意协议-----------");
            }

            @Override
            public void doCancel() {
                ASDKReport.getInstance().startSDKReport(PrivacyActivity.this, EventManager.SDK_EVENT_REFUSE_PRIVACY);
                privacyDialog.dismiss();
                finish();
                System.exit(0);
            }
        });
        MLog.a("第一次安装-----------");
        privacyDialog.show();
        ASDKReport.getInstance().startSDKReport(this, EventManager.SDK_EVENT_SHOW_PRIVACY);
    }

    private void startGameActivity() {
        try {
            new OaidHelper(new OaidHelper.AppIdsUpdater() {
                @Override
                public void onIdsValid(String ids) {
                    Log.i("ASDK","OAID-----------"+ids);
                    int count_0 = getCount(ids, "0");
                    String spDeviceID = MyApplication.context.getSharedPreferences("user_info", 0).getString("device_id", "");
                    if(spDeviceID.equals("") && count_0 < 10){
                        Log.i("ASDK","配置OAID");
                        PhoneTool.setOAID(ids);
                    }
                    if (isAgree){
                        ASDKReport.getInstance().startSDKReport(PrivacyActivity.this, EventManager.SDK_EVENT_AGREE_PRIVACY);
                    }
                }
            }).getDeviceIds(PrivacyActivity.this,oaidKey);
            InputStream ins = getResources().getAssets().open("gameEntrance.txt");
            String gameEntrance = new BufferedReader(new InputStreamReader(ins)).readLine().trim();
            MLog.a("XwanSDK--gameEntrance------------>" + gameEntrance);
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), gameEntrance);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int getCount(String str, String key) {
        if (str != null && key != null && !"".equals(str.trim()) && !"".equals(key.trim())) {
            int count = 0;

            for(int index = 0; (index = str.indexOf(key, index)) != -1; ++count) {
                index += key.length();
            }

            return count;
        } else {
            return 0;
        }
    }


}
