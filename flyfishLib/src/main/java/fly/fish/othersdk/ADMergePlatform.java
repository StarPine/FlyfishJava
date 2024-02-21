package fly.fish.othersdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import fly.fish.asdk.R;
import fly.fish.open.ad.ADVideoListener;
import fly.fish.open.ad.IVideo;

public class ADMergePlatform implements IVideo {

    private Dialog alertDialog;
    private ADVideoListener adVideoListener;
    private Activity activity;
    private CountDownTimer mCountDownTimer;
    private LinearLayout linearLayout;

    private ADMergePlatform() {

    }

    @Override
    public void initAD(Application application) {
        Log.i(TAG, "initAD: ");
    }

    private void initData() {
        linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 200);
        TextView textView = new TextView(activity);
        textView.setText("广告模拟展示中....");
        textView.setTextSize(20);
        textView.setTextColor(Color.GRAY);
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button button = new Button(activity);
        button.setText("关闭");
        button.setTextColor(Color.BLACK);
        button.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(textView, layoutParams);
        linearLayout.addView(button, buttonLayoutParams);

        textView.setOnClickListener(v -> {
            adVideoListener.onClickAd();
        });
        button.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        //初始化倒计时类，第一个参数为总的倒计时时长,第二个为间隔时长
        mCountDownTimer = new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔相应间隔时间回调一次
                textView.setText("广告模拟展示中...." + (millisUntilFinished - 1000) / 1000);
            }

            @Override
            public void onFinish() {    //倒计时结束的回调
                textView.setText("广告模拟展示完成....");
                adVideoListener.onCompletedAd();
            }
        };
    }

    @Override
    public void loadAD(Activity activity, String posID,boolean isLoadedShow, ADVideoListener adVideoListener) {
        this.adVideoListener = adVideoListener;
        this.activity = activity;
        initData();

        //调用游戏自带的退出框
        if (alertDialog == null)
            alertDialog = new Dialog(activity,  activity.getResources().getIdentifier("MyDialog", "style", activity.getPackageName()));
        alertDialog.setContentView(linearLayout);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnDismissListener(dialog -> {
            mCountDownTimer.cancel();
            adVideoListener.onCloseAd();
        });
        try {
            Thread.sleep(500);
            adVideoListener.onReadyAd();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show() {
        if (alertDialog != null) {
            alertDialog.show();
            mCountDownTimer.start();
            adVideoListener.onShowedAd();
        } else {
            adVideoListener.onErrorAd(8801, "广告未准备好...");
        }
    }
}
