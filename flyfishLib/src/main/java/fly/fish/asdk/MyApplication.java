package fly.fish.asdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
 

 


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import fly.fish.aidl.ITestListener;
import fly.fish.aidl.MyRemoteService;
import fly.fish.beans.GameArgs;
import fly.fish.beans.GridViewModel;
import fly.fish.beans.PayBackModel;
import fly.fish.config.Configs;
import fly.fish.impl.HttpCallBack;
import fly.fish.impl.HttpErrorHandler;
import fly.fish.tools.Dao;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;

//百度（ android.support.multidex.MultiDexApplication）
//悟饭4.0 （com.csfuse.sdk.VSFuseApplication）
//游戏fan 新3.0 (com.yaoyue.release.YYSDKApplication)
//鲸旗1.0.7（com.kingcheergame.jqgamesdk.app.JqApplication）
//银狐2.2（com.yinhu.sdk.YHApplication 实现IApplicationListener接口）  2.5  MyApplication extends Application implements YHApplicationListener
//天瑀1.2.5（com.tygame.tianyu.TYApplication）
//复娱2.0（com.foyoent.FoyoentApplication）
//游龙3.9（com.ylwl.supersdk.api.YLSuperApp）com.ylwl.supersdk.api.YLSuperApp
//松鼠2.2（com.songshu.sdk.YHApplication 实现IApplicationListener接口）
//拇指玩3391（com.muzhiwan.sdk.app.MzwApplication）
//畅娱 1.0 （com.sygsdk.common.SYGApplication）
//play800  (com.wx.platform.WXApplication)
//大臣2.0（com.dcgame.mintsdk.MGApplication）
//当乐4.6（com.downjoy.DownjoyApplication）
//乐游3.0（com.leyou.sdk.LeyouApplication）
//29游（com.ejyx.common.EJYXApplication）
//FireTypeSDK(cn.skyfire.best.sdk.application.SkyFireApplication)
//享玩QuickSDK（com.quicksdk.QuickSdkApplication）
//pptv(com.vas.vassdk.VasApplication)
//真爱玩游戏谷（com.yxgsdk.common.YXGApplication）
//大臣游戏（com.dc.sdk.DCApplication）
//头条联运sdk(com.bytedance.ugame.rocketapi.RocketApplication   
//大秦SDK（com.renard.hjyGameSs.SDKApplication）
//中顺和盈（com.u8.sdk.U8Application）
//鼎多多（com.yysy.yygamesdk.app.YyApplication）
//悦游(com.rhsdk.RhApplication)
//天宇游 曼巴（com.tygrm.sdk.TYRApplication）
//簡玩SDK（com.datasdk.DataSdkApplication）
//悠谷 BT渠道（com.s.plugin.platform.SPlatformApplication）
//欢聚游（com.yu.sdkapi.ApiApplication）
//6533（com.sqk.sdk.SuperSYApplication）
//quick（com.quicksdk.QuickSdkApplication）
//大喜互娱cn.gogaming.api.IGameApplication 
//手盟（mobi.shoumeng.integrate.game.method.GameApplication）
//颖趣 （com.yq.fm.sdk.YQFMApplication)
//当玩（ com.dwg.sdk.DGGameApplication）
//闲闲（com.wanyugame.wygamesdk.app.WyApplication）
//顺游（com.shunyou.sdk.sy.ShunyouApplication）
//IUNO yingpai( com.iuno.sdk.YPApp )
//心维（com.app.yjy.game.OneNineGameApplication）
//瀚城（com.mikusdk.common.MKApplication）
//创亿（com.cy.sdk.HdApplication）
//玖趣（com.sboran.game.sdk.SboRanApplication）
//阅文（com.yw.game.plugin.BaseApplication）
//朋克(com.hnxd.pksuper.protocol.sdk.PKGameApplication)
//m1699(com.huosdk.huounion.sdk.HuoUnionApplication)
//怪猫(com.game.sdk.GMApplication)

//chitu (com.chitu350.game.sdk.ChituApplication)

public class MyApplication extends Application{

	public static Context context;
	private static MyApplication myself;
	/** 容纳所有activity */
	private List<MyActivity> activityList = new LinkedList<MyActivity>();
	/** 容纳不同游戏的activity */
	private Map<String, List<MyActivity>> activitymap = new HashMap<String, List<MyActivity>>();

	/** 容纳所有lua文件名 */
	public List<String> lualist = new LinkedList<String>();

	/** Lua解析和执行由此对象完成 */
	private LuaState mLuaState;

	/** 有木有完成Lua初始化 */
	public boolean luainitok = false;
	/** 有木有完成Lua初始化 */
	public boolean luaupdateok = false;

	/** APK升级已走 */
	public boolean bvupdateAPK = false;

	/** 游戏信息 */
	private GameArgs gameargs = null;

	/** 游戏实例表 */
	private Map<String, GameArgs> gamemap = null;
	/** 回调接口集合 */
	private Map<String, ITestListener> callbacks = null;

