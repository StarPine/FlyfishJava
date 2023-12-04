package fly.fish.config;

public class StatusCode {
    //初始化
    public static final String INIT_SUCCESS = "0";
    public static final String INIT_FAIL = "1";

    //登录
    public static final String LOGIN_SUCCESS = "0";
    public static final String LOGIN_FAIL = "1";
    public static final String LOGOUT_SUCCESS = "2";

    //支付
    public static final String PAY_SUCCESS = "0";
    public static final String PAY_FAIL = "1";
    public static final String PAY_REVIEW = "2";

    //数据上报 "场景id（0-登陆上报，1-创建角色上报，2-升级上报，3-退出时上报）"
    public static final String UPLOAD_LOGIN = "0";
    public static final String UPLOAD_CREAT = "1";
    public static final String UPLOAD_UPDATE = "2";
    public static final String UPLOAD_EXIT_GAME = "3";
}
