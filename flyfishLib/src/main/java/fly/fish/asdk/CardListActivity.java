package fly.fish.asdk;

import fly.fish.tools.MLog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * 充值卡列表
 * 
 * @author kete
 * 
 */
public class CardListActivity extends MyActivity {
	private LinearLayout myLay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLay = new LinearLayout(this);
		setContentView(myLay);
		init();
		if(android.os.Build.VERSION.SDK_INT >= 11){
			this.setFinishOnTouchOutside(false);
		}
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
		MLog.s(this + " ----> onResume doing2 ");
		mLuaState.pushJavaObject(myLay);
		mLuaState.setGlobal("rootview");
		MLog.s(this + " ----> onResume end ");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

}
