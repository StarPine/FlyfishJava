package fly.fish.tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.starpine.oaid.OAIDManager;

import fly.fish.asdk.MyApplication;
import fly.fish.othersdk.OaidHelper;
import fly.fish.report.ASDKReport;
import fly.fish.report.EventManager;

public class AsdkOAIDManager {
    private Context context;
    private String oaidKey;
    private boolean isAgree;

    public AsdkOAIDManager(Context context, String oaidKey, boolean isAgree) {
        this.context = context;
        this.oaidKey = oaidKey;
        this.isAgree = isAgree;
    }

    public void manageOAID() {
        if (TextUtils.isEmpty(oaidKey)) {
            OAIDManager.getOAID(context, deviceId -> {
                String oaId = deviceId.getOaId();
                Log.i("ASDK", "OAID versinon 1.0.13: " + oaId);
                setOaid(oaId);
            });
        } else {
            new OaidHelper(ids -> {
                Log.i("ASDK", "OAID versinon 2.2.0: " + ids);
                setOaid(ids);
            }).getDeviceIds(context, oaidKey);
        }
    }

    private void setOaid(String ids) {
        int count_0 = getCount(ids, "0");
        String spDeviceID = MyApplication.context.getSharedPreferences("user_info", 0).getString("device_id", "");
        if (spDeviceID.equals("") && count_0 < 10 && !TextUtils.isEmpty(ids)) {
            Log.i("ASDK", "配置OAID");
            PhoneTool.setOAID(ids);
        }
        if (isAgree) {
            ASDKReport.getInstance().startSDKReport(context, EventManager.SDK_EVENT_AGREE_PRIVACY);
        }
    }

    private static int getCount(String str, String key) {
        if (str != null && key != null && !"".equals(str.trim()) && !"".equals(key.trim())) {
            int count = 0;
            for (int index = 0; (index = str.indexOf(key, index)) != -1; ++count) {
                index += key.length();
            }

            return count;
        } else {
            return 0;
        }
    }
}
