package fly.fish.act;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import fly.fish.dialog.PrivacyActivity;
import fly.fish.tools.ManifestInfo;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        long delayMillis = ManifestInfo.getMetaInt(this, "WELCOME_TIME", 2000);
        boolean isLandscape = getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        String welcomeName = isLandscape ? "welcome_landscape" : "welcome_portrait";
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.gravity = Gravity.CENTER;
        imageView.setLayoutParams(layoutParams1);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(getResources().getIdentifier(welcomeName, "drawable", getPackageName()));
        linearLayout.addView(imageView);
        this.setContentView(linearLayout);
        new Handler().postDelayed(() -> {
            WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, PrivacyActivity.class));
            WelcomeActivity.this.finish();
            WelcomeActivity.this.overridePendingTransition(0, 0);
        }, delayMillis);
    }

}
