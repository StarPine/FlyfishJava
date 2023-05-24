package fly.fish.asdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import fly.fish.aidl.MyRemoteService;
import fly.fish.beans.GameArgs;
import fly.fish.othersdk.WXinSDK;
import fly.fish.tools.MLog;

/**
 * 已弃用
 * @author Vathe
 *
 */

public class WXCallPayActivity extends Activity implements IWXAPIEventHandler{

	private IWXAPI api;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 隐去标题栏（应用程序的名字）
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐去状态栏部分(电池等图标和一切修饰部分)
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);   
//		Bundle bundle = getIntent().getExtras();
//       
//        String appId = bundle.getString("appId");
//        String partnerId = bundle.getString("partnerId");
//        String prepayId = bundle.getString("prepayId");
//        String packageValue = bundle.getString("packageValue");
//        String nonceStr = bundle.getString("nonceStr");
//        String timeStamp = bundle.getString("timeStamp");
//        String sign = bundle.getString("sign");
//        
//        MLog.a("appId----------->"+appId);
//		MLog.a("partnerId----------->"+partnerId);
//		MLog.a("prepayId----------->"+prepayId);
//		MLog.a("packageValue----------->"+packageValue);
//		MLog.a("nonceStr----------->"+nonceStr);
//		MLog.a("timeStamp----------->"+timeStamp);
//		MLog.a("sign----------->"+sign);
//        
//        api = WXAPIFactory.createWXAPI(this, null);
//        Boolean isreg = api.registerApp(appId);
//        MLog.a("isreg----------->"+isreg);
        
        api= WXAPIFactory.createWXAPI(this, WXinSDK.AppID);
        
        api.handleIntent(getIntent(), this);
        
//		PayReq request = new PayReq();
//		request.appId = appId;
//		request.partnerId = partnerId;
//		request.prepayId = prepayId;
//		request.packageValue = packageValue;
//		request.nonceStr = nonceStr;
//		request.timeStamp = timeStamp;
//		request.sign = sign;
//		
//		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//		MLog.a("api.getWXAppSupportAPI()----------->"+api.getWXAppSupportAPI());
//		MLog.a("Build.PAY_SUPPORTED_SDK_INT----------->"+Build.PAY_SUPPORTED_SDK_INT);
//		MLog.a("isPaySupported--------------->"+isPaySupported);
//		if(isPaySupported){
//			boolean issuccess = api.sendReq(request);
//			MLog.a("issuccess---------------->"+issuccess);
//			if(!issuccess){
//				Toast.makeText(this, "微信支付失败", Toast.LENGTH_LONG).show();
//				payBack("1");
//			}
//		}else{
//			Toast.makeText(this, "您的微信版本不支持微信支付功能", Toast.LENGTH_LONG).show();
//			payBack("1");
//		}
		
		
	}
	
	private void payBack(String status) {
		GameArgs gameargs = MyApplication.getAppContext().getGameArgs();
		Intent intent = new Intent(this, MyRemoteService.class);
		Bundle bundle = new Bundle();
		bundle.putString("status",status);
		bundle.putString("custominfo",gameargs.getSelf());
		bundle.putString("msg",gameargs.getDesc());
		bundle.putString("flag","pay");
		bundle.putString("customorderid",gameargs.getCustomorderid());
		bundle.putString("sum",gameargs.getSum());
		bundle.putString("chargetype","wxpay");
		intent.putExtras(bundle);
		this.startService(intent);
		this.finish();
		MyApplication.getAppContext().backToGame();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	@Override
	public void onReq(BaseReq req) {
		MLog.a("WXCallPayActivity","-------------onReq----------------");
	}

	@Override
	public void onResp(BaseResp resp) {
		MLog.a("-------------onResp-------------errCode="+resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			MLog.a("WXCallPayActivity","-------------COMMAND_PAY_BY_WX-------------errCode="+resp.errCode);
			MLog.a("WXCallPayActivity","-------------COMMAND_PAY_BY_WX-------------errStr="+resp.errStr);
			if(resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL){
				this.finish();
				return;
			}
		}
		payBack("2");
	}

}
