package fly.fish.othersdk;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.InfoCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.bun.miitmdid.pojo.IdSupplierImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import fly.fish.tools.Base64;

/**
 * Date: 16:27 2021/2/26 0026
 * Version: 1.0.3
 **/
public class OaidHelper implements IIdentifierListener {
    public static long startTimeMillis;
    public static long endTimeMillis;
    public static final String TAG = "OaidHelper";
    public static final int HELPER_VERSION_CODE = 20230516; // DemoHelper版本号
    private final AppIdsUpdater appIdsUpdater;
    private boolean isCertInit = false;
    private boolean isArchSupport = false;
    public boolean isSDKLogOn = true;          // TODO （1）设置 是否开启sdk日志
    private String certContent = "";  //

    public OaidHelper(AppIdsUpdater appIdsUpdater) {
        loadLibrary("msaoaidsec");
        if (isArchSupport) {
            if (MdidSdkHelper.SDK_VERSION_CODE != HELPER_VERSION_CODE) {
                Log.w(TAG, "SDK version not match.");
            }
        }
        this.appIdsUpdater = appIdsUpdater;
    }

    public void getDeviceIds(Context cxt, String cert) {
        certContent = new String(Base64.decode(cert));
        getDeviceIds(cxt, true, true, true);
        endTimeMillis = System.nanoTime();
    }


    /**
     * 获取OAID
     *
     * @param cxt
     */
    public void getDeviceIds(Context cxt, boolean isGetOAID, boolean isGetVAID, boolean isGetAAID) {
        // TODO （4）初始化SDK证书
        if (!isCertInit) { // 证书只需初始化一次
            // 证书为PEM文件中的所有文本内容（包括首尾行、换行符）
            try {
                startTimeMillis = System.currentTimeMillis();
                isCertInit = MdidSdkHelper.InitCert(cxt, certContent);
            } catch (Error e) {
                e.printStackTrace();
            }
            if (!isCertInit) {
                Log.w(TAG, "getDeviceIds: cert init failed");
            }
        }

        //（可选）设置InitSDK接口回调超时时间(仅适用于接口为异步)，默认值为5000ms.
        // 注：请在调用前设置一次后就不再更改，否则可能导致回调丢失、重复等问题
        try {
            MdidSdkHelper.setGlobalTimeout(5000);
        } catch (Error error) {
            error.printStackTrace();
        }
        int code = 0;
        // TODO （5）调用SDK获取ID
        try {
            // if x86 throws Error
            code = MdidSdkHelper.InitSdk(cxt, isSDKLogOn, isGetOAID, isGetVAID, isGetAAID, this);
        } catch (Error error) {
            error.printStackTrace();
        } finally {
//            long time = endTimeMillis - startTimeMillis;
//            Log.d(TAG, "Time Consume:" + time);
        }
        // TODO （6）根据SDK返回的code进行不同处理
        IdSupplierImpl unsupportedIdSupplier = new IdSupplierImpl();
        if (code == InfoCode.INIT_ERROR_CERT_ERROR) {                         // 证书未初始化或证书无效，SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG, "cert not init or check not pass");
            onSupport(unsupportedIdSupplier);
        } else if (code == InfoCode.INIT_ERROR_DEVICE_NOSUPPORT) {             // 不支持的设备, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG, "device not supported");
            onSupport(unsupportedIdSupplier);
        } else if (code == InfoCode.INIT_ERROR_LOAD_CONFIGFILE) {            // 加载配置文件出错, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG, "failed to load config file");
            onSupport(unsupportedIdSupplier);
        } else if (code == InfoCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {      // 不支持的设备厂商, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG, "manufacturer not supported");
            onSupport(unsupportedIdSupplier);
        } else if (code == InfoCode.INIT_ERROR_SDK_CALL_ERROR) {             // sdk调用出错, SDK内部不会回调onSupport
            // APP自定义逻辑
            Log.w(TAG, "sdk call error");
            onSupport(unsupportedIdSupplier);
        } else if (code == InfoCode.INIT_INFO_RESULT_DELAY) {             // 获取接口是异步的，SDK内部会回调onSupport
            Log.i(TAG, "result delay (async)");
        } else if (code == InfoCode.INIT_INFO_RESULT_OK) {                  // 获取接口是同步的，SDK内部会回调onSupport
            Log.i(TAG, "result ok (sync)");
        } else {
            // sdk版本高于DemoHelper代码版本可能出现的情况，无法确定是否调用onSupport
            // 不影响成功的OAID获取
            Log.w(TAG, "getDeviceIds: unknown code: " + code);
        }
    }

    /**
     * APP自定义的getDeviceIds(Context cxt)的接口回调
     *
     * @param supplier
     */
    @Override
    public void onSupport(IdSupplier supplier) {
        if (supplier == null) {
            Log.w(TAG, "onSupport: supplier is null");
            return;
        }
        if (appIdsUpdater == null) {
            Log.w(TAG, "onSupport: callbackListener is null");
            return;
        }
        boolean isSupported;
        boolean isLimited;
        String oaid, vaid, aaid;
        if (isArchSupport == true) {
            // 获取Id信息
            // 注：IdSupplier中的内容为本次调用MdidSdkHelper.InitSdk()的结果，不会实时更新。 如需更新，需调用MdidSdkHelper.InitSdk()
            isSupported = supplier.isSupported();
            isLimited = supplier.isLimited();
            oaid = supplier.getOAID();
            vaid = supplier.getVAID();
            aaid = supplier.getAAID();
        } else {
            isSupported = false;
            isLimited = false;
            oaid = null;
            vaid = null;
            aaid = null;
        }
        String idsText = oaid;
        Log.d(TAG, "onSupport: ids: \n" + idsText);
        appIdsUpdater.onIdsValid(idsText);

        endTimeMillis = System.currentTimeMillis();
        long time = endTimeMillis - startTimeMillis;
        Log.d(TAG, "Time Consume:" + time);
    }

    public interface AppIdsUpdater {
        void onIdsValid(String ids);
    }

    /**
     * 从asset文件读取证书内容
     *
     * @param context
     * @param assetFileName
     * @return 证书字符串
     */
    public static String loadPemFromAssetFile(Context context, String assetFileName) {
        try {
            InputStream is = context.getAssets().open(assetFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "loadPemFromAssetFile failed");
            return "";
        }
    }

    public long getTimeConsume() {
        //因为证书只初始化一次，所以这个只能获取一次
        return this.endTimeMillis - this.startTimeMillis;
    }

    public String loadLibrary(String lib) {
        String value = "arm";
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(clazz, "ro.product.cpu.abi", ""));

            if (value.contains("x86")) {
                isArchSupport = false;
            } else {
                isArchSupport = true;
                System.loadLibrary(lib);  // TODO （3）SDK初始化操作
            }
        } catch (Throwable e) {

        }
        if (!isArchSupport) {
            return "Arch: x86\n";
        } else {
            return "Arch: Not x86";
        }
    }

    public boolean isArchSupport() {
        return isArchSupport;
    }
}

