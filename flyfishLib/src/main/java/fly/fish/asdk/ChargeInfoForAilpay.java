package fly.fish.asdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import fly.fish.alipay.AlixId;
import fly.fish.alipay.BaseHelper;
import fly.fish.alipay.MobileSecurePayer;
import fly.fish.alipay.PartnerConfig;
import fly.fish.alipay.SignUtils;
import fly.fish.beans.GameArgs;
import fly.fish.beans.PayBackModel;
import fly.fish.tools.MLog;

public class ChargeInfoForAilpay extends MyActivity implements Callback, Runnable {
	// public boolean mark =true;
	private ProgressDialog mProgress = null;
	private LinearLayout myLay;

	GameArgs cp = MyApplication.getAppContext().getGameArgs();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLay = new LinearLayout(this);
		setContentView(myLay);
		init();
		mContext2 = this;
		mHandler2 = new Handler(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 系统初始化
	 */
	public void init() {
		super.init();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

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

	/********************************* 支付宝 **********************************/

	/**
	 * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
	 */
	public static class AlixOnCancelListener implements DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	/**
	 * get the selected order info for pay. 获取商品订单信息
	 * @param gamename 
	 * 
	 * @param position
	 *            商品在列表中的位置
	 * @return
	 */
	String getOrderInfo(String total_fee, String customorderid, String notify_url, String gamename) {
		
		// 合作者身份ID
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		// 卖家支付宝账号
		strOrderInfo += "seller_id=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		// 商户网站唯一订单号
		strOrderInfo += "out_trade_no=" + "\"" + customorderid + "\"";
		strOrderInfo += "&";
		// 商品名称
		strOrderInfo += "subject=" + "\"" + gamename + "：" + cp.getDesc() + "\"";
		strOrderInfo += "&";
		// 商品详情
		strOrderInfo += "body=" + "\"" + gamename + "：" +  cp.getDesc() + "\"";
		strOrderInfo += "&";
		// 商品金额
		strOrderInfo += "total_fee=" + "\"" + total_fee + "\"";
		strOrderInfo += "&";
		// 服务器异步通知页面路径
		strOrderInfo += "notify_url=" + "\"" + notify_url + "\"";
		strOrderInfo += "&";
		// 接口名称， 固定值
		strOrderInfo += "service=" + "\"" + "mobile.securitypay.pay" + "\"";
		strOrderInfo += "&";
		// 参数编码， 固定值
		strOrderInfo += "_input_charset=" + "\"" + "utf-8" + "\"";
		strOrderInfo += "&";
		// 支付类型， 固定值
		strOrderInfo += "payment_type=" + "\"" + "1" + "\"";
		strOrderInfo += "&";
		//设置未付款交易的超时时间
		strOrderInfo += "it_b_pay=" + "\"" + "30m" + "\"";
		MLog.s("SDK CALLBACK =========> " + notify_url);
		
		return strOrderInfo;

	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param signType
	 *            签名方式
	 * @param content
	 *            待签名订单信息
	 * @return
	 */
	String sign( String content) {
		return SignUtils.sign(content, PartnerConfig.RSA_PRIVATE);

	}
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 * @return
	 */
	String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	/**
	 * 给lua调用的pay方法
	 * 
	 * @param total_fee
	 *            金额：从LUA传
	 */

	Context mContext = null;

	public void pay(String total_fee, Context context, String customorderid, String notify_url) {
		mContext = ChargeInfoForAilpay.this;
		MLog.s("TTTTTTTTT ---------> " + context);
		// check to see if the MobileSecurePay is already installed.
		
		String gamename = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
		try {
			// prepare the order info.
			// 准备订单信息
			String orderInfo = getOrderInfo(total_fee, customorderid, notify_url,gamename);
			MLog.a("Alipay", "orderInfo:"+orderInfo);
			// 这里根据签名方式对订单信息进行签名
			String sign = sign(orderInfo);
			Log.v("sign:", sign);
			// 对签名进行编码
			sign = URLEncoder.encode(sign, "UTF-8");
			// 组装好参数
			final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
					+ getSignType();
			Log.v("orderInfo:", payInfo);
			// start the pay.
			// 调用pay方法进行支付
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(payInfo, myhand, AlixId.RQF_PAY, (Activity) mContext);

			if (bRet) {
				// 显示“正在支付”进度条
				closeProgress();
				mProgress = BaseHelper.showProgress(mContext, null, "正在支付", false, true);
			}
		} catch (Exception ex) {
			Toast.makeText(mContext, "Failure calling remote service", Toast.LENGTH_SHORT).show();
			ex.printStackTrace();
		}
	}

	// 这里接收支付结果，支付宝手机端同步通知
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String strRet = (String) msg.obj;

				Log.e("myTag", strRet); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();

					BaseHelper.log("myTag", strRet);

					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = strRet.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = strRet.indexOf("};memo=");
						tradeStatus = strRet.substring(imemoStart, imemoEnd);
						MLog.s("===========>>" + tradeStatus);
						if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
							MLog.s("238sdsds");
							// 调用二次确认
							// 找准程序入口
							Message mMsg = new Message();
							mMsg.what = 1;
							MyHandler kmyhand = new MyHandler(ChargeInfoForAilpay.this);
							kmyhand.sendMessage(mMsg);
						} else {
							MLog.s("===========>>" + tradeStatus);
						}

					} catch (Exception e) {
						MLog.s("========>" + e);
						MLog.s("错误");
						e.printStackTrace();
					}
				}
					break;

				}
				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// 关闭进度框
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/********************************* 手机银联 **********************************/
	private static final String LOG_TAG = "PayDemo";
	private Context mContext2 = null;
	private int mGoodsIdx = 0;
	private Handler mHandler2 = null;
	private ProgressDialog mLoadingDialog = null;

	private static final int PLUGIN_VALID = 0;
	private static final int PLUGIN_NOT_INSTALLED = -1;
	private static final int PLUGIN_NEED_UPGRADE = 2;

	/*****************************************************************
	 * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
	 *****************************************************************/
	private String mMode = "01";
	private static String TN_URL_01 = "";

	public void upPay(String tn, String mMode2) {
		MLog.s("=======" + tn);
		TN_URL_01 = tn;
		mMode = mMode2;
		MLog.s("==============>upPay()  s" + TN_URL_01);
		MLog.s("==============>upPay()  s" + mMode);
		mLoadingDialog = ProgressDialog.show(mContext2, // context
				"", // title
				"正在努力的获取tn中,请稍候...", // message
				true); // 进度是否是不确定的，这只和创建进度条有关

		/*************************************************
		 * 
		 * 步骤1：从网络开始,获取交易流水号即TN
		 * 
		 ************************************************/
		new Thread(ChargeInfoForAilpay.this).run();

	}

	@Override
	public boolean handleMessage(Message msg) {
		MLog.s("kete335");
		Log.e(LOG_TAG, " " + "handleMessage" + msg.obj);
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}

		String tn = "";
		if (msg.obj == null || ((String) msg.obj).length() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("错误提示");
			builder.setMessage("网络连接失败,请重试!");
			builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ChargeInfoForAilpay c = (ChargeInfoForAilpay) mContext2;
					List<PayBackModel> plist = c.getApp().getbList(c.getApp().curKey);
					MLog.s(plist.size() + " unipay SSSSSSS ");
					for (PayBackModel payBackModel : plist) {
						if (payBackModel.getCustomorderid().equals(c.getApp().getGameArgs().getCustomorderid())) {
							plist.remove(payBackModel);
							break;
						}
					}
					MLog.s(plist.size() + " unipay EEEEEEE ");
					// 关闭SDK
					c.getApp().backToGame();
					c.finish();

					dialog.dismiss();
				}
			});
			builder.create().show();
		} else {
			MLog.s("kete354");
			tn = (String) msg.obj;
			/*************************************************
			 * 
			 * 步骤2：通过银联工具类启动支付控件
			 * 
			 ************************************************/
			// mMode参数解释：
			// 0 - 启动银联正式环境
			// 1 - 连接银联测试环境
			
			UPPayAssistEx.startPayByJAR(ChargeInfoForAilpay.this, PayActivity.class, null, null, tn, mMode);
			
//			int ret = UPPayAssistEx.startPay(ChargeInfoForAilpay.this, null, null, tn, mMode);
//			if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
//
//				List<PayBackModel> p = this.getApp().getbList(this.getApp().curKey);
//				MLog.s(p.size() + " unipay SSSSSSS ");
//				for (PayBackModel payBackModel : p) {
//					if (payBackModel.getCustomorderid().equals(getApp().getGameArgs().getCustomorderid())) {
//						p.remove(payBackModel);
//						break;
//					}
//				}
//				MLog.s(p.size() + " unipay EEEEEEE ");
//
//				// 需要重新安装控件
//				Log.e(LOG_TAG, " plugin not found or need upgrade!!!");
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setCancelable(false);
//				builder.setTitle("提示");
//				builder.setMessage("完成购买需要安装银联支付控件，是否安装？");
//
//				builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//
//						// 获取系统缓冲绝对路径 获取/data/data/***/cache目录
//						File cacheDir = mContext2.getCacheDir();
//						final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
//
//						// 捆绑安装
//						retrieveApkFromAssets(mContext2, "UPPayPluginEx.apk", cachePath);
//
//						// 启动安装
//						String filename = cachePath;
//						String command = "chmod 777 " + filename;
//						Runtime runtime = Runtime.getRuntime();
//						try {
//							runtime.exec(command);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						Intent intent = new Intent(Intent.ACTION_VIEW);
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");
//						mContext2.startActivity(intent);
//						ChargeInfoForAilpay.this.finish();
//					}
//				});
//
//				builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//						dialog.dismiss();
//
//						List<PayBackModel> p = getApp().getbList(getApp().curKey);
//						MLog.s(p.size() + " SSSSSSS ");
//						for (PayBackModel payBackModel : p) {
//							if (payBackModel.getCustomorderid().equals(getApp().getGameArgs().getCustomorderid())) {
//								p.remove(payBackModel);
//								break;
//							}
//						}
//						MLog.s(p.size() + " EEEEEEE ");
//						// getApp().backToGame();
//						finish();
//					}
//				});
//				builder.create().show();
//
//			}
//			Log.e(LOG_TAG, "" + ret);
		}

		return false;
	}

	public Message mMsg;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		mMsg = new Message();
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			// msg = "支付成功！";
			// 向自己的handle发送验证订单请求
			mMsg.what = 2;
			Handler kmyhand = ChargeInfoForAilpay.this.myhand;
			kmyhand.sendMessage(mMsg);
		} else if (str.equalsIgnoreCase("fail")) {
			// msg = "支付失败！";

			// 向自己的handle发送验证订单请求
			mMsg.what = 3;
			Handler kmyhand = ChargeInfoForAilpay.this.myhand;
			kmyhand.sendMessage(mMsg);
		} else if (str.equalsIgnoreCase("cancel")) {
			// msg = "用户取消了支付";

			// 向自己的handle发送验证订单请求
			mMsg.what = 3;
			Handler kmyhand = ChargeInfoForAilpay.this.myhand;
			kmyhand.sendMessage(mMsg);
		}
	}

	@Override
	public void run() {
		MLog.s("kete442");

		Message msg = mHandler2.obtainMessage();
		msg.obj = TN_URL_01;
		MLog.s("===============" + TN_URL_01);
		mHandler2.sendMessage(msg);
	}

	/**
	 * assets文件夹下的apk拷贝到path
	 * 
	 * @param context
	 * @param fileName
	 * @param path
	 * @return
	 */
	private boolean retrieveApkFromAssets(Context context, String fileName, String path) {
		boolean bRet = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bRet;
	}

	/**************************** end *******************************************/

	/**
	 * 返回键监听事件
	 */

	//
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("myTag", "onDestroy");

		try {
			if (mProgress != null) {
				mProgress.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
