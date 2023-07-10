package fly.fish.open.ad;

import android.app.Activity;

public interface IVideo {

    void loadAD(Activity activity, boolean isLoadedShow, ADVideoListener adVideoListener);

    void show();
}
