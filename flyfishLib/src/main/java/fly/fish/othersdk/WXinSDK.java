package fly.fish.othersdk;

import org.keplerproject.luajava.LuaState;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

//import com.switfpass.pay.MainApplication;
//import com.switfpass.pay.activity.PayPlugin;
//import com.switfpass.pay.bean.RequestMsg;
//import com.ulopay.android.h5_library.manager.WebViewManager;
//import com.wechatsdk.Entry;
//import com.wechatsdk.info.ReturnCodeInfo;

import fly.fish.aidl.MyRemoteService;
import fly.fish.asdk.MyApplication;
import fly.fish.beans.GameArgs;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

public class WXinSDK {
	public static String AppID = "";
	private static String prepay_id = "";
	private static String prepayUrl = "";
	private static boolean isDes = false;
	public static boolean isDes() {
		return isDes;
	}
	public static void setDes(boolean isDes) {
		WXinSDK.isDes = isDes;
//		Entry.onExit(mCon);
	}
	public static String getPrepayid() {
		return prepay_id;
	}
	public static void setPrepayid(String prepay_id) {
		WXinSDK.prepay_id = prepay_id;
	}
	
	public static String getPrepayUrl() {
		return prepayUrl;
	}
	public static void setPrepayUrl(String prepayUrl) {
		WXinSDK.prepayUrl = prepayUrl;
	}

	public static void PaySDK(Activity con ,String appId,String partnerId,String prepayId,String packageValue,String nonceStr,String timeStamp,String sign,String uri,String orderid) {
//		MLog.a("appId:",appId);
//		MLog.a("partnerId:",partnerId);
//		MLog.a("prepayId:",prepayId);
//		MLog.a("packageValue:",packageValue);
//		MLog.a("nonceStr:",nonceStr);
//		MLog.a("timeStamp:",timeStamp);
//		MLog.a("sign:",sign);
//		MLog.a("uri:",uri);
//		MLog.a("orderid:",orderid);
//		if(uri!=null){
//			Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(uri));
//			con.startActivity(intent);
//			return;
//		}
//		
//		
//		AppID = appId;
		
//		IWXAPI api = WXAPIFactory.createWXAPI(con, null);
//        Boolean isreg = api.registerApp(appId);
////        MLog.a("isreg----------->"+isreg);
//        api= WXAPIFactory.createWXAPI(con, appId);
//        
//		PayReq request = new PayReq();
//		request.appId = appId;
//		request.partnerId = partnerId;
//		request.prepayId = prepayId;
//		request.packageValue = packageValue;
//		request.nonceStr = nonceStr;
//		request.timeStamp = timeStamp;
//		request.sign = sign;
//		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//		MLog.a("weixin","api.getWXAppSupportAPI()----------->"+api.getWXAppSupportAPI());
//		MLog.a("weixin","Build.PAY_SUPPORTED_SDK_INT----------->"+Build.PAY_SUPPORTED_SDK_INT);
//		MLog.a("weixin","isPaySupported--------------->"+isPaySupported);
//		if(isPaySupported){
//			boolean issuccess = api.sendReq(request);
//			MLog.a("weixin","issuccess---------------->"+issuccess);
//			if(!issuccess){
//				Toast.makeText(con, "微信支付失败", Toast.LENGTH_LONG).show();
//			}
//		}else{
//			Toast.makeText(con, "您的微信版本不支持微信支付功能", Toast.LENGTH_LONG).show();
//		}
	}
	
	//威富通
	
	public static void PaySDK(final Activity con,final String token_id,String orderid){
//		GameArgs gameargs = MyApplication.getAppContext().getGameArgs();
//		float money = 0;
//		try {
//			money = Float.parseFloat(gameargs.getSum());
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//		MLog.a("WXinSDK", "money---->"+money);
//		RequestMsg msg = new RequestMsg();
////		msg.setMoney(money);
//		msg.setTokenId(token_id);
//		msg.setOutTradeNo(orderid);
//		// 微信wap支付
//		msg.setTradeType(MainApplication.PAY_WX_WAP);
//		PayPlugin.unifiedH5Pay(con, msg);
	}
	
