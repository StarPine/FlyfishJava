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

public class MyPayAdapter extends BaseAdapter{
	// 布局容器
		private LinearLayout linearLayout2;
		private List<GridViewModel> msgData;
		private MyActivity context; // 运行上下文

		// 有参
		public MyPayAdapter(MyActivity context) {
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			if (convertView == null) {

				synchronized (context.mLuaState) {
					context.mLuaState.getGlobal("chargelua");
					int index = context.mLuaState.getTop();
					context.mLuaState.getField(index, "addItems");
					// context.mLuaState.getField(LuaState.LUA_GLOBALSINDEX,"addItems");
					context.mLuaState.pushJavaObject(msgData.get(position).getImgTag());// 压入参数,图片
					context.mLuaState.pushJavaObject(msgData.get(position).getTitle());// 压入参数，标题
					context.mLuaState.pushJavaObject(msgData.get(position).getId());// 压入参数，支付id
					context.mLuaState.pushJavaObject(context);// 压入参数，上下文对象
					context.mLuaState.pushInteger(position);// 压入参数，索引
					LuaTools.dbcall(context.mLuaState, 5, 1);// 代表5个参数，1个返回值
				}

				try {
					linearLayout2 = (LinearLayout) context.mLuaState.toJavaObject(-1);
				} catch (LuaException e) {
					e.printStackTrace();
				}
				convertView = linearLayout2;
//			} else {
//				linearLayout2 = (LinearLayout) convertView;
//			}
			return linearLayout2;
		}

		public List<GridViewModel> getMsgData() {
			return msgData;
		}
}
