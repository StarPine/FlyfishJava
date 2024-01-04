package fly.fish.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import fly.fish.asdk.MyApplication;

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

    /**
     * 判断是否为主进程
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        String processName = AppUtils.getProcessName(context);
        String packageName = AppUtils.getPackageName(context);
        return processName.equals(packageName);
    }

    // 获取应用程序签名的MD5值
    public static String getSignatureMD5(Context context) {
        return getSignatureDigest(context,"MD5");
    }

    // 获取应用程序签名的SHA256值
    public static String getSignatureSha256(Context context) {
        return getSignatureDigest(context,"SHA256");
    }

    public static String getSignatureDigest(Context context, String digest) {
        try {
            // 获取包管理器
            PackageManager packageManager = context.getPackageManager();

            // 获取签名信息
            Signature[] signatures ;
            if (Build.VERSION.SDK_INT >= 28) {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                signatures = packageInfo.signingInfo.getApkContentsSigners();
            } else {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                signatures = packageInfo.signatures;
            }

            // 计算摘要值
            MessageDigest md = MessageDigest.getInstance(digest);
            md.update(signatures[0].toByteArray());
            byte[] md5Bytes = md.digest();

            // 转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                sb.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 打开指定包名的App应用信息界面
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void openAppInfo(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    /**
     * [获取IP]<BR>
     * [功能详细描述]
     *
     * @return
     */
    public static String getIPAddress(final boolean useIPv4) {
        try {
            for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                // 防止小米手机返回10.0.2.15
                if (!ni.isUp()) continue;
                for (Enumeration<InetAddress> addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        boolean isIPv4 = hostAddress.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) return hostAddress;
                        } else {
                            if (!isIPv4) {
                                int index = hostAddress.indexOf('%');
                                return index < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, index).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context){
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取应用versionName
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用versionCode
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }
    public static ApkInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                appInfo.sourceDir = path;
                appInfo.publicSourceDir = path;
                return (new ApkInfo(path, appInfo.packageName, pm.getApplicationLabel(appInfo).toString(), info.versionCode, info.versionName, appInfo.loadIcon(pm)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static class ApkInfo {
        public String path;// apk文件对应的路径
        public String packageName;// apk文件对应的包名
        public String appName;// apk文件对应的名称
        public String appVersion;// apk文件对应的版本信息
        public int versionCode; // apk文件的versionCode
        public Drawable appIcon;// apk文件对应icon

        public ApkInfo(String path, String packageName, String appName, int versionCode, String appVersion, Drawable appIcon) {
            this.path = path;
            this.packageName = packageName;
            this.appName = appName;
            this.versionCode = versionCode;
            this.appVersion = appVersion;
            this.appIcon = appIcon;
        }

        @Override
        public String toString() {
            return "ApkInfo{" +
                    "path='" + path + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", appName='" + appName + '\'' +
                    ", appVersion='" + appVersion + '\'' +
                    ", versionCode=" + versionCode +
                    ", appIcon=" + appIcon +
                    '}';
        }
    }


}
