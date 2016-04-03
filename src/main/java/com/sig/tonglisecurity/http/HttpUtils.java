package com.sig.tonglisecurity.http;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Http 工具类
 */
public class HttpUtils {

    private static TrustManager truseAllManager = new X509TrustManager() {

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public static String doPost(String url, List<NameValuePair> params) {
        HttpPost httpRequest = new HttpPost(url);
        String strResult = "";

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpResponse httpResponse = getHttpClient().execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                strResult = EntityUtils.toString(httpResponse.getEntity(), "gbk");
            }
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            strResult = "{\"result\":\"3\"}";
        } catch (ClientProtocolException e) {

            e.printStackTrace();
            strResult = "{\"result\":\"3\"}";
        } catch (IOException e) {

            e.printStackTrace();
            strResult = "{\"result\":\"3\"}";
        }
        return strResult;
    }

    public static HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

        HttpClientParams.setRedirecting(httpParams, true);

        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        HttpProtocolParams.setUserAgent(httpParams, userAgent);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        enableSSL(httpClient);

        return httpClient;
    }

    private static void enableSSL(DefaultHttpClient httpclient) {
        // 设置 ssl
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{truseAllManager}, null);
            SSLSocketFactory sf = new SSLSocketFactory(null);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme https = new Scheme("https", sf, 443);
            httpclient.getConnectionManager().getSchemeRegistry().register(https);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String doGet(String url, Map params) {
        String paramStr = "";
        if (params != null) {
            for (Object o : params.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object key = entry.getKey();
                Object val = entry.getValue();
                paramStr += paramStr = "&" + key + "=" + val;
            }
            if (!paramStr.equals("")) {
                paramStr = paramStr.replaceFirst("&", "?");
                url += paramStr;
            }
        }

        String strResult = "doGetError";
        try {
            HttpGet httpRequest = new HttpGet(url);

            HttpResponse httpResponse = getHttpClient().execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                /**
                 * 获取响应码
                 */
                strResult = EntityUtils.toString(httpResponse.getEntity());

            } else {
                strResult = "Error Response: "
                        + httpResponse.getStatusLine().toString();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            strResult = "{\"result\":\"3\"}";
        } catch (IOException e) {
            e.printStackTrace();
            strResult = "{\"result\":\"3\"}";
        } catch (Exception e) {
            e.printStackTrace();
            strResult = "{\"result\":\"3\"}";
        }

        return strResult;
    }
}
