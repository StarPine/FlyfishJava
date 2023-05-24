package fly.fish.checkorder;

import org.keplerproject.luajava.LuaState;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import fly.fish.asdk.MyApplication;
import fly.fish.tools.FilesTool;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

public class IntentServiceDemo extends IntentService {
	private LuaState myLuaState;
	public static boolean Mark = true;
	// public static List<PayBackModel> orders = new ArrayList<PayBackModel>();
	private Bundle mBundle = null;
	public static int index;

	private MyApplication app;

	public static void setIndex(int index) {
		IntentServiceDemo.index = index;
	}

	public static void setMark(boolean mark) {
		Mark = mark;
	}

	public IntentServiceDemo() {
		// 必须实现父类的构造方法
		super("IntentServiceDemo");
	}

	@Override
	public IBinder onBind(Intent intent) {
		MLog.s("second confirm onBind");
		return super.onBind(intent);
	}

	@Override
	public void onCreate() {
		app = MyApplication.getAppContext();
		myLuaState = app.getmLuaState();
		MLog.s("second confirm onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		MLog.s("second confirm onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MLog.s("second confirm onStartCommand");
		Mark = true;
		mBundle = intent.getExtras();

		/*
		 * String msg = mBundle.getString("msg"); String sum =
		 * mBundle.getString("sum"); String chargetype =
		 * mBundle.getString("chargetype"); String customorderid =
		 * mBundle.getString("customorderid"); String custominfo =
		 * mBundle.getString("custominfo"); PayBackModel pbm=new
		 * PayBackModel(customorderid, msg, sum, chargetype, custominfo);
		 * orders.add(pbm);
		 */

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void setIntentRedelivery(boolean enabled) {
		MLog.s("second confirm setIntentRedelivery");
		super.setIntentRedelivery(enabled);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		MLog.s("second confirm onHandleIntent");

		FilesTool.loadLuaScript("lua/check_charge_result.lua");
		int checkTimes = mBundle.getInt("checkTimes");
		while (Mark) {
			// 定时验证实现
			index++;

			synchronized (myLuaState) {
				myLuaState.getField(LuaState.LUA_GLOBALSINDEX, "check");
				myLuaState.pushJavaObject(app.getOrders().get(app.curKey).get(0));// 压入第一个参数
				LuaTools.dbcall(myLuaState, 1, 0);// 代表两个参数，0个返回值
			}

			try {
				Thread.sleep(checkTimes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onDestroy() {
		MLog.s("second confirm onDestroy");
		super.onDestroy();
	}

}
