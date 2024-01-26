package fly.fish.asdk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fly.fish.aidl.MyRemoteService;
import fly.fish.alipay.PartnerConfig;
import fly.fish.beans.GameArgs;
import fly.fish.http.RequestConfig;
import fly.fish.http.RequestUtils;
import fly.fish.tools.AppUtils;
import fly.fish.tools.JsonUtils;
import fly.fish.tools.ResUtils;
import fly.fish.tools.StringUtils;
import fly.fish.wechat.WXinSDK;

public class PaymentTypeDisplayActivity extends Activity {

    private TextView productDesc;
    private TextView productAmount;
    private ImageView checkWxpay;
    private ImageView checkAlipay;
    private static final int TYPE_WX_WEB_PAY = 23;//微信h5支付
    private static final int TYPE_ALI_WAP_PAY = 20;//阿里h5支付
    private static final int TYPE_ALI_PAY = 1;//阿里支付
    private int alipayId = 0;
    private int wechatPayId = 0;
    private int aliWappayId = 0;
    private int selectPayId = 0;
    private View itemAlipay;
    private View itemWxpay;
    private View payRootView;
    private Button confimPay;
    private ProgressBar progressBar;
    private int icon_checkbox_select;
    private int icon_checkbox_default;
    private String payserver;
    private String customorderid;
    private String callbackurl;
    private String sum;
    private String desc;
    private String callBackData;
    private String remark;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        View rootView = ResUtils.getRootView(this, "activity_pay", null);
        setContentView(rootView);
        initView(rootView);
        initListener();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        customorderid = bundle.getString("merchantsOrder");
        callbackurl = bundle.getString("url");
        sum = StringUtils.formatKeepTwo(bundle.getString("account"));
        desc = bundle.getString("desc");
        callBackData = bundle.getString("callBackData");

        confimPay.setText("确认支付 ￥" + sum);
        productDesc.setText(desc);
        productAmount.setText("￥ " + sum);

        GameArgs gameArgs = MyApplication.getAppContext().getGameArgs();
        gameArgs.setSelf(callBackData);
        gameArgs.setCustomorderid(customorderid);
        gameArgs.setCallbackurl(callbackurl);
        gameArgs.setSum(sum);
        gameArgs.setDesc(desc);
        accountId = gameArgs.getAccount_id();

