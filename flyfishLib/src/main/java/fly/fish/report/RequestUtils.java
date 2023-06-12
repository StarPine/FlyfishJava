package fly.fish.report;

import android.text.TextUtils;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fly.fish.tools.MLog;

public class RequestUtils {
    private static final String TAG = "RequestUtils";
    private static final int READ_TIME_OUT = 10000;
    private static final int CONNECTION_TIME_OUT = 10000;
    private static final String ENCODING_UTF8 = "UTF-8";

    private static final class METHOD_TYPE {
        private static final int GET = 0;
        private static final int POST = 1;
    }

    private static final class PROTOCOL_TYPE {
        private static final int HTTP = 0;
        private static final int HTTPS = 1;
    }

    public static String GET(RequestConfig config) {
        return requset(config, METHOD_TYPE.GET, getProtocolType(config.getUrl()));
    }

    public static String POST(RequestConfig config) {
        return requset(config, METHOD_TYPE.POST, getProtocolType(config.getUrl()));
    }

    private static int getProtocolType(String url) {
        return url.toLowerCase().startsWith("https") ? PROTOCOL_TYPE.HTTPS : PROTOCOL_TYPE.HTTP;
    }

    private static String requset(RequestConfig config, int methodType, int protocolType) {
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
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", ENCODING_UTF8);
            switch (methodType) {
                case METHOD_TYPE.GET:
                    connection.setRequestMethod("GET");
                    break;
                case METHOD_TYPE.POST:
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-url-encoded");
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

    public static String createBody(Map<String, String> params) throws UnsupportedEncodingException, JSONException {
        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entries : params.entrySet()) {
            String key = entries.getKey();
            String value = entries.getValue();
            Object o = jsonObject.get(key);
            builder.append(key);
            builder.append("=");
//            if (TextUtils.isEmpty(value)) {
//                builder.append("");
//            } else {
//                builder.append(URLEncoder.encode(value, ENCODING_UTF8));
//            }
            builder.append(value);
            builder.append("&");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }


}
