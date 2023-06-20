package fly.fish.othersdk;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import fly.fish.aidl.CallBackListener;

public interface ISdk {
    
    void applicationOnCreate(Application application);

    void initSDK(Activity activity);

    void InitLaunch(Activity activity, boolean isLandsape, CallBackListener callback);

    void onResume(Activity activity);

    void onPause(Activity act);

    void onDestroy(Activity act);

    void loginSDK(Activity act, Intent intent);

    void paySDK(Activity act, Intent intent);

    void submitData(String data);

    void exit(Activity context);

    void reyunsetLogin(String acc);

    void reyunandttsetPay(String desc,String orderid, String type, String sum, boolean issuccess);

    void logout(Activity act);

    void getCertificateInfo(Activity act, GetCertificationInfoCallback callback);
}
