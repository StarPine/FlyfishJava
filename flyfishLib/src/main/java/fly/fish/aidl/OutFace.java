package fly.fish.aidl;


import java.io.File;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import fly.fish.asdk.BuildConfig;
import fly.fish.asdk.ChargeActivity;
import fly.fish.asdk.LoginActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.asdk.MyCrashHandler;
import fly.fish.asdk.SkipActivity;
import fly.fish.dialog.CloseAccountCallBack;
import fly.fish.dialog.DialgTool;
import fly.fish.dialog.PrivacyDialog;
import fly.fish.othersdk.ExitCallBack;
import fly.fish.othersdk.GetCertificationInfoCallback;
import fly.fish.othersdk.JGSHaretools;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;
import fly.fish.tools.OthPhone;
import fly.fish.tools.PhoneTool;

public class OutFace {
	private static String d = "fly.fish.aidl.IMyTaskBinder";
	private String DOWN_ACTION = MyApplication.getAppContext().getPackageName()
			+ ".fly.fish.aidl.MyRemoteService.MYBROADCAST";

	public static final int SDK_VERSION_CODE = BuildConfig.SDK_VERSION_CODE;

	/**
	 * SDK版本名称(插件/宿主)
	 */
	public static final String SDK_VERSION_NAME = BuildConfig.SDK_VERSION_NAME;
	private static OutFace our = null;
	public static Activity mActivity;



    public Activity getmActivity() {
		return mActivity;
	}

	private Intent mIntent;
	private IMyTaskBinder ibinder = null;
	private ITestListener callback = null;

	private String Publisher;
	private String cpid = null;
	private String gameid = null;
	private String key = null;
	private String gamename = null;

	private MyBroadCast broadcast;
	private boolean isRegitered = false;

	private Intent service;

	public void outActivityResult(Activity act, int requestCode,
			int resultCode, Intent data) {
		SkipActivity.othActivityResult(act, requestCode, resultCode, data,
				mIntent);
	}

	public void outOnCreate(Activity activity) {
		mActivity = activity;
		SkipActivity.othInit(activity);
	}

	public void outOnCreate(Activity activity, Bundle savedInstanceState) {
		mActivity = activity;
		SkipActivity.othInit(activity, savedInstanceState);
	}

	public void outOnStart(Activity activity) {
		SkipActivity.othOnStart(activity);
	}

	public void outOnResume(Activity act) {
		SkipActivity.othOnResume(act);
	}

	public void outOnPause(Activity act) {
		SkipActivity.othOnPuse(act);
	}

	public void outOnStop(Activity act) {
		SkipActivity.othOnStop(act);
	}
	

	public void onSaveInstanceState(Activity act,Bundle outState) {
		SkipActivity.onSaveInstanceState(act,outState);
	}

	public void outDestroy(Activity activity) {
		SkipActivity.othDestroy(activity);
		// if (UCSDK610.mRepeatCreate) {
		// Log.i("UCSDK", "onDestroy is repeat activity!");
		// return;
		// }
		quit(activity);
	}

	public void outInGame(String abc) {
		SkipActivity.othInGame(abc);
	}

	// 社区
	public void outForum(Activity activity) {
		SkipActivity.callPerformFeatureBBS(activity);
	}

	/**
	 * 创建角色时调用
	 * 
	 * @param act
	 * @param serverid
	 * @param servername
	 * @param roleId
	 * @param rolename
	 * @param rolelevel
	 */
	public void createRole(Activity act, String serverid, String servername,
			String roleId, String rolename, int rolelevel) {
//		SkipActivity.CreateRole(act, serverid, servername, roleId, rolename,
//				rolelevel);
	}

	public void outNewIntent(Activity act, Intent intent) {
		SkipActivity.othNewIntent(act, intent);
	}

	public void outRestart(Activity act) {
		SkipActivity.othRestart(act);
	}

	public void outBackPressed(Activity activity) {
		SkipActivity.othBackPressed(activity);
	}

	public void outConfigurationChanged(Configuration newConfig) {
		SkipActivity.othConfigurationChanged(newConfig);
	}

