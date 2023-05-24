package fly.fish.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class DetailsActivity extends Activity {

    private Button close_bt;
    private WebView details_webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int details_activity_layout_id = DetailsActivity.this.getResources().getIdentifier("details_activity_layout", "layout", DetailsActivity.this.getPackageName());
        setContentView(details_activity_layout_id);

        int close_bt_id = DetailsActivity.this.getResources().getIdentifier("close_bt", "id", DetailsActivity.this.getPackageName());
        int details_webView_id = DetailsActivity.this.getResources().getIdentifier("details_webView", "id", DetailsActivity.this.getPackageName());
        close_bt=(Button) findViewById(close_bt_id);
        details_webView=(WebView) findViewById(details_webView_id);
        Intent intent=getIntent();

        String url=intent.getStringExtra("url");
        details_webView.getSettings().setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        details_webView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        details_webView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        details_webView.loadUrl(url);
        close_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsActivity.this.finish();
//                System.exit(0);
            }
        });
    }
}
