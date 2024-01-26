package fly.fish.dialog;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fly.fish.asdk.SkipActivity;
import fly.fish.config.Configs;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.AsdkOAIDManager;
import fly.fish.tools.JsonUtils;
import fly.fish.tools.MLog;
import fly.fish.tools.ManifestInfo;
import fly.fish.tools.PhoneTool;


public class PrivacyActivity extends Activity {

    String state = "";
    String qx_url = "";
    String ys_url = "";
    String yh_url = "";
    String oaidKey = "";
    private boolean isAgree;
    private boolean isShowDialog = true;
    private String updateUrl = "http://update.xxhd-tech.com:8082/getupdatestate.php?";
    private String asdkPublisher;
    private SharedPreferences sharedPreferences;
    private String IS_SHOWED = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean enableLogger = intent.getBooleanExtra("enable_logger", false);
        MLog.enableLogger(enableLogger);
        IS_SHOWED = "isShowed" + PhoneTool.getVersionName(PrivacyActivity.this) + "-" + PhoneTool.getVersionCode(PrivacyActivity.this);
        sharedPreferences = getSharedPreferences("asdk", MODE_PRIVATE);
        isShowDialog = ManifestInfo.getMetaBoolean(this, "PRIVACY_SHOW_STATUS", true);
        if (isShowDialog) {
            requestConfig();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
            startGameActivity();
        }


    }

    private void requestConfig() {
        new Thread(() -> {
            asdkPublisher = DialgTool.getpub("AsdkPublisher.txt");
            String address = DialgTool.getpub("address.txt");
            String data = DialgTool.getWebMethod(address + asdkPublisher + "&versionName=" + PhoneTool.getVersionName(PrivacyActivity.this));
            MLog.a("--------json------" + data);

            state = JsonUtils.getString(data,"state");
            qx_url = JsonUtils.getString(data,"qxurl");
            ys_url = JsonUtils.getString(data,"ysurl");
            yh_url = JsonUtils.getString(data,"yhurl");
            oaidKey = JsonUtils.getString(data,"oakey");

            Configs.enableToutiaoReport = JsonUtils.getBoolean(data,"istt", false);
            Configs.isEnableRequestPermission = JsonUtils.getBoolean(data,"isrequ", false);
            Configs.isEnableFormalMode = JsonUtils.getBoolean(data,"ischeck", false);
            Configs.isEnableOneKeyLogin = JsonUtils.getBoolean(data,"jgcheck", false);
            Configs.qqContactWay = JsonUtils.getString(data,"smkf");
            initPrivacy();

        }).start();
    }

    private void initPrivacy() {
        runOnUiThread(() -> {
            boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

            if (isFirstRun && state.equals("1")) { // 第一次则跳转到引导页面
                showPrivacyDialog();
            } else if (!isFirstRun || state.equals("0")) { // 如果是第二次启动则直接跳转到主页面
                loadUpdateData();
            }
        });
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
            String updateTitle = JsonUtils.getString(updateData,"updateTitle");
            String updateContent = JsonUtils.getString(updateData,"updateContent");
            int updateStatus = JsonUtils.getInt(updateData,"updateStatus", 0);
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
                    noAgainShowUpdate();

                    SkipActivity.update(PrivacyActivity.this);
                    updateDialog.dismiss();
                    finish();
                    System.exit(0);
                }

                @Override
                public void doCancel() {
                    updateDialog.dismiss();
                    startGameActivity();
                    noAgainShowUpdate();
                }
            });
            updateDialog.show();
        });

    }

    private void noAgainShowUpdate() {
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
                isAgree = true;
                loadUpdateData();
                MLog.a("同意协议-----------");
//                startGameActivity();
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
        AsdkOAIDManager oaidManager = new AsdkOAIDManager(this,oaidKey,isAgree);
        oaidManager.manageOAID();
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
