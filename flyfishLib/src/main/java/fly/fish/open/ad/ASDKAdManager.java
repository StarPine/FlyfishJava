package fly.fish.open.ad;


import android.app.Activity;

public class ASDKAdManager {

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
                adMergePlatform = (IVideo) Class.forName("fly.fish.othersdk.ADMergePlatform").newInstance();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
        return adMergePlatform;
    }

    public void loadAD(boolean isLoadedShow) {
        getADPlatform().loadAD(activity, isLoadedShow, new ADVideoListener() {
            @Override
            public void onShowedAd() {
                if (adVideoListener != null)
                    adVideoListener.onShowedAd();
            }

            @Override
            public void onClickAd() {
                if (adVideoListener != null)
                    adVideoListener.onClickAd();
            }

            @Override
            public void onCloseAd() {
                setReady(false);
                if (adVideoListener != null)
                    adVideoListener.onCloseAd();
            }

            @Override
            public void onErrorAd(int errorCode, String message) {
                if (adVideoListener != null)
                    adVideoListener.onErrorAd(errorCode, message);
            }

            @Override
            public void onReadyAd() {
                setReady(true);
                if (adVideoListener != null) {
                    adVideoListener.onReadyAd();
                }
            }

            @Override
            public void onCompletedAd() {
                setReady(false);
                if (adVideoListener != null)
                    adVideoListener.onCompletedAd();
            }
        });
    }

    public void show() {
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
