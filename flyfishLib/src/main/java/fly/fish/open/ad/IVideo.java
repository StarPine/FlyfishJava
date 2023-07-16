package fly.fish.open.ad;

import android.app.Activity;

public interface IVideo {
    String TAG = "IVideo";

    void loadAD(Activity activity, String posID, boolean isLoadedShow, ADVideoListener adVideoListener);

    void show();
}
