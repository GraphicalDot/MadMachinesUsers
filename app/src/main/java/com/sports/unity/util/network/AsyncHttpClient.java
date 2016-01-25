package com.sports.unity.util.network;

import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amandeep on 20/1/16.
 */
public class AsyncHttpClient extends Thread {

    public static final int HTTP_NO_INTERNET = -1;

    public static final String METHOD_TYPE_GET = "GET";
    public static final String METHOD_TYPE_POST = "POST";

    private Context context = null;
    private int connectionTimeOut = Constants.CONNECTION_TIME_OUT;
    private int readTimeOut = Constants.CONNECTION_READ_TIME_OUT;

    private String url = null;
    private HashMap<String,String> parameters = new HashMap<>();

    private ResponseListener responseListener = null;

    public AsyncHttpClient(Context context, String url, ResponseListener listener){
        this.url = url;
        this.responseListener = listener;
        this.context = context;
    }

    public void addParameters(String key, String value){
        parameters.put(key, value);
    }

    public void removeParameters(String key){
        parameters.remove(key);
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    @Override
    public void run() {
        super.run();

        makeHttpGetCall();
    }

    private void makeHttpGetCall(){
        byte[] data = null;
        int responseCode = 0;

        if( CommonUtil.isInternetConnectionAvailable(context) ){
            HttpURLConnection httpURLConnection = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            try {

                StringBuilder urlBuilder = new StringBuilder(this.url);
                Iterator<String> keys = parameters.keySet().iterator();
                String key = null;
                if(keys.hasNext()) {
                    key = keys.next();

                    urlBuilder.append(key);
                    urlBuilder.append("=");
                    urlBuilder.append(URLEncoder.encode(parameters.get(key), "UTF-8"));

                    while (keys.hasNext()){
                        urlBuilder.append("&");
                        urlBuilder.append(key);
                        urlBuilder.append("=");
                        urlBuilder.append(URLEncoder.encode(parameters.get(key), "UTF-8"));
                    }
                }

                URL url = new URL(urlBuilder.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(connectionTimeOut);
                httpURLConnection.setReadTimeout(readTimeOut);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod(METHOD_TYPE_GET);

                InputStream is = httpURLConnection.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();

                byte[] chunk = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = is.read(chunk)) != -1) {
                    byteArrayOutputStream.write(chunk, 0, bytesRead);
                }

                data = byteArrayOutputStream.toByteArray();
                responseCode = httpURLConnection.getResponseCode();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception ex) {
                }
                try {
                    httpURLConnection.disconnect();
                } catch (Exception ex) {
                }
            }
        } else {
            responseCode = HTTP_NO_INTERNET;
        }

        if( responseListener != null ){
            responseListener.response(responseCode, data);
        }
    }

}
