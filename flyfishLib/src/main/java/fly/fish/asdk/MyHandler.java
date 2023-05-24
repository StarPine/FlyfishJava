package fly.fish.asdk;

import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import fly.fish.aidl.MyRemoteService;
import fly.fish.beans.PayBackModel;
import fly.fish.tools.FilesTool;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

public class MyHandler extends Handler {
	private MyActivity sdk;

	public MyHandler(MyActivity asdk) {
		this.sdk = asdk;
	}

	/**
	 * 这是主线程
	 */
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		MLog.s(sdk + " 任务来了 ----> " + msg.what);

		if (msg.what > 400) {
			if (msg.what == 599) {
				PhoneTool.onCreateDialog(sdk, "正在升级", "正在进行整体升级，请稍候......");

			} else if (msg.what == 598) {

				PhoneTool.onCreateDialog(sdk, "正在升级", msg.obj.toString() + "\n请稍候......");

			} else {

				if (msg.obj == null) {
					MyApplication.getAppContext().errorhandler.ErrorHander(sdk, msg.what, null);
				} else {
					MyApplication.getAppContext().errorhandler.ErrorHander(sdk, msg.what, msg.obj.toString());
				}
			}

		} else if (msg.what > 200) {
			switch (msg.what) {
			case 400:// BV更新成功

				MLog.s("begin to install apk: " + msg.obj.toString());
				MyApplication.getAppContext().luaupdateok = true;
				MyApplication.getAppContext().bvupdateAPK = true;

				PhoneTool.notifyAndInstallApk(sdk, msg.obj.toString());

				// 通知游戏结果（这里通知没用）
				/*
				 * Intent intents=new Intent(sdk, MyRemoteService.class); Bundle
				 * bus = new Bundle(); bus.putString("flag", "init");
				 * bus.putString("status", "1"); intents.putExtras(bus);
				 * sdk.startService(intents);
				 */

				sdk.finish();

				break;
			case 399:// SV更新成功
				/****************** 本地重新加载环境 *********************/

				// 保存lua环境就是

				// 初始化LUA环境
				MyApplication.getAppContext().initLuaState();
				// 解析文件数据头
				MyApplication.getAppContext().parseData();
				// 加载全局文件（升级重新加载）
				LuaTools.loadUpdate();

				MLog.s("初始化lua环境");
				MyApplication.getAppContext().luaupdateok = true;

				// 还原lua环境变量

				/******************* 远程服务重新加载环境 ****************/
				// 通知远程服务更新头文件
				Intent intent = new Intent(sdk, MyRemoteService.class);
				Bundle bu = new Bundle();
				bu.putString("flag", "update");
				bu.putString("key", MyApplication.getAppContext().getGameArgs().getKey());
				intent.putExtras(bu);
				sdk.startService(intent);

				// 回到游戏
				sdk.finish();

				break;

			case 398:// 不需要更新

				MyApplication.getAppContext().luaupdateok = true;
				// 启动UI程序
				sendEmptyMessage(0);

				break;
			}

		} else if (sdk instanceof ChargeActivity) {
			switch (msg.what) {
			case 0:// 启动Lua主界面
					// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/chargemain.lua");
				FilesTool.loadLuaScript("json/json.lua");
				FilesTool.loadLuaScript("lua/chager_info.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "addChargeViews");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}

				break;

			case 1:// 文件下载失败
				break;

			default:
				break;
			}
		} else if (sdk instanceof CardListActivity) {// 充值卡列表
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/card_list.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "addCardListViews");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}

				break;

			case 1:// 文件下载失败
				break;

