package fly.fish.asdk;

import android.os.AsyncTask;

import fly.fish.tools.HttpUtils;

public class HttpAsynTask extends AsyncTask<String, Integer, String> {

	public String flag = null;
	public String par = null;

	@Override
	protected void onPreExecute() {
		// 第一个执行方法
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		if (params[0].contains("=")) {
			int sh = params[0].lastIndexOf("=");
			flag = params[0].substring(sh + 1, params[0].length());
		} else {
			flag = "httptest";
		}

		// 第二个执行方法,onPreExecute()执行完后执行
		String json = HttpUtils.postMethod(params[0], params[1], params[2]);
		// publishProgress(5);可传多个参数
		par = params[1];
		return json;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// 这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数
		// 但是这里取到的是一个数组,所以要用progesss[0]来取值
		// 第n个参数就用progress[n]来取值

		// 这里可以用来进行UI更新提示

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
		// 这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"

		// 这里可以用来UI结果显示
		synchronized (MyApplication.getAppContext().getmLuaState()) {
			MyApplication.getAppContext().httpback.httpcallback(flag, result, par);
		}

	}
}
