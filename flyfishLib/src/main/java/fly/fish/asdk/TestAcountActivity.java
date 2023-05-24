package fly.fish.asdk;

import fly.fish.tools.MLog;
import android.os.Bundle;
import android.widget.LinearLayout;

public class TestAcountActivity extends MyActivity {
	private LinearLayout mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mLayout = new LinearLayout(this);
		setContentView(mLayout);
		init();
	}

	@Override
	protected void onResume() {// 出栈从这里进入
		super.onResume();
	}

	/**
	 * 系统初始化
	 */
	public void init() {
		super.init();
	}

	/**
	 * 初始化lua开发环境
	 */
	public void initLua() {
		super.initLua();
		MLog.s(this + " ----> onResume doing2 ");
		mLuaState.pushJavaObject(mLayout);
		mLuaState.setGlobal("rootview");
		MLog.s(this + " ----> onResume end ");
	}

}
