package fly.fish.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fly.fish.asdk.ChargeActivity;
import fly.fish.beans.CardItem;

public class MyCardAdapter extends BaseAdapter {

	private Context con = null;
	private ArrayList<CardItem> list_ = null;
	private Intent intent = null;
	private TextView tv = null;
	
	public MyCardAdapter(Context mAct, Intent mIntnet, ArrayList<CardItem> list) {
		con = mAct;
		list_ = list;
		intent = mIntnet;
	}

	@Override
	public int getCount() {
		return list_.size();
	}

	@Override
	public Object getItem(int position) {
		return list_.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int Layout_id = con.getResources().getIdentifier("layout_cardrecharge_item", "layout", con.getPackageName());
		int cardpay_item_tv = con.getResources().getIdentifier("cardpay_item_tv", "id", con.getPackageName());
		final CardItem item = list_.get(position);
		convertView = LayoutInflater.from(con).inflate(Layout_id, null);
		
		tv = (TextView)convertView.findViewById(cardpay_item_tv);
		tv.setText(item.getRemark());
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.setClass(con, ChargeActivity.class);
				Bundle bundle = intent.getExtras();
				bundle.putString("account", item.getMoney());
				intent.putExtras(bundle);
				con.startActivity(intent);
			}
		});
		
		return convertView;
	}

}
