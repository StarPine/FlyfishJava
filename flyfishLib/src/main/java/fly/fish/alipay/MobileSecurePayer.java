/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package fly.fish.alipay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.alipay.android.app.IAlixPay;
import com.alipay.sdk.app.PayTask;

import fly.fish.tools.MLog;

/**
 * 和安全支付服务通信，发送订单信息进行支付，接收支付宝返回信息
 * 
 */
public class MobileSecurePayer {
	static String TAG = "MobileSecurePayer";
	Integer lock = 0;
	IAlixPay mAlixPay = null;
	boolean mbPaying = false;
	Activity mActivity = null;

	// 和安全支付服务建立连接
	private ServiceConnection mAlixPayConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			//
			// wake up the binder to continue.
			// 获得通信通道
			synchronized (lock) {
				mAlixPay = IAlixPay.Stub.asInterface(service);
				lock.notify();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mAlixPay = null;
		}
	};

	/**
	 * 向支付宝发送支付请求
	 * 
	 * @param strOrderInfo
	 *            订单信息
	 * @param callback
	 *            回调handler
	 * @param myWhat
	 *            回调信息
	 * @param activity
	 *            目标activity
	 * @return
	 */
	public boolean pay(final String strOrderInfo, final Handler callback, final int myWhat, final Activity activity) {
		if (mbPaying)
			return false;
		mbPaying = true;

		//
		mActivity = activity;

		// 实例一个线程来进行支付
		new Thread(new Runnable() {
			public void run() {
				
					// 构造PayTask 对象
					PayTask alipay = new PayTask(activity);
					//获取支付宝版本
					String version = alipay.getVersion();
					MLog.a("alipay","version-------------->"+version);
					// 调用支付接口
					String result = alipay.pay(strOrderInfo,true);
					
					Message msg = new Message();
					msg.what = myWhat;
					msg.obj = result;
					callback.sendMessage(msg);
			}
			}).start();

		return true;
	}

}