package fly.fish.open.impl;

public interface CommonCallback<T> {
    void onSuccess(T data);

    void onFail(int code, String msg);
}
