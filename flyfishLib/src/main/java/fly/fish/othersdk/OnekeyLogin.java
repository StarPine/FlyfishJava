package fly.fish.othersdk;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jiguang.verifysdk.api.AuthPageEventListener;
import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jiguang.verifysdk.api.JVerifyUIClickCallback;
import cn.jiguang.verifysdk.api.JVerifyUIConfig;
import cn.jiguang.verifysdk.api.LoginSettings;
import cn.jiguang.verifysdk.api.PreLoginListener;
import cn.jiguang.verifysdk.api.RequestCallback;
import cn.jiguang.verifysdk.api.VerifyListener;
import cn.jpush.android.api.JPushInterface;

import com.org.suspension.model.JXResUtils;

import fly.fish.aidl.MyRemoteService;
import fly.fish.asdk.LoginActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.beans.GameArgs;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;
import fly.fish.tools.HttpUtils;
import fly.fish.tools.PhoneTool;

public class OnekeyLogin {
	private static boolean isclickOtherlogin = false;
	private static boolean isAbleOnekeyLogin = false; 		//是否可以进行一键登录操作

	public static void initJVerification(Context context){
		JVerificationInterface.init(context, 5000, new RequestCallback<String>() {
			
			@Override
			public void onResult(int code, String msg) {
				//返回码，8000代表初始化成功，其他为失败
				System.out.println("initcode:"+code);
			}
		});
		JVerificationInterface.setDebugMode(true);
		//推送
		JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
	}
	public static void preLogin(final Activity activity){
		JVerificationInterface.preLogin(activity, 5000, new PreLoginListener() {
			
			@Override
			public void onResult(int code, String msg) {
				System.out.println("preLogincode:"+code);
				System.out.println("preLoginmsg:"+msg);
				if(code==7000){
					isAbleOnekeyLogin = true;
					System.out.println("预取号获取成功");
				}else{
					isAbleOnekeyLogin = false;
					System.out.println("预取号获取失败");
				}
			}
		});
	}
	private static SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);

	public static void loginAuth(final Activity activity,final Intent intent,boolean ischeck){
//		preLogin(activity);
		//是否检查一键登录缓存
		if(ischeck){
			//检查一键登录是否有缓存账号，有则自动登录，否则往下执行一键登录
			if(checkOneKeyLogin(activity,intent)){
				return;
			}
		}
		LoginSettings settings = new LoginSettings();
	    settings.setAutoFinish(true);//设置登录完成后是否自动关闭授权页
	    settings.setTimeout(15 * 1000);//设置超时时间，单位毫秒。 合法范围（0，30000],范围以外默认设置为10000
	    settings.setAuthPageEventListener(new AuthPageEventListener() {
	        @Override
	        public void onEvent(int code, String msg) {
	            //do something...
	        	System.out.println("onEventcmd:"+code);
	        	if(code == 2){//打开一键登录界面
					ASDKReport.getInstance().startSDKReport(activity, EventManager.SDK_EVENT_SHOW_ONEKEY_LOGIN);
				}else if(code == 8){//一键登录按钮（可用状态下）点击事件
	        		ASDKReport.getInstance().startSDKReport(activity,EventManager.SDK_EVENT_ONCLICK_ONEKEY_LOGIN);
	        	}
	        }
	    });//设置授权页事件监听
		TextView mBtn = new TextView(activity);
		mBtn.setText(Html.fromHtml("<u>" + "其他登录方式" + "</u>"));
		mBtn.setTextColor(activity.getResources().getColor(android.R.color.holo_red_light));
		RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLayoutParams1.setMargins(0, JXResUtils.dip2px(activity, 240.0f), 0, 0);
		mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mBtn.setLayoutParams(mLayoutParams1);
		JVerifyUIConfig uiConfig = new JVerifyUIConfig.Builder()
				.setAuthBGImgPath("asdk_bg")
				.setNavText("本机号一键登录")
				.setNavTextColor(activity.getResources().getColor(android.R.color.black))
				.setNavColor(activity.getResources().getColor(android.R.color.holo_blue_bright))
				.setNavReturnImgPath("cancle")
				.setLogoWidth(20)
				.setLogoHeight(20)
				.setLogoHidden(true)
				.setNumFieldOffsetY(120)//设置号码栏相对于标题栏下边缘 y 偏移
				.setSloganOffsetY(160)//设置 slogan 相对于标题栏下边缘 y 偏移
				.setLogBtnOffsetY(190)//设置登录按钮相对于标题栏下边缘 y 偏移
				.setLogBtnWidth(180)
				.setLogBtnImgPath("login_btn")
				.setLogBtnTextColor(activity.getResources().getColor(android.R.color.white))
				.setLogBtnTextSize(17)
				.setNumberSize(18)
				.setNumberColor(activity.getResources().getColor(android.R.color.black))
				.setPrivacyState(true)
				.setCheckedImgPath("protocol_gou_on")
				.setUncheckedImgPath("protocol_gou_off")
				.setPrivacyCheckboxInCenter(true)
				.setPrivacyCheckboxSize(10)
				.setPrivacyOffsetX(25)
				.setPrivacyOffsetY(15)
//        .setPrivacyTextWidth(260)
				.setAppPrivacyOne("用户协议", sharedPreferences.getString("othersdkextdata1", ""))
				.setPrivacyText("登录即同意", "和", "", "")
				.setAppPrivacyColor(0xff858585, 0xff000000)
				.enableHintToast(true, null)
				.setNavTransparent(false)
				.setNavHidden(false)
				.setStatusBarColorWithNav(true)
				.addCustomView(mBtn, true, new JVerifyUIClickCallback() {
					@Override
					public void onClicked(Context context, View view) {
						ASDKReport.getInstance().startSDKReport(context, EventManager.SDK_EVENT_ONCLICK_OTHER_LOGIN_TYPE);
						isclickOtherlogin = true;
						intent.setClass(activity, LoginActivity.class);
						intent.putExtra("fromonekeylogin", "2");//1-转到历史账号页；2转到手机验证码登录
						intent.putExtra("otheronekey", "2");//1-转到历史账号页；2转到手机验证码登录
						activity.startActivity(intent);
					}
				})
				.setDialogTheme(300, 300, 0, 0, false)
				.build();
	    JVerificationInterface.setCustomUIWithConfig(uiConfig);
		JVerificationInterface.loginAuth(activity, settings, new VerifyListener() {

			@Override
			public void onResult(int code, String content, String operator) {
				System.out.println("loginAuthcode:"+code);
				System.out.println("loginAuthcontent:"+content);
				System.out.println("loginAuthoperator:"+operator);
				if (code == 6001){
					ASDKReport.getInstance().startSDKReport(activity,EventManager.SDK_EVENT_ONEKEY_LOGIN_GET_TOKEN_FAIL);
				}
				if(code==6000){
					System.out.println("loginToken获取成功");
					ASDKReport.getInstance().startSDKReport(activity,EventManager.SDK_EVENT_ONEKEY_LOGIN_GET_TOKEN_SUCC);
					sengTokenToLogin(activity,intent,content);
				}else if(code==6002){//用户取消获取loginToken
					if(isclickOtherlogin){//点击的其他登录方式则不回调登录失败
						isclickOtherlogin = false;
					}else{
						Bundle locBundle = intent.getExtras();
						intent.setClass(activity, MyRemoteService.class);
						locBundle.putString("flag", "login");
						locBundle.putString("sessionid", "0");
						locBundle.putString("accountid", "0");
						locBundle.putString("status", "1");
						locBundle.putString("custominfo", locBundle.getString("callBackData"));
						intent.putExtras(locBundle);
						activity.startService(intent);
					}
				}else{
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							intent.setClass(activity, LoginActivity.class);
							intent.putExtra("fromonekeylogin", "2");//1-转到历史账号页；2转到手机验证码登录
		                	activity.startActivity(intent);
						}
					}, 500);

				}
			}

		});
	}
	//检查一键登录是否有缓存账号，有则自动登录
	private static boolean checkOneKeyLogin(Activity activity, Intent intent) {
		String phone = sharedPreferences.getString("onekeylogin_phone", "");
		if(phone!=null&&!"".equals(phone)){
			intent.setClass(activity, OnekeyAutoActivity.class);
			activity.startActivity(intent);
			return true;
		}
		return false;
	}
	private static void sengTokenToLogin(final Activity act, final Intent intent, final String token) {
		//请求服务端登录
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				JSONObject param = null;
				try {
					param = new JSONObject();
					param.put("token", token);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String url = sharedPreferences.getString("accountserver", "")
						+ "gameparam=accbyphone";
				String s = HttpUtils.postMethod(url, param.toString(), "utf-8");
				try {
					JSONObject data = new JSONObject(s);
					String code = data.getString("code");
					Bundle locBundle = intent.getExtras();
					intent.setClass(act, MyRemoteService.class);
					if("0".equals(code)){
						JSONObject acc = data.getJSONObject("data").getJSONObject("account");
						String accid = acc.getString("accountid");
						String sid = acc.getString("sessionid");
						String phone = acc.getString("phone");
						String psd = acc.getString("password");
						
						sharedPreferences.edit().putString("asdk_accountid",accid).commit();
						sharedPreferences.edit().putString("asdk_sessionid",sid).commit();
						//保存一键登录态
						GameArgs ga = MyApplication.getAppContext().getGameArgs();
						String idno = ga.getCpid()+ga.getGameno();
						sharedPreferences.edit().putString(idno+"isphoneoracc","10").commit();
						//保存一键登录返回的号码和密码
						sharedPreferences.edit().putString("onekeylogin_phone", phone).commit();
						sharedPreferences.edit().putString("onekeylogin_psd", psd).commit();
						//保存一键登录账号到本地
						PhoneTool.savePhoneAccount(phone, psd);
						//登录返回
						locBundle.putString("flag", "login");
						locBundle.putString("sessionid", sid);
						locBundle.putString("accountid", accid);
						locBundle.putString("phone",phone);
						locBundle.putString("status", "0");
						locBundle.putString("custominfo", locBundle.getString("callBackData"));
						intent.putExtras(locBundle);
						act.startService(intent);
						//防沉迷查询
						checkidcard(act,phone);
					}else{
						act.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(act, "登录失败，请切换登录方式", Toast.LENGTH_SHORT).show();
							}
						});
						intent.setClass(act, LoginActivity.class);
                    	intent.putExtra("fromonekeylogin", "1");//1-转到历史账号页；2转到手机验证码登录
                    	act.startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	public static void checkidcard(Activity act, String phonenum) {
		JSONObject param = null;
		try {
			param = new JSONObject();
			param.put("username", phonenum);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String url = sharedPreferences.getString("accountserver", "")
				+ "gameparam=checkidcard";
		String s = HttpUtils.postMethod(url, param.toString(), "utf-8");
		try {
			JSONObject data = new JSONObject(s);
			String code = data.getString("code");
			JSONObject info = data.getJSONObject("data");
			if("0".equals(code)){
				int age = 18;
				try {
					age = Integer.parseInt(info.getString("age"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				String isindulge = info.getString("isindulge");
				if(age<18&&isindulge.equals("1")){
					PhoneTool.OpenAntiAddiction(s);
				}
				
			}else if("1".equals(code)){
				String switch_ = data.getString("switch");
				if(!switch_.equals("0")){//0关闭，1强制实名，2可关闭
					//控制弹实名的时间
					String checkminuts = "0";
					try {
						checkminuts = info.getString("checkminuts");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String isautoreg = "";
					try {
						isautoreg = info.getString("isautoreg");
					} catch (Exception e) {
						e.printStackTrace();
					}
					PhoneTool.controlRealName(isautoreg,checkminuts,switch_);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
			
}
