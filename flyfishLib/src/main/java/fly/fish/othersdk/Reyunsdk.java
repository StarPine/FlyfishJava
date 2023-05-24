package fly.fish.othersdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import fly.fish.asdk.MyApplication;
import fly.fish.tools.MLog;

/*
 * 作为中间类
 */
public class Reyunsdk {

	private static Class<?> clazz_ry = null;// ry
	private static Class<?> clazz = null;// toutiao
	private static Class<?> clazz2 = null;// gdt
	private static Class<?> clazz3 = null;// aqy
	private static Class<?> clazz4 = null;// ucgism
	private static Class<?> clazz_ks = null;// kuaishou
	private static Class<?> clazz_xjy = null;// 心迹云

	public static void reyunApplicationInit(Application application) {
		// ry
		try {
			clazz_ry = Class.forName("com.reyun.tracking.sdk.Tracking");
		} catch (ClassNotFoundException e) {
			MLog.a("no-ry");
			;
		}
		// gdt
		try {
			clazz2 = Class.forName("fly.fish.othersdk.GDTSDK");
		} catch (ClassNotFoundException e) {
			MLog.a("no-gdt");
			;
		}
		if (clazz2 != null) {
			Method method2 = getMethod(clazz2, "gdtApplicationInit",
					MyApplication.class);
			invoke(method2, (MyApplication) application);
		}
		// aqy
		try {
			clazz3 = Class.forName("fly.fish.othersdk.AQYSDK");
		} catch (ClassNotFoundException e) {
			MLog.a("no-aqy");
			;
		}
		if (clazz3 != null) {
			Method method = getMethod(clazz3, "applicationInit",
					Application.class);
			invoke(method, application);
		}
		// uc
		try {
			clazz4 = Class.forName("fly.fish.othersdk.UCGismSDK");
		} catch (ClassNotFoundException e) {
			MLog.a("no-uc");
			;
		}
		if (clazz4 != null) {
			Method method = getMethod(clazz4, "applicationInit",
					Application.class);
			invoke(method, application);
		}

		// kuaishou
		try {
			clazz_ks = Class.forName("fly.fish.othersdk.KuaiShouSDK");
		} catch (ClassNotFoundException e) {
			MLog.a("no-kuaishou");
			;
		}
		
		// 心迹云
				try {
					clazz_xjy = Class.forName("fly.fish.othersdk.XinJiYunsdk");
				} catch (ClassNotFoundException e) {
					System.out.println("Reyunsdk------no  XinJiYunsdk");
					;
				}
	}

	public static void reyuninit(Activity activity) {
		String appkey = "";
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open("cloud.txt");
			appkey = new BufferedReader(new InputStreamReader(ins)).readLine()
					.trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ry
		if (clazz_ry != null) {
			// open debug log
			Method method1 = getMethod(clazz_ry, "setDebugMode", Boolean.class);
			invoke(method1, false);
			// init
			Method method2 = getMethod(clazz_ry, "initWithKeyAndChannelId",
					Application.class, String.class, String.class);
			invoke(method2, MyApplication.getAppContext(), appkey, "_default_");
		}
		// 头条初始化
		try {
			clazz = Class.forName("fly.fish.othersdk.TouTiaoSDK");
		} catch (ClassNotFoundException e) {
			MLog.a("no-tt");
			;
		}
		if (clazz != null) {
			Method method1 = getMethod(clazz, "toutiaoinit", Activity.class);
			invoke(method1, activity);
		}
		// aqy
		if (clazz3 != null) {
			Method method = getMethod(clazz3, "initsdk", new Class<?>[0]);
			invoke(method, new Object[0]);
		}
		// uc
		if (clazz4 != null) {
			Method method = getMethod(clazz4, "initsdk", new Class<?>[0]);
			invoke(method, new Object[0]);
		}

		// kuaishou
		if (clazz_ks != null) {
			Method method = getMethod(clazz_ks, "kuaishouinit",  Activity.class);
			invoke(method,activity);
		}
		
		// 心迹云
		if (clazz_xjy != null) {
			System.out.println("Reyunsdk------xinjiyuninit");
			Method method4 = getMethod(clazz_xjy, "xinjiyuninit", Activity.class);
			invoke(method4, activity);
		}
	}

