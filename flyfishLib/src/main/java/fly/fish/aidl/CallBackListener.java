package fly.fish.aidl;

public interface CallBackListener {
	/**
	 *
	 * @param code			返回码：0成功,1失败;
	 * @param isHasExitBox	true有退出框,false:无退出框
	 */
	public void callback(int code,boolean isHasExitBox);
}
