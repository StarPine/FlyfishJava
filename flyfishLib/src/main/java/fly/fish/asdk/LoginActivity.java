package fly.fish.asdk;

import java.util.HashMap;
import java.util.Map;
import org.keplerproject.luajava.LuaState;

import fly.fish.aidl.OutFace;
import fly.fish.beans.GameArgs;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.FilesTool;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginActivity extends MyActivity {
	private Activity outContext;
	private LinearLayout mLayout;
	private EditText account;
	private EditText password;
	public String tag = "LoginActivity";
	public LoginActivity() {
		super();
	}

	public LoginActivity(Context context) {
		super();
		outContext = (Activity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = new LinearLayout(this);
		setContentView(mLayout);
		init();

	}

	@Override
	public void finish() {
		MLog.a(tag,this + " ---->LoginActivity finish  is runing!!");
		super.finish();
	}

	@Override
	protected void onPause() {
		MLog.a(tag,this + " ---->LoginActivity onPause  is runing!!");
		super.onPause();
	}

	@Override
	protected void onResume() {// 出栈从这里进入
		super.onResume();

	}

	@Override
	protected void onStop() {
		MLog.a(tag,this + " ---->LoginActivity onStop  is runing!!");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MLog.a(tag,this + " ---->LoginActivity onDestroy  is runing!!");
	}

	/**
	 * 系统初始化
	 */
	public void init() {
		super.init();
	}

	private void outInit() {
		super.init();
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
	 * 初始化参数（创建型）
	 */
	public boolean initArgsCreate() {
		super.initArgsCreate();

		// 获得参数
		Intent itn = getIntent();
		if (itn != null) {
			Bundle bu = itn.getExtras();
			if (bu != null) {
				String a = bu.getString("cpid");
				String b = bu.getString("gameid");
				String c = bu.getString("key");
				String d = bu.getString("gamename");
				String e = bu.getString("callBackData");

				// 游戏KEY
				getApp().curKey = c;
				getApp().curPid = getTaskId() + "";

				GameArgs gameargs = getApp().getGameArgsMapKey();
				if (gameargs == null) {
					gameargs = new GameArgs();
				}
				gameargs.setCpid(a);
				gameargs.setGameno(b);
				gameargs.setKey(c);
				gameargs.setPublisher(OutFace.getInstance(null).getPublisher());
				gameargs.setName(d);
				gameargs.setSelf(e);

				MLog.s(this + " ------------ initArgsCreate ");
				if (getApp().curKey != null) {
					getApp().putGameArgsMap(getApp().curKey, gameargs);
					getApp().putGameArgsMap(getApp().curPid, gameargs);

					// 设置成当前使用
					getApp().setGameArgs(gameargs);

					// 查找数据库中有没有对应数据
					Map<String, String> map = new HashMap<String, String>();
					// ContentResolver contentResolver =
					// this.getContentResolver();
					// Uri url = Uri.parse("content://" +
					// ShareContent.PROVIDER_URL + "/config/2");
					// Cursor cursor = contentResolver.query(url, new String[] {
					// "name", "urlabc", "gamekey" }, "gamekey = \'" +
					// getApp().curKey + "\'", null, null);
					//
					// while (cursor.moveToNext()) {
					// map.put(cursor.getString(cursor.getColumnIndex("name")),
					// cursor.getString(cursor.getColumnIndex("urlabc")));
					// MLog.a("name ==== " +
					// cursor.getString(cursor.getColumnIndex("name")));
					// MLog.a("urlabc ==== " +
					// cursor.getString(cursor.getColumnIndex("urlabc")));
					// MLog.a("gamekey ==== " +
					// cursor.getString(cursor.getColumnIndex("gamekey")));
					// }
					// cursor.close();
					SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
					map.put("gamekey", sharedPreferences.getString("gamekey", ""));
//					MLog.a("gamekey" + "======out========" + sharedPreferences.getString("gamekey", ""));
					map.put("accountserver", sharedPreferences.getString("accountserver", ""));
//					MLog.a("accountserver" + "======out========" + sharedPreferences.getString("accountserver", ""));
					map.put("payserver", sharedPreferences.getString("payserver", ""));
//					MLog.a("payserver" + "=======out=======" + sharedPreferences.getString("payserver", ""));
					map.put("notify_url", sharedPreferences.getString("notify_url", ""));
//					MLog.a("notify_url" + "======out========" + sharedPreferences.getString("notify_url", ""));
					map.put(sharedPreferences.getString("gamekey", ""), sharedPreferences.getString(sharedPreferences.getString("gamekey", ""), ""));
//					MLog.a(sharedPreferences.getString("gamekey", "") + "====out==========" + sharedPreferences.getString(sharedPreferences.getString("gamekey", ""), ""));
					map.put("othersdkextdata1", sharedPreferences.getString("othersdkextdata1", ""));
//					MLog.a("othersdkextdata1" + "=======out=======" + sharedPreferences.getString("othersdkextdata1", ""));
					map.put("othersdkextdata2", sharedPreferences.getString("othersdkextdata2", ""));
//					MLog.a("othersdkextdata2" + "======out========" + sharedPreferences.getString("othersdkextdata2", ""));
					map.put("othersdkextdata3", sharedPreferences.getString("othersdkextdata3", ""));
//					MLog.a("othersdkextdata3" + "=====out=========" + sharedPreferences.getString("othersdkextdata3", ""));
					map.put("othersdkextdata4", sharedPreferences.getString("othersdkextdata4", ""));
//					MLog.a("othersdkextdata4" + "======out========" + sharedPreferences.getString("othersdkextdata4", ""));
					map.put("othersdkextdata5", sharedPreferences.getString("othersdkextdata5", ""));
//					MLog.a("othersdkextdata5" + "=======out=======" + sharedPreferences.getString("othersdkextdata5", ""));
					getApp().re1 = map.get("accountserver");
					getApp().re2 = map.get("payserver");
					getApp().re3 = map.get("notify_url");
					String status = map.get(getApp().curKey);

					// 查找数据库中有没有对应数据
					/*
					 * String status = null; ContentResolver contentResolver =
					 * this.getContentResolver(); Uri url =
					 * Uri.parse("content://"
					 * +ShareContent.PROVIDER_URL+"/config/2"); Cursor cursor =
					 * contentResolver.query(url, new String[]{"urlabc"},
					 * "name="+"\'"+getApp().curKey+"\'" +
					 * "and gamekey="+"\'"+getApp().curKey+"\'", null, null);
					 * while(cursor.moveToNext()){ status =
					 * cursor.getString(cursor.getColumnIndex("urlabc")); }
					 */

					if (status == null || (status != null && !status.equals("success"))) {
						MLog.s(this + " false AAAA");
						return false;
					}
					MLog.s(this + " true BBBB");
					return true;
				} else {
					MLog.s(this + " false CCCC");
					return false;
				}
			}
		}
		MLog.s(this + " false DDDD");
		return false;
	}

	/**
	 * 初始化参数（重来型）
	 */
	public void initArgsResume() {
		super.initArgsResume();
		String status = MyApplication.context.getSharedPreferences("user_info", 0).getString(getApp().curKey, "");
		// String status = getApp().getDb().getInitStatus(getApp().curKey);
//		if (status == null || (status != null && !status.equals("success"))) {
//
//			// final boolean bo = FilesTool.loadLuaScript("lua/cancelback.lua");
//
//			AlertDialog.Builder builder = new Builder(this);
//			builder.setMessage("请先成功初始化");
//			builder.setTitle("通知");
//			builder.setCancelable(false);
//			builder.setPositiveButton("确认", new OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//
//					// 都没有成功初始化，怎么给回调
//					/*
//					 * if(bo){ synchronized (mLuaState) {
//					 * mLuaState.getGlobal("cancelback"); int index =
//					 * mLuaState.getTop(); mLuaState.getField(index,
//					 * "loginCallBack"); LuaTools.dbcall(mLuaState, 0, 0);//
//					 * 代表0个参数，0个返回值 } }else{ cancelCallback(); }
//					 */
//
//					dialog.dismiss();
//					LoginActivity.this.finish();
//				}
//			});
//			builder.create().show();
//
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ASDKReport.getInstance().startSDKReport(this, EventManager.SDK_EVENT_CLOSE_LOGIN_VIEW);

			// 通知远程服务更新头文件
			/*
			 * Intent intent=new Intent(this, MyRemoteService.class); Bundle bu
			 * = new Bundle(); bu.putString("sessionid","0");
			 * bu.putString("accountid","0"); bu.putString("status","1");
			 * bu.putString("flag","login"); bu.putString("key",
			 * MyApplication.getAppContext().getGameArgs().getKey());
			 * intent.putExtras(bu); startService(intent);
			 */

			// 通知远程服务更新头文件
//			synchronized (mLuaState) {
//				mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "loginCallBack");
//				LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
//			}
//
//			return super.onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public EditText getAccount() {
		return account;
	}

	public void setAccount(EditText account) {
		this.account = account;
	}

	public EditText getPassword() {
		return password;
	}

	public void setPassword(EditText password) {
		this.password = password;
	}

	/**
	 * 取消登陆
	 */
	/*
	 * public void cancelCallback(){ Intent intent=new Intent(this,
	 * MyRemoteService.class); Bundle bu = new Bundle();
	 * bu.putString("sessionid","0"); bu.putString("accountid","0");
	 * bu.putString("status","1"); bu.putString("flag","login");
	 * bu.putString("custominfo",getApp().getGameArgs().getSelf());
	 * bu.putString("key", getApp().getGameArgs().getKey());
	 * intent.putExtras(bu); startService(intent); }
	 */
}
