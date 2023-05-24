package fly.fish.tools;

import org.keplerproject.luajava.CPtr;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaObject;
import org.keplerproject.luajava.LuaState;

import android.content.DialogInterface.OnClickListener;
import fly.fish.asdk.MyApplication;
import fly.fish.impl.HttpCallBack;
import fly.fish.impl.HttpErrorHandler;

public class LuaTools {

	/**
	 * 错误捕获函数
	 * 
	 * @param ptr
	 */
	private static synchronized native void pushErrorHandling(CPtr ptr);

	private static synchronized native void encrtyimei(String str);

	/**
	 * 调试调用
	 * 
	 * @param L
	 * @param nargs
	 * @param nresults
	 */
	public static void dbcall(LuaState L, int nargs, int nresults) {
		int status = -1;
		int base = L.getTop() - nargs; /* function index */

		L.getField(LuaState.LUA_REGISTRYINDEX, "trackback");
		if (L.isNil(-1)) {
			L.pop(1);
			pushErrorHandling(L.getLuaState());// 错误处理函数入栈
			L.pushValue(-1);// 错误处理函数再入栈
			L.setField(LuaState.LUA_REGISTRYINDEX, "trackback");// 错误处理函数出栈，到trackback表中
		}
		L.insert(base); /* put it under chunk and args */
		status = L.pcall(nargs, nresults, base);
		L.remove(base); /* remove traceback function */
		// L.toString(-1)--这里取的就是全堆栈信息

		// 下面传给lua处理
		if (status != 0) {
			L.gc(LuaState.LUA_GCCOLLECT, 0);
			String s = L.toString(-1);
			MLog.e("super_error-luatools", s);

			/* 要上传到服务器 */
			MyApplication app = MyApplication.getAppContext();
			final String ss = app.getGameArgs().getAccount_id() + "|" + PhoneTool.getPhoneInfo(MyApplication.context) + "|" + s;
			new Thread(new Runnable() {
				@Override
				public void run() {

					MyApplication app = MyApplication.getAppContext();
					synchronized (app.getmLuaState()) {
						app.getmLuaState().getGlobal("update");
						int index = app.getmLuaState().getTop();
						app.getmLuaState().getField(index, "reportBug");
						app.getmLuaState().pushString(ss);
						app.getmLuaState().call(1, 0);
					}

				}
			}).start();

		}

		// 下面传给lua处理
		/*
		 * if (status != 0) { L.gc(LuaState.LUA_GCCOLLECT, 0);
		 * L.getField(LuaState.LUA_GLOBALSINDEX, "LuaPanicCallback"); if
		 * (L.isNil(-1)){ MLog.s("if (lua_isnil(L, -1))"); L.pop(1); L.error();
		 * return; } L.pushValue(-2); status = L.pcall(1, 0, 0); if (status !=
		 * 0){ MLog.s("if (status != 0)"); L.error(); } }
		 */
	}

	/**
	 * 加载升级文件,调用初始化函数
	 */
	public static void loadUpdate() {
		// 加载并执行LUA文件(其实这段代码只能跑一遍)
		boolean b1 = FilesTool.loadLuaScript("json/json.lua");
		boolean b2 = FilesTool.loadLuaScript("utils.lua");
		boolean b3 = FilesTool.loadLuaScript("nettool.lua");
		boolean b4 = FilesTool.loadLuaScript("update.lua");

		boolean b5 = FilesTool.loadLuaScript("lua/widget.lua");
		boolean b6 = FilesTool.loadLuaScript("main.lua");
		boolean b7 = FilesTool.loadLuaScript("lua/cancelback.lua");

		try {
			MyApplication.getAppContext().httpback = null;
			LuaObject luaobj = MyApplication.getAppContext().getmLuaState().getLuaObject("HttpCallBack");
			MyApplication.getAppContext().httpback = (HttpCallBack) (luaobj.createProxy("fly.fish.impl.HttpCallBack"));
			MyApplication.getAppContext().httpbackkjava = null;
			luaobj = MyApplication.getAppContext().getmLuaState().getLuaObject("HttpCallBackKjava");
			MyApplication.getAppContext().httpbackkjava = (HttpCallBack) (luaobj.createProxy("fly.fish.impl.HttpCallBack"));

			MyApplication.getAppContext().errorhandler = null;
			luaobj = MyApplication.getAppContext().getmLuaState().getLuaObject("HttpErrorHandler");
			MyApplication.getAppContext().errorhandler = (HttpErrorHandler) (luaobj.createProxy("fly.fish.impl.HttpErrorHandler"));

			MyApplication.getAppContext().onclick = null;
			luaobj = MyApplication.getAppContext().getmLuaState().getLuaObject("OnClickListener");
			MyApplication.getAppContext().onclick = (OnClickListener) (luaobj.createProxy("android.content.DialogInterface$OnClickListener"));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (LuaException e) {
			e.printStackTrace();
		}
		// 启动初始化
		try {
			LuaState mLuaState = MyApplication.getAppContext().getmLuaState();
			mLuaState.getGlobal("update");
			int index = mLuaState.getTop();
			mLuaState.getField(index, "init");
			LuaTools.dbcall(mLuaState, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MLog.s("init environment!!!");
	}

	public static String myTrim(String str) {
		return str.trim();
	}
}
