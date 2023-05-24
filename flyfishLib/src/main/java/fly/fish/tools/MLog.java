package fly.fish.tools;

import android.util.Log;

public class MLog {
	private static boolean isLog = false;
	private static boolean debug = false;
	private static boolean error = true;
	
	public static void setDebug(boolean debug) {
		MLog.debug = debug;
		MLog.isLog = debug;
	}

	public static void d(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg);
		}
	}

	public static void a(String str) {
		if (isLog) {
			Log.i("ASDK",str);
		}
	}
	
	public static void a(String tag,String str) {
		if (isLog) {
			Log.i(tag, str);
		}
	}

	public static void s(String str) {
		if (debug) {
			Log.i("ASDK",str);
		}
	}

	public static void e(String tag, String str) {
		if (error) {
			Log.e(tag, str);
		}
	}

	public static void b(Object str) {
		if (debug) {
			System.out.println("NONONO " + str.toString());
		}
	}

	public static void err(Object str) {
		if (debug) {
			System.err.println(str.toString());
		}
	}
}
