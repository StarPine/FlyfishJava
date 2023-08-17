package fly.fish.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.keplerproject.luajava.LuaException;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import fly.fish.adapter.MyAccAdapter;
import fly.fish.aidl.OutFace;
import fly.fish.asdk.AsdkActivity;
import fly.fish.asdk.MyActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.config.Configs;

public class PhoneTool {
	public static String TAG = "PhoneTool";

	private static String OAID = "";
	private static String devicesFlag = "";

	//0代表IMEI号或IDFA，1代表OAID，2代表自生成设备号
	private static String pnType = "0";
	private static boolean isUseNewMode = true;


	private static void setPnType(String pnType) {
		PhoneTool.pnType = pnType;
	}

	public static void setOAID(String oAID) {
		OAID = oAID;
	}
	public static String getOAID(){
		return OAID;
	}

	public static String getVersionName(Context context){
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1.0";
	}

	/**
	 * 安装APK
	 * 
	 * @param apkFileString
	 */
	public static void notifyAndInstallApk(Activity from, String apkFileString) {
		String name = HttpUtils.getUrlFileName(apkFileString).split("\\|")[0];
		String filename = Configs.ASDKROOT + from.getPackageName() + File.separator + name;
		if (!Configs.SDEXIST) {
			filename = Configs.ASDKROOT  + name;
		}

		MLog.s("apkroot -------> " + filename);
		String command = "chmod 777 " + filename;
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File apkfile = new File(filename);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
        	Uri apkUri = ASProvider.getUriForFile(from, from.getPackageName()+".fileprovider", apkfile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
        	intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        }
		from.startActivityForResult(intent, 1);
	}

	/**
	 * IMSI：international mobiles subscriber
	 * identity国际移动用户号码标识，这个一般大家是不知道，GSM必须写在卡内相关文件中 MSISDN:mobile subscriber
	 * ISDN用户号码，这个是我们说的139，136那个号码； ICCID:ICC identity集成电路卡标识，这个是唯一标识一张卡片物理号码的；
	 * IMEI：international mobile Equipment identity手机唯一标识码； TelephonyManager tm
	 * = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
	 * String imei = tm.getDeviceId(); //取出IMEI String tel =
	 * tm.getLine1Number(); //取出MSISDN，很可能为空(電話號碼) String imei
	 * =tm.getSimSerialNumber(); //取出ICCID String imsi =tm.getSubscriberId();
	 * //取出IMEI
	 * 
	 * @return
	 */
	public static String getIMEI(Context context) {
		isUseNewMode = ManifestInfo.getMetaBoolean(context,"DEVICEID_USE_NEW_MODE",true);
		if (isUseNewMode){
			String channelDeviceId = getChannelDeviceId(context,"device_id");
			Log.i(TAG, "getIMEI: " + channelDeviceId + "  ,devicesFlag: " + getOSVersion());
			return channelDeviceId;
		}
		return oldModeIMEI(context);
	}

	/**
	 * 旧形式获取设备id
	 * @param con
	 * @return
	 */
	private static String oldModeIMEI(Context con) {
		if(!isgetDeId(con)){
			if(!"".equals(OAID)){
				setPnType("1");
				return OAID;
			}else{
				setPnType("2");
			}
		}else{
			setPnType("0");
		}
		String spDeviceID = MyApplication.context.getSharedPreferences("user_info", 0).getString("device_id", "");
		if("".equals(spDeviceID)){
			spDeviceID = getDeviceId(con);
			MyApplication.context.getSharedPreferences("user_info", 0).edit().putString("device_id", spDeviceID).commit();
		}

		return spDeviceID;
	}

	public static String getPurchaseDeviceId(Context context){
		return getChannelDeviceId(context,"purchase_device_id");
	}

