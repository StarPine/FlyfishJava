package fly.fish.othersdk;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fly.fish.aidl.MyRemoteService;
import fly.fish.asdk.LoginActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.beans.GameArgs;
import fly.fish.tools.HttpUtils;
import fly.fish.tools.PhoneTool;

public class OnekeyAutoActivity extends Activity {
	SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
	boolean islogin = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int lay_id = this.getResources().getIdentifier("onekey_autoactivity", "layout", this.getPackageName());
		setContentView(lay_id);
		
		final String phonenum = sharedPreferences.getString("onekeylogin_phone", "");
		final String psd = sharedPreferences.getString("onekeylogin_psd", "");
		
		int loding_id = this.getResources().getIdentifier("onekey_loding", "id", this.getPackageName());
		int phone_id = this.getResources().getIdentifier("onekey_phone", "id", this.getPackageName());
		int changeacc_id = this.getResources().getIdentifier("onekey_changeacc", "id", this.getPackageName());
		TextView loding = (TextView)findViewById(loding_id);
		TextView phone = (TextView)findViewById(phone_id);
		TextView changeacc = (TextView)findViewById(changeacc_id);
		
		PhoneTool.auto_Login_Animator(loding,0.0f,720.0f,4000);
		String text = phonenum;
		try {
			text = phonenum.replace(phonenum.substring(3, 7), "****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		phone.setText(text);
		
		
		changeacc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				islogin = false;
				//取消一键登录态
				GameArgs ga = MyApplication.getAppContext().getGameArgs();
				String idno = ga.getCpid()+ga.getGameno();
				sharedPreferences.edit().putString(idno+"isphoneoracc","").commit();
//				OnekeyLogin.loginAuth(OnekeyAutoActivity.this, OnekeyAutoActivity.this.getIntent(), false);//此处切换不再显示一键登录，而显示历史账号
				Intent intent = OnekeyAutoActivity.this.getIntent();
				intent.setClass(OnekeyAutoActivity.this, LoginActivity.class);
				intent.putExtra("fromonekeylogin", "1");//1-转到历史账号页；2转到手机验证码登录
				OnekeyAutoActivity.this.startActivity(intent);
				OnekeyAutoActivity.this.finish();
			}
		});
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(islogin){
					login(OnekeyAutoActivity.this,phonenum,psd);
					OnekeyAutoActivity.this.finish();
				}
			}
		}, 1000);
	}

	protected void login(final Activity act, final String phonenum, final String psd) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				JSONObject param = null;
				try {
					param = new JSONObject();
					param.put("username", phonenum);
					param.put("userpassword", psd);
					param.put("isphone", "1");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String url = sharedPreferences.getString("accountserver", "")
						+ "gameparam=acclogin";
				String s = HttpUtils.postMethod(url, param.toString(), "utf-8");
				try {
					JSONObject data = new JSONObject(s);
					String code = data.getString("code");
					Intent intent = act.getIntent();
					Bundle locBundle = intent.getExtras();
					intent.setClass(act, MyRemoteService.class);
					if("0".equals(code)){
						JSONObject acc = data.getJSONObject("data").getJSONObject("account");
						String accid = acc.getString("accountid");
						String sid = acc.getString("sessionid");
						
						sharedPreferences.edit().putString("asdk_accountid",accid).commit();
						sharedPreferences.edit().putString("asdk_sessionid",sid).commit();
						//保存一键登录态
						GameArgs ga = MyApplication.getAppContext().getGameArgs();
						String idno = ga.getCpid()+ga.getGameno();
						sharedPreferences.edit().putString(idno+"isphoneoracc","10").commit();
						//保存一键登录账号到本地
						PhoneTool.savePhoneAccount(phonenum, psd);
						//登录返回
						locBundle.putString("flag", "login");
						locBundle.putString("sessionid", sid);
						locBundle.putString("accountid", accid);
						locBundle.putString("phone",phonenum);
						locBundle.putString("status", "0");
						locBundle.putString("custominfo", locBundle.getString("callBackData"));
						intent.putExtras(locBundle);
						act.startService(intent);
						//防沉迷查询
						OnekeyLogin.checkidcard(act,phonenum);
					}else{
						locBundle.putString("flag", "login");
						locBundle.putString("sessionid", "0");
						locBundle.putString("accountid", "0");
						locBundle.putString("status", "1");
						locBundle.putString("custominfo", locBundle.getString("callBackData"));
						intent.putExtras(locBundle);
						act.startService(intent);
						System.out.println("login--"+data.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			islogin = false;
			Intent intent = this.getIntent();
			Bundle locBundle = intent.getExtras();
			intent.setClass(this, MyRemoteService.class);
			locBundle.putString("flag", "login");
			locBundle.putString("sessionid", "0");
			locBundle.putString("accountid", "0");
			locBundle.putString("status", "1");
			locBundle.putString("custominfo", locBundle.getString("callBackData"));
			intent.putExtras(locBundle);
			this.startService(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

}
