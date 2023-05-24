package fly.fish.adapter;

import java.util.ArrayList;
import java.util.List;
import org.keplerproject.luajava.LuaException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import fly.fish.asdk.MyActivity;
import fly.fish.beans.GridViewModel;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

/**
 * UI适配器类，
 * */
public class MyAdapter extends BaseAdapter {
	// 布局容器
	private LinearLayout linearLayout2;
	private List<GridViewModel> msgData;
	private MyActivity context; // 运行上下文

	public MyAdapter() {

	}

	// 有参
	public MyAdapter(MyActivity context) {
		super();
		this.context = context;
		msgData = new ArrayList<GridViewModel>(); // key代表文字 value代表图片
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return msgData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// 忽略null 检查。
	@SuppressWarnings("null")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

			synchronized (context.mLuaState) {
				context.mLuaState.getGlobal("cardlistlua");
				int index = context.mLuaState.getTop();
				context.mLuaState.getField(index, "ListaddItems");
				context.mLuaState.pushJavaObject(msgData.get(position));// 压入第一个参数
				context.mLuaState.pushJavaObject(context);// 压入第二个参数
				context.mLuaState.pushInteger(position);// 压入第三个参数
				LuaTools.dbcall(context.mLuaState, 3, 1);// 代表两个参数，1个返回值
			}

			try {
				MLog.s(context.mLuaState.toJavaObject(-1) + " NONONO");
				linearLayout2 = (LinearLayout) context.mLuaState.toJavaObject(-1);
			} catch (LuaException e) {
				e.printStackTrace();
			}
			convertView = linearLayout2;
		} else {
			linearLayout2 = (LinearLayout) convertView;
		}
		return linearLayout2;
	}

	public List<GridViewModel> getMsgData() {
		return msgData;
	}
}
