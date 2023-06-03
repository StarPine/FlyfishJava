package fly.fish.othersdk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.org.suspension.model.JXActivityUtils;
import com.org.suspension.model.JXGameBall;
import com.org.suspension.model.JXResUtils;

import fly.fish.aidl.CallBackListener;
import fly.fish.aidl.MyRemoteService;
import fly.fish.aidl.OutFace;
import fly.fish.asdk.ChargeActivity;
import fly.fish.asdk.LoginActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.beans.GameArgs;
import fly.fish.tools.HttpUtils;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;
/**
 * 
 * @author 56547
 *
 */
public class Asdk {
	private static Class<?> clazz1 = null;//ry
	private static Class<?> clazz_mp = null;//MH
	private static Class<?> clazz_onekeylogin = null;//onekeylogin
	private static boolean hasball = false;
	private static boolean isLoginSuccess = false;
	private static Intent loginIntent ;

	public static void setHasball(boolean hasball) {
		Asdk.hasball = hasball;
		MLog.a("setHasball-hasball--"+hasball);
	}

	public static void setLogiinState(boolean isLoginSuccess, Intent intent) {
		Asdk.isLoginSuccess = isLoginSuccess;
		Asdk.loginIntent = intent;
	}

	private static SharedPreferences share = null;
	public static void applicationOnCreate(Application application){
		try {
			clazz_mp = Class.forName("fly.fish.othersdk.MiitHelper");
			Method mp_method = getMethod(clazz_mp, "InitEntry", Application.class);
			invoke(mp_method, application);
		} catch (ClassNotFoundException e) {
			MLog.a("no-mp");
		}
		//ry
		try {
			clazz1 = Class.forName("fly.fish.othersdk.Reyunsdk");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyunApplicationInit", Application.class);
			invoke(method1, application);
		}


	}

	public static void initSDK(final Activity act){
		//onekeylogin一键登录
		if (OutFace.getOneLoginCheck()){
			try {
				clazz_onekeylogin = Class.forName("fly.fish.othersdk.OnekeyLogin");
				Method onekeylogin_method = getMethod(clazz_onekeylogin, "initJVerification", Context.class);
				invoke(onekeylogin_method, act);
			} catch (ClassNotFoundException e) {
				MLog.a("no-clazz_onekeylogin");
			}
		}
		//获取OAID
		if(clazz_mp!=null){
			Method mp_method = getMethod(clazz_mp, "getDeviceIds", Context.class);
			invoke(mp_method, act);
		}
		try {
			Class.forName("com.org.suspension.model.JXActivityUtils");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		JXActivityUtils.getInstance().setGameActivity(act);
		
	}

	public static void InitLaunch(final Activity activity, final boolean isLandsape,
			final CallBackListener mcallback) {
		//ry
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyuninit", Activity.class);
			invoke(method1,activity);
		}
		//onekeylogin
		if(clazz_onekeylogin!=null){
			Method method1 = getMethod(clazz_onekeylogin, "preLogin", Activity.class);
			invoke(method1,activity);
		}
		
		share = MyApplication.getAppContext().getSharedPreferences("user_info", 0);
		mcallback.callback(0, false);
	}