	private static String getChannelDeviceId(Context context, String devicesName){
		SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
		String devicesId = sharedPreferences.getString(devicesName, "");
		devicesFlag = sharedPreferences.getString("device_flag", "not");

		if (!TextUtils.isEmpty(devicesId)){
			return devicesId;
		}

		if (!TextUtils.isEmpty(OAID)){
			devicesId = OAID;
			devicesFlag = "O";
		}

		//获取设备的imei号，如获取到则直接返回并保存
		try {
			if (TextUtils.isEmpty(devicesId)) {
				TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				devicesId = mTelephonyMgr.getDeviceId();
				devicesFlag = "I";
			}
		} catch (Exception e) {
			MLog.a(TAG,"好吧没得到IMEI");
		}

		try {
			//获取设备的ANDROID_ID+SERIAL硬件序列号
			if (TextUtils.isEmpty(devicesId)) {
				String androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
				String serial= Build.SERIAL;
				devicesId = androidId + serial;
				devicesFlag = "A";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (TextUtils.isEmpty(devicesId)) {
			devicesId = UUID.randomUUID().toString().replace("-", "");
			devicesFlag = "U";
		}

		//生成随机数
		if (TextUtils.isEmpty(devicesId)) {
			devicesId=System.currentTimeMillis()+getRandomCode();
			devicesFlag = "Z";
		}

		if (!devicesFlag.equals("I") && !devicesFlag.equals("O")){
			//为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
			devicesId = MD5Util.getMD5String(devicesId);
		}

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(devicesName, devicesId).commit();
		editor.putString("device_flag", devicesFlag).commit();
		return devicesId;
	}

	private static boolean isgetDeId(Context con){
		boolean isgetDeId = false;
		int flag = -1;
		try {
			flag = MyApplication.context.getSharedPreferences("user_info", 0).getInt("isgetDeId", -1);
			if(flag==-1){
				//不管获取到与否，保存首次获取状态，避免同个设备上报两个设备码的问题
				TelephonyManager mTelephonyMgr = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
				String deviceId = mTelephonyMgr.getDeviceId();
				if (deviceId!=null&&deviceId.length() > 9) {
					isgetDeId = true;
					MyApplication.context.getSharedPreferences("user_info", 0).edit().putInt("isgetDeId", 1).commit();
					//持久化操作, 进行保存到SD卡中
					MyApplication.context.getSharedPreferences("user_info", 0).edit().putString("device_id", deviceId).commit();
					saveDeviceID(deviceId, con);
					
				}
			}else if(flag==1){
				isgetDeId = true;
			}
		} catch (Exception e) {
			MLog.a(TAG,"NOIMEI");
		}
		if(!isgetDeId&&flag==-1){
			MyApplication.context.getSharedPreferences("user_info", 0).edit().putInt("isgetDeId", 0).commit();
		}
		return isgetDeId;
	}
	@SuppressWarnings("deprecation")
	private static String getDeviceId(Context context) {
        //读取保存的在sd卡中的唯一标识符
        String deviceId = readDeviceID(context);
        //判断是否已经生成过,有则直接返回
        System.out.println("dd1:"+deviceId);
        if (deviceId != null && !"".equals(deviceId)) {
        	return deviceId;
        }
        //用于生成最终的唯一标识符
        StringBuffer s = new StringBuffer();
        //获取设备的imei号，如获取到则直接返回并保存
        try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = mTelephonyMgr.getDeviceId();
			s.append(deviceId);
		} catch (Exception e) {
			MLog.a(TAG,"好吧没得到IMEI");
		}
        System.out.println("dd2:"+deviceId);
        if (deviceId!=null&&deviceId.length() > 9) {
            //持久化操作, 进行保存到SD卡中
            saveDeviceID(deviceId, context);
            return deviceId;
        }
        try {
            //获取设备的ANDROID_ID+SERIAL硬件序列号
        	deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID); 
        	String serial= Build.SERIAL;
            s.append(deviceId).append(serial);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
        if (s == null || s.length() <= 0) {
            UUID uuid = UUID.randomUUID();
            deviceId = uuid.toString().replace("-", "");
            s.append(deviceId);
        }

		//生成随机数
		if (s == null || s.length() <= 0) {
			deviceId=System.currentTimeMillis()+getRandomCode();
			s.append(deviceId);
		}
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
        String md5 = MD5Util.getMD5String(s.toString());
        System.out.println("deviceId:"+ s);System.out.println("md5:"+md5);
        if (s.length() > 0) {
            //持久化操作, 进行保存到SD卡中
            saveDeviceID(md5, context);
        }
        return md5;
	}

	public static String getRandomCode() {
		String randomcode = "";
		// 用字符数组的方式随机
		String model = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		char[] m = model.toCharArray();
		for (int j = 0; j < 12; j++) {
			char c = m[(int) (Math.random() * 62)];
			// 保证六位随机数之间没有重复的
			if (randomcode.contains(String.valueOf(c))) {
				j--;
				continue;
			}
			randomcode = randomcode + c;
		}
		return randomcode;

	}
	/**
     * 读取固定的文件中的内容,这里就是读取sd卡中保存的设备唯一标识符
     *
     * @param context
     * @return
     */
	private static String readDeviceID(Context context) {
        File file = getDevicesDir(context);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String deviceID = in.readLine().trim();
            in.close();
            isr.close();
            fis.close();
            return deviceID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	//保存文件的路径
    private static final String CACHE_IMAGE_DIR = "aray/cache/devices";
    //保存的文件 采用隐藏文件的形式进行保存
    private static final String DEVICES_FILE_NAME = ".asdkDevice";
	/**
     * 统一处理设备唯一标识 保存的文件的地址
     * @param context
     * @return
     */
    private static File getDevicesDir(Context context) {
        File mCropFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cropdir = new File(Environment.getExternalStorageDirectory(), CACHE_IMAGE_DIR);
            if (!cropdir.exists()) {
                cropdir.mkdirs();
            }
            mCropFile = new File(cropdir, DEVICES_FILE_NAME); // 用当前时间给取得的图片命名
        } else {
            File cropdir = new File(context.getFilesDir(), CACHE_IMAGE_DIR);
            if (!cropdir.exists()) {
                cropdir.mkdirs();
            }
            mCropFile = new File(cropdir, DEVICES_FILE_NAME);
        }
        return mCropFile;
    }
    /**
     * 保存 内容到 SD卡中,  这里保存的就是 设备唯一标识符
     * @param str
     * @param context
     */
    private static void saveDeviceID(String str, Context context) {
        File file = getDevicesDir(context);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //处理设备号
    public static void managerIMEI(final Activity activity){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				//sd卡读取
				String readDeviceID = readDeviceID(activity);
				//app缓存读取
				String spDeviceID = MyApplication.context.getSharedPreferences("user_info", 0).getString("device_id", readDeviceID);
				
                if (readDeviceID==null||"".equals(readDeviceID)) {// sd卡缓存为空
                    
                	if(spDeviceID==null||"".equals(spDeviceID)){//app缓存为空
                		readDeviceID = getDeviceId(activity);
                	}else{////app缓存不为空
                		readDeviceID = spDeviceID;
                		saveDeviceID(readDeviceID, activity);
                	}
                }
                MyApplication.context.getSharedPreferences("user_info", 0).edit().putString("device_id", readDeviceID).commit();
			}
		}).start();
    }
	// 取出IMSI
	public static String getIMSI(Context con) {
		String imsi = "a1s2d3f4t5no";
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = mTelephonyMgr.getSubscriberId();
		} catch (Exception e) {
			MLog.a(TAG,"好吧没得到IMSI");
		}
		if (("").equals(imsi) || ("null").equals(imsi) || imsi == null || imsi == "null") {
			return "a1s2d3f4t5no";
		}
		return imsi;
	}

	/**
	 * 加密后的IMEI
	 * 
	 * @param imei
	 * @return
	 */
	public static String getIEMI(String imei) {
		StringBuilder ji = new StringBuilder();
		StringBuilder ou = new StringBuilder();
		imei = MD5Util.getMD5String(imei);
		for (int i = 0; i < imei.length(); i++) {
			if (i % 2 == 0) {
				ou.append(imei.charAt(i));
			} else {
				ji.append(imei.charAt(i));
			}
		}

		imei = MD5Util.getMD5String(ji + "jsk412lj21j5klj362dfanbvkc59874590asfk" + ou);
		return imei;
	}

	/**
	 * 判断有木有使用cmwap代理
	 * 
	 * @return
	 */
	public static boolean isProxy(Context con) {
		ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni == null || (ni != null && !ni.isAvailable())) {// 未开启网络
				return false;
			} else { // 开启了网络
				if (ni.getTypeName().equals("WIFI")) {
					System.out.println("NET mode : " + "WIFI");
					return true;
				} else {
					NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					String netString = networkInfo.getExtraInfo();
					System.out.println("NET mode : " + netString);
					if (netString.contains("cmnet")) {
						System.out.println("NET mode : " + "cmnet");
						return true;
					} else if (netString.contains("cmwap")) {
						System.out.println("NET mode : " + "cmwap");
						return true;
					} else if (netString.contains("internet")) {
						System.out.println("NET mode : " + "internet");
						return true;
					} else {
						System.out.println("NET mode : " + "未知");
						return true;
					}
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * 返回IP地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getIP(Context context) {
		String ip = "";
		try {
			ConnectivityManager conMann = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mobileNetworkInfo != null) {
				if (mobileNetworkInfo.isConnected()) {
					ip = getLocalIpAddress();
					System.out.println("本地ip-----" + ip);
				}
				return ip;
			}
			if (wifiNetworkInfo != null) {
				if (wifiNetworkInfo.isConnected()) {
					WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					int ipAddress = wifiInfo.getIpAddress();
					ip = intToIp(ipAddress);
					System.out.println("wifi_ip地址为------" + ip);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return ip;
	}

	public static String intToIp(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * 判断网络连接是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}

		isProxy(context);
		System.out.println("IP address :" + getLocalIpAddress());
		return bisConnFlag;

	}

	/**
	 * Role:Telecom service providers获取手机服务商信息 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 */
	public static String getProvidersName(Context con) {
		// String ProvidersName = "未知";
		String gateway = "3";
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
			// 返回唯一的用户ID;就是这张卡的编号神马的
			String IMSI = mTelephonyMgr.getSubscriberId();
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			// 后面还有10位，是不知的
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
				// ProvidersName = "中国移动";
				gateway = "0";
			} else if (IMSI.startsWith("46001")) {
				// ProvidersName = "中国联通";
				gateway = "1";
			} else if (IMSI.startsWith("46003")) {
				// ProvidersName = "中国电信";
				gateway = "2";
			}

		} catch (Exception e) {
			MLog.a(TAG,"是不是没有卡呢？");
			return gateway;

		}

		/*
		 * String operator = mTelephonyMgr.getSimOperator(); if
		 * (operator.equals("46000") || operator.equals("46002")) {
		 * ProvidersName = "中国移动"; } else if (operator.equals("46001")) {
		 * ProvidersName = "中国联通"; } else if (operator.equals("46003")) {
		 * ProvidersName = "中国电信"; }
		 * 
		 * Configuration conf =
		 * AsdkActivity.asdk.getResources().getConfiguration();
		 * if(conf.mcc==460){//中国 if(conf.mnc==0 || conf.mnc==2){ ProvidersName
		 * = "中国移动"; }else if(conf.mnc==1){ ProvidersName = "中国联通"; }else
		 * if(conf.mnc==3){ ProvidersName = "中国电信"; } }
		 */

		// Configuration conf = con.getResources().getConfiguration();

		return gateway;
	}

	/**
	 * 获取设备系统版本号
	 *
	 * @return 设备系统版本号
	 */
	public static int getSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 获取设备系统版本名
	 *
	 * @return 设备系统版本名
	 */
	public static String getOSVersion() {
		return Build.VERSION.RELEASE + "|" + devicesFlag;
	}



	/**
	 * 获取网络信号类型
	 */
	public static String getNetType(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = telephonyManager.getNetworkType();
		if (networkType == TelephonyManager.NETWORK_TYPE_UMTS || networkType == TelephonyManager.NETWORK_TYPE_HSDPA || networkType == TelephonyManager.NETWORK_TYPE_EVDO_0 || networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
			return "3G";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS || networkType == TelephonyManager.NETWORK_TYPE_EDGE || networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
			return "2G";
		}
		return networkType + "";
	}
	public static String getVERSION(Context con) {
		String version = Build.VERSION.RELEASE;// 版本
		MLog.a("version:"+version);
		return version;
	}
	

	/**
	 * 获取手机信息
	 */
	public static String getPhoneInfo(Context con) {
		TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
		String mtyb = Build.BRAND;// 手机品牌
		String mtype = Build.MODEL; // 手机型号
		String version = Build.VERSION.RELEASE;// 版本
		String imei = tm.getDeviceId();// IMEI
		String imsi = tm.getSubscriberId();// IMSI
		String serviceName = tm.getSimOperatorName(); // 运营商
		String numer = tm.getLine1Number(); // 手机号码
		return imei + "|" + version + "|" + mtype + "|" + mtyb;
	}

	/**
	 * 获取网络运营商名称
	 * <p>中国移动、如中国联通、中国电信</p>
	 *
	 * @return 运营商名称
	 */
	public static String getNetworkOperatorName(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		return tm != null ? tm.getNetworkOperatorName() : null;
	}

	/**
	 * 获取手机方位是什么
	 */
	public static String getCL(Context con) {
		MLog.a("pnType--"+pnType);
		return pnType;
//		String cl = "feizhou";
//		CellLocation cell = null;
//		try {
//			TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
//			cell = tm.getCellLocation(); // 手机号码
//			cl = String.valueOf(cell);
//		} catch (Exception e) {
//			MLog.a(TAG,"好吧没得到手机方位");
//			return "feizhou";
//		}
//		if (("").equals(String.valueOf(cell)) || ("null").equals(String.valueOf(cell)) || String.valueOf(cell) == null || String.valueOf(cell) == "null") {
//			return "feizhou";
//		}
//		return cl;
	}

	/**
	 * 获取手机型号
	 */
	public static String getPT(Context con) {
		String mtyb = "copycat";
		String mtype = "brands";
		try {
			TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
			mtyb = Build.BRAND;// 手机品牌
			mtype = Build.MODEL; // 手机型号
		} catch (Exception e) {
			// TODO: handle exception
			MLog.a(TAG,"好吧，没得到手机型号");
			return "copycat|brands";
		}
		return mtype + "|" + mtyb;
	}
	
	public static String getMac(Context con){
		String mac = "";
		WifiManager wifi = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info;
		try {
			info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		MLog.a(TAG, "mac:" + mac);
		return mac == null?"":mac;
		
	}

	/**
	 * 手机分辨率信息
	 * 
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context con) {

		DisplayMetrics dm = new DisplayMetrics();
		Display display = ((Activity) con).getWindowManager().getDefaultDisplay();
		display.getMetrics(dm);
		// dm = AsdkActivity.asdk.getResources().getDisplayMetrics();

		/*
		 * String str = ""; int screenWidth = dm.widthPixels; int screenHeight =
		 * dm.heightPixels; float density = dm.density; float xdpi = dm.xdpi;
		 * float ydpi = dm.ydpi; str += "The absolute width:" +
		 * String.valueOf(screenWidth) + "pixels\n"; str +=
		 * "The absolute height:" + String.valueOf(screenHeight) + "pixels\n";
		 * str += "The logical density of the display.:" +
		 * String.valueOf(density) + "\n"; str += "X dimension :" +
		 * String.valueOf(xdpi) + "pixels per inch\n"; str += "Y dimension :" +
		 * String.valueOf(ydpi) + "pixels per inch\n";
		 */
		return dm;
	}

	/**
	 * 得到UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String s = uuid.toString();
		return s;
	}

	/**
	 * 打开设置网络界面
	 */
	public static void setNetworkMethod(final Context context) {
		Builder builder = new Builder(context);

		builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				if (Build.VERSION.SDK_INT > 10) {// 判断手机系统的版本
															// 即API大于10
															// 就是3.0或以上版本
					intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				context.startActivity(intent);
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MyApplication.getAppContext().exit(0);
			}
		});

		builder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
				}
				return true;// 自己消费，不劳上层费心
			}
		});

		builder.show();
	}

	/**
	 * 弹出等待框
	 * 
	 * @param act
	 */
	public static void onCreateDialog(final MyActivity activity, String title, String msg) {
//		ProgressDialog dialog = act.dialog;
//		if (dialog == null) {
//			dialog = new ProgressDialog(act);
//			act.dialog = dialog;
//		}
////		dialog.setTitle(title);
////		dialog.setMessage(msg);
//		dialog.setIndeterminate(true);
//		dialog.setCancelable(false);
//		ImageView iv = new ImageView(act);
//		BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeStream(FilesTool.getFileStream(MyApplication.context, "images/warning.png", 1)));
//		iv.setBackground(bd);
//		dialog.setProgressDrawable(bd);
//		if (!dialog.isShowing()) {
//			dialog.show();
//		}
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				Builder builder = new Builder(activity);
				//需要使用相对布局
				RelativeLayout view = new RelativeLayout(activity);
				RelativeLayout.LayoutParams  par = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(par);
				
//				ImageView iv = null;
//				synchronized (activity.mLuaState) {
//					activity.mLuaState.getGlobal("loginmodle");
//					int index = activity.mLuaState.getTop();
//					activity.mLuaState.getField(index, "loading_dialog");
//					activity.mLuaState.pushJavaObject(activity);
//					LuaTools.dbcall(activity.mLuaState, 1, 1);// 代表1个参数，1个返回值
//				}
//				try {
//					iv = (ImageView) activity.mLuaState.toJavaObject(-1);
//				} catch (Exception e) {
//					e.printStackTrace();
//					return;
//				}
				
				ImageView iv2 = new ImageView(activity);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(40, 40);
				iv2.setLayoutParams(param);
				Bitmap bm = FilesTool.getBitmap("images/loding_bg.png", 1);
				BitmapDrawable draw = new BitmapDrawable(bm);
				iv2.setBackground(draw);
				
				ObjectAnimator animator=ObjectAnimator.ofFloat(iv2,"rotation",0F,360F);
				animator.setDuration(1500);
				animator.setRepeatCount(ObjectAnimator.INFINITE);
				animator.setRepeatMode(ObjectAnimator.RESTART);
				animator.start();
				
				view.addView(iv2);
				if (!activity.isFinishing()){
					activity.alertDialog =builder.create();
					activity.alertDialog.show();
					activity.alertDialog.setCancelable(false);
					activity.alertDialog.getWindow().setContentView(view);
				}

			}
		});
	}
	
	public static void auto_Login_Animator(Object ob,float start,float end,long time){
		ObjectAnimator animator=ObjectAnimator.ofFloat(ob,"rotation",start,end);
		animator.setDuration(time);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.RESTART);
		animator.start();
	}
	public static void setEllipsize(Object ob,int length,int type){
		((TextView)ob).setSingleLine(true);
		((TextView)ob).setEllipsize(TruncateAt.END);
		((TextView)ob).setMaxEms(length);//无效
	}

	/**
	 * 查看有木有当前显示
	 * 
	 * @param dialog
	 * @return
	 */
	public static Object isShowing(Dialog dialog) {
		if (dialog != null) {
			if (dialog.isShowing()) {
				return 1;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 对话框
	 * 
	 * @param act
	 * @param title
	 * @param msg
	 * @param sure
	 * @param cancel
	 */
	public static void showDialog(MyActivity act, String title, String msg, String sure, String cancel) {
		AlertDialog builder = act.builder;
		if (builder != null) {
			builder.dismiss();
		}

		Builder ss = null;
		ss = new Builder(act);
		ss.setMessage(msg);
		ss.setTitle(title);
		ss.setCancelable(false);
		if (sure != null) {
			ss.setPositiveButton(sure, MyApplication.getAppContext().onclick);
		}
		if (cancel != null) {
			ss.setNegativeButton(cancel, MyApplication.getAppContext().onclick);
		}
		builder = ss.create();
		act.builder = builder;
		builder.show();
	}

	/**
	 * 绑定邮箱
	 * 
	 * @param act
	 * @param title
	 * @param msg
	 * @param sure
	 * @param cancel
	 */
	public static void showEmailDialog(MyActivity act, String title, String sure, String cancel) {
		AlertDialog builder = act.builder;
		if (builder != null) {
			builder.dismiss();
		}
		Builder ss = null;
		ss = new Builder(act);
		ss.setTitle(title);
		ss.setCancelable(false);

		LinearLayout layout = new LinearLayout(act);
		layout.setOrientation(1);
		LinearLayout layout1 = new LinearLayout(act);
		layout1.setOrientation(0);
		TextView textView = new TextView(act);
		textView.setText("邮    箱：");
		EditText editText = new EditText(act);
		editText.setHint("请输入邮箱");
		layout1.addView(textView);
		layout1.addView(editText);

		LinearLayout layout2 = new LinearLayout(act);
		layout2.setOrientation(0);
		TextView textView1 = new TextView(act);
		textView1.setText("验证码：");
		EditText editText1 = new EditText(act);
		editText1.setHint("请输入验证码");
		Button button1 = new Button(act);
		layout2.addView(textView1);
		layout2.addView(editText1);
		layout2.addView(button1);

		button1.setText("获取验证码");
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MLog.a(TAG,"123456");
			}
		});

		layout.addView(layout1);
		layout.addView(layout2);
		ss.setView(layout);

		if (sure != null) {
			ss.setPositiveButton(sure, MyApplication.getAppContext().onclick);
		}
		if (cancel != null) {
			ss.setNegativeButton(cancel, MyApplication.getAppContext().onclick);
		}
		builder = ss.create();
		act.builder = builder;
		builder.show();
	}

	/**
	 * 绑定手机
	 * 
	 * @param act
	 * @param title
	 * @param msg
	 * @param sure
	 * @param cancel
	 */
	public static void showPhoneDialog(MyActivity act, String title, String sure, String cancel) {
		AlertDialog builder = act.builder;
		if (builder != null) {
			builder.dismiss();
		}
		Builder ss = null;
		ss = new Builder(act);
		ss.setTitle(title);
		ss.setCancelable(false);

		LinearLayout layout = new LinearLayout(act);
		layout.setOrientation(1);
		LinearLayout layout1 = new LinearLayout(act);
		layout1.setOrientation(0);
		TextView textView = new TextView(act);
		textView.setText("手机号：");
		EditText editText = new EditText(act);
		editText.setHint("请输入手机号");
		layout1.addView(textView);
		layout1.addView(editText);

		LinearLayout layout2 = new LinearLayout(act);
		layout2.setOrientation(0);
		TextView textView1 = new TextView(act);
		textView1.setText("验证码：");
		EditText editText1 = new EditText(act);
		editText1.setHint("请输入验证码");
		Button button1 = new Button(act);
		layout2.addView(textView1);
		layout2.addView(editText1);
		layout2.addView(button1);

		button1.setText("获取验证码");
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MLog.a(TAG,"123456");
			}
		});

		layout.addView(layout1);
		layout.addView(layout2);
		ss.setView(layout);

		if (sure != null) {
			ss.setPositiveButton(sure, MyApplication.getAppContext().onclick);
		}
		if (cancel != null) {
			ss.setNegativeButton(cancel, MyApplication.getAppContext().onclick);
		}
		builder = ss.create();
		act.builder = builder;
		builder.show();
	}

	private static String acc_file_name = AsdkActivity.getURL3().contains("xxhd")?"asdk-xx.acc":"asdk.acc";
	//应美（欣欣h5）