	private Map<String, List<GridViewModel>> firstCardmap = null;

	private Map<String, List<GridViewModel>> secondCardmap = null;

	/** ORDERS */
	private Map<String, List<PayBackModel>> orders = null;

	/** 当前游戏KEY */
	public String curKey = "";
	/** 当前游戏pid */
	public String curPid = "";

	/** 逻辑主类 */
	public LogicMain logicmain;

	/** HttpCallBack */
	public HttpCallBack httpback = null;
	/** HttpCallBack */
	public HttpCallBack httpbackkjava = null;
	/** HttpErrorHandler */
	public HttpErrorHandler errorhandler = null;
	/** android.content.DialogInterface.OnClickListener */
	public OnClickListener onclick = null;

	/** 是否完全退出 */
	public boolean isExit;

	/** 主要用来监听系统广播 */
//	private HomeKeyEventBroadCastReceiver broadcast;
	/** 数据库类 */
	private Dao db = null;
	/** 是否已发送MYPID到远程服务 */
	private boolean isPostMyPid = false;

	public String re1 = "";
	public String re2 = "";
	public String re3 = "";

	public MyApplication(Context contex) {
		super();
		if (context == null) {
			context = this;
		}
	}

	public MyApplication() {
		super();
		if (context == null) {
			context = this;
		}
	}

	/**
	 * 创建全局变量 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
	 * 
	 * 这里使用了在Application中添加数据的方法实现全局变量
	 * 
	 */
	private LayoutParams wmParams = new LayoutParams();

	public LayoutParams getMywmParams() {
		return wmParams;
	}

	/** 锁返回键 */
	public boolean lockback = false;
	