	// 注册
	public static void reyunsetRegister(String accountId) {
		// ry
		if (clazz_ry != null) {
			Method method1 = getMethod(clazz_ry, "setRegisterWithAccountID",
					String.class);
			invoke(method1, accountId);
		}
		// 头条注册上报
		if (clazz != null) {
			Method method = getMethod(clazz, "setRegister", String.class);
			invoke(method, accountId);
		}

		// gdt
		if (clazz2 != null) {
			Method method2 = getMethod(clazz2, "register", new Class<?>[0]);
			invoke(method2, new Object[0]);
		}
		// 爱奇艺
		if (clazz3 != null) {
			Method method3 = getMethod(clazz3, "register", new Class<?>[0]);
			invoke(method3, new Object[0]);
		}

		// uc
		if (clazz4 != null) {
			Method method4 = getMethod(clazz4, "register", new Class<?>[0]);
			invoke(method4, new Object[0]);
		}

		// kuaishou
		if (clazz_ks != null) {
			Method method4 = getMethod(clazz_ks, "setRegister", String.class);
			invoke(method4, accountId);
		}
		
		// 心迹云
				if (clazz_xjy != null) {
					Method method4 = getMethod(clazz_xjy, "setRegister", String.class);
					invoke(method4, accountId);
				}
	}

	public static void reyunLogin(String accountId) {
		System.out.println("Reyunsdk------reyunLogin");
		// ry
		if (clazz_ry != null) {
			Method method1 = getMethod(clazz_ry, "setLoginSuccessBusiness",
					String.class);
			invoke(method1, accountId);
		}
		// 头条登录上报
		if (clazz != null) {
			Method method = getMethod(clazz, "setLogin", String.class);
			invoke(method, accountId);
		}
		// aqy
		if (clazz3 != null) {
			Method method3 = getMethod(clazz3, "setLogin", new Class<?>[0]);
			invoke(method3, new Object[0]);
		}
		// 心迹云
				if (clazz_xjy != null) {
					Method method3 = getMethod(clazz_xjy, "setLogin", String.class);
					invoke(method3, accountId);
				}
	}

	// 下单设置
	public static void reyunSetOrder(String orderid,String goodName, String price) {
		System.out.println("Reyunsdk------reyunSetOrder");
		// 头条
		if (clazz != null) {
			Method method = getMethod(clazz, "setGoodNameAndPrice",
					String.class, String.class);
			invoke(method, goodName, price);
		}
		// gdt
		if (clazz2 != null) {
			Method method1 = getMethod(clazz2, "complete_order",
					new Class<?>[0]);
			invoke(method1, new Object[0]);
		}
		// aqy
		if (clazz3 != null) {
			Method method3 = getMethod(clazz3, "setorder", new Class<?>[0]);
			invoke(method3, new Object[0]);
		}

		// kuaishou
		if (clazz_ks != null) {

			Method method4 = getMethod(clazz_ks, "setGoodNameAndPrice",
					String.class, String.class);
			invoke(method4, goodName, price);

			Method method5 = getMethod(clazz_ks, "OrderSubmit", new Class<?>[0]);
			invoke(method5, new Object[0]);
		}
		
		// 心迹云
				if (clazz_xjy != null) {
					System.out.println("Reyunsdk------OrderSubmit");
					Method method6 = getMethod(clazz_xjy, "OrderSubmit", String.class, String.class, String.class);
					invoke(method6, orderid,goodName,price);
				}
	}
	
	
	