			default:
				break;
			}
		} else if (sdk instanceof ChargeInfo) {
			// 充值信息填写
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/chager_info.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "addCardView");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;
			case 1:// 文件下载失败
				break;

			default:
				break;
			}
		}else if (sdk instanceof ChargeForJW) {
			// 充值信息填写
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/charge_junwang.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "addCardJWView");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;
			case 1:// 文件下载失败
				break;

			default:
				break;
			}
		}
		else if (sdk instanceof ChargeForZS) {
			// 充值信息填写
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/charge_zhizunbao.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "addZSView");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;
			case 1:
				break;

			default:
				break;
			}
		}
		
		else if (sdk instanceof BindPhoneTipActivity) {	//绑定提示界面
			// 充值信息填写
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/bindphonetip.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "bindphonetipView");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;
			case 1:
				break;

			default:
				break;
			}
		}
		
		else if (sdk instanceof BindAccountActivity) {	//绑定帐号界面
			// 充值信息填写
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/bindaccount.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "bindaccountView");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;
			case 1:
				break;

			default:
				break;
			}
		}
		else if (sdk instanceof BindPhoneAndEmailActivity) {	//绑定手机和邮箱界面
			// 充值信息填写
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/bindphoneandemail.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "bindphoneandemailView");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;
			case 1:
				break;

			default:
				break;
			}
		}
		else if (sdk instanceof ChargeMessage) {// 充值消息
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("lua/charge_message.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "addChargeMessage");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}

				break;
			case 1:// 文件下载失败
				break;

			default:
				break;
			}
		} else if (sdk instanceof ChargeInfoForAilpay) {// 支付SDK充值页面

			switch (msg.what) {

			case 0:// 启动Lua主界面
				if (sdk.getApp().getGameArgs().getSum().equals("0")) {

					MLog.s("进来了ChargeInfoForAilpay2");
					FilesTool.loadLuaScript("json/json.lua");
					FilesTool.loadLuaScript("main.lua");
					FilesTool.loadLuaScript("lua/widget.lua");
					FilesTool.loadLuaScript("lua/chager_info.lua");
					FilesTool.loadLuaScript("lua/charge_info_alipay.lua");

					// 找准程序入口
					synchronized (sdk.mLuaState) {
						sdk.mLuaState.getGlobal("main");
						int index = sdk.mLuaState.getTop();
						sdk.mLuaState.getField(index, "addchagerInfoAlipayViews");
						LuaTools.dbcall(sdk.mLuaState, 0, 0);
					}
				} else {
					MLog.s("进来了ChargeInfoForAilpay1");
					FilesTool.loadLuaScript("json/json.lua");
					FilesTool.loadLuaScript("main.lua");
					FilesTool.loadLuaScript("lua/widget.lua");
					FilesTool.loadLuaScript("lua/chager_info.lua");
					FilesTool.loadLuaScript("lua/charge_info_special.lua");
					FilesTool.loadLuaScript("lua/charge_info_alipay.lua");

					// 找准程序入口
					synchronized (sdk.mLuaState) {
						sdk.mLuaState.getGlobal("main");
						int index = sdk.mLuaState.getTop();
						sdk.mLuaState.getField(index, "addchagerInfoSpecialViews");
						LuaTools.dbcall(sdk.mLuaState, 0, 0);
					}

				}
				break;

			case 1:// 走定时查询
				String strRet = msg.obj.toString();
				((ChargeInfoForAilpay) sdk).closeProgress();
				// 处理交易结果
				try {
					// 获取交易状态码，具体状态代码请参看文档
					String tradeStatus = "resultStatus={";
					int imemoStart = strRet.indexOf("resultStatus=");
					imemoStart += tradeStatus.length();
					int imemoEnd = strRet.indexOf("};memo=");
					tradeStatus = strRet.substring(imemoStart, imemoEnd);

					MLog.s(sdk + " 进来了2055-------- " + sdk.mLuaState);

					if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
						// 调用二次确认
						// 找准程序入口
						synchronized (sdk.mLuaState) {
							sdk.mLuaState.getGlobal("chagerInfolua");
							int index = sdk.mLuaState.getTop();
							sdk.mLuaState.getField(index, "goCheckModule");
							LuaTools.dbcall(sdk.mLuaState, 0, 0);// 代表0个参数，0个返回值
						}
						// 关闭SDK
						sdk.getApp().backToGame();
						sdk.finish();

					} else { // 失败给游戏返回失败

						if (!tradeStatus.equals("6000")) {
							synchronized (sdk.mLuaState) {
								sdk.mLuaState.getGlobal("chagerInfolua");
								int index = sdk.mLuaState.getTop();
								sdk.mLuaState.getField(index, "cancelCallBack");
								LuaTools.dbcall(sdk.mLuaState, 0, 0);// 代表0个参数，0个返回值
							}
						}

						// 删除订单
						List<PayBackModel> p = sdk.getApp().getbList(sdk.getApp().curKey);
						MLog.s(p.size() + " 删除订单前 ");
						for (PayBackModel payBackModel : p) {
							if (payBackModel.getCustomorderid().equals(sdk.getApp().getGameArgs().getCustomorderid())) {
								p.remove(payBackModel);
								break;
							}
						}
						MLog.s(p.size() + " 删除订单后 ");
						// 关闭SDK

						if (!tradeStatus.equals("6000")) {
							sdk.getApp().backToGame();
							sdk.finish();
						}

					}

				} catch (Exception e) {

					synchronized (sdk.mLuaState) {
						sdk.mLuaState.getGlobal("chagerInfolua");
						int index = sdk.mLuaState.getTop();
						sdk.mLuaState.getField(index, "cancelCallBack");
						LuaTools.dbcall(sdk.mLuaState, 0, 0);// 代表0个参数，0个返回值
					}
					// 关闭SDK
					sdk.getApp().backToGame();
					sdk.finish();

					e.printStackTrace();
				}

				break;

			case 2: // 银联定时验证
				/*************************************************
				 * 
				 * 走定时查询接口
				 * 
				 ************************************************/
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("chagerInfolua");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "goCheckModule");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);// 代表0个参数，0个返回值
				}
				// 关闭SDK
				sdk.getApp().backToGame();
				sdk.finish();
				break;

			case 3: // 3代表银联用户取消给游戏返回失败

				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("chagerInfolua");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "cancelCallBack");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);// 代表0个参数，0个返回值
				}

				List<PayBackModel> p = sdk.getApp().getbList(sdk.getApp().curKey);
				MLog.s(p.size() + " unipay SSSSSSS ");
				for (PayBackModel payBackModel : p) {
					if (payBackModel.getCustomorderid().equals(sdk.getApp().getGameArgs().getCustomorderid())) {
						p.remove(payBackModel);
						break;
					}
				}
				MLog.s(p.size() + " unipay EEEEEEE ");

				// 关闭SDK
				sdk.getApp().backToGame();
				sdk.finish();

				break;

			case 4: // 获取手机号码

				MLog.s("MYTELEPHONE NUMBER: " + msg.obj.toString());

				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("chagerInfo_alipay_lua");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "smsPay");
					sdk.mLuaState.pushString(msg.obj.toString());
					LuaTools.dbcall(sdk.mLuaState, 1, 0);// 代表1个参数，0个返回值
				}

				break;
			case 5:
				PhoneTool.showDialog(sdk, "温馨提示", "暂不支持该充值方式,请选择其他充值方式", "确定", null);
				break;
			case 6:
				PhoneTool.showDialog(sdk, "温馨提示", "暂不支持该充值方式,请选择其他充值方式", "确定", null);
				break;
			default:
				break;
			}
		} else if (sdk instanceof LoginActivity) {// 登入
			switch (msg.what) {
			case 0:

				FilesTool.loadLuaScript("json/json.lua");
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("lua/Login.lua");
				FilesTool.loadLuaScript("lua/bindphonetip.lua");
				FilesTool.loadLuaScript("main.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startLogin");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}

				break;

			default:
				break;
			}
		} 
		else if (sdk instanceof AccountActivity) {// 登入
			switch (msg.what) {
			case 0:

				FilesTool.loadLuaScript("json/json.lua");
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("lua/account.lua");
				FilesTool.loadLuaScript("main.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "showaccounts");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}

				break;

			default:
				break;
			}
		}
		else if (sdk instanceof AsdkActivity) {
			switch (msg.what) {
			case 0:// 启动Lua主界面

				// 加载并执行LUA文件
				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");

				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startLua");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}

				break;

			case 1:// 增
				Toast.makeText(sdk, "增加文件: " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case 2:// 改
				Toast.makeText(sdk, "应用补丁文件: " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case 3:// 删
				Toast.makeText(sdk, "删除文件: " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case 4:// 弹出等待框
				PhoneTool.onCreateDialog(sdk, "温馨提示", "正在检查版本，请稍候......");
				break;
			case 5:// 取消等待框
				if (sdk.dialog != null) {
					sdk.dialog.dismiss();
				}
				break;
			default:
				break;
			}
		} else if (sdk instanceof UserinfoActivity) {// 用户信息
			switch (msg.what) {
			case 0:
				if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FilesTool.loadLuaScript("lua/user_info.lua");
				} else if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FilesTool.loadLuaScript("lua/user_info.lua");
				}

				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startUserInfo");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;

			default:
				break;
			}
		} else if (sdk instanceof RegisterActivity) {// 注册
			switch (msg.what) {
			case 0:
				if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FilesTool.loadLuaScript("lua/register.lua");
				} else if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FilesTool.loadLuaScript("lua/register.lua");
				}

				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startRegister");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;

			default:
				break;
			}
		} else if (sdk instanceof ChangePasswordActivity) {// 修改密码
			switch (msg.what) {
			case 0:
				if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FilesTool.loadLuaScript("lua/change_password.lua");
				} else if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FilesTool.loadLuaScript("lua/change_password.lua");
				}

				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startChangePassword");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;

			default:
				break;
			}
		} else if (sdk instanceof TestAcountActivity) {// 账号是否绑定手机号码或邮箱
			switch (msg.what) {
			case 0:
				if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FilesTool.loadLuaScript("lua/test_acount.lua");
				} else if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FilesTool.loadLuaScript("lua/test_acount.lua");
				}

				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startTestAcount");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;

			default:
				break;
			}
		} else if (sdk instanceof FindWayActivity) {// 找密码方式
			switch (msg.what) {
			case 0:
				String acount = "";
				String phone = "";
				String email = "";
				if (sdk.getIntent() != null) {
					acount = sdk.getIntent().getStringExtra("acount");
					phone = sdk.getIntent().getStringExtra("phone");
					email = sdk.getIntent().getStringExtra("email");
				}
				MLog.s(acount + phone + email);
				if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FilesTool.loadLuaScript("lua/find_way.lua");
				} else if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FilesTool.loadLuaScript("lua/find_way.lua");
				}

				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startFindWay");
					sdk.mLuaState.pushString(phone);
					sdk.mLuaState.pushString(email);
					LuaTools.dbcall(sdk.mLuaState, 2, 0);
				}
				break;

			default:
				break;
			}
		} else if (sdk instanceof ForgetPasswordActivity) {// 手机方式找回密码
			switch (msg.what) {
			case 0:
				if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FilesTool.loadLuaScript("lua/forget_password.lua");
				} else if (sdk.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FilesTool.loadLuaScript("lua/forget_password.lua");
				}

				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startFrogetPassword");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;

			default:
				break;
			}
		} else {
			switch (msg.what) {
			case 0:// 启动Lua主界面
					// 加载并执行LUA文件

				FilesTool.loadLuaScript("lua/widget.lua");
				FilesTool.loadLuaScript("main.lua");
				FilesTool.loadLuaScript("json/json.lua");
				// 找准程序入口
				synchronized (sdk.mLuaState) {
					sdk.mLuaState.getGlobal("main");
					int index = sdk.mLuaState.getTop();
					sdk.mLuaState.getField(index, "startLua");
					LuaTools.dbcall(sdk.mLuaState, 0, 0);
				}
				break;

			case 1:// 文件下载失败
				break;

			default:
				break;
			}
		}

	}
}
