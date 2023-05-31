package fly.fish.othersdk;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import fly.fish.aidl.MyRemoteService;
import fly.fish.asdk.MyApplication;
import fly.fish.tools.MLog;

public class YXWebActivity extends Activity {

	private WebView webView;
	private boolean ispay = false;
	private static boolean ispayfinish = false;
	public static boolean isIspayfinish() {
		return ispayfinish;
	}

	public static void setIspayfinish(boolean ispayfinish) {
		YXWebActivity.ispayfinish = ispayfinish;
	}

	String url;TextView tv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// 隐去标题栏（应用程序的名字）
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent intent =getIntent(); 
		url=intent.getStringExtra("user_protocol_url");
		if(url==null){
			
			WindowManager.LayoutParams lp=getWindow().getAttributes();
	        lp.dimAmount=0.4f;//设置黑暗度
	        lp.alpha=0.0f;//设置透明度
	        getWindow().setAttributes(lp);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	        
	     // 隐去状态栏部分(电池等图标和一切修饰部分)
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        
			url=intent.getStringExtra("weixin_zfurl");
			if(url!=null){
				if(!isWxInstall()){
					Toast.makeText(getApplicationContext(), "请先安装微信客户端", Toast.LENGTH_LONG).show();
					goback();
					return;
				}
			}else{
				lp.dimAmount=0.4f;//设置黑暗度
		        lp.alpha=1.0f;//设置透明度
		        getWindow().setAttributes(lp);
				url=intent.getStringExtra("zfb_zfurl");
			}
		}
		System.out.println("url------->"+url);
		if("".equals(url)){
			Toast.makeText(MyApplication.context, "no_url", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		LinearLayout lay = new LinearLayout(this);
		LinearLayout.LayoutParams params_lay = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lay.setLayoutParams(params_lay);
		lay.setGravity(Gravity.CENTER);
		lay.setOrientation(LinearLayout.VERTICAL);
		
		webView = new WebView(this);
		
        WebSettings setting = webView.getSettings();

		setting.setJavaScriptEnabled(true);
		setting.setJavaScriptCanOpenWindowsAutomatically(true);
		setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		setting.setDomStorageEnabled(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			
			setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
//		setting.setAllowFileAccess(true);
//		setting.setRenderPriority(RenderPriority.HIGH);
//		setting.setCacheMode(WebSettings.LOAD_DEFAULT);
//		// setting.setDatabaseEnabled(true);
//		String cacheDirPath = getFilesDir().getAbsolutePath() + "/webcache";
//		// setting.setDatabasePath(cacheDirPath);
//		setting.setAppCachePath(cacheDirPath);
//		setting.setAppCacheEnabled(true);
//
//		setting.setSupportZoom(true);
		webView.resumeTimers();//唤醒webview
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new MyWebViewClient());
        if(intent.getStringExtra("zfb_zfurl")!=null){
        	MLog.a("sdk", "load_zfubao");
        	webView.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);
        }else{
        	webView.loadUrl(url);
        }
        tv = new TextView(this);
        if(intent.getStringExtra("user_protocol_url")!=null){
        	tv.setGravity(Gravity.CENTER);
        	tv.setText("x");tv.setTextSize(18);
        	tv.setTextColor(getResources().getColor(android.R.color.black));
        	tv.getPaint().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        	tv.setPadding(0,5, 0, 5);
        	tv.setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View v) {
        			YXWebActivity.this.finish();
        		}
        	});
        	lay.addView(tv,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        	tv.setBackgroundColor(YXWebActivity.this.getResources().getColor(android.R.color.white));
        }
        ScrollView sv = new ScrollView(this);
        sv.setHorizontalScrollBarEnabled(false);
        sv.addView(webView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        lay.addView(sv,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setContentView(lay);
        
	}
	
	private boolean isloaded = false;
	
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			MLog.a("weixin", "shouldOverrideUrlLoading--"+isloaded+"--"+url);
			if(!isloaded){
				if(!url.startsWith("weixin:")){
					if(url.startsWith("alipays:")|| url.startsWith("alipay")){
						try {
							YXWebActivity.this.startActivity(new Intent("android.intent.action.VIEW",Uri.parse(url)));
						} catch (Exception e) {
							e.printStackTrace();
//							new AlertDialog.Builder(YXWebActivity.this)
//						     .setMessage("未检测到支付宝客户端，请安装后重试。")
//						     .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {
//						 
//						      @Override
//						      public void onClick(DialogInterface dialog, int which) {
//						         Uri alipayUrl = Uri.parse("https://d.alipay.com");
//						         YXWebActivity.this.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
//						      }
//						     }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									payfail();
//									MyApplication.getAppContext().backToGame();
//									YXWebActivity.this.finish();
//								}
//
//								
//							}).show();
						}
						return true;
					}
//					if(url.contains("zfubaowapreturnurl")||url.contains("wx.tenpay")){
					if(url.contains("zfubaowapreturnurl")){
						paybackqurey();
					}
					System.out.println("super.shouldOverrideUrlLoading--"+url);
					return super.shouldOverrideUrlLoading(view, url);
				}else{
					isloaded = true;
					ispay = true;
					try {
						YXWebActivity.this.startActivity(new Intent("android.intent.action.VIEW",Uri.parse(url)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
			}else{
				return true;
			}
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			System.out.println("------------onReceivedError-----------"+description);
			super.onReceivedError(view, errorCode, description, failingUrl);
			MyApplication.getAppContext().backToGame();
			YXWebActivity.this.finish();
		}
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			System.out.println("-----------onReceivedSslError-------------");
			handler.proceed();
			super.onReceivedSslError(view, handler, error);
		}
		
	}
	private void paybackqurey() {
		ispayfinish = true;
		Intent locIntent = getIntent();
		Bundle bundle = locIntent.getExtras();
		locIntent.setClass(this, MyRemoteService.class);
		bundle.putString("flag", "sec_confirmation");
		locIntent.putExtras(bundle);
		this.startService(locIntent);
		MyApplication.getAppContext().backToGame();
		this.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MLog.a("YXWeb", "------onResume------");
		if(ispay){
//			Intent locIntent = getIntent();
//			locIntent.setClass(this, MyRemoteService.class);
//			Bundle locBundle = new Bundle();
//			Bundle mBundle = locIntent.getExtras();
//			locBundle.putString("flag", "pay");
//			locBundle.putString("msg", mBundle.getString("desc"));
//			locBundle.putString("sum", mBundle.getString("account"));
//			locBundle.putString("chargetype", "pay");
//			locBundle.putString("custominfo", mBundle.getString("callBackData"));
//			locBundle.putString("customorderid", mBundle.getString("merchantsOrder"));
//			locBundle.putString("status", "2");
//			locIntent.putExtras(locBundle);
//			startService(locIntent);
			
			paybackqurey();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			url=getIntent().getStringExtra("user_protocol_url");
			if(url==null){
				if(getIntent().getStringExtra("zfb_zfurl")!=null){
					paybackqurey();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean isWxInstall() { 
		final PackageManager packageManager = this.getPackageManager();
		// 获取packagemanager 
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		// 获取所有已安装程序的包信息 
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName; 
				if (pn.equals("com.tencent.mm")) {
					return true; 
				} 
			} 
		}
		return false; 
	}
	
	public void goback(){
		Intent locIntent = getIntent();
		locIntent.setClass(this, MyRemoteService.class);
		Bundle locBundle = new Bundle();
		Bundle mBundle = locIntent.getExtras();
		locBundle.putString("flag", "pay");
		locBundle.putString("msg", mBundle.getString("desc"));
		locBundle.putString("sum", mBundle.getString("account"));
		locBundle.putString("chargetype", "pay");
		locBundle.putString("custominfo", mBundle.getString("callBackData"));
		locBundle.putString("customorderid", mBundle.getString("merchantsOrder"));
		locBundle.putString("status", "1");
		locIntent.putExtras(locBundle);
		startService(locIntent);
		MyApplication.getAppContext().backToGame();
		this.finish();
	}
}