	// 支付完成
	public static void reyunsetPay(String order, String paytype, String money,
			boolean issuccess) {
		if (issuccess) {
			// ry
			if (clazz_ry != null) {
				Method method1 = getMethod(clazz_ry, "setPayment",
						String.class, String.class, String.class, Float.class);
				invoke(method1, order, paytype, "CNY", Float.parseFloat(money));
			}
			// gdt
			if (clazz2 != null) {
				Method method2 = getMethod(clazz2, "purchase", new Class<?>[0]);
				invoke(method2, new Object[0]);
			}
			// aqy
			if (clazz3 != null) {
				Method method3 = getMethod(clazz3, "purchase", new Class<?>[0]);
				invoke(method3, new Object[0]);
			}
			// uc
			if (clazz4 != null) {
				Method method4 = getMethod(clazz4, "purchase", String.class);
				invoke(method4, money);
			}

			// kuaishou
			if (clazz_ks != null) {
				Method method5 = getMethod(clazz_ks, "OrderPayed",
						new Class<?>[0]);
				invoke(method5, new Object[0]);
			}
			
			// 心迹云
			if (clazz_xjy != null) {
				System.out.println("Reyunsdk------PayOver");
				Method method6 = getMethod(clazz_xjy, "PayOver", String.class, String.class);
				invoke(method6, order,money);
			}
		}
		// 头条
		if (clazz != null) {
			Method method2 = getMethod(clazz, "setPurchase", boolean.class);
			invoke(method2, issuccess);
		}

	}

	public static void reyunResume(Activity act) {
		// 头条
		if (clazz != null) {
			Method method = getMethod(clazz, "onResume", Activity.class);
			invoke(method, act);
		}
		// gdt
		if (clazz2 != null) {
			Method method2 = getMethod(clazz2, "onResume", Activity.class);
			invoke(method2, act);
		}
		// aqy
		if (clazz3 != null) {
			Method method3 = getMethod(clazz3, "setResume", new Class<?>[0]);
			invoke(method3, new Object[0]);
		}

		// kuaishou
		if (clazz_ks != null) {
			Method method4 = getMethod(clazz_ks, "onResume", Activity.class);
			invoke(method4, act);
		}
	}

	public static void reyunPause(Activity act) {
		// 头条
		if (clazz != null) {
			Method method = getMethod(clazz, "onPause", Activity.class);
			invoke(method, act);
		}

		// kuaishou
		if (clazz_ks != null) {
			Method method2 = getMethod(clazz_ks, "onPause", Activity.class);
			invoke(method2, act);
		}
	}

	public static void reyunDestroy() {
		// aqy
		if (clazz3 != null) {
			Method method3 = getMethod(clazz3, "setDestroy", new Class<?>[0]);
			invoke(method3, new Object[0]);
		}
		// uc
		if (clazz4 != null) {
			Method method4 = getMethod(clazz4, "setDestroy", new Class<?>[0]);
			invoke(method4, new Object[0]);
		}
		
		// 心迹云
		if (clazz_xjy != null) {
			Method method4 = getMethod(clazz_xjy, "onDestroy", new Class<?>[0]);
			invoke(method4, new Object[0]);
		}
		
		
	}

	public static void reyunexit() {
		// ry
		if (clazz_ry != null) {
			Method method1 = getMethod(clazz_ry, "exitSdk", new Class<?>[0]);
			invoke(method1, new Object[0]);
		}
		// uc
		if (clazz4 != null) {
			Method method = getMethod(clazz4, "setExit", new Class<?>[0]);
			invoke(method, new Object[0]);
		}

	}

	public static void reyunSetUserData(String data) {
		// 头条
		if (clazz != null) {
			Method method = getMethod(clazz, "myOutInGame", String.class);
			invoke(method, data);
		}
		// aqy
		if (clazz3 != null) {
			Method method3 = getMethod(clazz3, "myOutInGame", String.class);
			invoke(method3, data);
		}
		// uc
		if (clazz4 != null) {
			Method method4 = getMethod(clazz4, "myOutInGame", String.class);
			invoke(method4, data);
		}

		// kuaishou
		if (clazz_ks != null) {
			Method method5 = getMethod(clazz_ks, "myOutInGame", String.class);
			invoke(method5, data);
		}
	}

	private static Method getMethod(Class<?> clazz, String flag,
			Class<?>... clas) {
		try {
			return clazz.getMethod(flag, clas);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void invoke(Method method, Object... prams) {
		if (method == null) {
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
}
