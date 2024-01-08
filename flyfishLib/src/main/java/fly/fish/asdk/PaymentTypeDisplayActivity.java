package fly.fish.asdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import fly.fish.tools.ResUtils;

public class PaymentTypeDisplayActivity extends Activity {

    private TextView productDesc;
    private TextView productAmount;
    private ImageView checkWxpay;
    private ImageView checkAlipay;
    private int payType = 0;//支付宝：0，微信：1
    private View itemAlipay;
    private View itemWxpay;
    private Button confimPay;
    private int icon_checkbox_select;
    private int icon_checkbox_default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        View rootView = ResUtils.getRootView(this, "activity_pay", null);
        setContentView(rootView);
        initView(rootView);
        initListener();

    }

    private void initView(View rootView) {
        icon_checkbox_select = ResUtils.getDrawableID(this, "icon_checkbox_select");
        icon_checkbox_default = ResUtils.getDrawableID(this, "icon_checkbox_default");

        itemAlipay = ResUtils.getView(rootView, "rl_alipay");
        itemWxpay = ResUtils.getView(rootView, "rl_wxpay");
        productDesc = ResUtils.getView(rootView, "tv_desc");
        productAmount = ResUtils.getView(rootView, "tv_amount");
        checkWxpay = ResUtils.getView(rootView, "iv_check_wxpay");
        checkAlipay = ResUtils.getView(rootView, "iv_check_alipay");
        confimPay = ResUtils.getView(rootView, "button_cofirm");

        confimPay.setText("确认支付 ￥648");
        productDesc.setText("6480钻石");
        productAmount.setText("￥ 648 ");
    }

    private void initListener() {
        itemAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payType = 0;
                checkAlipay.setImageResource(icon_checkbox_select);
                checkWxpay.setImageResource(icon_checkbox_default);
            }
        });
        itemWxpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payType = 1;
                checkWxpay.setImageResource(icon_checkbox_select);
                checkAlipay.setImageResource(icon_checkbox_default);
            }
        });
        confimPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType == 0){

                    Toast.makeText(PaymentTypeDisplayActivity.this,"支付宝支付",Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(PaymentTypeDisplayActivity.this,"微信支付",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
