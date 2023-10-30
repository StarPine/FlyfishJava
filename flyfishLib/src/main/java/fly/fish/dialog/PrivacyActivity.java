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

import fly.fish.aidl.OutFace;
import fly.fish.asdk.SkipActivity;
import fly.fish.config.Configs;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.JsonUtils;
import fly.fish.tools.MLog;
import fly.fish.tools.ManifestInfo;
import fly.fish.tools.OthPhone;
import fly.fish.tools.PhoneTool;


public class PrivacyActivity extends Activity {

    String state = "";
    String qx_url = "";
    String ys_url = "";
    String yh_url = "";
    private boolean isShowDialog = true;
    private String updateUrl = "http://update.xxhd-tech.com:8082/getupdatestate.php?";
    private String asdkPublisher;
    private SharedPreferences sharedPreferences;
    private String IS_SHOWED = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        IS_SHOWED = "isShowed" + PhoneTool.getVersionName(PrivacyActivity.this) + "-" + PhoneTool.getVersionCode(PrivacyActivity.this);
        sharedPreferences = getSharedPreferences("asdk", MODE_PRIVATE);
        isShowDialog = ManifestInfo.getMetaBoolean(this, "PRIVACY_SHOW_STATUS", true);
        if (isShowDialog) {
            requestShowDialog();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
            startGameActivity();
        }


    }

    private void requestShowDialog() {
        Thread urlthred = new Thread(new Runnable() {

            @Override
            public void run() {
                asdkPublisher = DialgTool.getpub("AsdkPublisher.txt");
                String address = DialgTool.getpub("address.txt");
                String data = DialgTool.getWebMethod(address + asdkPublisher + "&versionName=" + PhoneTool.getVersionName(PrivacyActivity.this));
                MLog.a("--------json------" + data);
                Log.i("asdk", "versionName:" + PhoneTool.getVersionName(PrivacyActivity.this));

                JsonUtils jsonUtils = new JsonUtils(data);
                boolean isrequ = jsonUtils.getBoolean("isrequ",false);
                boolean ischeck = jsonUtils.getBoolean("ischeck",false);
                boolean oneLoginCheck = jsonUtils.getBoolean("jgcheck",false);

                OutFace.setCheckState(ischeck);
                OutFace.setOneLoginCheck(oneLoginCheck);
                OutFace.setisreq(isrequ);
                OthPhone.setisreq(isrequ);
                state = jsonUtils.getString("state");
                qx_url = jsonUtils.getString("qxurl");
                ys_url = jsonUtils.getString("ysurl");
                yh_url = jsonUtils.getString("yhurl");
                Configs.qqContactWay = jsonUtils.getString("smkf");
                MLog.a("--------请求完成------qx=" + qx_url + ";ys_url=" + ys_url + ";yh_url=" + yh_url);

            }
        });
        urlthred.start();

        try {
            urlthred.join();

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

                    if (isFirstRun && state.equals("1")) { // 第一次则跳转到引导页面
                        showPrivacyDialog();

                    } else if (!isFirstRun || state.equals("0")) { // 如果是第二次启动则直接跳转到主页面
                        loadUpdateData();
                    }

                }
            });

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadUpdateData() {
        new Thread(() -> {
            StringBuffer builder = new StringBuffer();
            builder.append(updateUrl)
                    .append("pub=").append(asdkPublisher)
                    .append("&versionName=").append(PhoneTool.getVersionName(PrivacyActivity.this))
                    .append("&versionCode=").append(PhoneTool.getVersionCode(PrivacyActivity.this))
                    .append("&packname=").append(PrivacyActivity.this.getPackageName());
            showUpdateDialog(DialgTool.getWebMethod(builder.toString()));
        }).start();

    }

    private void showUpdateDialog(String updateData) {
        runOnUiThread(() -> {
            JsonUtils jsonUtils = new JsonUtils(updateData);
            String updateTitle = jsonUtils.getString("updateTitle");
            String updateContent = jsonUtils.getString("updateContent");
            int updateStatus = jsonUtils.getInt("updateStatus", 0);
            boolean isShowed = sharedPreferences.getBoolean(IS_SHOWED, false);
            if (updateStatus == 0 || isShowed) {
                startGameActivity();
                return;
            }

            UpdateDialog updateDialog = new UpdateDialog(this, updateTitle, updateContent, updateStatus);
            updateDialog.setCancelable(false);// 点击返回键或者空白处不消失
            updateDialog.setClickListener(new UpdateDialog.ClickInterface() {
                @Override
                public void doCofirm() {

                    //运营需求：强更的状态下也不进行二次弹出
                    noLongerShowUpdate();

                    SkipActivity.update(PrivacyActivity.this);
                    updateDialog.dismiss();
                    finish();
                    System.exit(0);

                }

                @Override
                public void doCancel() {
                    updateDialog.dismiss();
                    startGameActivity();
                    noLongerShowUpdate();
                }
            });
            updateDialog.show();
        });

    }

    private void noLongerShowUpdate() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SHOWED, true);
        editor.apply();
    }

    private void showPrivacyDialog() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final PrivacyDialog privacyDialog = new PrivacyDialog(PrivacyActivity.this, yh_url, ys_url, qx_url,
                getResources().getIdentifier("MyDialog", "style", getPackageName()));

        privacyDialog.setCancelable(false);// 点击返回键或者空白处不消失
        privacyDialog.setClickListener(new PrivacyDialog.ClickInterface() {
            @Override
            public void doCofirm() {
                privacyDialog.dismiss();
                editor.putBoolean("isFirstRun", false);
                editor.commit();
                ASDKReport.getInstance().startSDKReport(PrivacyActivity.this, EventManager.SDK_EVENT_AGREE_PRIVACY);
                MLog.a("同意协议-----------");
//                startGameActivity();
                loadUpdateData();
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
            InputStream ins = getResources().getAssets().open("gameEntrance.txt");
            String gameEntrance = new BufferedReader(new InputStreamReader(ins)).readLine().trim();
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), gameEntrance);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
