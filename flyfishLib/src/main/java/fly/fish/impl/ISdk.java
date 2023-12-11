package fly.fish.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import fly.fish.aidl.CallBackListener;
import fly.fish.open.impl.CommonCallback;
import fly.fish.open.impl.SimpleCallback;

public interface ISdk {

    void applicationOnCreate(Application application);

    void InitLaunch(Activity activity, boolean isLandsape, CallBackListener callback);

    void initSDK(Activity activity);

    void loginSDK(Activity activity, Intent intent);

    default void paySDK(Activity activity, Intent intent){}

    void paySDK(Activity activity, Intent intent, String order, String paynotifyurl, String extra1, String extra2, String extdata3);

    void submitData(String data);

    void logout(Activity activity);

    void exit(Activity activity);

    default String getOrderExtdata() {
        return "";
    }

    default void reyunsetLogin(String acc) {
    }

    default void reyunandttsetPay(String desc, String orderid, String type, String sum, boolean issuccess) {
    }

    default void getCertificateInfo(Activity activity, GetCertificationInfoCallback callback) {
    }

    default void onStart(Activity activity) {
    }

    default void onRestart(Activity activity) {
    }

    default void onResume(Activity activity) {
    }

    default void onPause(Activity activity) {
    }

    default void onStop(Activity activity) {
    }

    default void onNewIntent(Intent newIntent) {
    }

    default void onDestroy(Activity activity) {
    }

    default void commonApi1(Object... objects) {

    }

    default void commonApi2(Object... objects) {

    }

    default Object commonApi3(Context context, Object... objects) {
        return null;
    }

    default Object commonApi4(Context context, Object... objects) {
        return null;
    }

    default void commonApi5(Context context, SimpleCallback callback, Object... objects) {

    }

    default void commonApi6(Context context, CommonCallback callback, Object... objects) {

    }

}
