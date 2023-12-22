package fly.fish.report;

public class EventManager {

    public static final int EVENT_START_APPLICATION = 1000000;            //启动游戏
    public static final int EVENT_START_GAME_HOT_REFRESH = 1001000;       //游戏热更新开始
    public static final int EVENT_GAME_HOT_REFRESH_FINISH = 1002000;      //游戏热更新成功
    public static final int EVENT_START_LOAD_LOCAL_RES = 1003000;         //游戏加载本地资源开始
    public static final int EVENT_LOAD_LOCAL_RES_FINISH = 1004000;        //游戏加载本地资源成功
    public static final int EVENT_INVOKE_SDK_INIT = 1005000;              //游戏调用SDK初始化
    public static final int EVENT_SDK_INIT_SUCCESS = 1006000;             //SDK初始化成功
    public static final int EVENT_SDK_INIT_FAIL = 1007000;                //SDK初始化失败
    public static final int EVENT_INVOKE_SDK_LOGIN = 1008000;             //游戏调用SDK登陆页
    public static final int EVENT_SDK_LOGIN_SUCCESS = 1009000;            //SDK返回登陆成功
    public static final int EVENT_SDK_LOGIN_FAIL = 1010000;               //SDK返回登陆失败
    public static final int EVENT_GAME_SERVER_SELECTION_SHOW = 1011000;   //游戏选服界面弹出
    public static final int EVENT_GAME_ANNOUNCEMENT_SHOW = 1012000;       //游戏公告界面弹出
    public static final int EVENT_GAME_CREATE_ROLE_SHOW = 1013000;        //玩家创角界面弹出
    public static final int EVENT_GAME_CREATE_ROLE_SUCCESS = 1014000;     //玩家创建角色成功
    public static final int EVENT_ENTER_THE_GAME = 1015000;               //正式进入游戏
    public static final int EVENT_NOVICE_GUIDE = 1016000;                 //玩家新手引导分节点上报
    public static final int EVENT_SDK_PAY_SUCCESS = 1017000;              //请求SDK充值接口成功
    public static final int EVENT_SERVICE_GET_PAY_SUCCESS = 1018000;      //服务器收到SDK充值成功回调
    public static final int EVENT_SERVICE_DISPOSE_PAY_FAIL = 1019000;     //服务器收到SDK充值回调处理失败
    public static final int EVENT_PLAYER_CURRENCY_GENERATE = 1020000;     //玩家货币产生
    public static final int EVENT_PLAYER_CURRENCY_CONSUME = 1021000;      //玩家货币消耗
    public static final int EVENT_PLAYER_PROPERTY_GENERATE = 1022000;     //玩家道具产生
    public static final int EVENT_PLAYER_PROPERTY_CONSUME = 1023000;      //玩家道具消耗
    public static final int EVENT_PLAYER_ROEL_UPDATE = 1024000;           //玩家角色升级
    public static final int EVENT_PLAYER_VIP_UPDATE = 1025000;            //玩家VIP等级升级
    public static final int EVENT_PLAYER_MAIN_LINE_TASK = 1026000;        //玩家主线任务上报
    public static final int EVENT_PLAYER_MAIN_LEVEL = 1027000;            //主线关卡上报
    public static final int EVENT_PLAYER_EXIT_GAME = 1028000;             //玩家退出游戏
    public static final int EVENT_ROLE_RENAME = 1029000;                  //角色命名/改名
    public static final int EVENT_SERVER_LAUNCH = 1030000;                //开服（合服）


    public static final String SDK_EVENT_SHOW_ONEKEY_LOGIN = "A000";                //进入到sdk一键登录界面
    public static final String SDK_EVENT_SHOW_VERIFICATION_CODE = "A001";           //进入到sdk手机号登录界面
    public static final String SDK_EVENT_ONCLICK_ONEKEY_LOGIN  = "A002";            //点击一键登录按钮
    public static final String SDK_EVENT_ONCLICK_OTHER_LOGIN_TYPE = "A003";         //点击其他登录方式进入手机号登录界面
    public static final String SDK_EVENT_SEND_PHONE_CODE = "A004";                  //输入了手机号码点击了发送验证码
    public static final String SDK_EVENT_IN_PHONE_CODE_VIEW = "A005";               //发送验证码进入了输入验证码页面
    public static final String SDK_EVENT_LOGIN_SUCCESS = "A006";                    //登录成功
    public static final String SDK_EVENT_LOGIN_FAIL = "A007";                       //登录失败
    public static final String SDK_EVENT_SHOW_REAL_NAME_AUTH = "A008";              //进入实名认证界面
    public static final String SDK_EVENT_ONCLICK_REAL_NAME_AUTH_AGREE = "A009";     //点击实名认证界面确定按钮
    public static final String SDK_EVENT_ONCLICK_REAL_NAME_AUTH_REFUSE = "A010";    //点击实名认证界面拒绝按钮
    public static final String SDK_EVENT_AUTO_LOGIN = "A011";                       //触发自动登录
    public static final String SDK_EVENT_CANCEL_AUTO_LOGIN = "A012";                //取消自动登录，显示选号界面
    public static final String SDK_EVENT_CLOSE_LOGIN_VIEW = "A013";                 //关闭了SDK界面
    public static final String SDK_EVENT_ONEKEY_LOGIN_GET_TOKEN_SUCC = "A014";      // 一键登录token成功
    public static final String SDK_EVENT_ONEKEY_LOGIN_GET_TOKEN_FAIL = "A015";      // 一键登录token失败
    public static final String SDK_EVENT_CANCEL_SEND_CODE_SUCC = "A016";            // 发送验证码成功
    public static final String SDK_EVENT_CANCEL_SEND_CODE_FAIL = "A017";            // 发送验证码失败

    public static final String SDK_EVENT_SHOW_PRIVACY = "C001";                     //显示隐私窗口
    public static final String SDK_EVENT_AGREE_PRIVACY = "C002";                    //同意隐私按钮
    public static final String SDK_EVENT_REFUSE_PRIVACY = "C003";                   //拒绝隐私按钮
    public static final String SDK_EVENT_INIT_SUCCESS = "C004";                     //SDK初始化成功
    public static final String SDK_EVENT_INIT_FAIL = "C005";                        //SDK初始化失败

    public static final String SDK_EVENT_CHANNEL_INIT_SUCCESS = "Q001";             //渠道初始化成功
    public static final String SDK_EVENT_CHANNEL_INIT_FAIL = "Q002";                //渠道初始化失败
    public static final String SDK_EVENT_CHANNEL_LOGIN_SUCCESS = "Q003";            //执行渠道登录成功
    public static final String SDK_EVENT_CHANNEL_LOGIN_FAIL = "Q004";               //执行渠道登录失败
    public static final String SDK_EVENT_SDK_LOGIN_SUCCESS = "Q005";                //执行SDK登录成功
    public static final String SDK_EVENT_SDK_LOGIN_FAIL = "Q006";                   //执行SDK登录失败
    public static final String SDK_EVENT_CHANNEL_LOGIN_AGREE_IMPOWER = "Q007";      //渠道登录同意授权
    public static final String SDK_EVENT_CHANNEL_LOGIN_CANCEL_IMPOWER = "Q008";     //渠道登录取消授权

    public static final String SDK_EVENT_TT_INIT = "L001";                          //准备执行头条sdk初始化上报
    public static final String SDK_EVENT_TT_REGISTER = "L002";                      //准备执行头条sdk注册上报
    public static final String SDK_EVENT_TT_PAY = "L003";                           //准备执行头条sdk支付上报

}