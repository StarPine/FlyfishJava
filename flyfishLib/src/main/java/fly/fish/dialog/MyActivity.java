package fly.fish.dialog;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fly.fish.dialog.DialgTool;
import fly.fish.dialog.PrivacyDialog;
import fly.fish.tools.MLog;


public class MyActivity extends Activity {

	
	String state = "";
	String qx_url = "";
	String ys_url = "";
	String yh_url = "";
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		activity =this;
		Thread urlthred = new Thread(new Runnable() {

			@Override
			public void run() {

				 Intent inten=new Intent();


				MLog.a("--------pub------"
						+ DialgTool.getpub("AsdkPublisher.txt"));
				MLog.a("--------url------" + DialgTool.getpub("address.txt"));
				String s = DialgTool.getWebMethod(DialgTool
						.getpub("address.txt")
						+ DialgTool.getpub("AsdkPublisher.txt"));
				MLog.a("--------json------" + s);

				// if(s==null){
				// handler.sendEmptyMessage(1);
				// return;
				// }

				try {

					JSONObject jsonObject = new JSONObject(s);
					state = jsonObject.getString("state");
					qx_url = jsonObject.getString("qxurl");
					ys_url = jsonObject.getString("ysurl");
					yh_url = jsonObject.getString("yhurl");

					// handler.sendEmptyMessage(0);

					MLog.a("--------请求完成------qx=" + qx_url + ";ys_url="
							+ ys_url + ";yh_url=" + yh_url);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		urlthred.start();

		try {
			urlthred.join();

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					SharedPreferences sharedPreferences = activity
							.getSharedPreferences("asdk", activity.MODE_PRIVATE);
					boolean isFirstRun = sharedPreferences.getBoolean(
							"isFirstRun", true);
					final SharedPreferences.Editor editor = sharedPreferences
							.edit();

					
//					ys_url ="http://jinjunyouxi.com/yszc.html";
//					yh_url ="http://jinjunyouxi.com/dmandzb.html";
					final PrivacyDialog privacyDialog = new PrivacyDialog(
							activity, yh_url, ys_url, qx_url, activity
									.getResources().getIdentifier("MyDialog",
											"style", activity.getPackageName()));

					privacyDialog.setCancelable(false);// 点击返回键或者空白处不消失
					privacyDialog
							.setClickListener(new PrivacyDialog.ClickInterface() {
								@Override
								public void doCofirm() {
									privacyDialog.dismiss();

									editor.putBoolean("isFirstRun", false);
									editor.commit();

//									inItLaunch(activity, isLandscape, callback);
									MLog.a("同意协议-----------");
									
									try {
										InputStream ins = activity.getResources().getAssets().open("gameEntrance1.txt");
										String gameEntrance = new BufferedReader(new InputStreamReader(ins)).readLine().trim();
										MLog.a("XwanSDK--gameEntrance------------>"+gameEntrance);
										Intent intent = new Intent();
										intent.setClassName(activity.getPackageName(), gameEntrance);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										activity.startActivity(intent);
										
										activity.finish();
									} catch (IOException e) {
										e.printStackTrace();
									}

								}

								@Override
								public void doCancel() {
									privacyDialog.dismiss();
									activity.finish();
									System.exit(0);
								}
							});

					// privacyDialog.show();

					if ((isFirstRun == true) && (state.equals("1"))) { // 第一次则跳转到引导页面

						MLog.a("第一次安装-----------");
						privacyDialog.show();

					} else if ((isFirstRun == false)) { // 如果是第二次启动则直接跳转到主页面

						MLog.a("非第一次安装-----------");
//						inItLaunch(activity, isLandscape, callback);
						
						try {
							InputStream ins = activity.getResources().getAssets().open("gameEntrance1.txt");
							String gameEntrance = new BufferedReader(new InputStreamReader(ins)).readLine().trim();
							MLog.a("XwanSDK--gameEntrance------------>"+gameEntrance);
							Intent intent = new Intent();
							intent.setClassName(activity.getPackageName(), gameEntrance);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							activity.startActivity(intent);
							
							activity.finish();
						} catch (IOException e) {
							e.printStackTrace();
						}
						

					} else if (state.equals("0")) { // 如果是第二次启动则直接跳转到主页面

						MLog.a("协议关闭-----------");
//						inItLaunch(activity, isLandscape, callback);
						
						try {
							InputStream ins = activity.getResources().getAssets().open("gameEntrance1.txt");
							String gameEntrance = new BufferedReader(new InputStreamReader(ins)).readLine().trim();
							MLog.a("XwanSDK--gameEntrance------------>"+gameEntrance);
							Intent intent = new Intent();
							intent.setClassName(activity.getPackageName(), gameEntrance);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							activity.startActivity(intent);
							
							activity.finish();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}
			});

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	 

	}

 
 
}
