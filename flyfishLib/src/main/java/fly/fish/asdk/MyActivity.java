package fly.fish.asdk;

import java.util.HashMap;
import java.util.Map;

import org.keplerproject.luajava.LuaState;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import fly.fish.aidl.MyRemoteService;
import fly.fish.beans.GameArgs;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

public class MyActivity extends Activity {
	/** 类名 */
	public String tag = "MyActivity";
	/** Lua解析和执行由此对象完成 */
	public LuaState mLuaState;
	/** UI处理 */
	public MyHandler myhand;
	/** 切屏与压栈返回（要不要重新加载） */
	public boolean isNeedReload = false;
	/** 图片集合 */
	public Map<String, Bitmap> bitmap = null;
	/** 等待框 */
	public ProgressDialog dialog = null;

	/** 对话框 */
	public AlertDialog builder = null;

	public boolean initok = true;
	// public String re1 = "";
	// public String re2 = "";
	// public String re3 = "";
	
	public AlertDialog alertDialog = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MLog.s(this + " ----> onCreate");
		// 隐去标题栏（应用程序的名字）
		// 隐去状态栏部分(电池等图标和一切修饰部分)
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置竖屏
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		configFontScale(getResources(), 1.0f);
	}

	public static void configFontScale(Resources resource, float fontScale) {
		if (resource == null) {
			return;
		}
		try {
			Configuration c = resource.getConfiguration();
			c.fontScale = fontScale; //字体缩放设置为1.0
			resource.updateConfiguration(c, resource.getDisplayMetrics());
		} catch (Exception e) {
			//do what you want
		}
	}

	/**
	 * 系统初始化（升级）
	 */
	public void init() {
		MLog.s(this + " super ----> initbegin");

		int rl = getClass().getName().lastIndexOf('.');
		tag = getClass().getName().substring(rl + 1);

		isNeedReload = true;
		bitmap = new HashMap<String, Bitmap>();
		getApp();
		mLuaState = getApp().getmLuaState();
		setMyhand(new MyHandler(this));

		initLua();

		// 正确则加载UI
		initok = initArgsCreate();
		if (initok) {
			getApp().logicmain.initUpdate(this); // 加载或者解析头文件,唤醒线程，进入UI程序
			MLog.s(this + " 启动线程");
		}
		MLog.s(this + " super ----> initbegin10");
		getApp().addActivity(this);
		MLog.s(this + " super ----> initend");

		/*
		 * if (getResources().getConfiguration().orientation ==
		 * Configuration.ORIENTATION_LANDSCAPE) {
		 * 
		 * } else if(getResources().getConfiguration().orientation ==
		 * Configuration.ORIENTATION_PORTRAIT) {
		 * 
		 * }
		 */
	}

	/**
	 * 唤醒后台线程
	 */
	public void notifyBackThread() {
		synchronized (getApp()) {
			getApp().notify();
			MLog.s("Notify thread thread");
		}
	}

	/**
	 * 初始化lua开发环境
	 */
	public void initLua() {

		MLog.s(this + " ----> onResume start ");
		mLuaState = getApp().getmLuaState();
		mLuaState.pushJavaObject(this);
		mLuaState.setGlobal("activity");
		mLuaState.pushJavaObject(getResources());
		mLuaState.setGlobal("res");
		MLog.s(this + " ----> onResume doing1 ");

	}

	/**
	 * 初始化参数（重来初始化）
	 */
	public boolean initArgsCreate() {
		if (getApp().re1 == null || getApp().re1.equals("")) {

			/*
			 * Map<String,String> urls =
			 * MyApplication.getAppContext().getDb().getInfos(); getApp().re1 =
			 * urls.get("accountserver"); getApp().re2 = urls.get("payserver");
			 * getApp().re3 = urls.get("notify_url");
			 */

			/*
			 * Map<String, String> map = new HashMap<String, String>();
			 * ContentResolver contentResolver = this.getContentResolver(); Uri
			 * url =
			 * Uri.parse("content://"+ShareContent.PROVIDER_URL+"/config/2");
			 * Cursor cursor = contentResolver.query(url, new String[]
			 * {"name","urlabc","gamekey"}, "gamekey = \'"+getApp().curKey+"\'",
			 * null, null);
			 * 
			 * while (cursor.moveToNext()) {
			 * map.put(cursor.getString(cursor.getColumnIndex
			 * ("name")),cursor.getString(cursor.getColumnIndex("urlabc")));
			 * MLog.s("name ==== " +
			 * cursor.getString(cursor.getColumnIndex("name")));
			 * MLog.s("urlabc ==== " +
			 * cursor.getString(cursor.getColumnIndex("urlabc")));
			 * MLog.s("gamekey ==== " +
			 * cursor.getString(cursor.getColumnIndex("gamekey"))); }
			 * cursor.close();
			 * 
			 * getApp().re1 = map.get("accountserver"); getApp().re2 =
			 * map.get("payserver"); getApp().re3 = map.get("notify_url");
			 * 
			 * MLog.s("accountserver ----> " + getApp().re1);
			 * MLog.s("payserver --------> " + getApp().re2);
			 * MLog.s("notify_url -------> " + getApp().re3);
			 */

		}
		return true;
	}

	/**
	 * 初始化参数（重来初始化）
	 */
	public void initArgsResume() {

		/*
		 * Map<String,String> urls = getApp().getDb().getInfos(); re1 =
		 * urls.get("accountserver"); re2 = urls.get("payserver"); re3 =
		 * urls.get("notify_url");
		 */
		getApp().curPid = getTaskId() + "";
		GameArgs gameargs = getApp().getGameArgsMapPid();

		MLog.s(this + " ----> initArgs start " + getApp().curPid);

		if (gameargs != null && initok) {
			getApp().curKey = gameargs.getKey();// 获得当前KEY
			getApp().setGameArgs(gameargs);// 当前使用的游戏对象

			MLog.s(this + " ----> initArgs doing ");

			// 当前游戏的KEY传给后台服务
			Intent intent = new Intent(this, MyRemoteService.class);
			Bundle bu = new Bundle();
			bu.putString("flag", "curkey");
			bu.putString("key", getApp().curKey);
			intent.putExtras(bu);
			this.startService(intent);
		}

		MLog.s(this + " ----> initArgs end " + android.os.Process.myPid() + "-" + android.os.Process.myTid() + "-" + android.os.Process.myUid());
	}

	/**
	 * 新参数，相对于老参数
	 * 
	 * @param intent
	 */
	private void initArgs2b(Intent intent) {
		if (intent != null) {
			Bundle bu = intent.getExtras();
			if (bu != null) {
				getApp().re1 = bu.getString("re1");
				getApp().re2 = bu.getString("re2");
				getApp().re3 = bu.getString("re3");

				GameArgs gameargs = bu.getParcelable("gameargs");
				getApp().setGameArgs(gameargs);

				MLog.s("CPID = " + gameargs.getCpid());
				MLog.s("GAMENO = " + gameargs.getGameno());
				MLog.s("KEY = " + gameargs.getKey());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MLog.s(this + " ----> onResume");

		initLua();
		initArgsResume();

		// ///走升级\\\\\\
		// ///checkEnvNET();\\\\\\\
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		MLog.s(this + " ----> onNewIntent");

		// initArgs2b(intent);

		// getMyhand().sendEmptyMessage(0);

	}

	/**
	 * 
	 * 不重新载入（会触发此函数） android:configChanges="orientation|keyboardHidden"
	 * （不配置的话，默认会重新载入） 当横屏变竖屏的时候，他会调用两次onConfigurationChanged， 而竖屏转横屏时他只调用一次
	 * 
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		MLog.s("不重新载入");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MLog.a("tag",this + " ----> onDestroy");

		myhand = null;

		if (dialog != null) {
			dialog.cancel();
		}

		releaseRes();

		if (!getApp().isExit) {// 仅本activity销毁
			if (!getApp().getCurActivityList().isEmpty()) {
				if (appExit()) {// 整个应用销毁
					MLog.a("tag",this + " ----> application game over (I not kill it) ");
					// deleteKey();
					// getApp().exit();

				} else {// 仅本activity销毁
					if (getApp().getCurActivityList().contains(this)) {
						MLog.a("tag",this + " ----> only myself game over");
						getApp().getCurActivityList().remove(this);
					}
				}
			} else {
				// deleteKey();
			}
		} else {// 整个应用销毁
			MLog.s(this + " ----> application game over");
		}
		bitmap.clear();
	}

	/**
	 * 删除数据库中关于key的记录
	 * 
	 * @param key
	 */
	public void deleteKey() {
		// ContentResolver contentResolver = this.getContentResolver();
		// Uri url = Uri.parse("content://" + ShareContent.PROVIDER_URL +
		// "/config/2");
		// contentResolver.delete(url, "name=" + "\'" + getApp().curKey + "\'",
		// null);
	}

	/**
	 * 收集Bitmap引用
	 * 
	 * @param bit
	 */
	public void addBitmap(String name, Bitmap bit) {
		bitmap.put(name, bit);
	}

	public Bitmap getBitmap(String name, int flag) {
		Bitmap bit = null;
		if (bitmap.containsKey(name)) {
			bit = bitmap.get(name);
		} else {
			bit = FilesTool.getBitmap(name, flag);
			addBitmap(name, bit);
		}
		return bit;
	}

	/**
	 * 释放资源
	 */
	public void releaseRes() {
		MLog.s(this + " ----> releaseRes");
		for (Bitmap v : bitmap.values()) {
			FilesTool.releaseBitmap(v);
		}
	}

	/**
	 * 当前栈顶activity是不是第一个activity 当前活动栈集合是否已到头
	 * 
	 * @return
	 */
	public boolean appExit() {
		String s1 = getApp().getCurActivityList().get(0).toString();
		String s2 = getApp().getTopActivity().getClassName();
		if (s1.contains(s2)) {
			return true;
		}
		return false;
	}

	/**
	 * 开始环境升级 （检查网络）
	 */
	public void checkEnvNET() {
		if (PhoneTool.isNetConnected(this)) {
			getApp().logicmain.initUpdate(this);
		} else {
			PhoneTool.setNetworkMethod(this);
		}
	}

	/**
	 * 监听返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		MLog.s("lockback" + tag + "---> " + getApp().lockback);
		// 退出activity
		boolean bo = true;
		if (!getApp().lockback) {
			bo = super.onKeyDown(keyCode, event);
		}
		// 退出程序

		// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1)
		// {
		// getApp().exit(keyCode);
		// return true;
		// }

		return bo;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MLog.s("X ---> " + event.getX() + "\nY ---> " + event.getY());
		return super.onTouchEvent(event);
	}

	/**
	 * 设置返回数据
	 * 
	 * @param result
	 */
	public void setResults(int result) {
		// 数据是使用Intent返回
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("self", getApp().getGameArgs().getSelf());
		// 设置返回数据
		setResult(result, intent);
		// 关闭Activity
		// finish();
		MLog.s(this + " ----> set result value");
	}

	/**
	 * 得到全局应用
	 * 
	 * @return
	 */
	public MyApplication getApp() {
		// return ((MyApplication) getApplication());
		return MyApplication.getAppContext();
	}

	public MyHandler getMyhand() {
		return myhand;
	}

	public void setMyhand(MyHandler myhand) {
		this.myhand = myhand;
	}

	/**
	 * 返回锁的状态
	 * 
	 * @return
	 */
	public boolean isLockback() {
		return getApp().lockback;
	}

	/**
	 * 设置是否锁返回键
	 * 
	 * @param lockback
	 */
	public void setLockback(boolean lockback) {
		getApp().lockback = lockback;
	}

}
