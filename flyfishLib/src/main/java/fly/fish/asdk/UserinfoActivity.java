package fly.fish.asdk;

import android.os.Bundle;
import android.widget.LinearLayout;

public class UserinfoActivity extends MyActivity {
	private LinearLayout myLay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLay = new LinearLayout(this);
		setContentView(myLay);
		init();

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
	}

	/**
	 * 初始化lua开发环境
	 */
	public void initLua() {
		super.initLua();
		mLuaState.pushJavaObject(myLay);
		mLuaState.setGlobal("rootview");
	}

}
