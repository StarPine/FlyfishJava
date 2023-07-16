package fly.fish.open.ad;


import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Constructor;

public class ASDKAdManager {
    private String TAG = "ASDKAdManager";
    private Activity activity;
    private ADVideoListener adVideoListener;
    private IVideo adMergePlatform;

    private boolean isReady;

    public ASDKAdManager(Activity activity, ADVideoListener adVideoListener) {
        this.activity = activity;
        this.adVideoListener = adVideoListener;
    }

    private IVideo getADPlatform() {
        if (this.adMergePlatform == null) {
            try {

                Class<?> aClass = Class.forName("fly.fish.othersdk.ADMergePlatform");
                Constructor con = aClass.getDeclaredConstructor();
                con.setAccessible(true);
                adMergePlatform = (IVideo) con.newInstance();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
        return adMergePlatform;
    }

    public void loadAD(String posID) {
        loadAD(posID, false);
    }

    public void loadAD(String posID, boolean isLoadedShow) {
        Log.i(TAG, "loadAD: "+posID);
        getADPlatform().loadAD(activity, posID, isLoadedShow, new ADVideoListener() {
            @Override
            public void onShowedAd() {
                Log.i(TAG, "onShowedAd: ");
                if (adVideoListener != null) {
                    adVideoListener.onShowedAd();
                }
            }

            @Override
            public void onClickAd() {
                Log.i(TAG, "onClickAd: ");
                if (adVideoListener != null) {
                    adVideoListener.onClickAd();
                }
            }

            @Override
            public void onCloseAd() {
                Log.i(TAG, "onCloseAd: ");
                setReady(false);
                if (adVideoListener != null) {
                    adVideoListener.onCloseAd();
                }
            }

            @Override
            public void onErrorAd(int errorCode, String message) {
                Log.i(TAG, "errorCode: " + errorCode + "  ,onErrorAdmessage: " + message);
                if (adVideoListener != null) {
                    adVideoListener.onErrorAd(errorCode, message);
                }
            }

            @Override
            public void onReadyAd() {
                Log.i(TAG, "onReadyAd: ");
                setReady(true);
                if (adVideoListener != null) {
                    adVideoListener.onReadyAd();
                }
            }

            @Override
            public void onCompletedAd() {
                Log.i(TAG, "onCompletedAd: ");
                setReady(false);
                if (adVideoListener != null) {
                    adVideoListener.onCompletedAd();
                }
            }
        });
    }

    public void show() {
        Log.i(TAG, "show: ");
        if (isReady) {
            getADPlatform().show();
        } else {
            if (adVideoListener != null)
                adVideoListener.onErrorAd(8001, "广告未准备好");
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }


}
