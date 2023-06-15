package fly.fish.tools;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.List;

public class AppUtils {

    private AppUtils(){
        throw new UnsupportedOperationException("Don't be naughty...");
    }

    /**
     * 获取当前进程名
     *
     * @param ctx
     * @return
     */
    public static String getProcessName(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List infos = am.getRunningAppProcesses();
        Iterator var4 = infos.iterator();
        while (var4.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) var4.next();
            if (info.pid == android.os.Process.myPid()) {
                return info.processName;
            }
        }
        return null;
    }

}
