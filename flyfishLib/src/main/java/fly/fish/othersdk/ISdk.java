package fly.fish.othersdk;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import fly.fish.aidl.CallBackListener;

public interface ISdk {

    void applicationOnCreate(Application application);

    void initSDK(Activity activity);

    void InitLaunch(Activity activity, boolean isLandsape, CallBackListener callback);

    void onStart(Activity activity);

    void onResume(Activity activity);

    void onPause(Activity activity);

    void onStop(Activity activity);

    void onNewIntent(Intent newIntent);

    void onDestroy(Activity activity);

    void onRestart(Activity activity);

    void loginSDK(Activity activity, Intent intent);

    void paySDK(Activity activity, Intent intent);

    void submitData(String data);

    void exit(Activity activity);

    default void reyunsetLogin(String acc) {
    }

    default void reyunandttsetPay(String desc, String orderid, String type, String sum, boolean issuccess) {
    }

    void logout(Activity activity);

    void getCertificateInfo(Activity activity, GetCertificationInfoCallback callback);
}
