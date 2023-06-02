package fly.fish.asdk;

/**
 * 充值首页
 * 
 */
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.LinearLayout;

//import com.ulopay.android.h5_library.manager.CheckOderManager;
//import com.ulopay.android.h5_library.manager.CheckOderManager.QueryPayListener;

import fly.fish.aidl.MyRemoteService;
import fly.fish.beans.GameArgs;
import fly.fish.othersdk.WXinSDK;
import fly.fish.othersdk.YXWebActivity;
import fly.fish.tools.FilesTool;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

public class ChargeActivity extends MyActivity{
	private LinearLayout myLay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLay = new LinearLayout(this);
		setContentView(myLay);
		init();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (YXWebActivity.isIspayfinish())
		{
			YXWebActivity.setIspayfinish(false);
			finish();
		}
//		if(!"".equals(WXinSDK.getPrepayid())){
//			new CheckOderManager().checkState(ChargeActivity.this, WXinSDK.getPrepayUrl(),WXinSDK.getPrepayid(), new QueryPayListener() {
//				
//				@Override
//				public void getPayState(String payState) {
//					//返回支付状态，做对应的UI和业务操作
//                    if ("SUCCESS".equalsIgnoreCase(payState)) {
//                    	Bundle bundle = getIntent().getExtras();
//            			getIntent().setClass(ChargeActivity.this, MyRemoteService.class);
//            			bundle.putString("flag", "sec_confirmation");
//            			getIntent().putExtras(bundle);
//            			ChargeActivity.this.startService(getIntent());
//                    } else {
//                    	synchronized (mLuaState) {
//            				mLuaState.getGlobal("chagerInfolua");
//            				int index = mLuaState.getTop();
//            				mLuaState.getField(index, "cancelCallBack");
//            				LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
//            			}
//                    }
//                    getApp().backToGame();
//            		
//                    ChargeActivity.this.finish();
//				}
//			});
//			WXinSDK.setPrepayid("");
//		}
	}

	/**
	 * 系统初始化
	 */
	public void init() {
		super.init();
	}

	/**
	 * 初始化参数
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
				String c = bu.getString("gamename");
				String d = bu.getString("merchantsOrder");
				String e = bu.getString("url");
				String f = bu.getString("account");
				String g = bu.getString("desc");
				String h = bu.getString("callBackData");
				String i = bu.getString("key");

				// 游戏KEY
				getApp().curKey = i;
				getApp().curPid = getTaskId() + "";

				GameArgs gameargs = getApp().getGameArgsMapKey();
				if (gameargs == null) {
					gameargs = new GameArgs();
				}
				gameargs.setCpid(a);
				gameargs.setGameno(b);
				gameargs.setKey(i);
				gameargs.setName(c);
				gameargs.setSelf(h);
				gameargs.setCustomorderid(d);
				gameargs.setCallbackurl(e);
				gameargs.setSum(f);
				gameargs.setDesc(g);
				String publisher = FilesTool.getPublisherString()[0];
				gameargs.setPublisher(publisher);

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
					// int count = 0;
					// while (cursor.moveToNext()) {
					// map.put(cursor.getString(cursor.getColumnIndex("name")),
					// cursor.getString(cursor.getColumnIndex("urlabc")));
					// MLog.a("======================out  database  " + count +
					// " Internet Protocol===========================");
					// MLog.a("name ==== " +
					// cursor.getString(cursor.getColumnIndex("name")));
					// MLog.a("urlabc ==== " +
					// cursor.getString(cursor.getColumnIndex("urlabc")));
					// MLog.a("gamekey ==== " +
					// cursor.getString(cursor.getColumnIndex("gamekey")));
					// count++;
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
					 * String status = null; contentResolver =
					 * this.getContentResolver(); url =
					 * Uri.parse("content://"+ShareContent
					 * .PROVIDER_URL+"/config/2"); cursor =
					 * contentResolver.query(url, new String[] {"urlabc"},
					 * "name="+"\'"+getApp().curKey+"\'", null, null);
					 * while(cursor.moveToNext()){ status =
					 * cursor.getString(cursor.getColumnIndex("urlabc")); }
					 */