//	private static String acc_file_name = "asdk-xxh5.acc";
	//单机网游
//	private static String acc_file_name = "asdk-djwy.acc";
	//星凯
//	private static String acc_file_name = "asdk-xk.acc";
	public static ArrayList<String> getAccs(){
		boolean isHaveAcc = true;
		ArrayList<String> arr = new ArrayList<String>();
		StringBuffer strbuf = new StringBuffer();
		
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				file.mkdir();
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			BufferedReader br = new BufferedReader(new FileReader(accfile));
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf.append(content);
			}
			br.close();
			if(strbuf.length()==0){
				isHaveAcc = false;
			}else{
				String[] list = strbuf.toString().split("\\|");
				for (String item : list) {
					String str = des.decrypt(item);
					if(str.split("\\@").length>1){
						arr.add(str);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!isHaveAcc){
			Toast.makeText(MyApplication.context, "无账号记录", Toast.LENGTH_SHORT).show();
			return null;
		}else{
			return arr;
		}
	}
	public static void deleteAcc(String acc){
		FileWriter fw =null;
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			Reader rd = new FileReader(accfile);
			BufferedReader br = new BufferedReader(rd);
			String content = br.readLine();
			br.close();
			if(content!=null && !"".equals(content)){
				String[] list = content.split("\\|");
				for (int i = 0;i<list.length;i++) {
					String acc_item =des.decrypt(list[i]);
					if(acc_item.split("\\@").length>1){
						if(acc_item.equals(acc)){
							content = content.replace(list[i]+"|", "");
							break;
						}else{
						}
					}
				}
			}
			fw = new FileWriter(accfile);
			fw.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int time_cd = 0;
	static Handler mhandle = new Handler(Looper.getMainLooper()){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Object[] data= (Object[]) msg.obj;
				MyActivity activity = (MyActivity)data[0];
				int i=(Integer) data[1];
				synchronized (activity.mLuaState) {
					activity.mLuaState.getGlobal("bindphoneandemaillua");
					int index = activity.mLuaState.getTop();
					activity.mLuaState.getField(index, "verifycode_cd_");
					activity.mLuaState.pushInteger(i);// 压入倒计时秒数
					LuaTools.dbcall(activity.mLuaState, 1, 0);// 代表1个参数，0个返回值
				}
				break;
			case 1://验证码输入完成即登录
				Object[] data1= (Object[]) msg.obj;
				MyActivity activity1 = (MyActivity)data1[0];
				String verifycode = (String)data1[1];
				synchronized (activity1.mLuaState) {
					activity1.mLuaState.getGlobal("loginmodle");
					int index = activity1.mLuaState.getTop();
					activity1.mLuaState.getField(index, "login_with_verifycode");
					activity1.mLuaState.pushString(verifycode);
					LuaTools.dbcall(activity1.mLuaState, 1, 0);// 代表1个参数，0个返回值
				}
				break;
			case 2://sj_edAcount输入字符的改变
				Object[] data2= (Object[]) msg.obj;
				MyActivity activity2 = (MyActivity)data2[0];
				int length = (Integer)data2[1];
				synchronized (activity2.mLuaState) {
					activity2.mLuaState.getGlobal("loginmodle");
					int index = activity2.mLuaState.getTop();
					activity2.mLuaState.getField(index, "change_sj_edAcount");
					activity2.mLuaState.pushInteger(length);
					LuaTools.dbcall(activity2.mLuaState, 1, 0);// 代表1个参数，0个返回值
				}
				break;
			default:
				break;
			}
		};
	};
	public static void verifycode_cd(final MyActivity activity,final int cd){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i=cd;
				if(time_cd!=0){
					i=time_cd;
				}
				while (i>0) {
					i--;
					time_cd=i;
					
					if(activity!=null){
						MLog.a(TAG,"verifycode_cd_----------------"+i);
						Message msg=new Message();
						msg.what=0;
						msg.obj=new Object[]{activity,i};
						mhandle.sendMessage(msg);
					}else{
						time_cd = 0;
						return;
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				time_cd=0;
			}
		}).start();
	}
	
	/**
	 * QQ/客服联系方式界面
	 * @param activity
	 */
	public static void showConnectDialog(MyActivity activity){
		Builder builder = new Builder(activity);
		//需要使用相对布局
		RelativeLayout view = new RelativeLayout(activity);
		RelativeLayout.LayoutParams  par = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(par);
		
		synchronized (activity.mLuaState) {
			activity.mLuaState.getGlobal("chargelua");
			int index = activity.mLuaState.getTop();
			activity.mLuaState.getField(index, "showconnect_dialog");
			activity.mLuaState.pushJavaObject(activity);
			LuaTools.dbcall(activity.mLuaState, 1, 1);// 代表1个参数，1个返回值
		}
		MLog.a(TAG,"showConnectDialog-------------dbcall-------------end");
		try {
			MLog.a(TAG,"showConnectDialog-------------赋值connet_layout-------------"+activity.mLuaState.toJavaObject(-1));
			LinearLayout ll = (LinearLayout) activity.mLuaState.toJavaObject(-1);
			view.addView(ll);
		} catch (LuaException e) {
			e.printStackTrace();
		}
		MLog.a(TAG,"showConnectDialog-------------赋值connet_layout-------------end");
		AlertDialog dia =builder.create();
		dia.show();
		dia.getWindow().setContentView(view);
	}
	//快速注册弹窗
	public static void showaccautoregDialog(MyActivity activity){
		Builder builder = new Builder(activity);
		//需要使用相对布局
		RelativeLayout view = new RelativeLayout(activity);
		RelativeLayout.LayoutParams  par = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(par);
		MLog.a(TAG,"showaccautoregDialog-------------1");
		synchronized (activity.mLuaState) {
			activity.mLuaState.getGlobal("loginmodle");
			int index = activity.mLuaState.getTop();
			activity.mLuaState.getField(index, "showconnect_dialog");
			activity.mLuaState.pushJavaObject(activity);
			LuaTools.dbcall(activity.mLuaState, 1, 1);// 代表1个参数，1个返回值
		}
		MLog.a(TAG,"showaccautoregDialog-------------2");
		try {
			LinearLayout ll = (LinearLayout) activity.mLuaState.toJavaObject(-1);
			view.addView(ll);
		} catch (LuaException e) {
			e.printStackTrace();
		}
		AlertDialog dia =builder.create();
		dia.show();dia.setCancelable(false);
		dia.getWindow().setContentView(view);
	}

	public static void showTipsDialog(Activity activity, String content) {
		if (content.equals("qq_kefu")) {
			content = Configs.qqContactWay;
		}
		String finalContent = content;
		activity.runOnUiThread(() -> {
			int style_id = activity.getResources().getIdentifier("MyDialog", "style", activity.getPackageName());
			Dialog dialog = new Dialog(activity, style_id);

			int privacydialog_layout_id = activity.getResources().getIdentifier("dialog_tips", "layout", activity.getPackageName());
			View contentView = LayoutInflater.from(activity).inflate(privacydialog_layout_id, null);
			dialog.setContentView(contentView);
			dialog.setCancelable(false);

			ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
			layoutParams.height = dp2px(activity,210);
			layoutParams.width = dp2px(activity,340);
			contentView.setLayoutParams(layoutParams);
			dialog.getWindow().setGravity(Gravity.CENTER);

			int tipsContentId = activity.getResources().getIdentifier("tv_tips_content", "id", activity.getPackageName());
			int tipsComfirmId = activity.getResources().getIdentifier("btn_tips_comfirm", "id", activity.getPackageName());
			contentView.findViewById(tipsComfirmId).setOnClickListener(v -> {
				dialog.dismiss();
			});
			TextView tipsContent = contentView.findViewById(tipsContentId);
			if (!TextUtils.isEmpty(finalContent)) {
				tipsContent.setText(finalContent);
			}
			dialog.show();

		});
	}

	public static int dp2px(Context context, float dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
	}
	
	/**
	 * 显示所有帐号
	 * @param activity
	 */
	public static void showAccDialog(final MyActivity activity){
		boolean isHaveAcc = true;
		ArrayList<String> arr = new ArrayList<String>();
		StringBuffer strbuf = new StringBuffer();
		
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				file.mkdir();
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			BufferedReader br = new BufferedReader(new FileReader(accfile));
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf.append(content);
			}
			br.close();
			if(strbuf.length()==0){
				isHaveAcc = false;
			}else{
				String[] list = strbuf.toString().split("\\|");
				for (String item : list) {
					arr.add(des.decrypt(item));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!isHaveAcc){
			Toast.makeText(MyApplication.context, "无账号记录", Toast.LENGTH_SHORT).show();
		}else{
			Builder builder1 = new Builder(activity);
			RelativeLayout view = new RelativeLayout(activity);
			RelativeLayout.LayoutParams  par = null;
			if(arr.size()>6){
				DisplayMetrics dm = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
				int screenHeight = dm.heightPixels;
				int screenWidth = dm.widthPixels;
				if(screenHeight>screenWidth){
					par = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(screenHeight*0.28));
				}else{
					par = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(screenWidth*0.28));
				}
			}else{
				par = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			}
			par.addRule(RelativeLayout.CENTER_VERTICAL);
			ListView list = new ListView(activity);
			list.setDividerHeight(1);
			list.setFadingEdgeLength(0);
			list.setBackgroundColor(17170458);
			list.setAdapter(new MyAccAdapter(activity,arr));
			view.addView(list, par);
			builder1.setCustomTitle(view);
			builder1.setCancelable(true);
			final AlertDialog ad=builder1.show();
			list.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String content = (String)view.getTag();
					String acc = content.split("\\@")[0];
					String password = content.split("\\@")[1];
					synchronized (activity.mLuaState) {
						activity.mLuaState.getGlobal("main");
						int index = activity.mLuaState.getTop();
						activity.mLuaState.getField(index, "setaccount");
						activity.mLuaState.pushJavaObject(acc);// 压入第一个参数
						activity.mLuaState.pushJavaObject(password);// 压入第二个参数
						LuaTools.dbcall(activity.mLuaState, 2, 0);// 代表两个参数，0个返回值
					}
					ad.dismiss();
				}
			});
		}
	}
	
	/**
	 * 登录页显示帐号密码
	 * @param activity
	 */
	public static int fillAcc(final MyActivity activity){
		int isHaveAcc = 0;
		StringBuffer strbuf = new StringBuffer();
		String accstr = null;
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				file.mkdir();
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			BufferedReader br = new BufferedReader(new FileReader(accfile));
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf.append(content);
			}
			br.close();
			if(strbuf.length()==0){
				isHaveAcc = 1;
			}else{
				String[] list = strbuf.toString().split("\\|");
				accstr = des.decrypt(list[0]);
			}
			if(isHaveAcc == 0){
				String acc = accstr.split("\\@")[0];
				String password = accstr.split("\\@")[1];
				synchronized (activity.mLuaState) {
					activity.mLuaState.getGlobal("main");
					int index = activity.mLuaState.getTop();
					activity.mLuaState.getField(index, "setaccount");
					activity.mLuaState.pushJavaObject(acc);// 压入第一个参数
					activity.mLuaState.pushJavaObject(password);// 压入第二个参数
					LuaTools.dbcall(activity.mLuaState, 2, 0);// 代表两个参数，0个返回值
				}
			}
		} catch (FileNotFoundException e) {
			isHaveAcc = 1;
			e.printStackTrace();
		} catch (IOException e) {
			isHaveAcc = 1;
			e.printStackTrace();
		} catch (Exception e) {
			isHaveAcc = 1;
			e.printStackTrace();
		}
		return isHaveAcc;
	}
	
	/**
	 * 保存帐号密码到本地
	 * @param acc
	 * @param password
	 */
	public static void saveAccount(String acc,String password){
		
		String myacc= acc+"@"+password;
		FileWriter fw =null;
		StringBuffer strbuf1 = new StringBuffer();
		StringBuffer strbuf2 = new StringBuffer();
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			Reader rd = new FileReader(accfile);
			BufferedReader br = new BufferedReader(rd);
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf1.append(content);
			}
			br.close();
			strbuf2.append(des.encrypt(myacc)+"|");
			if(strbuf1.toString()!=null && !"".equals(strbuf1.toString())){
				String[] list = strbuf1.toString().split("\\|");
				for (int i = 0;i<list.length;i++) {
					String acc_item =des.decrypt(list[i]);
					if(acc_item.split("\\@").length>1){
						if(acc_item.split("\\@")[0].equals(acc)){
						}else{
							strbuf2.append(list[i]+"|");
						}
					}
				}
			}
			fw = new FileWriter(accfile);
			fw.write(strbuf2.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
//		try {
//			String str= acc+"@"+password;
//			DesUtils des = new DesUtils("leemenz");
//			System.out.println("加密前的字符：" + str);
//			System.out.println("加密后的字符：" + des.encrypt(str));
//		    System.out.println("解密后的字符：" + des.decrypt(des.encrypt(str)));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}//自定义密钥   
//		Toast.makeText(MyApplication.context, "保存成功", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 保存phone帐号密码到本地*****************************************************************************
	 * @param acc
	 * @param password
	 */
	public static void savePhoneAccount(String acc,String password){
		
		String myacc= acc+"@"+password;
		FileWriter fw =null;
		StringBuffer strbuf1 = new StringBuffer();
		StringBuffer strbuf2 = new StringBuffer();
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = getFilepath();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			Reader rd = new FileReader(accfile);
			BufferedReader br = new BufferedReader(rd);
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf1.append(content);
			}
			br.close();
			strbuf2.append(des.encrypt(myacc)+"|");
			if(strbuf1.toString()!=null && !"".equals(strbuf1.toString())){
				String[] list = strbuf1.toString().split("\\|");
				for (int i = 0;i<list.length;i++) {
					String acc_item =des.decrypt(list[i]);
					if(acc_item.split("\\@").length>1){
						if(acc_item.split("\\@")[0].equals(acc)){
						}else{
							strbuf2.append(list[i]+"|");
						}
					}
				}
			}
			fw = new FileWriter(accfile);
			fw.write(strbuf2.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	/**
	 * 历史账号页显示首个帐号
	 * @param activity
	 */
	public static int fillPhoneAcc(final MyActivity activity){
		int isHaveAcc = 4;
		StringBuffer strbuf = new StringBuffer();
		String accstr = null;
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = getFilepath();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				file.mkdir();
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			BufferedReader br = new BufferedReader(new FileReader(accfile));
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf.append(content);
			}
			br.close();
			if(strbuf.length()==0){
				isHaveAcc = 1;
			}else{
				String[] list = strbuf.toString().split("\\|");
				accstr = des.decrypt(list[0]);
			}
			if(isHaveAcc == 4){
				String acc = accstr.split("\\@")[0];
				String password = accstr.split("\\@")[1];
				synchronized (activity.mLuaState) {
					activity.mLuaState.getGlobal("main");
					int index = activity.mLuaState.getTop();
					activity.mLuaState.getField(index, "setphoneaccount");
					activity.mLuaState.pushJavaObject(acc);// 压入第一个参数
					activity.mLuaState.pushJavaObject(password);// 压入第二个参数
					LuaTools.dbcall(activity.mLuaState, 2, 0);// 代表两个参数，0个返回值
				}
			}
		} catch (FileNotFoundException e) {
			isHaveAcc = 1;
			e.printStackTrace();
		} catch (IOException e) {
			isHaveAcc = 1;
			e.printStackTrace();
		} catch (Exception e) {
			isHaveAcc = 1;
			e.printStackTrace();
		}
		return isHaveAcc;
	}
	public static ArrayList<String> getPhoneAccs(){
		boolean isHaveAcc = true;
		ArrayList<String> arr = new ArrayList<String>();
		StringBuffer strbuf = new StringBuffer();
		
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = getFilepath();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				file.mkdir();
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			BufferedReader br = new BufferedReader(new FileReader(accfile));
			String content = null;
			while ((content=br.readLine())!=null) {
				strbuf.append(content);
			}
			br.close();
			if(strbuf.length()==0){
				isHaveAcc = false;
			}else{
				String[] list = strbuf.toString().split("\\|");
				for (String item : list) {
					String str = des.decrypt(item);
					if(str.split("\\@").length>1){
						arr.add(str);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!isHaveAcc){
			return null;
		}else{
			return arr;
		}
	}
	public static void deletePhoneAcc(String acc){
		FileWriter fw =null;
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = getFilepath();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+acc_file_name);
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			Reader rd = new FileReader(accfile);
			BufferedReader br = new BufferedReader(rd);
			String content = br.readLine();
			br.close();
			if(content!=null && !"".equals(content)){
				String[] list = content.split("\\|");
				for (int i = 0;i<list.length;i++) {
					String acc_item =des.decrypt(list[i]);
					if(acc_item.split("\\@").length>1){
						if(acc_item.equals(acc)){
							content = content.replace(list[i]+"|", "");
							break;
						}else{
						}
					}
				}
			}
			fw = new FileWriter(accfile);
			fw.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static File getFilepath() {
		File path = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
			path = OutFace.mActivity.getExternalFilesDir(null);
		}else{
			path = OutFace.mActivity.getFilesDir();
		}
		return path;
	}
	
	/**
	 * 保存游客账号到本地
	 * @param acc
	 * @param password
	 * @param accid 
	 */
	public static void savevstAccount(String acc,String password, String accid){
		String myacc= acc+"@"+password+"@"+accid;
		FileWriter fw =null;
		try {
			DesUtils des = new DesUtils("leemenz");
			
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getAbsolutePath()+File.separator+Configs.ASDK+File.separator);
			if(!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			File accfile = new File(file.getAbsoluteFile()+File.separator+"asdk.accvst");
			if(!accfile.exists()){
				try {
					accfile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			fw = new FileWriter(accfile);
			fw.write(des.encrypt(myacc));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取手机号码
	 * **/
	public static TelephonyManager getPhoneManager(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager;
	}
	
	/**
	 * 复制内容到剪切板
	 * 
	 * @param context
	 * @param number
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	public static void copy(Context context, String number) {
		if (Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager clip = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(number);
		} else {
			ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(number);
		}
		Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
	}
	
	public static InputFilter[] inputFilter(int length){
		return new InputFilter[]{new InputFilter.LengthFilter(length)};
	}
	private static int autoLogin_time_milliseconds = -1;
	public static void setAutoLogin_time_milliseconds(
			int autoLogin_time_milliseconds) {
		PhoneTool.autoLogin_time_milliseconds = autoLogin_time_milliseconds;
	}

	public static void autoLogin(final MyActivity activity,int time_secends){
		autoLogin_time_milliseconds = time_secends*1000;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (autoLogin_time_milliseconds>0) {
					try {
						Thread.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
						autoLogin_time_milliseconds = -1;
						Thread.currentThread().interrupt();
					}
					autoLogin_time_milliseconds--;
					autoLogin_time_milliseconds--;
				}
				if(autoLogin_time_milliseconds==0){//登录
					synchronized (activity.mLuaState) {
						activity.mLuaState.getGlobal("loginmodle");
						int index = activity.mLuaState.getTop();
						activity.mLuaState.getField(index, "auto_login");
						LuaTools.dbcall(activity.mLuaState, 0, 0);// 代表0个参数，0个返回值
					}
				}
			}
		}).start();
	}
	public static void removeview(final MyActivity activity){
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				synchronized (activity.mLuaState) {
					activity.mLuaState.getGlobal("loginmodle");
					int index = activity.mLuaState.getTop();
					activity.mLuaState.getField(index, "removeview");
					LuaTools.dbcall(activity.mLuaState, 0, 0);// 代表0个参数，0个返回值
				}
			}
		});
	}
	@SuppressWarnings("deprecation")
	public static boolean isTopActivity(Activity act)  
    {  
        boolean isTop = false;  
        ActivityManager am = (ActivityManager)act.getSystemService(Context.ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        String name = act.getClass().getName();
        if (cn.getClassName().contains(name))  
        {  
            isTop = true;  
        }  
        return isTop;  
    } 
	//未成年人当日可在线时间默认90分
	private static int times_nomal = 90;
	public static void OpenAntiAddiction(String json){
		SharedPreferences sharef = MyApplication.context.getSharedPreferences("user_info", 0);
		String account = "",isholiday = "0",age = "18";
		System.out.println("OpenAntiAddiction--"+json);
		try {
			JSONObject data = new JSONObject(json).getJSONObject("data");
			account = data.getString("account");
			age = data.getString("age");
			String isindulge = data.getString("isindulge");//是否开启防沉迷0否1是
			isholiday = data.getString("isholiday");//是否节假日0否1是
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		times_nomal = "0".equals(isholiday)?90:180;
		//当前账号的游戏时间
		int cumulative_duration = sharef.getInt("asdk_"+account+"_duration", 0);
		//格式化当前时间
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattime = formatter.format(new Date());
		String pre = sharef.getString("asdk_"+account+"_entergametime", "");//当前账号的当天首次登录时间
		boolean istoday = IsToday(sharef, account,pre, formattime);
		System.out.println("istoday--"+istoday);
		System.out.println("cumulative_duration--"+cumulative_duration);
		System.out.println("times_nomal--"+times_nomal);
		if(istoday&&(cumulative_duration>=times_nomal)){
			//超过规定在线时长，弹窗提示防沉迷
			mhandler.sendEmptyMessage(0);
		}else{
			//未成年人防沉迷登录每次提示
			int age_ = 18;
			try {
				age_ = Integer.parseInt(age);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if(age_<8){
				mhandler.sendEmptyMessage(6);
			}else if(age_<16){
				mhandler.sendEmptyMessage(7);
			}else if(age_<18){
				mhandler.sendEmptyMessage(8);
			}
			
			//开启定时防沉迷
			getNetTime(sharef,account, isholiday, times_nomal);
		}
	}
	//保存当日首次公告时间
	public static void saveFirstGGTime(SharedPreferences share){
		//格式化当前时间
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattime = formatter.format(new Date());
		share.edit().putString("asdk_first_entergametime", formattime).commit();
	}
	//判断是否当日弹过公告
	public static void checkGGShowedToday(SharedPreferences share){
		//获取埋点集合
		getMaidiantype();
		
		//判断是否当日弹过公告
		String entergametime = share.getString("asdk_first_entergametime", "");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattime = formatter.format(new Date());
		boolean istoday = IsToday(share, "",entergametime, formattime);
		if(!istoday){//判断当日没弹过则重置弹公告条件
			share.edit().putBoolean("isshowtoday", true).commit();
			share.edit().putString("asdk_first_entergametime", "").commit();
		}
	}
	//获取网络时间
	private static void getNetTime(final SharedPreferences sharef,final String account,final String isholiday,final int times) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url = null;//取得资源对象
		        try {
		            url = new URL("http://www.baidu.com");
		            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
		            //url = new URL("http://www.bjtime.cn");
		            URLConnection uc = url.openConnection();//生成连接对象
		            uc.connect(); //发出连接
		            long ld = uc.getDate(); //取得网站日期时间
		            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            Calendar calendar = Calendar.getInstance();
		            calendar.setTimeInMillis(ld);
		            int hour = calendar.get(Calendar.HOUR_OF_DAY);
		            int minute = calendar.get(Calendar.MINUTE);
		            //22点到次日8点不能登录（20点后进游戏的开启定时判断到了22点强制退出）
		            if(hour>=22||hour<8){
		            	//22点到次日8点不能登录
		            	mhandler.sendEmptyMessage(1);
		            	return;
		            }
		            int valid_time = (22-hour)*60-minute;
	            	if(valid_time<=times){
	            		//距离22点的可玩时间小于规定时长，开启定时器到时弹出防沉迷框
	            		Message msg=new Message();
						msg.what = 2;
						msg.obj = new Object[]{valid_time};
	            		mhandler.sendMessage(msg);
	            	}
		            String formattime = formatter.format(calendar.getTime());
		            System.out.println("formattime:"+formattime);
		            OpenAntiAddictionTimer(sharef,account, isholiday, times,formattime);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}).start();
        
	}
	
	/**
	 * 
	 * @param sharef
	 * @param account	账号
	 * @param isholiday	是否节假日
	 * @param times	可在线时长
	 * @param formattime	当前网络时间
	 */
	private static void OpenAntiAddictionTimer(final SharedPreferences sharef,String account,String isholiday,int times,String formattime){
		int cumulative_duration = 0;
		String pre = sharef.getString("asdk_"+account+"_entergametime", "");//当前账号的当天首次登录时间
		boolean istoday = IsToday(sharef, account,pre, formattime);
		if(!istoday){
			sharef.edit().putInt("asdk_"+account+"_duration", 0).commit();
			sharef.edit().putString("asdk_"+account+"_entergametime", formattime).commit();
		}
		long bl = 0;
		while (bl>-1) {
			
			if(bl>0){
				String acc = "";
				String key = ""+MyApplication.getAppContext().getGameArgs().getCpid()+MyApplication.getAppContext().getGameArgs().getGameno();
				String isphoneoracc = sharef.getString(key+"isphoneoracc", "");
				if(isphoneoracc.equals("0")){//为手机号
					acc = sharef.getString(key+"phone_name", "");
				}else if (isphoneoracc.equals("1")){//为普通账号
					acc = sharef.getString(key+"name", "");
				}else if (isphoneoracc.equals("2")){//为游客
					acc = sharef.getString(key+"youke_name", "");
				}
				if(!acc.equals(account)){//判断是否跟当前登录的账号一致，否则退出
					break;
				}
				cumulative_duration = sharef.getInt("asdk_"+account+"_duration", 0)+5;
				sharef.edit().putInt("asdk_"+account+"_duration", cumulative_duration).commit();
				System.out.println(cumulative_duration);
				if(cumulative_duration>=times){
					//超过规定在线时长，弹窗提示防沉迷
					mhandler.sendEmptyMessage(3);
					break;
				}
			}
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				bl = -2;
			}
			bl++;
		}
	}
	
	//控制弹实名时间
	public static void controlRealName(String isautoreg,String checkminuts,String switch_){
		SharedPreferences share = MyApplication.context.getSharedPreferences("user_info", 0);
		String uid = share.getString("asdk_accountid", "");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattime = formatter.format(new Date());
		if(checkminuts==null||"".equals(checkminuts)){
			checkminuts="0";
		}
		if("1".equals(isautoreg)){//1为游客
			String entergametime = share.getString(uid+"_entergametimetoday", "");
			if("".equals(entergametime)){
				share.edit().putString(uid+"_entergametimetoday",formattime).commit();
			}else{
				boolean istoday = IsToday(share, "",entergametime, formattime);
				if(!istoday){//非当日重置游客时间
					share.edit().putString(uid+"_entergametimetoday",formattime).commit();
					share.edit().putInt(uid+"_controlRealName_time", 0).commit();
				}
			}
		}
		try {
			timingRealName(share, uid, Integer.parseInt(checkminuts), formattime,switch_);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	//开启实名弹窗计时
	private static void timingRealName(final SharedPreferences sharef,final String uid,final int times_permit,String formattime,final String switch_){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				//账号被记录的弹实名时间已用时间
				int uid_controlRealName_time = sharef.getInt(uid+"_controlRealName_time", 0);
				long bl = 0;
				while (bl>-1) {
					String uid_login = sharef.getString("asdk_accountid", "");
					if(!uid_login.equals(uid)){
						break;
					}
					if(uid_controlRealName_time>=times_permit){
						//超过规定在线时长，弹实名
						Message msg = new Message();
						msg.what = 5;
						msg.obj = new Object[]{switch_};
						mhandler.sendMessage(msg);
						break;
					}
					try {
						Thread.sleep(300000);
						uid_controlRealName_time = sharef.getInt(uid+"_controlRealName_time", 0)+5;
						sharef.edit().putInt(uid+"_controlRealName_time", uid_controlRealName_time).commit();
					} catch (InterruptedException e) {
						e.printStackTrace();
						bl = -2;
					}
					bl++;
				}
			}
		}).start();
	}
	
	//判断是否为当日
	private static boolean IsToday(SharedPreferences sharef,String account,String pre_,String day){
		System.out.println(pre_);
		System.out.println(day);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar pre = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        Date date_pre = null,date_cal = null;
		try {
			date_pre = formatter.parse(pre_);
			date_cal = formatter.parse(day);
			pre.setTime(date_pre);
	        cal.setTime(date_cal);
	        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
	            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
	                    - pre.get(Calendar.DAY_OF_YEAR);

	            if (diffDay == 0) {
	                return true;
	            }
	        }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
        return false;
    }
	
	//判断是否为当日
	public static boolean isAdult(String id){
		boolean isAd = false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		int id_year = Integer.parseInt(id.substring(6, 10));
		String id_month = id.substring(10, 12);
		if(id_month.startsWith("0")){
			id_month = id_month.substring(1);
		}
		String id_day= id.substring(12, 14);
		if(id_day.startsWith("0")){
			id_day = id_day.substring(1);
		}
		System.out.println("id_year------>"+id_year);
		System.out.println("id_month------>"+id_month);
		System.out.println("id_day------>"+id_day);
		if(year-id_year>18){
			System.out.println("已成年");
			isAd = true;
		}else if (year-id_year==18){
			if(month>Integer.parseInt(id_month)){
				System.out.println("已成年");
				isAd = true;
			}else if(month==Integer.parseInt(id_month)){
				if(day>=Integer.parseInt(id_day)){
					System.out.println("已成年");
					isAd = true;
				}
			}
		}
		return isAd;
	}
	
	
	static Handler mhandler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final Activity act= OutFace.getInstance(null).getmActivity();
			String str = "";
			String str_fading = "根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》，法定节假日当日未成年人玩家累计在线时间不能超过3小时,您累计已满3小时，请您下线休息";
			String str0 = "根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》，非法定节假日当日未成年人玩家累计在线时间不能超过1.5小时,您累计已满1.5小时，请您下线休息";
			if(times_nomal==180){
				str0 = str_fading;
			}
			String str1 = "根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》，未成年人玩家当日22点到次日8点无法获得游戏服务。";
			switch (msg.what) {
			case 2:
				Object[] data= (Object[]) msg.obj;
				int valid_time = (Integer)data[0];
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						mhandler.sendEmptyMessage(4);
					}
				}, valid_time*60*1000);
				break;
			case 0:
				str = "".equals(str)?str0:str;
			case 1:
				str = "".equals(str)?str1:str;
			case 4:
				str = "".equals(str)?str1:str;
			case 3:
				str = "".equals(str)?str0:str;
				final String str_ = str;
				act.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Builder builder =new Builder(act);
			            builder.setTitle("未成年人游戏限制");  
			            builder.setMessage(str_);
			            builder.setPositiveButton("退出游戏",
				                new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int which) {
				                        dialog.dismiss();
				                       //执行游戏退出
				                        act.finish();
				                        System.exit(0);
				                    }
				                });
			            builder.setCancelable(false);
			            builder.create().show();
					}
				});
				break;
			case 5://弹实名
				String switch_ = (String)((Object[])msg.obj)[0];
				Activity act_= OutFace.getInstance(null).getmActivity();
				Intent intent = new Intent();
				intent.setClass(act_,fly.fish.asdk.BindPhoneAndEmailActivity.class);
				intent.putExtra("type","5");
				intent.putExtra("switch",switch_);
				act_.startActivity(intent);
				break;
			case 6://未满8周岁
				String str6 = "系统识别到您的实名信息显示您未满8周岁，根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》，您将在每日22点-次日8点不可登录游戏，法定节假日登录在线时间不能超过3小时，非法定节假日不能超过1.5小时，不可充值";
				str = "".equals(str)?str6:str;
			case 7://未满16周岁
				String str7 = "系统识别到您的实名信息显示您未满16周岁，根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》，您将在每日22点-次日8点不可登录游戏，法定节假日登录在线时间不能超过3小时，非法定节假日不能超过1.5小时，单次充值金额不超过50元，每月充值金额累计不超过200元";
				str = "".equals(str)?str7:str;
			case 8://未满18周岁
				String str8 = "系统识别到您的实名信息显示您未满18周岁，根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》，您将在每日22点-次日8点不可登录游戏，法定节假日登录在线时间不能超过3小时，非法定节假日不能超过1.5小时，单次充值金额不超过100元，每月充值金额累计不超过400元";
				str = "".equals(str)?str8:str;
				final String str_2 = str;
				act.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Builder builder =new Builder(act);
			            builder.setTitle("未成年人游戏限制");  
			            builder.setMessage(str_2);
			            builder.setPositiveButton("我知道了",
				                new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int which) {
				                        dialog.dismiss();
				                    }
				                });
			            builder.setCancelable(false);
			            builder.create().show();
					}
				});
				break;
			default:
				break;
			}
		}
	};
	public static View getInputLayout(final Activity act){
		final PhoneCode phonecode_lay = new PhoneCode(act);
		phonecode_lay.setOnInputListener(new PhoneCode.OnInputListener() {
			
			@Override
			public void onSucess(String codes) {
				System.out.println("codes:"+codes);
				phonecode_lay.hideSoftInput();
				Message msg=new Message();
				msg.what=1;
				msg.obj=new Object[]{act,codes};
				mhandle.sendMessage(msg);
			}
			
			@Override
			public void onInput() {
				
			}
		});
		return phonecode_lay;
	}
	public static void addTextChangedListener(final Activity act,EditText sj_edit){
		sj_edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				int length = editable.toString().length();
				Message msg=new Message();
				msg.what=2;
				msg.obj=new Object[]{act,length};
				mhandle.sendMessage(msg);
			}
		});
	}
	//保存AndId的路径
    private static final String CACHE_DIR_AndId = "aray/cache/devices";
    //保存的AndId 采用隐藏文件的形式进行保存
    private static final String DEVICES_FILE_NAME_AndId = ".asdkDeviceAndId";
	/**
     * 统一处理设备唯一标识 保存AndId的地址
     * @param context
     * @return
     */
    private static File getDevicesDirAndId(Context context) {
        File mCropFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cropdir = new File(Environment.getExternalStorageDirectory(), CACHE_DIR_AndId);
            if (!cropdir.exists()) {
                cropdir.mkdirs();
            }
            mCropFile = new File(cropdir, DEVICES_FILE_NAME_AndId); // 用当前时间给取得的图片命名
        } else {
            File cropdir = new File(context.getFilesDir(), CACHE_DIR_AndId);
            if (!cropdir.exists()) {
                cropdir.mkdirs();
            }
            mCropFile = new File(cropdir, DEVICES_FILE_NAME_AndId);
        }
        return mCropFile;
    }
    /**
     * 保存 内容到 SD卡中,  这里保存的就是 设备唯一标识符
     * @param str
     * @param context
     */
    private static void saveAndId(String str, Context context) {
        File file = getDevicesDirAndId(context);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //读取存储的AndId
    private static String readDeviceAndID(Context context) {
        File file = getDevicesDirAndId(context);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String deviceID = in.readLine().trim();
            in.close();
            isr.close();
            fis.close();
            return deviceID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	//自生成设备id
	public static String getAndId(Context context){
		String andid = readDeviceAndID(context);
		System.out.println("readandid");
		if(andid!=null&&!"".equals(andid)){
			MLog.a("andid-->"+andid);
			return andid;
		}
		String deviceId = "";
		StringBuffer s = new StringBuffer();
		try {
            //获取设备的ANDROID_ID+SERIAL硬件序列号
        	deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID); 
        	String serial= Build.SERIAL;
            s.append(deviceId).append(serial);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
        String md5 = MD5Util.getMD5String(s.toString());
        MLog.a("getAndId-->"+md5);
        saveAndId(md5, context);
        return md5;
	}
	//埋点上报
	private static HashMap<String, String> map = new HashMap<String, String>();
	//SDK主体判断
	private static String subject = "";
	public static String getSubject() {
		if("".equals(subject)){
			System.out.println("acc_file_name--"+acc_file_name);
			String s = acc_file_name.split("\\.")[0];
			subject = "asdk".equals(s)?"zs":s.split("-")[1];
		}
		return subject;
	}
	static {
		map.put("0",	"A000");map.put("1",	"A001");map.put("2",	"A002");
		map.put("3",	"B000");map.put("4",	"B001");map.put("5",	"B002");
		map.put("6",	"B003");map.put("7",	"B004");map.put("8",	"C000");
		map.put("9",	"C001");map.put("10",	"D000");map.put("11",	"D001");
		map.put("12",	"D002");map.put("13",	"E000");map.put("14",	"E001");
		map.put("15",	"E002");map.put("16",	"F000");map.put("17",	"F001");
		map.put("18",	"G000");map.put("19",	"G001");map.put("20",	"G002");
		map.put("21",	"H000");map.put("22",	"H001");map.put("23",	"H002");
		map.put("24",	"I000");map.put("25",	"I001");map.put("26",	"I002");
		map.put("27",	"I003");map.put("28",	"J000");map.put("29",	"J001");
		map.put("30",	"K000");map.put("31",	"K001");map.put("32",	"K002");
		map.put("33",	"L000");map.put("34",	"L001");map.put("35",	"L002");
		map.put("36",	"M000");map.put("37",	"M001");map.put("38",	"M002");
		map.put("39",	"N000");map.put("40",	"N001");map.put("41",	"N002");
		map.put("42",	"O000");map.put("43",	"O001");
	}
	//获取埋点集合
	private static String maidian_list = "";
	private final static String get_maiidan_url = "http://iospingtai.xinxinjoy.com:8084/outerinterface/maidiantype.php?";
	private final static String submit_maiidan_url = "http://iospingtai.xinxinjoy.com:8084/outerinterface/maidian.php?";
	
	private static void getMaidiantype(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String response = HttpUtils.postMethod(get_maiidan_url, "{\"server\":\""+getSubject()+"\"}", "utf-8");
				System.out.println("response--->"+response);
				try {
					JSONObject data = new JSONObject(response);
					String code = data.getString("code");
					if("0".equals(code)){//成功获取
						maidian_list = data.getJSONObject("data").getString("needmaidian");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	//上报埋点
	public static void submitEvent(final String flag){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String value = map.get(flag);
				if("".equals(maidian_list)){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(!"".equals(maidian_list)){
					if(value!=null && maidian_list.contains(value)){
						JSONObject param = null;
						try {
							param = new JSONObject();
							param.put("menuid", "");
							param.put("server", getSubject());
							param.put("daihao", value);
							param.put("nettype", getProvidersName(MyApplication.context));
							param.put("system", "android"+getVERSION(MyApplication.context));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						String response = HttpUtils.postMethod(submit_maiidan_url, param.toString(), "utf-8");
						System.out.println("response--->"+response);
					}
				}else{
					System.out.println("no-maidian_list");
				}
			}
		}).start();
	}
}
