package fly.fish.http;

import android.text.TextUtils;

import org.keplerproject.luajava.LuaState;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fly.fish.asdk.MyApplication;
import fly.fish.tools.AESSecurity;
import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

public class RequestUtils {
    private static final String TAG = "RequestUtils";
    private static final int READ_TIME_OUT = 10000;
    private static final int CONNECTION_TIME_OUT = 10000;
    private static final String ENCODING_UTF8 = "UTF-8";
    public final static String KEY = "48fhd5748sayuh12";

    private static final class METHOD_TYPE {
        private static final int GET = 0;
        private static final int POST = 1;
    }

    private static final class PROTOCOL_TYPE {
        private static final int HTTP = 0;
        private static final int HTTPS = 1;
    }

    public static String GET(RequestConfig config) {
        if (config == null || TextUtils.isEmpty(config.getUrl())) {
            return null;
        }
        String fixedUrl = config.getUrl();
        if (!TextUtils.isEmpty(config.getBody())) {
            if (fixedUrl.endsWith("?")) {
                fixedUrl = fixedUrl + config.getBody();
            } else {
                fixedUrl = fixedUrl + "?" + config.getBody();
            }
        }
        config.setUrl(fixedUrl);
        return requset(config, METHOD_TYPE.GET, getProtocolType(config.getUrl()));
    }

    public static String GETEncrypt(RequestConfig config, boolean isconstant) {
        String encryptionResult = "";
        String fixedUrl = config.getUrl();
        if (!TextUtils.isEmpty(config.getBody())) {
            if (isconstant) {
                encryptionResult = AESSecurity.constantEncryptionResult(config.getBody(), KEY);
            } else {
                encryptionResult = AESSecurity.encryptionResult(config.getBody());
            }
            if (fixedUrl.endsWith("?")) {
                fixedUrl = fixedUrl + encryptionResult;
            } else {
                fixedUrl = fixedUrl + "?" + encryptionResult;
            }
            config.setUrl(fixedUrl);
        }
        String result = requset(config, METHOD_TYPE.GET, getProtocolType(config.getUrl()));
        if (isconstant) {
            return AESSecurity.constantdecryptResult(result, KEY);
        }
        return AESSecurity.decryptResult(result);
    }

    public static String POST(RequestConfig config) {
        return requset(config, METHOD_TYPE.POST, getProtocolType(config.getUrl()));
    }

    public static String POSTEncrypt(RequestConfig config, boolean isconstant) {
        String encryptionResult = "";
        if (!TextUtils.isEmpty(config.getBody())) {
            if (isconstant) {
                encryptionResult = AESSecurity.constantEncryptionResult(config.getBody(), KEY);
            } else {
                encryptionResult = AESSecurity.encryptionResult(config.getBody());
            }
            config.setBody(encryptionResult);
        }

        String result = requset(config, METHOD_TYPE.POST, getProtocolType(config.getUrl()) ,true);
        if (isconstant) {
            return AESSecurity.constantdecryptResult(result, KEY);
        }
        return AESSecurity.decryptResult(result);
    }

    private static int getProtocolType(String url) {
        return url.toLowerCase().startsWith("https") ? PROTOCOL_TYPE.HTTPS : PROTOCOL_TYPE.HTTP;
    }

    private static String requset(RequestConfig config, int methodType, int protocolType) {
        return requset(config, methodType, protocolType,false);
    }

    private static String requset(RequestConfig config, int methodType, int protocolType, boolean isAddCustomHead) {
        String body = config.getBody();
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        try {
            URL httpUrl = new URL(config.getUrl());
            switch (protocolType) {
                case PROTOCOL_TYPE.HTTP:
                    connection = (HttpURLConnection) httpUrl.openConnection();
                    break;
                case PROTOCOL_TYPE.HTTPS:
                    connection = (HttpURLConnection) getHttpsURLConnection(httpUrl.openConnection());
                    break;
            }
            if (isAddCustomHead){
                addCustomHead(connection);
            }
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", ENCODING_UTF8);
            connection.setRequestProperty("gagasd", ENCODING_UTF8);

            switch (methodType) {
                case METHOD_TYPE.GET:
                    connection.setRequestMethod("GET");
                    break;
                case METHOD_TYPE.POST:
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(body.getBytes(ENCODING_UTF8).length));
                    OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                    outputStream.write(body.getBytes(ENCODING_UTF8));
                    outputStream.flush();
                    outputStream.close();
                    break;
            }

            connection.connect();
            BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = buff.readLine()) != null) {
                result.append(line);
            }
            buff.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
        return result.toString();
    }

    /**
     * 封装请求头信息
     *
     * @return
     * @throws IOException
     */
    public static void addCustomHead(URLConnection connection) {
        LuaState Luastate = MyApplication.getAppContext().getmLuaState();
        synchronized (Luastate) {
            Luastate.getGlobal("utils");
            int index = Luastate.getTop();

            Luastate.getField(index, "httphead");
            Luastate.pushJavaObject(connection);
            LuaTools.dbcall(Luastate, 1, 0);
        }
    }

    private static URLConnection getHttpsURLConnection(URLConnection urlConnection) {
        try {
            TrustManager easyTrustManager = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{easyTrustManager}, null);
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslcontext.getSocketFactory());
        } catch (Exception e) {
            MLog.e(TAG, "getHttpsURLConnection" + e);
        }
        return urlConnection;
    }

    public static String createBody(Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entries : params.entrySet()) {
            String key = entries.getKey();
            Object value = entries.getValue();
            builder.append(key);
            builder.append("=");
            builder.append(value);
            builder.append("&");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }


}