//					MLog.a("------------------" + i + "-----------------------");
//					MLog.a("------------------" + bu.getString("key") + "-----------------------");
//					MLog.a("------------------" + getApp().curKey + "-----------------------");
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
		if (status == null || (status != null && !status.equals("success"))) {
			// final boolean bo = FilesTool.loadLuaScript("lua/cancelback.lua");
			Builder builder = new Builder(this);
			builder.setMessage("请先成功初始化");
			builder.setTitle("通知");
			builder.setCancelable(false);
			builder.setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					// 都没有成功初始化，怎么给回调
					/*
					 * if(bo){ synchronized (mLuaState) {
					 * mLuaState.getGlobal("cancelback"); int index =
					 * mLuaState.getTop(); mLuaState.getField(index,
					 * "cancelCallBack"); LuaTools.dbcall(mLuaState, 0, 0);//
					 * 代表0个参数，0个返回值 } }else{ cancelCallback(); }
					 */
					dialog.dismiss();
					ChargeActivity.this.finish();
				}
			});
			builder.create().show();
		}
	}

	/**
	 * 初始化lua开发环境
	 */
	public void initLua() {
		super.initLua();
		MLog.s(this + " ----> onResume doing2 ");
		mLuaState.pushJavaObject(myLay);
		mLuaState.setGlobal("rootview");
		MLog.s(this + " ----> onResume end ");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			synchronized (mLuaState) {
				mLuaState.getGlobal("chagerInfolua");
				int index = mLuaState.getTop();
				mLuaState.getField(index, "cancelCallBack");
				LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
			}
			WXinSDK.setPrepayid("");
			getApp().backToGame();
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data==null){
			return;
		}
//		if (resultCode == Constant.RESULTCODE) {//围信海贝微信h5支付回调
//			String respCode = data.getExtras().getString("respCode");
////			String respMessage = data.getExtras().getString("respMessage");
//			if (!TextUtils.isEmpty(respCode)) {
//				// 支付结果状态（01成功/00处理中/-1 失败）
//				if ("01".equals(respCode)||"00".equals(respCode)) {
//					Bundle bundle = getIntent().getExtras();
//					getIntent().setClass(this, MyRemoteService.class);
//					bundle.putString("flag", "sec_confirmation");
//					getIntent().putExtras(bundle);
//					this.startService(getIntent());
//				}else if ("-1".equals(respCode)) {
//					synchronized (mLuaState) {
//						mLuaState.getGlobal("chagerInfolua");
//						int index = mLuaState.getTop();
//						mLuaState.getField(index, "cancelCallBack");
//						LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
//					}
//				}
//			}
//			getApp().backToGame();
//			
//			this.finish();
//			return;
//		}
		String respCode = data.getExtras().getString("resultCode");
		if (!TextUtils.isEmpty(respCode) && respCode.equalsIgnoreCase("success")) {//标示支付成功
			Bundle bundle = getIntent().getExtras();
			getIntent().setClass(this, MyRemoteService.class);
			bundle.putString("flag", "sec_confirmation");
			getIntent().putExtras(bundle);
			this.startService(getIntent());
	    }else{  //其他状态NOPAY状态：取消支付，未支付等状态
	    	synchronized (mLuaState) {
				mLuaState.getGlobal("chagerInfolua");
				int index = mLuaState.getTop();
				mLuaState.getField(index, "cancelCallBack");
				LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
			}
	    }
		getApp().backToGame();
		
		this.finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(WXinSDK.isDes()){
			WXinSDK.setDes(false);
		}
	}
	

	/**
	 * 取消充值
	 */
	/*
	 * public void cancelCallback(){ Intent intent=new Intent(this,
	 * MyRemoteService.class); Bundle bu = new Bundle();
	 * 
	 * GameArgs gameargs = getApp().getGameArgs(); bu.putString("status","1");
	 * bu.putString("custominfo",gameargs.getSelf());
	 * bu.putString("msg",gameargs.getDesc()); bu.putString("flag","pay");
	 * bu.putString("customorderid",gameargs.getCustomorderid());
	 * bu.putString("sum",gameargs.getSum()); bu.putString("chargetype","null");
	 * bu.putString("key", gameargs.getKey());
	 * 
	 * intent.putExtras(bu); startService(intent); }
	 */

}
