package fly.fish.open.ad;

public interface ADVideoListener {
    /**
     * 已显示广告
     */
    void onShowedAd();

    /**
     *点击广告
     */
    void onClickAd();

    /**
     * 关闭广告
     */
    void onCloseAd();

    /**
     * 加载失败
     * @param errorCode
     * @param message
     */
    void onErrorAd(int errorCode, String message);

    /**
     * 广告已加载完成
     */
    void onReadyAd();

    /**
     * 视频播放完成
     */
    void onCompletedAd();
}
