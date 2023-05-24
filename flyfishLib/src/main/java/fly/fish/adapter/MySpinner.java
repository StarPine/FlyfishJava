package fly.fish.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MySpinner {
	private List<String> datas = new ArrayList<String>();

	public List<String> getDatas() {
		return datas;
	}

	public void setDatas(String data) {
		this.datas.add(data);
	}

	/**
	 * 返回下拉框控件
	 * 
	 * @param context
	 * @param datas
	 * @return
	 */
	public Spinner getSpinner(Context context) {
		Spinner s = new Spinner(context);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, datas);
		s.setAdapter(adapter);
		return s;
	}
}
