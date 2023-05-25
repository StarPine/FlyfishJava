package com.android.zs.push;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushMessageReceiver extends JPushMessageReceiver
{
    private static final String TAG = "PushMessageReceiver";

    public void onMessage(Context paramContext, CustomMessage paramCustomMessage)
    {
        Log.e("PushMessageReceiver", "[onMessage] " + paramCustomMessage);
    }

    public void onNotifyMessageOpened(Context paramContext, NotificationMessage paramNotificationMessage)
    {
        System.out.println("[onNotifyMessageOpened] " + paramNotificationMessage);
        try
        {
            Class<?> clazz_mp = Class.forName("fly.fish.aidl.OutFace");
            Method method = clazz_mp.getMethod("getmActivity");
            Activity localActivity = (Activity) method.invoke(null);
            if (localActivity == null)
            {
                Intent localIntent = paramContext.getPackageManager().getLaunchIntentForPackage(paramContext.getPackageName());
                localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                paramContext.startActivity(localIntent);
            }
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public void onMultiActionClicked(Context paramContext, Intent paramIntent)
    {
        Log.e("PushMessageReceiver", "[onMultiActionClicked] 用户点击了通知栏按钮");
        String str = paramIntent.getExtras().getString("cn.jpush.android.NOTIFIACATION_ACTION_EXTRA");
        if (str == null)
        {
            Log.d("PushMessageReceiver", "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (str.equals("my_extra1"))
            Log.e("PushMessageReceiver", "[onMultiActionClicked] 用户点击通知栏按钮一");
        else if (str.equals("my_extra2"))
            Log.e("PushMessageReceiver", "[onMultiActionClicked] 用户点击通知栏按钮二");
        else if (str.equals("my_extra3"))
            Log.e("PushMessageReceiver", "[onMultiActionClicked] 用户点击通知栏按钮三");
        else
            Log.e("PushMessageReceiver", "[onMultiActionClicked] 用户点击通知栏按钮未定义");
    }

    public void onNotifyMessageArrived(Context paramContext, NotificationMessage paramNotificationMessage)
    {
        Log.e("PushMessageReceiver", "[onNotifyMessageArrived] " + paramNotificationMessage);
    }

    public void onNotifyMessageDismiss(Context paramContext, NotificationMessage paramNotificationMessage)
    {
        Log.e("PushMessageReceiver", "[onNotifyMessageDismiss] " + paramNotificationMessage);
    }

    public void onRegister(Context paramContext, String paramString)
    {
        Log.e("PushMessageReceiver", "[onRegister] " + paramString);
    }

    public void onConnected(Context paramContext, boolean paramBoolean)
    {
        Log.e("PushMessageReceiver", "[onConnected] " + paramBoolean);
    }

    public void onCommandResult(Context paramContext, CmdMessage paramCmdMessage)
    {
        Log.e("PushMessageReceiver", "[onCommandResult] " + paramCmdMessage);
    }

    public void onTagOperatorResult(Context paramContext, JPushMessage paramJPushMessage)
    {
        super.onTagOperatorResult(paramContext, paramJPushMessage);
    }

    public void onCheckTagOperatorResult(Context paramContext, JPushMessage paramJPushMessage)
    {
        super.onCheckTagOperatorResult(paramContext, paramJPushMessage);
    }

    public void onAliasOperatorResult(Context paramContext, JPushMessage paramJPushMessage)
    {
        super.onAliasOperatorResult(paramContext, paramJPushMessage);
    }

    public void onMobileNumberOperatorResult(Context paramContext, JPushMessage paramJPushMessage)
    {
        super.onMobileNumberOperatorResult(paramContext, paramJPushMessage);
    }

    public void onNotificationSettingsCheck(Context paramContext, boolean paramBoolean, int paramInt)
    {
        super.onNotificationSettingsCheck(paramContext, paramBoolean, paramInt);
        Log.e("PushMessageReceiver", "[onNotificationSettingsCheck] isOn:" + paramBoolean + ",source:" + paramInt);
    }
}