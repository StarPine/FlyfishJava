package fly.fish.asdk;

import android.os.AsyncTask;

import fly.fish.tools.HttpUtils;
import fly.fish.tools.MLog;

public class KjavaAsynTask extends AsyncTask<String, Integer, String> {

	public String flag = null;
	public String par = null;

	@Override
	protected void onPreExecute() {
		// 第一个执行方法
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		/*
		 * params[0]--请求的url params[1]--请求步骤 params[2]--请求编码
		 */
		String back = null;
		flag = params[1];
		if (flag.equals("1")) {
			back = HttpUtils.getWebMethod(params[0], 1);
		} else if (flag.equals("2")) {
			back = HttpUtils.getWebMethod(params[0], 1);
		} else if (flag.equals("3")) {
			back = HttpUtils.getWebMethod(params[0], 1);
		} else if (flag.equals("4")) {
			back = HttpUtils.getDownLoad(params[0], 2);
		} else if (flag.equals("5")) {
			back = HttpUtils.postWebMethod(params[0], 1);
		} else if (flag.equals("6")) {
			back = HttpUtils.getWebMethod(params[0], 1);
		} else if (flag.equals("7")) {
			back = HttpUtils.getWebMethod(params[0], 1);
		} else {
			flag = "error";
		}

		/*
		 * if(params[2].equalsIgnoreCase("get")){ back =
		 * HttpUtils.postMethod(params[0], params[1], params[2]); }else{ back =
		 * HttpUtils.postMethod(params[0], params[1], params[2]); }
		 */

		// publishProgress(5);可传多个参数
		return back;
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
			MLog.s(flag + " ============ " + result);
			MyApplication.getAppContext().httpbackkjava.httpcallback(flag, result, par);
		}

	}
}