        new Thread(() -> {
            SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
            payserver = sharedPreferences.getString("payserver", "");
            if (TextUtils.isEmpty(PartnerConfig.getSELLER2())) {
                requestPayInformation();
            }
            requestPaylist();
        }).start();
    }

    /**
     * 请求商品列表
     */
    private void requestPaylist() {
        String url = payserver + "gameparam=paylist";
        RequestConfig config = new RequestConfig(url, "");
        String result = RequestUtils.POSTEncrypt(config, false);

        JSONArray jsonArray = JsonUtils.getJSONArray(result, "data");
        int code = JsonUtils.getInt(result, "code");
        String msg = JsonUtils.getString(result, "msg");

        if (code == 0) {
            hideLoading();
            if (jsonArray == null || jsonArray.length() <= 0) {
                payCancelCallback();
                return;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = JsonUtils.getJSONObject(jsonArray, i);
                int payId = JsonUtils.getInt(jsonObject, "pay_id");
                String payName = JsonUtils.getString(jsonObject, "pay_name");
                switch (payId){
                    case TYPE_ALI_PAY:
                    case TYPE_ALI_WAP_PAY:
                        remark = JsonUtils.getString(jsonObject, "remark");
                        alipayId = payId;
                        selectPayId = alipayId;
                        break;
                    case TYPE_WX_WEB_PAY:
                        wechatPayId = payId;
                        break;
                }
            }
        } else if (code == -1) {
            showToast(msg);
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private void hideLoading() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (payRootView.getVisibility() == View.GONE) {
                payRootView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showToast(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, "错误信息:" + msg, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 开始支付
     *
     */
    private void startPay() {
        showLoading();
        switch (selectPayId) {
            case TYPE_WX_WEB_PAY:
            case TYPE_ALI_WAP_PAY:
                Map<String, Object> payInfo = new HashMap<>();
                payInfo.put("sum", sum);
                payInfo.put("callbackurl", callbackurl);
                payInfo.put("paytypeid", String.valueOf(selectPayId));
                payInfo.put("customorderid", customorderid);
                payInfo.put("accountid", accountId);
                payInfo.put("custominfo", callBackData);
                payInfo.put("desc", desc);
                payInfo.put("bundleid", AppUtils.getPackageName(this));
                String payParameter = JsonUtils.map2JsonString(payInfo);
                String url = payserver + "gameparam=asdkpay";
                RequestConfig config = new RequestConfig(url, payParameter);
                new Thread(() -> {
                    String result = RequestUtils.POSTEncrypt(config, false);
                    hideLoading();
                    JSONObject data = JsonUtils.getJSONObject(result, "data");
                    int code = JsonUtils.getInt(result, "code");
                    String msg = JsonUtils.getString(result, "msg");
                    if (code == 0) {
                        String payparams = JsonUtils.getString(data, "extdata");
                        String orderid = JsonUtils.getString(data, "orderid");
                        String pay_info = JsonUtils.getString(data, "pay_info");
                        if (selectPayId == TYPE_WX_WEB_PAY){
                            WXinSDK.HeePaySDK(PaymentTypeDisplayActivity.this, payparams);
                        }else if (selectPayId == TYPE_ALI_WAP_PAY){
                            WXinSDK.zfb_h5PaySDK(PaymentTypeDisplayActivity.this, pay_info);
                        }
                    } else {
                        showToast(msg);
                    }
                    finish();

                }).start();
                break;
            case TYPE_ALI_PAY:
                Intent intent = new Intent(this, ChargeInfoForAilpay.class);
                intent.putExtra("chargeTypeId", String.valueOf(selectPayId));
                intent.putExtra("chargeName", remark);
                startActivity(intent);
                hideLoading();
                finish();
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            payCancelCallback();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void payCancelCallback() {
        Intent intent = new Intent(this, MyRemoteService.class);
        Bundle bundle = new Bundle();

        GameArgs gameargs = MyApplication.getAppContext().getGameArgs();
        bundle.putString("status", "1");
        bundle.putString("custominfo", gameargs.getSelf());
        bundle.putString("msg", gameargs.getDesc());
        bundle.putString("flag", "pay");
        bundle.putString("customorderid", gameargs.getCustomorderid());
        bundle.putString("sum", gameargs.getSum());
        bundle.putString("chargetype", String.valueOf(selectPayId));
        bundle.putString("key", gameargs.getKey());

        intent.putExtras(bundle);
        startService(intent);
    }

    private void requestPayInformation() {
        String url = payserver + "gameparam=payinformation";
        RequestConfig config = new RequestConfig(url, "");
        String result = RequestUtils.POSTEncrypt(config, false);
        JSONObject jsonObject = JsonUtils.getJSONObject(result, "data");
        PartnerConfig.setPARTNER2(JsonUtils.getString(jsonObject, "partner"));
        PartnerConfig.setSELLER2(JsonUtils.getString(jsonObject, "seller"));
        PartnerConfig.setRSA_PRIVATE2(JsonUtils.getString(jsonObject, "devPrivateKey"));
        PartnerConfig.setRSA_ALIPAY_PUBLIC2(JsonUtils.getString(jsonObject, "devPublicKey"));
        PartnerConfig.setALIPAY_PLUGIN_NAME2(JsonUtils.getString(jsonObject, "apkname"));
        PartnerConfig.setQQ(JsonUtils.getString(jsonObject, "qq"));
        PartnerConfig.setPHONE(JsonUtils.getString(jsonObject, "phone"));
    }

    private void initView(View rootView) {
        icon_checkbox_select = ResUtils.getDrawableID(this, "icon_checkbox_select");
        icon_checkbox_default = ResUtils.getDrawableID(this, "icon_checkbox_default");

        payRootView = ResUtils.getView(rootView, "rl_pay_view");
        itemAlipay = ResUtils.getView(rootView, "rl_alipay");
        itemWxpay = ResUtils.getView(rootView, "rl_wxpay");
        productDesc = ResUtils.getView(rootView, "tv_desc");
        productAmount = ResUtils.getView(rootView, "tv_amount");
        checkWxpay = ResUtils.getView(rootView, "iv_check_wxpay");
        checkAlipay = ResUtils.getView(rootView, "iv_check_alipay");
        confimPay = ResUtils.getView(rootView, "button_cofirm");
        progressBar = ResUtils.getView(rootView, "pb_loading");

    }

    private void initListener() {
        itemAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPayId = alipayId;
                checkAlipay.setImageResource(icon_checkbox_select);
                checkWxpay.setImageResource(icon_checkbox_default);
            }
        });
        itemWxpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPayId = wechatPayId;
                checkWxpay.setImageResource(icon_checkbox_select);
                checkAlipay.setImageResource(icon_checkbox_default);
            }
        });
        confimPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startPay();
            }
        });
    }

}
