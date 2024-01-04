package fly.fish.asdk;

import java.util.HashMap;
import java.util.Map;
import org.keplerproject.luajava.LuaState;

import fly.fish.aidl.OutFace;
import fly.fish.beans.GameArgs;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;
import fly.fish.tools.ManifestInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginActivity extends MyActivity {
	private static Activity outContext;
	private LinearLayout mLayout;
	private EditText account;
	private EditText password;
	private static boolean isLoadError = false;
	public String tag = "LoginActivity";

	public static Activity getActivity(){
		isLoadError = true;
		return outContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		outContext = this;
		mLayout = new LinearLayout(this);
		setContentView(mLayout);
		init();
	}

	@Override
	public void finish() {
		if (isLoadError){
			isLoadError= false;
			synchronized (mLuaState) {
				mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "loginCallBack");
				LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
			}
		}
		super.finish();
	}

	/**
	 * 初始化lua开发环境
	 */
	public void initLua() {
		super.initLua();
		mLuaState.pushJavaObject(mLayout);
		mLuaState.setGlobal("rootview");
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
				gameargs.setPublisher(OutFace.getInstance().getPublisher());
				gameargs.setName(d);
				gameargs.setSelf(e);

				MLog.s(this + " ------------ initArgsCreate ");
				if (getApp().curKey != null) {
					getApp().putGameArgsMap(getApp().curKey, gameargs);
					getApp().putGameArgsMap(getApp().curPid, gameargs);

					// 设置成当前使用
					getApp().setGameArgs(gameargs);

					// 查找数据库中有没有对应数据
					Map<String, String> map = new HashMap<>();
					SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
					map.put("gamekey", sharedPreferences.getString("gamekey", ""));
					map.put("accountserver", sharedPreferences.getString("accountserver", ""));
					map.put("payserver", sharedPreferences.getString("payserver", ""));
					map.put("notify_url", sharedPreferences.getString("notify_url", ""));
					map.put(sharedPreferences.getString("gamekey", ""), sharedPreferences.getString(sharedPreferences.getString("gamekey", ""), ""));
					map.put("othersdkextdata1", sharedPreferences.getString("othersdkextdata1", ""));
					map.put("othersdkextdata2", sharedPreferences.getString("othersdkextdata2", ""));
					map.put("othersdkextdata3", sharedPreferences.getString("othersdkextdata3", ""));
					map.put("othersdkextdata4", sharedPreferences.getString("othersdkextdata4", ""));
					map.put("othersdkextdata5", sharedPreferences.getString("othersdkextdata5", ""));
					getApp().re1 = map.get("accountserver");
					getApp().re2 = map.get("payserver");
					getApp().re3 = map.get("notify_url");
					String status = map.get(getApp().curKey);

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ASDKReport.getInstance().startSDKReport(this, EventManager.SDK_EVENT_CLOSE_LOGIN_VIEW);

			// 通知远程服务更新头文件
			boolean metaBoolean = ManifestInfo.getMetaBoolean(this, "ENABLE_CLOSE_LOGIN", true);
			if (metaBoolean) {
				synchronized (mLuaState) {
					mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "loginCallBack");
					LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
				}

				return super.onKeyDown(keyCode, event);
			}

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

}