	public void outQuit(Activity act) {
		SkipActivity.othQuit(act);
	}

	public void outQuitCallBack(Activity act, ExitCallBack exitcallback) {
		SkipActivity.outQuitCallBack(act, exitcallback);
	}

	public void outLogout(Activity act) {
		SkipActivity.othLogout(act);
	}

	public void outonWindowFocusChanged(boolean hasFocus) {
		SkipActivity.othonWindowFocusChanged(hasFocus);
	}

	
	public void getCertificateInfo(Activity act,GetCertificationInfoCallback callback) {
		
		SkipActivity.getCertificateInfo(act, callback);
		//callback.callback(true, "0", "20");
	}
	
	
	private String state = "";
	private String qx_url = "";
	private String ys_url = "";
	private String yh_url = "";
	private static boolean isrequst=false;
	
	public void outInitLaunch(final Activity activity,
 		final boolean isLandscape, final CallBackListener callback) {
		mActivity = activity;
		//SkipActivity.othInitLaunch(activity, isLandscape, callback);
		MyCrashHandler mCrashHandler=MyCrashHandler.getInstance();
		mCrashHandler.init(activity.getApplicationContext());
		Thread urlthred = new Thread(new Runnable() {

			@Override
			public void run() {

				MLog.a("--------pub------"
						+ DialgTool.getpub("AsdkPublisher.txt"));
				MLog.a("--------url------" + DialgTool.getpub("address.txt"));
				String s = DialgTool.getWebMethod(DialgTool
						.getpub("address.txt")
						+ DialgTool.getpub("AsdkPublisher.txt"));
				MLog.a("--------json------" + s);

				// if(s==null){
				// handler.sendEmptyMessage(1);
				// return;
				// }

				try {

					JSONObject jsonObject = new JSONObject(s);
					state = jsonObject.getString("state");
					qx_url = jsonObject.getString("qxurl");
					ys_url = jsonObject.getString("ysurl");
					yh_url = jsonObject.getString("yhurl");

					// handler.sendEmptyMessage(0);

					MLog.a("--------请求完成------qx=" + qx_url + ";ys_url="
							+ ys_url + ";yh_url=" + yh_url);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		urlthred.start();

		try {
			urlthred.join();

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					//inItLaunch(activity, isLandscape, callback);
					SharedPreferences sharedPreferences = activity
							.getSharedPreferences("asdk", activity.MODE_PRIVATE);
					boolean isFirstRun = sharedPreferences.getBoolean(
							"isFirstRun", true);
					final SharedPreferences.Editor editor = sharedPreferences
							.edit();

					final PrivacyDialog privacyDialog = new PrivacyDialog(
							activity, yh_url, ys_url, qx_url, activity
									.getResources().getIdentifier("MyDialog",
											"style", activity.getPackageName()));

					privacyDialog.setCancelable(false);// 点击返回键或者空白处不消失
					privacyDialog
							.setClickListener(new PrivacyDialog.ClickInterface() {
								@Override
								public void doCofirm() {
									privacyDialog.dismiss();

									editor.putBoolean("isFirstRun", false);
									editor.commit();

									inItLaunch(activity, isLandscape, callback);
									MLog.a("同意协议-----------");

								}

								@Override
								public void doCancel() {
									privacyDialog.dismiss();
									activity.finish();
									System.exit(0);
								}
							});

					// privacyDialog.show();
					System.out.println("X7SDK   ---state 000==="+state);
					if ((isFirstRun == true) && (state.equals("1"))) { // 第一次则跳转到引导页面

						MLog.a("第一次安装-----------");
						privacyDialog.show();

					} else if ((isFirstRun == false)) { // 如果是第二次启动则直接跳转到主页面

						MLog.a("非第一次安装-----------");
						inItLaunch(activity, isLandscape, callback);

					} else if (state.equals("0")) { // 如果是第二次启动则直接跳转到主页面

						MLog.a("协议关闭-----------");
						inItLaunch(activity, isLandscape, callback);

					}

				}
			});

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

 	}

	private void inItLaunch(final Activity activity, final boolean isLandscape,
			final CallBackListener callback) {
		
		outrequ();
		MyCrashHandler mCrashHandler=MyCrashHandler.getInstance();
		mCrashHandler.init(activity.getApplicationContext());
		SkipActivity.othInitLaunch(activity, isLandscape, callback);
	}

	public void setDebug(boolean isDebug) {
		MLog.setDebug(isDebug);
	}

	public void onRequestPermissionsResult(Activity paramActivity,
			int requestCode, String[] permissions, int[] grantResults) {
		SkipActivity.onRequestPermissionsResult(paramActivity, requestCode,
				permissions, grantResults);
	}

	/**
	 * 
	 * 连接远程服务
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		/**
		 * bindService
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ibinder = IMyTaskBinder.Stub.asInterface(service);
			System.out.println("onServiceConnected init ----> " + ibinder);
			if (ibinder != null) {
				try {
					ibinder.registerCallBack(callback, key);
					ibinder.init(cpid, gameid, key, gamename);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * unbindService
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out.println("onServiceDisconnected quit ----> " + ibinder);
			ibinder = null;
		}
	};

	public String getPublisher() {
		return Publisher;
	}

	public void setPublisher(String publisher) {
		Publisher = publisher;
	}

	private OutFace() {
		// 创建广播实现类
		broadcast = new MyBroadCast(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DOWN_ACTION);
		Publisher = FilesTool.getPublisherStringContent();

		System.out.println("registerReceiver");
		MyApplication.getAppContext().registerReceiver(broadcast, filter);
		isRegitered = true;

		initFloatService();
	}

	/**
	 * 单例方法
	 * 
	 * @param
	 * @return
	 */
	public static OutFace getInstance(Context con) {
		if (our == null) {
			our = new OutFace();
		}
		return our;
	}

	/**
	 * 注册回调接口
	 * 
	 * @param callback
	 * @throws RemoteException
	 */
	public void callBack(FlyFishSDK callback, String key) {
		this.callback = callback;
		if (ibinder != null) {
			try {
				ibinder.registerCallBack(callback, key);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化接口
	 * 
	 * @param cpid
	 * @param gameid
	 * @return
	 * @throws RemoteException
	 */
	public boolean init(String cpid, String gameid, String key, String gamename) {

		// if(Publisher.startsWith("asdk_mssg_294")||Publisher.startsWith("asdk_longyin2_091")){
		// FTSppSDK.init(MyApplication.getAppContext());
		// }

		PhoneTool.managerIMEI(mActivity);

		// 先保存参数
		this.cpid = cpid;
		this.gameid = gameid;
		this.key = key;
		this.gamename = MyApplication.getAppContext().getApplicationInfo()
				.loadLabel(MyApplication.getAppContext().getPackageManager())
				.toString();
		OthPhone.setGamekey(cpid+""+gameid);

		// 判断是走sdk还是jar包
		if (ibinder == null) {
			if (Publisher.contains("kdygfk")) {
				d = MyApplication.getAppContext().getPackageName() + "." + d;
			}
			service = new Intent(d);
			service.setPackage(MyApplication.getAppContext().getPackageName());
			boolean isbinded = MyApplication.getAppContext().bindService(
					service, serviceConnection, Context.BIND_AUTO_CREATE);
			System.out.println("action------------------------->" + d);
			System.out.println("bindService-------------------->" + isbinded);

		} else {
			try {
				ibinder.init(cpid, gameid, key, gamename);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	public void outCloseAccountWithUserInfo(Activity act,String userInfo,CloseAccountCallBack exitcallback) {
		SkipActivity.CloseAccountWithUserInfo(act,userInfo,exitcallback);
	} 
	
	/**
	 * 登陆接口
	 * 
	 * @param callBackData
	 *            自定义参数
	 * @throws RemoteException
	 */
	public void login(Activity act, String callBackData, String key) {

		// if(Publisher.startsWith("asdk_mssg_294")||Publisher.startsWith("asdk_longyin2_091")){
		// if(FTSppSDK.isLogin()){
		// String str1 =
		// MyApplication.getAppContext().getGameArgs().getCpid()+MyApplication.getAppContext().getGameArgs().getGameno()+"name";
		// String account =
		// MyApplication.getAppContext().getSharedPreferences("user_info",
		// 0).getString(str1, "");
		// FTSppSDK.accountLogout(account);
		// }
		// }

		mActivity = act;
		mIntent = new Intent();
		Bundle localBundle = new Bundle();
		localBundle.putString("cpid", cpid);
		localBundle.putString("gameid", gameid);
		localBundle.putString("gamename", gamename);
		localBundle.putString("callBackData", callBackData);
		localBundle.putString("key", key);
		localBundle.putString("pid", Binder.getCallingPid() + "");
		localBundle.putString("mode", "login");

		mIntent.putExtras(localBundle);
		SkipActivity.othLogin(act, mIntent);
	}

	/**
	 * 充值接口
	 * 
	 * @param merchantsOrder
	 *            支付订单
	 * @param url
	 *            回调接口
	 * @param account
	 *            支付金额
	 * @param desc
	 *            商品描述
	 * @param callBackData
	 *            自定义参数
	 * @throws RemoteException
	 */
	public void pay(final Activity act, final String merchantsOrder,
			String url, final String account, final String desc,
			final String callBackData, String key) {

		mActivity = act;
		mIntent = new Intent();
		Bundle localBundle = new Bundle();

		localBundle.putString("cpid", cpid);
		localBundle.putString("gameid", gameid);
		localBundle.putString("gamename", gamename);

		localBundle.putString("merchantsOrder", merchantsOrder);
		localBundle.putString("url", url);
		localBundle.putString("account", account);
		localBundle.putString("desc", desc);
		localBundle.putString("callBackData", callBackData);
		localBundle.putString("key", key);
		localBundle.putString("pid", Binder.getCallingPid() + "");
		localBundle.putString("mode", "pay");
		localBundle.putString("flag", "getOrder");

		mIntent.putExtras(localBundle);

		// if(Publisher.startsWith("asdk_mssg_294")||Publisher.startsWith("asdk_longyin2_091")){
		// SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		// String reqTime = fmt.format(new Date());
		// FTSppSDK.formatTime(reqTime);
		// }

		// if(Publisher.startsWith("asdk_mssg_1000")){
		// mIntent.setClass(act, CardRechargeActivity.class);
		// act.startActivity(mIntent);
		// return;
		// }

		if (Publisher.startsWith("asdk")||Publisher.startsWith("baichuansdk")||Publisher.startsWith("qdasdk")) {
//			mIntent.setClass(act, ChargeActivity.class);
//			act.startActivity(mIntent);
			
			SkipActivity.asdkPay(act, this.mIntent);
			
		} else if (Publisher.startsWith("qgsdk")) {

			// ============================ qgsdk
			// start======================================
			final AlertDialog dlg = new AlertDialog.Builder(act).create();
			dlg.show();
			int qq_pay = act.getResources().getIdentifier(
					act.getPackageName() + ":drawable/" + "qq_pay", null, null);
			int other_pay = act.getResources().getIdentifier(
					act.getPackageName() + ":drawable/" + "other_pay", null,
					null);
			LinearLayout layout = new LinearLayout(act);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			act.getWindowManager().getDefaultDisplay()
					.getMetrics(displayMetrics);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setPadding(5, 5, 5, 5);

			LinearLayout layout2 = new LinearLayout(act);
			layout2.setGravity(Gravity.CENTER);

			layout2.setOrientation(LinearLayout.VERTICAL);// 1垂直

			layout2.setPadding(10, 10, 10, 10);
			Button button2 = new Button(act);
			button2.setBackgroundResource(qq_pay);
			// button2.setText("QQ钱包支付");

			Button button3 = new Button(act);
			button3.setBackgroundResource(other_pay);
			// button3.setText("其他方式支付");

			LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-2,
					-2);
			rlp.topMargin = 15;
			layout2.addView(button2, rlp);
			layout2.addView(button3, rlp);
			// layout.setBackgroundColor(Color.TRANSPARENT);
			layout.addView(layout2);
			Window window = dlg.getWindow();

			window.setContentView(layout);
			dlg.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// 取消支付
					Intent locIntent = new Intent();
					locIntent.setClass(act, MyRemoteService.class);
					Bundle locBundle = new Bundle();
					locBundle.putString("flag", "pay");
					locBundle.putString("msg", desc);
					locBundle.putString("sum", account);
					locBundle.putString("chargetype", "pay");
					locBundle.putString("custominfo", callBackData);
					locBundle.putString("customorderid", merchantsOrder);
					locBundle.putString("status", "1");
					locIntent.putExtras(locBundle);
					act.startService(locIntent);

				}
			});
			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// qq钱包支付
					mIntent.setClass(act, MyRemoteService.class);
					act.startService(mIntent);

				}
			});
			button3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 阿游戏支付
					mIntent.setClass(act, ChargeActivity.class);
					act.startActivity(mIntent);
				}
			});

		}

		// ===================== qgsdk end =======================
		else {
			mIntent.setClass(act, MyRemoteService.class);
			act.startService(mIntent);
		}

	}
	

	/**
	 * 充值接口
	 * 
	 * @param merchantsOrder
	 *            支付订单
	 * @param url
	 *            回调接口
	 * @param account
	 *            支付金额
	 * @param feepoint
	 *            计费点
	 * @param desc
	 *            商品描述
	 * @param callBackData
	 *            自定义参数
	 * @throws RemoteException
	 */
	public void pay(Activity act, String merchantsOrder, String url,
			String account, String feepoint, String desc, String callBackData,
			String key) {

		mActivity = act;
		mIntent = new Intent();
		Bundle localBundle = new Bundle();

		localBundle.putString("cpid", cpid);
		localBundle.putString("gameid", gameid);
		localBundle.putString("gamename", gamename);

		localBundle.putString("merchantsOrder", merchantsOrder);
		localBundle.putString("url", url);
		localBundle.putString("account", account);
		localBundle.putString("feepoint", feepoint);
		localBundle.putString("desc", desc);
		localBundle.putString("callBackData", callBackData);
		localBundle.putString("key", key);
		localBundle.putString("pid", Binder.getCallingPid() + "");
		localBundle.putString("mode", "pay");
		localBundle.putString("flag", "getOrder");

		mIntent.putExtras(localBundle);

		// if(Publisher.startsWith("asdk_mssg_294")||Publisher.startsWith("asdk_longyin2_091")){
		// SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		// String reqTime = fmt.format(new Date());
		// FTSppSDK.formatTime(reqTime);
		// }

		// if(Publisher.startsWith("asdk_mssg_1000")){
		// mIntent.setClass(act, CardRechargeActivity.class);
		// act.startActivity(mIntent);
		// return;
		// }

		if (Publisher.startsWith("asdk") || Publisher.startsWith("qgsdk")||Publisher.startsWith("baichuansdk")||Publisher.startsWith("qdasdk")||Publisher.startsWith("dxcpsasdk")) {
//			mIntent.setClass(act, ChargeActivity.class);
//			act.startActivity(mIntent);
			SkipActivity.asdkPay(act, this.mIntent);
			
		} else {
			mIntent.setClass(act, MyRemoteService.class);
			act.startService(mIntent);
		}

	}

	/**
	 * 查询接口
	 * 
	 * @param merchantsOrder
	 *            支付订单
	 * @param callBackData
	 *            自定义参数
	 * @throws RemoteException
	 */
	public void query(String merchantsOrder, String callBackData, String key) {
		// checkBind(3, merchantsOrder, callBackData, key);
	}

	/**
	 * 退出接口
	 * 
	 * @throws RemoteException
	 */
	public void quit(final Activity activity) {

		if (MyApplication.getAppContext() != null) {
			if (ibinder != null) {
				Intent service = null;
				service = new Intent(d);
				service.setPackage(MyApplication.getAppContext()
						.getPackageName());
				MyApplication.getAppContext().unbindService(serviceConnection);
				MyApplication.getAppContext().stopService(service);
				ibinder = null;
				System.out.println("unbindService");
			}
			if (isRegitered) {
				MyApplication.getAppContext().unregisterReceiver(broadcast);
				System.out.println("unregisterReceiver");
				isRegitered = false;
			}
		}
		cpid = null;
		gameid = null;
		key = null;
		gamename = null;
		our = null;
		System.out.println("---------asdk--exit--end-----------");

	}

	/**
	 * 对外接口类
	 * 
	 * @author Administrator
	 */
	public static abstract class FlyFishSDK extends ITestListener.Stub {
	}

	/**
	 * 广播接口类 可以在其他地方操作广播
	 * 
	 * @author Administrator
	 */
	public class MyBroadCast extends BroadcastReceiver {

		public MyBroadCast(OutFace out) {
			super();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionCode = intent.getAction();
			if (DOWN_ACTION.equals(actionCode)) {
				SkipActivity.myReceive(mActivity, mIntent, intent);
			}
		}

	}
	
	

	private boolean isLandScape = false;
	private String packageName = "";
	private IGhostWindowService mBind = null;
	private ServiceConnection ghostServiceConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			if (service != null) {
				try {
					mBind = IGhostWindowService.Stub.asInterface(service);
					if (mBind != null) {
						mBind.initGhostWindow();
						mBind.showGhostWindow(packageName, isLandScape);
						mBind.hideGhostWindow();
						mBind.showChatWindow();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * 启动悬浮框服务
	 */
	public void initFloatService() {
		getApplicationConfig();
		Intent intent = new Intent();
		intent.setAction("fly.fish.ghostWindowService");
		intent.setPackage("com.zshd.GameCenter");
		MyApplication.getAppContext().bindService(intent, ghostServiceConn,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * 获取程序配置信息，横竖屏，包名等
	 */
	public void getApplicationConfig() {
		Configuration configuration = MyApplication.getAppContext()
				.getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandScape = true;
		}
		packageName = MyApplication.getAppContext().getPackageName();
	}

	/**
	 * login for float view only --- 只适用于悬浮框登录
	 * 
	 * @param act
	 * @param callBackData
	 * @param key
	 */
	public void loginByContext(Context act, String callBackData, String key) {
		mIntent = new Intent();
		Bundle localBundle = new Bundle();
		localBundle.putString("cpid", cpid);
		localBundle.putString("gameid", gameid);
		localBundle.putString("gamename", gamename);
		localBundle.putString("callBackData", callBackData);
		localBundle.putString("key", key);
		localBundle.putString("pid", Binder.getCallingPid() + "");
		localBundle.putString("mode", "login");

		mIntent.putExtras(localBundle);
		mIntent.setClass(act, LoginActivity.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		act.startActivity(mIntent);
	}

	//跳转小程序
	public void outshare(Activity activity,int code ){

		JGSHaretools.othshare(activity,code);
	}

	//分享
	public void outJGshare(Activity activity,int code,File file ){

		JGSHaretools.othJgshare(activity,code,file );
	}


	//控制是否请求敏感权限
	public static boolean isrequ = false;
	//控制是否默认同意隐私协议
	private static boolean checkState = false;
	private static boolean oneLoginCheck = false;

	public static void setOneLoginCheck(boolean ischeck) {
		oneLoginCheck = ischeck;
	}

	public static boolean getOneLoginCheck() {
		return oneLoginCheck;
	}

	//获取审核状态
	public boolean getCheckState(){
		return checkState;
	}

	public static void setisreq(boolean isreq){
		isrequ = isreq;
	}

	/**
	 * 是否选中隐私（控制提审模式）
	 * @param
	 */
	public static void setCheckState(boolean isCheck) {
		checkState = isCheck;
	}

	public static void outrequ(){
		if(isrequ){
			if (Build.VERSION.SDK_INT < 23) {
				return;
			}

			if (ContextCompat.checkSelfPermission(mActivity,
					Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| ContextCompat.checkSelfPermission(mActivity,
					Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
				// || ContextCompat.checkSelfPermission(activity,
				// Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

			) {
				System.out.println("shouldShowRequestPermissionRationale  ----> 000 " );
				ActivityCompat.requestPermissions(mActivity, new String[] {
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.READ_PHONE_STATE
						// ,Manifest.permission.CAMERA
				}, 1);

			}
		}
	}

}
