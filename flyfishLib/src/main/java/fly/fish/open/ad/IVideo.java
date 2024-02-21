package fly.fish.open.ad;

import android.app.Activity;
import android.app.Application;

public interface IVideo {
    String TAG = "IVideo";

    default void initAD(Application application){};

    void loadAD(Activity activity, String posID, boolean isLoadedShow, ADVideoListener adVideoListener);

    void show();
}
