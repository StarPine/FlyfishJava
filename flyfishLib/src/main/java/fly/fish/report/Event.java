package fly.fish.report;

public class Event {

    //启动游戏
    public static final int START_APPLICATION = 1000000;
    //游戏热更新开始
    public static final int START_GAME_HOT_REFRESH = 1001000;
    //游戏热更新成功
    public static final int GAME_HOT_REFRESH_FINISH = 1002000;
    //游戏加载本地资源开始
    public static final int START_LOAD_LOCAL_RES = 1003000;
    //游戏加载本地资源成功
    public static final int LOAD_LOCAL_RES_FINISH = 1004000;
    //游戏调用SDK初始化
    public static final int INVOKE_SDK_INIT = 1005000;
    //SDK初始化成功
    public static final int SDK_INIT_SUCCESS = 1006000;
    //SDK初始化失败
    public static final int SDK_INIT_FAIL = 1007000;
    //游戏调用SDK登陆页
    public static final int INVOKE_SDK_LOGIN = 1008000;
    //SDK返回登陆成功
    public static final int SDK_LOGIN_SUCCESS = 1009000;
    //SDK返回登陆失败
    public static final int SDK_LOGIN_FAIL = 1010000;
    //游戏选服界面弹出
    public static final int GAME_SERVER_SELECTION_SHOW = 1011000;
    //游戏公告界面弹出
    public static final int GAME_ANNOUNCEMENT_SHOW = 1012000;
    //玩家创角界面弹出
    public static final int GAME_CREATE_ROLE_SHOW = 1013000;
    //玩家创建角色成功
    public static final int GAME_CREATE_ROLE_SUCCESS = 1014000;
    //正式进入游戏
    public static final int ENTER_THE_GAME = 1015000;

    //玩家新手引导分节点上报
    public static final int NOVICE_GUIDE = 1016000;
    //请求SDK充值接口成功
    public static final int SDK_PAY_SUCCESS = 1017000;
    //服务器收到SDK充值成功回调
    public static final int SERVICE_GET_PAY_SUCCESS = 1018000;
    //服务器收到SDK充值回调处理失败
    public static final int SERVICE_DISPOSE_PAY_FAIL = 1019000;
    //玩家货币产生
    public static final int PLAYER_CURRENCY_GENERATE = 1020000;
    //玩家货币消耗
    public static final int PLAYER_CURRENCY_CONSUME = 1021000;
    //玩家道具产生
    public static final int PLAYER_PROPERTY_GENERATE = 1022000;
    //玩家道具消耗
    public static final int PLAYER_PROPERTY_CONSUME = 1023000;
    //玩家角色升级
    public static final int PLAYER_ROEL_UPDATE = 1024000;
    //玩家VIP等级升级
    public static final int PLAYER_VIP_UPDATE = 1025000;
    //玩家主线任务上报
    public static final int PLAYER_MAIN_LINE_TASK = 1026000;
    //主线关卡上报
    public static final int PLAYER_MAIN_LEVEL = 1027000;
    //玩家退出游戏
    public static final int PLAYER_EXIT_GAME = 1028000;
    //角色命名/改名
    public static final int ROLE_RENAME = 1029000;
    //开服（合服）
    public static final int SERVER_LAUNCH = 1030000;
}