	@Override
	public void onCreate() {
		System.out.println("111111111111111111111");
		
//		MyCrashHandler mCrashHandler=MyCrashHandler.getInstance();
//		mCrashHandler.init(this);
		
		super.onCreate();
		try {
			ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			boolean isDebug = appInfo.metaData.getBoolean("ASDK_LOG");
			MLog.setDebug(isDebug);
		} catch (Exception e) {
			Log.i("asdk", "log标识获取失败");
		}
		
		if (context == null) {
			context = this;
		}
		myself = this;
		luainitok = false;
		luaupdateok = false;
		isExit = false;
		// gameargs = new GameArgs();
		gamemap = new HashMap<String, GameArgs>();
		callbacks = new HashMap<String, ITestListener>();
		firstCardmap = new HashMap<String, List<GridViewModel>>();
		secondCardmap = new HashMap<String, List<GridViewModel>>();
		orders = new HashMap<String, List<PayBackModel>>();

		logicmain = new LogicMain();
		checkSD();
		initLuaState();

		// 解析头文件
		// parseData();
		// 加载通用文件
		// LuaTools.loadUpdate();

		// 创建广播实现类
//		broadcast = new HomeKeyEventBroadCastReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
//		context.registerReceiver(broadcast, filter);

		db = new Dao(context);

		if (!isPostMyPid()) {
			// 当前游戏的KEY传给后台服务
			try {
				Intent intent = new Intent(context, MyRemoteService.class);
				Bundle bu = new Bundle();
				bu.putString("flag", "getmypid");
				bu.putInt("mypid", android.os.Process.myPid());
				intent.putExtras(bu);
				this.startService(intent);
				setPostMyPid(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SkipActivity.APPOnCreate(this);

	}
	@Override
	public void attachBaseContext(Context base) {
	    super.attachBaseContext(base);
	    System.out.println("attachBaseContext");	    
//	    MultiDex.install(this);
	    SkipActivity.APPAttachBaseContext(this,base);
	    
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SkipActivity.APPConfigurationChanged(this,newConfig);
		
	}

	/**
	 * 解析数据头头头头头头
	 * 
	 * @param
	 */
	public void parseData() {
		if (Configs.USEXPKG || Configs.ALLUSED) {
			logicmain.getGameCPinfo();
		}
	}

	/**
	 * 添加Activity到容器中
	 * 
	 * @param activity
	 */
	public void addActivity(MyActivity activity) {
		String key = getGameArgs().getKey();
		activityList = activitymap.get(key);
		if (activityList == null) {
			activityList = new LinkedList<MyActivity>();
			activitymap.put(key, activityList);
		}
		activityList.add(activity);
	}

	/**
	 * 遍历所有Activity并finish
	 */
	@SuppressWarnings("deprecation")
	public void exit(int mypid) {
		isExit = true;

		for (String key : activitymap.keySet()) {
			activityList = activitymap.get(key);
			for (Activity activity : activityList) {
				if (activity instanceof MyActivity) {
					// ((MyActivity)activity).setResults(100);
				}
				activity.finish();
			}
		}
		activityList.clear();
		activitymap.clear();

		if (logicmain.htback != null) {
			logicmain.htback.endThread();
		}

		lualist.clear();
		mLuaState.close();
		gameargs = null;
		gamemap.clear();
		logicmain = null;
//		if (broadcast != null) {
//			context.unregisterReceiver(broadcast);
//			broadcast = null;
//		}
		getDb().closeDb();

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (android.os.Build.VERSION.SDK_INT < 8) {// 2.2版本
			activityManager.restartPackage(getPackageName());
		} else {
			if (mypid != 0) {
				MLog.s("kill -----> " + mypid);
				android.os.Process.killProcess(mypid);
			}
			activityManager.killBackgroundProcesses(getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());
		}

		// System.exit(0);
	}

	/**
	 * 返回游戏接口
	 */
	public void backToGame() {
		for (Activity activity : getCurActivityList()) {
			if(activity!=null){
				activity.finish();
			}
		}
		if(activityList!=null){
			activityList.clear();
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 防止内存泄露，清理相关数据务必调用SDK结束接口
		SkipActivity.APPOnTerminate(this);
		
	}
	


	/**
	 * 返回当前活动集合
	 * 
	 * @return
	 */
	public List<MyActivity> getCurActivityList() {
		return activitymap.get(getGameArgs().getKey());
	}

	/**
	 * 栈顶活动名称
	 * 
	 * @return
	 */
	public ComponentName getTopActivity() {
		ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null) {
			return (runningTaskInfos.get(0).topActivity);
		} else {
			return null;
		}
	}

	/**
	 * 加载SD卡相关信息
	 */
	public void checkSD() {
		FilesTool.ExistSDCard();
		long size = FilesTool.getSDFreeSize();

		MLog.a("SDFreeSize----->" + size+"MB");
		System.out.println("Configs.ASDKROOT----------------->"+Configs.ASDKROOT);
	}

	public LuaState getmLuaState() {
		return mLuaState;
	}

	/**
	 * 初始化Lua环境
	 */
	public void initLuaState() {
		if (lualist != null) {
			lualist.clear();
		}
		if (logicmain != null && logicmain.headerlist != null) {
			logicmain.headerlist.clear();
		}
		if (mLuaState != null) {
			mLuaState.close();
		}
		mLuaState = LuaStateFactory.newLuaState();
		mLuaState.openLibs();

		logicmain.setmLuaState(mLuaState);

		luainitok = false;

		// 如果这样，自动更新走完了就不能跑了
		// gameargs.setInit(false);

		// 每个activity启动都会解析数据头
		// 意味着要拷贝dat文件到sd or files

	}

	/**
	 * 得到当前全局游戏实体
	 * 
	 * @return
	 */
	public GameArgs getGameArgs() {
		return gameargs;
	}

	/**
	 * 设置当前全局游戏实体
	 * 
	 * @param gameargs
	 */
	public void setGameArgs(GameArgs gameargs) {
		this.gameargs = gameargs;
	}

	/**
	 * 将游戏参数放入哈希表
	 * 
	 * @param key
	 * @param g
	 */
	public void putGameArgsMap(String key, GameArgs g) {
		gamemap.put(key, g);
	}

	/**
	 * 根据游戏KEY取游戏实体
	 * 
	 * @return
	 */
	public GameArgs getGameArgsMapKey() {
		return gamemap.get(curKey);
	}

	/**
	 * 根据游戏PID取游戏实体
	 * 
	 * @return
	 */
	public GameArgs getGameArgsMapPid() {
		return gamemap.get(curPid);
	}

	/**
	 * 得到游戏哈希表
	 * 
	 * @return
	 */
	public Map<String, GameArgs> getGamemap() {
		return gamemap;
	}
	public Map<String, ITestListener> getCallback() {
		return callbacks;
	}

	/**
	 * 充值首页哈希表
	 * 
	 * @return
	 */
	public Map<String, List<GridViewModel>> getFirstCardmap() {
		return firstCardmap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, List<GridViewModel>> getSecondCardmap() {
		return secondCardmap;
	}

	/**
	 * 充值回调数据集合
	 * 
	 * @return
	 */

	public List<PayBackModel> getbList(String key) {
		if (orders.containsKey(key)) {
			return orders.get(key);
		}
		orders.put(key, new ArrayList<PayBackModel>());
		return orders.get(key);
	}

	public Map<String, List<PayBackModel>> getOrders() {
		return orders;
	}

	public static MyApplication getAppContext() {
		return myself;
	}

	public LogicMain getLogicmain() {
		return logicmain;
	}

	public Dao getDb() {
		return db;
	}

	public boolean isPostMyPid() {
		return isPostMyPid;
	}

	public void setPostMyPid(boolean isPostMyPid) {
		this.isPostMyPid = isPostMyPid;
	}

//	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
//
//		static final String SYSTEM_REASON = "reason";
//		static final String SYSTEM_HOME_KEY = "homekey";// home key
//		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//				String reason = intent.getStringExtra(SYSTEM_REASON);
//				if (reason != null) {
//					if (reason.equals(SYSTEM_HOME_KEY)) {
//
//						// home key处理点
//						// MyApplication.this.exit();
//
//					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
//						// long home key处理点
//					}
//				}
//			}
//		}
//	}

}
