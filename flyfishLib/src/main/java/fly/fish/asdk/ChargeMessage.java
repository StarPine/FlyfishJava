package fly.fish.asdk;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * 充值消息
 * 
 * @author
 * 
 */
public class ChargeMessage extends MyActivity {
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
		initLua();
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
		mLuaState.pushJavaObject(myLay);
		mLuaState.setGlobal("rootview");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return true;
	}
}
