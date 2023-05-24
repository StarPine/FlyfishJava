package fly.fish.asdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import fly.fish.aidl.MyRemoteService;
import fly.fish.aidl.OutFace;
import fly.fish.config.Configs;
import fly.fish.tools.FilesTool;
import fly.fish.tools.HttpUtils;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

/**
 * 升级专用类
 * 
 * 
 * @author Administrator
 */
public class AsdkActivity extends MyActivity {

	//单机-网游检测更新地址
	private static String URL1 = "http://opupdate.ay99.net:8082/init.php?gameparam=replace";
	//围信-网游检测更新地址
	private static String URL2 = "http://update.qweixinq.com:8082/init.php?gameparam=replace";
	//值尚-网游检测更新地址
	private static String URL3 = "http://update.ay99.net/init.php?gameparam=replace";
	//应美-网游检测更新地址
	private static String URL4 = "http://update.baban-inc.cn:8082/init.php?gameparam=replace";
	private static String publisher = FilesTool.getPublisherStringContent();
	private static String URL = "";
	/** 根布局 */
	private LinearLayout mLayout;
	/** 升级结果 */
	public int result = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = new LinearLayout(this);
		setContentView(mLayout);
		if(publisher.contains("ymsg")||publisher.contains("xj")){
			URL = URL4;
		}else{
			URL = URL3;
		}
		init();
	}
	
	public static String getURL3() {
		return URL3;
	}


	@Override
	protected void onResume() {// 出栈从这里进入
		super.onResume();
		/*
		 * if(MyApplication.getAppContext().bvupdateAPK){ Intent intent=new
		 * Intent(this, MyRemoteService.class); Bundle bu = new Bundle();
		 * bu.putString("flag", "init"); bu.putString("status", "0");
		 * intent.putExtras(bu); startService(intent);
		 * MyApplication.getAppContext().bvupdateAPK = false;
		 * 
		 * finish(); }
		 */

	}
	
	Handler han = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				update();
				break;

			default:
				break;
			}
		}
	};
	private void update() {
		Builder builder = new Builder(this);
		builder.setMessage(str);
		builder.setTitle("更新");
		builder.setCancelable(false);
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DownloadAPk(AsdkActivity.this, updateUrl);
			}
		})
		.setNegativeButton("退出", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AsdkActivity.this.finish();
				try {
					OutFace.getInstance(null).getmActivity().finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		builder.create().show();
	}

	String updateUrl = "";
	String str = "";
	/**
	 * 系统初始化，我没有super();
	 */
	public void init() {
		MLog.s(this + " super ----> initbegin");
		isNeedReload = true;
		bitmap = new HashMap<String, Bitmap>();

		mLuaState = getApp().getmLuaState();
		setMyhand(new MyHandler(this));
		MLog.s(this + " super ----> initbegin2");
		initArgsCreate();
		MLog.s(this + " super ----> initbegin3");
		initLua();
		MLog.s(this + " super ----> initend");

		if (result == 3) {
			MLog.a("AsdkActivity", "----3----");
			str = "游戏有新版本，请更新！";
		}
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int versionCode = HttpUtils.getAppCode();
				String data = HttpUtils.postMethod(URL, "{\"version_appVersion\":"+versionCode+"}", "utf-8");
				try {
					JSONObject order = new JSONObject(data);
					updateUrl = order.getJSONObject("data").getString("updateUrl");
					str = order.getJSONObject("data").getString("verdesc");
					str = ("".equals(str)||"null".equals(str))?"游戏有新版本，请更新！":str;
					han.sendEmptyMessage(0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
		

		getApp().addActivity(this);

		/*
		 * File sd=Environment.getExternalStorageDirectory(); String path =
		 * sd.getPath()+"/client/photo/"; File file = new File(path);
		 * if(!file.exists()){ file.mkdirs(); } File a = new File(path+"a.txt");
		 * if(!a.exists()){ try { a.createNewFile(); } catch (IOException e) {
		 * e.printStackTrace(); } }
		 */

		/*
		 * Bitmap b = null; NinePatch patch = new NinePatch(b,
		 * b.getNinePatchChunk(), ""); NinePatchDrawable d = new
		 * NinePatchDrawable(getResources(), patch);
		 */
	}

	/**
	 * 初始化lua开发环境
	 */
	public void initLua() {
		super.initLua();
		MLog.s(this + " ----> onResume doing2 ");
		mLuaState.pushJavaObject(mLayout);
		mLuaState.setGlobal("rootview");
		MLog.s(this + " ----> onResume end ");
	}

	/**
	 * 初始化参数
	 */
	public boolean initArgsCreate() {
		super.initArgsResume();

		// 获得参数
		Intent itn = getIntent();
		if (itn != null) {
			Bundle bu = itn.getExtras();
			if (bu != null) {
				result = bu.getInt("result");
				MLog.a("AsdkActivity", "----result----"+result);
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MLog.s("KEYCODE_BACK");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MLog.s(requestCode + " onActivityResult " + resultCode);
	}

	/**
	 * 启动下载
	 * 
	 * @param logic
	 */
	public void startUpdateThread() {
		MyApplication app = getApp();
		// 走升级程序（这里不能使用rootview）一遍足以（升级返回结果）
		synchronized (app.getmLuaState()) {
			app.getmLuaState().getGlobal("update");
			int index = app.getmLuaState().getTop();
			app.getmLuaState().getField(index, "startup");
			LuaTools.dbcall(app.getmLuaState(), 0, 1);
		}

		// 取检查结果
		int re = app.getmLuaState().toInteger(-1);
		// 如果网络出错了怎么办,回调2
		if (re == 0 || re == 1) {

			// 通知远程初始化回调
			Intent intent = new Intent(this, MyRemoteService.class);
			Bundle bu = new Bundle();
			bu.putString("status", "2");
			bu.putString("flag", "init");
			intent.putExtras(bu);
			startService(intent);

			getMyhand().sendEmptyMessage(600);
		} else {
			MLog.s("gothrough thread luainitok over!!!");
		}
	}
	
	private ProgressDialog pd = null;
	private int progress = 0;
	private String gameName;
	private final int APK_DOWNLOADING = 4001;
	private final int APK_DOWNLOAD_COMPLETE = 4002;
	private final int APK_DOWNLOAD_ERROR = 4003;
	private void DownloadAPk(Activity context, final String url) {
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		gameName = HttpUtils.getUrlFileName(url);
		pd = new ProgressDialog(context);
		pd.setTitle("游戏更新");
		pd.setMessage("正在下载更新，请稍后……");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		pd.setCancelable(false);
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						NetworkInfo info;
						File file = null;
						if (Configs.SDEXIST) {
							File dr = new File(Configs.ASDKROOT + AsdkActivity.this.getPackageName()+ File.separator);
							if (!dr.exists()) {
								dr.mkdirs();
							}
							file = new File(Configs.ASDKROOT + AsdkActivity.this.getPackageName() + File.separator + gameName);
						}else{
							file = new File(Configs.ASDKROOT + gameName);
						}
						file.delete();
						fileOutputStream = new FileOutputStream(file);
						byte[] b = new byte[1024];
						int charb = -1;
						int count = 0;
						while ((charb = is.read(b)) != -1) {
							info = cm.getActiveNetworkInfo();
							if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
								handler.sendEmptyMessage(APK_DOWNLOADING);
							} else {
								handler.sendEmptyMessage(APK_DOWNLOAD_ERROR);
							}
							count += charb;
							progress = (int) (((float) count / length) * 100);
							fileOutputStream.write(b, 0, charb);

						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					handler.sendEmptyMessage(APK_DOWNLOAD_COMPLETE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case APK_DOWNLOADING:
				pd.setProgress(progress);
				break;
			case APK_DOWNLOAD_COMPLETE:
				if (pd != null) {
					pd.cancel();
				}
				
				// 创建退出对话框
				final Builder dialog = new Builder(AsdkActivity.this);
				// 设置对话框标题
				dialog.setTitle("游戏更新");
				// 设置对话框消息
				dialog.setMessage("下载完成！");
				dialog.setCancelable(false);
				// 添加选择按钮并注册监听
				dialog.setPositiveButton("确认", new OnClickListener() {

					   @Override
					   public void onClick(DialogInterface dialog1, int which) {
						   dialog.create().show();
						   PhoneTool.notifyAndInstallApk(AsdkActivity.this, gameName);
					   }
				});
				// 显示对话框
				dialog.create().show();
				
				break;
			case APK_DOWNLOAD_ERROR:
				pd.cancel();
				pd.setTitle("错误");
				pd.setMessage("网络链接异常,下载失败!");
				pd.show();
				pd.setCancelable(true);
				break;
			}
		}
	};

}
