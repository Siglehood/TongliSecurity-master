package com.sig.tonglisecurity.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpManager {

    public static String postHttpRequest(HttpRequestInfo info) throws IOException {
        String contentAsString;
        URL url = new URL(info.getRequestUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(8000 /* milliseconds */);
        conn.setConnectTimeout(8000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        String paramsStr = info.getParamsStr();
        if (paramsStr != null) {
            conn.getOutputStream().write(paramsStr.getBytes("UTF-8"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
        }
        conn.connect();
        switch (conn.getResponseCode()) {
            case 200:
                InputStream is = conn.getInputStream();
                contentAsString = readIt(is);
                is.close();
                break;
            default:
                contentAsString = "{\"result\":\"3\"}";
                break;
        }
        return contentAsString;
    }

    // Reads an InputStream and converts it to a String.
    private static String readIt(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, Charset.forName("GBK")));
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String postHttpsRequest(HttpRequestInfo info) throws Exception {
        String contentAsString;
        URL url = new URL(info.getRequestUrl());
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{new MyTrustManager()},
                new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(8000 /* milliseconds */);
        conn.setConnectTimeout(8000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        String paramsStr = info.getParamsStr();
        if (paramsStr != null) {
            conn.getOutputStream().write(paramsStr.getBytes("GBK"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
        }
        conn.connect();
        switch (conn.getResponseCode()) {
            case 200:
                InputStream is = conn.getInputStream();
                contentAsString = readIt(is);
                is.close();
                break;
            default:
                contentAsString = "{\"result\":\"3\"}";
                break;
        }

        return contentAsString;
    }
}


class MyHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}

class MyTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