	//TCL
	public static void PaySDK(final Activity con,final String prepay_url,String prepay_id,String payid){
//		MLog.a("WXPAY", "payid----->"+payid);
//		WXinSDK.prepay_id = prepay_id;
//		WXinSDK.setPrepayUrl(prepay_url+"&type=android");
//		con.runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				
//				new WebViewManager(con, true).showWeiXinView(prepay_url+"&type=android");
//			}
//		});
	}

	private static Activity mCon = null;
	//围信-第三方微信支付
	public static void PaySDK(final Activity con,String orderid){
//		WXinSDK.isDes = true;
//		mCon = con;
//		Entry.onInit(con);
//		GameArgs gameargs = MyApplication.getAppContext().getGameArgs();
//		int money = 0;
//		try {
//			money = Integer.parseInt(gameargs.getSum())*100;
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//		String proName = gameargs.getDesc();
//		MLog.a("WXinSDK", "money---->"+money);
//		MLog.a("WXinSDK", "proName---->"+proName);
//		Entry.pay(con, orderid, money, proName, "1", mHandler);
	}
	

//	private static Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//				case ReturnCodeInfo.WX_PAY_SUCCESS:
//					System.out.println("支付成功！！");
////					Bundle bundle = mCon.getIntent().getExtras();
////					mCon.getIntent().setClass(mCon, MyRemoteService.class);
////        			bundle.putString("flag", "sec_confirmation");
////        			mCon.getIntent().putExtras(bundle);
////        			mCon.startService(mCon.getIntent());
//					Bundle locBundle1 = new Bundle();
//	        		Bundle mBundle = mCon.getIntent().getExtras();
//	        		mCon.getIntent().setClass(mCon, MyRemoteService.class);
//					locBundle1.putString("flag", "pay");
//					locBundle1.putString("msg", mBundle.getString("desc"));
//					locBundle1.putString("sum", mBundle.getString("account"));
//					locBundle1.putString("chargetype", "weixinpay");
//					locBundle1.putString("custominfo", mBundle.getString("callBackData"));
//					locBundle1.putString("customorderid", mBundle.getString("merchantsOrder"));
//					locBundle1.putString("status", "0");
//					mCon.getIntent().putExtras(locBundle1);
//					mCon.startService(mCon.getIntent());
//					break;
//				case ReturnCodeInfo.WX_PAY_CANCEL:
//					System.out.println("支付取消！！");
//				case ReturnCodeInfo.WX_PAY_FAIL:
//					System.out.println("支付失败！！");
//					LuaState mLuaState = MyApplication.getAppContext().getmLuaState();
//					synchronized (MyApplication.getAppContext().getmLuaState()) {
//        				mLuaState.getGlobal("chagerInfolua");
//        				int index = mLuaState.getTop();
//        				mLuaState.getField(index, "cancelCallBack");
//        				LuaTools.dbcall(mLuaState, 0, 0);// 代表0个参数，0个返回值
//        			}
//					break;
//				default:
//					break;
//			}
//			mCon.finish();
//		}
//	};
	//围信海贝微信h5支付调用
	public static void HeePaySDK(final Activity con,String payparams){
//		String payparams = tokenid+","+agentid+","+orderid+","+paytype;
//		HeepayPlugin.pay(con, payparams);
		
		Intent webIntent = con.getIntent();
		webIntent.setClass(con, YXWebActivity.class);
		webIntent.putExtra("weixin_zfurl", payparams.replace("\\/", "/"));
		MLog.a("WXinSDK", "payparams--->"+payparams);
		con.startActivity(webIntent);
	}
	//微信官方h5支付
	public static void h5PaySDK(Activity con,String payparams){
		Intent webIntent = con.getIntent();
		webIntent.setClass(con, YXWebActivity.class);
		webIntent.putExtra("weixin_zfurl", payparams.replace("\\/", "/"));
		con.startActivity(webIntent);
	}
	//支付宝h5支付
	public static void zfb_h5PaySDK(Activity con,String payparams){
		Intent webIntent = con.getIntent();
		webIntent.setClass(con, YXWebActivity.class);
		webIntent.putExtra("zfb_zfurl", payparams.replace("\\/", "/"));
		con.startActivity(webIntent);
	}
	
}
