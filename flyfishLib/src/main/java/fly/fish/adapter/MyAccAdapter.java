package fly.fish.adapter;

import java.util.ArrayList;

import org.keplerproject.luajava.LuaException;

import fly.fish.asdk.MyActivity;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAccAdapter extends BaseAdapter {

	private LinearLayout linearLayout;
	private MyActivity context;
	private ArrayList<String> arr;
	
	public MyAccAdapter(MyActivity context, ArrayList<String> arr) {
		this.context=context;
		this.arr=arr;
	}

	@Override
	public int getCount() {
		return arr.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		TextView v=new TextView(con);
//		v.setText(name[position]);
		
//		if (convertView == null) {

			synchronized (context.mLuaState) {
				context.mLuaState.getGlobal("main");
				int index = context.mLuaState.getTop();
				context.mLuaState.getField(index, "accListaddItems");
				context.mLuaState.pushJavaObject(arr.get(position).split("\\@")[0]);// 压入第一个参数
				context.mLuaState.pushJavaObject(context);// 压入第二个参数
				context.mLuaState.pushInteger(position);// 压入第三个参数
				LuaTools.dbcall(context.mLuaState, 3, 1);// 代表两个参数，1个返回值
			}

			try {
				MLog.s(context.mLuaState.toJavaObject(-1) + " NONONO");
				linearLayout = (LinearLayout) context.mLuaState.toJavaObject(-1);
			} catch (LuaException e) {
				e.printStackTrace();
			}
//			convertView = linearLayout;
//		} else {
//			linearLayout = (LinearLayout) convertView;
//		}
		linearLayout.setTag(arr.get(position));
		return linearLayout;
	}

}