	public static void onResume(Activity act) {
		//ry
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyunResume", Activity.class);
			invoke(method1,act);
		}
		MLog.a("onResume-hasball--"+hasball);
		if(hasball){
			JXGameBall.showWd();
		}
	}

	public static void onPause(Activity act) {
		//ry
		if(clazz1!=null){
			Method method = getMethod(clazz1, "reyunPause", Activity.class);
			invoke(method, act);
		}
		MLog.a("onPause-hasball--"+hasball);
		if(hasball){
			JXGameBall.closeWd();
		}
	}
	
	public static void onDestroy(Activity act) {
		//ry
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyunDestroy", new Class<?>[0]);
			invoke(method1,new Object[0]);
		}
		if(hasball){
			JXGameBall.dismiss();
		}
	}
	//登录
	public static void loginSDK(Activity act, Intent intent){
		if (isLoginSuccess){
			loginIntent.setClass(act, MyRemoteService.class);
			act.startService(loginIntent);
			return;
		}
		
		//ry
		if(clazz1!=null){
			Method method = getMethod(clazz1, "reyunLogin", Activity.class,Intent.class);
			try {
				boolean ob = (Boolean)method.invoke(null, act,intent);
				if(ob){//对应SWHT的云端环境
					return;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//是否一键登录开关，最高优先级
//		String isopen = share.getString("othersdkextdata4", "");
//		if(!isopen.equals("1")){//非1则关闭一键登录
//			intent.setClass(act, LoginActivity.class);
//			act.startActivity(intent);
//			return;
//		}
		//检查是否有登录过
		GameArgs ga = MyApplication.getAppContext().getGameArgs();
		String idno = ga.getCpid()+ga.getGameno();
		String isphoneoracc = share.getString(idno+"isphoneoracc", "");
		ArrayList<String> accs = PhoneTool.getPhoneAccs();
		if(isphoneoracc.equals("0")
				||isphoneoracc.equals("1")
				||isphoneoracc.equals("2")
				||(isphoneoracc.equals("")&&accs!=null)){
			intent.setClass(act, LoginActivity.class);
			act.startActivity(intent);
			return;
		}
		
		//onekeylogin
		String othersdkextdata5 = share.getString("othersdkextdata5", "");
		if(clazz_onekeylogin!=null && othersdkextdata5.equals("1")){
			Method method1 = getMethod(clazz_onekeylogin, "loginAuth", Activity.class,Intent.class,boolean.class);
			//检查是否有一键登录过
			boolean ischeck = isphoneoracc.equals("10");
			invoke(method1,act,intent,ischeck);
		}else{
			intent.setClass(act, LoginActivity.class);
			act.startActivity(intent);
		}
				
		
	}
	//支付
	public static void paySDK(Activity act, Intent intent){
		Bundle bundle = intent.getExtras();
		//ry下单设置
		if(clazz1!=null){
			Method method2 = getMethod(clazz1, "reyunSetOrder", String.class,String.class);
			invoke(method2,bundle.getString("desc"), bundle.getString("account"));
		}
		//ry
		if(clazz1!=null){
			Method method = getMethod(clazz1, "reyunPay", Activity.class,Intent.class);
			try {
				boolean ob = (Boolean)method.invoke(null, act,intent);
				if(ob){//对应SWHT的云端环境
					return;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		intent.setClass(act, ChargeActivity.class);
		act.startActivity(intent);
	}
	public static void submitData(String data){
		//ry
		if(clazz1!=null){
			Method method2 = getMethod(clazz1, "reyunSetUserData", String.class);
			invoke(method2,data);
		}
		
		Intent intent = new Intent(MyApplication.getAppContext()
				.getApplicationContext(), MyRemoteService.class);
		Bundle bundle = new Bundle();
		bundle.putString("flag", "gameinfo");
		bundle.putString("gameinfo", data);
		intent.putExtras(bundle);
		MyApplication.getAppContext().getApplicationContext().startService(intent);
	}
	public static void exit(Activity context) {
		//ry
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyunexit", new Class<?>[0]);
			invoke(method1,new Object[0]);
		}
		
		if(hasball){
			JXGameBall.dismiss();
		}
	}
	
	//ry登录完成
	public static void reyunsetLogin(String acc) {
		MLog.a("ASDK", "reyunlogin");
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyunsetLogin", String.class);
			invoke(method1,acc);
		}
		String isopenball = share.getString("othersdkextdata5", "");
		if("".equals(isopenball)||"1".equals(isopenball)){
			hasball = true;
		}else{
			hasball = false;
		}
		//默认关闭悬浮窗
		hasball = false;
		if(hasball){
			JXGameBall.openBall();
		}
		
		//判断登录后弹公告
		if("1".equals(gonggao_time)){
			gonggao_time = "";
			showGonggao(OutFace.getInstance(null).getmActivity(), gonggao_content);
		}
	}
	//ry支付完成
	public static void reyunandttsetPay(String desc,String orderid, String type, String sum, boolean issuccess) {
		MLog.a("ASDK", "reyunandttPay");
		if(clazz1!=null){
			Method method1 = getMethod(clazz1, "reyunsetPay", String.class,String.class,String.class,String.class,boolean.class);
			invoke(method1,desc,orderid,type,sum,issuccess);
		}
		
	}
	
	public static void logout(Activity act){
		Intent locIntent = new Intent();
		locIntent.setClass(act, MyRemoteService.class);
		Bundle locBundle = new Bundle();
		locBundle.putString("flag", "login");
		locBundle.putString("sessionid", "0");
		locBundle.putString("accountid", "0");
		locBundle.putString("status", "2");
		locBundle.putString("custominfo", "");
		locIntent.putExtras(locBundle);
		act.startService(locIntent);
	}
	
	private static GetCertificationInfoCallback certificationInfoCallback = null;
	public static void getCertificateInfo(final Activity act, GetCertificationInfoCallback callback) {
		certificationInfoCallback = callback;
		String username_ = "";
		GameArgs ga = MyApplication.getAppContext().getGameArgs();
		String idno = ga.getCpid()+ga.getGameno();
		SharedPreferences sh =act.getSharedPreferences("user_info", 0);
		String isphoneoracc = sh.getString(idno+"isphoneoracc", "1");
		if("0".equals(isphoneoracc)){//手机登录
			username_ = sh.getString(idno+"phone_name", "");
		}else{//账号登录
			username_ = sh.getString(idno+"name", "");
		}
		final String username = username_;
//		if("".equals(username)){
////			act.runOnUiThread(new Runnable() {
////				
////				@Override
////				public void run() {
////					Toast.makeText(act, "请先登录", Toast.LENGTH_SHORT).show();
////				}
////			});
//		}else{
//			final String url = MyApplication.context.getSharedPreferences("user_info", 0).getString("accountserver", "")
//					+ "gameparam=checkidcard";
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					JSONObject param = null;
//					String param_json=  "";
//					try {
//						param = new JSONObject();
//						param.put("username", username);
//					} catch (JSONException e2) {
//						e2.printStackTrace();
//					}
//					param_json = param.toString();
//					String s = HttpUtils.postMethod(url, param_json, "utf-8");
//					JSONObject data;
//					boolean issuccess = true;
//					String age = "1",hasAdult = "0";//hasAdult(-1未实名，0已实名)
//					try {
//						data = new JSONObject(s);
//						String code = data.getString("code");
//						if ("0".equals(code)) {//已实名
//							hasAdult = "0";
//							age = data.getJSONObject("data").getString("age");
//						}else if("1".equals(code)){//未实名
//							hasAdult = "-1";
//							age = data.getJSONObject("data").getString("age");
//						}else{//请求失败
//							issuccess = false;
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					certificationInfoCallback.callback(issuccess, hasAdult, age);
//				}
//			}).start();
//		}
		
	}
	
	private static Method getMethod(Class<?> clazz,String flag,Class<?>...clas){
		if(clazz==null){
			return null;
		}
		try {
			return clazz.getMethod(flag, clas);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static void invoke(Method method,Object...prams){
		if(method==null){
			return;
		}
		try {
			method.invoke(null, prams);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private static String gonggao_time = "";//0登录前弹；1登录后弹
	private static String gonggao_content = "";
	private final static String gaogong_url = "http://iospingtai.xinxinjoy.com:8084/outerinterface/getgonggao.php?";
	public static void getGonggao(){
		//检查是否当日弹过公告
		PhoneTool.checkGGShowedToday(share);
		
		boolean isshowtoday = share.getBoolean("isshowtoday", true);
		if(isshowtoday){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String onclick = "",content= "暂无内容";
					
					String response = HttpUtils.postMethod(gaogong_url, "{\"server\":\""+PhoneTool.getSubject()+"\"}", "utf-8");
					System.out.println("response--->"+response);
					try {
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if("0".equals(code)){//成功获取公告
							onclick = data.getJSONObject("data").getString("onclick");
							content = data.getJSONObject("data").getString("gonggao");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					gonggao_content = content;
					if("0".equals(onclick)){//登录前弹公告
						gonggao_time = "0";
						showGonggao(OutFace.getInstance(null).getmActivity(),content);
					}else if("1".equals(onclick)){//登录后弹公告
						gonggao_time = "1";
					}
				}
			}).start();
		}
	}
	private static void showGonggao(final Activity mAct,final String content){
		//保存首次公告时间
		PhoneTool.saveFirstGGTime(share);
		mAct.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				int window_web_layout = mAct.getResources().getIdentifier(mAct.getPackageName() + ":layout/" + "as_pop_window_layout", null, null);
                int pop_window_webview = mAct.getResources().getIdentifier(mAct.getPackageName() + ":id/" + "as_pop_window_webview", null, null);
                int pop_window_close = mAct.getResources().getIdentifier(mAct.getPackageName() + ":id/" + "as_pop_window_close", null, null);
                int kuang_btn = mAct.getResources().getIdentifier(mAct.getPackageName() + ":id/" + "kuang_btn", null, null);
                int gou_btn = mAct.getResources().getIdentifier(mAct.getPackageName() + ":id/" + "gou_btn", null, null);
                View popwindowView = View.inflate(mAct, window_web_layout, null);
                TextView webView = (TextView)popwindowView.findViewById(pop_window_webview);
                Button closeButton = (Button)popwindowView.findViewById(pop_window_close);
                Button kuangbtn = (Button)popwindowView.findViewById(kuang_btn);
                final Button goubtn = (Button)popwindowView.findViewById(gou_btn);

                webView.setText(content);

                final PopupWindow popupWindow = new PopupWindow(popwindowView, JXResUtils.dip2px(mAct, 290), JXResUtils.dip2px(mAct, 225));
                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(false);
//                int main = mAct.getResources().getIdentifier("asdk_bg", "drawable", mAct.getPackageName());
//                Bitmap bmap = BitmapFactory.decodeResource(mAct.getResources(), main);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        PhoneTool.submitEvent("34");
                    }
                });
                goubtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						goubtn.setVisibility(View.GONE);
						share.edit().putBoolean("isshowtoday", true).commit();
					}
				});
                kuangbtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						goubtn.setVisibility(View.VISIBLE);
						share.edit().putBoolean("isshowtoday", false).commit();
						PhoneTool.submitEvent("35");
					}
				});

                popupWindow.showAtLocation(popwindowView, Gravity.CENTER, 0, 0);
                PhoneTool.submitEvent("33");
			}
		});
	}

}
