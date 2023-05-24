package fly.fish.asdk;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import fly.fish.adapter.MyCardAdapter;
import fly.fish.aidl.MyRemoteService;
import fly.fish.beans.CardItem;
import fly.fish.tools.MLog;

public class CardRechargeActivity extends Activity implements TextWatcher {

	private TextView tv = null;
	private EditText et = null;
	private static GridView gv = null;
	
	private static Intent mIntent = null;
	private static CardRechargeActivity mAct = null;
	
	private InputMethodManager imm = null;
	private static ArrayList<CardItem> list = new ArrayList<CardItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		int Layout_id = this.getResources().getIdentifier("layout_cardrecharge", "layout", this.getPackageName());
		int bk_text = this.getResources().getIdentifier("bk_text", "id", this.getPackageName());
		int et_text = this.getResources().getIdentifier("et_text", "id", this.getPackageName());
		int grid_view = this.getResources().getIdentifier("grid_view", "id", this.getPackageName());
		int ic_back = this.getResources().getIdentifier("ic_back", "drawable", this.getPackageName());
		setContentView(Layout_id);
		tv = (TextView)findViewById(bk_text);
		et = (EditText)findViewById(et_text);
		gv = (GridView)findViewById(grid_view);
		tv.setBackgroundResource(ic_back);
		
		et.addTextChangedListener(this);
		
		mIntent = getIntent();
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelpayBack();
			}
		});
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		mAct = this;
		
		mIntent.setClass(this, MyRemoteService.class);
		Bundle bundle = mIntent.getExtras();
		bundle.putString("flag", "getCardPayInfo");
		mIntent.putExtras(bundle);
		MLog.a("CardRechargeActivity", "flag--"+mIntent.getExtras().getString("flag"));
		this.startService(mIntent);
		
		reqResult(0, "{\"code\":\"0\",\"data\":[{\"pay_id\":\"0\",\"remark\":\"10\",\"money\":\"10\"},{\"pay_id\":\"1\",\"remark\":\"20\",\"money\":\"20\"},{\"pay_id\":\"2\",\"remark\":\"30\",\"money\":\"30\"},{\"pay_id\":\"3\",\"remark\":\"40\",\"money\":\"40\"},{\"pay_id\":\"4\",\"remark\":\"50\",\"money\":\"50\"}]}");
		
	}
	
	public static void reqResult(final int code, final String cardPayInfo){
		mAct.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				if(code==0){
					try {
						JSONObject result = new JSONObject(cardPayInfo);
						if ("0".equals(result.getString("code"))) {
							JSONArray data = result.getJSONArray("data");
							int length = data.length();
							for (int i = 0; i < length; i++) {
								JSONObject item = data.getJSONObject(i);
								CardItem cardItem = new CardItem(item.getString("pay_id"),item.getString("remark"),item.getString("money"));
								list.add(cardItem);
							}
							gv.setAdapter(new MyCardAdapter(mAct,mIntent,list));
						}else{
							Toast.makeText(mAct, "获取数据失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(mAct, "获取数据失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			cancelpayBack();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void cancelpayBack(){
		Intent locIntent = new Intent();
		locIntent.setClass(CardRechargeActivity.this, MyRemoteService.class);
		Bundle locBundle = new Bundle();
		locBundle.putString("flag", "pay");
		locBundle.putString("msg", mIntent.getExtras().getString("desc"));
		locBundle.putString("sum", mIntent.getExtras().getString("account"));
		locBundle.putString("chargetype", "pay");
		locBundle.putString("custominfo", mIntent.getExtras().getString("callBackData"));
		locBundle.putString("customorderid", mIntent.getExtras().getString("merchantsOrder"));
		locBundle.putString("status", "1");
		locIntent.putExtras(locBundle);
		CardRechargeActivity.this.startService(locIntent);
		list.clear();
		CardRechargeActivity.this.finish();
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		MLog.a("CardRechargeActivity", "Editable----s----length----"+s.length());
		int length = s.length();
		if(length == 4||length == 9){
			String char_ = s.toString().substring(length-1, length);
			if(!char_.equals(" ")){
				String str = s.toString().substring(length-1, length);
				s.replace(length-1, length, " "+str);
			}else{
				s.replace(length-1, length, "");
			}
			return;
		}
		if(length == 13){
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
}
