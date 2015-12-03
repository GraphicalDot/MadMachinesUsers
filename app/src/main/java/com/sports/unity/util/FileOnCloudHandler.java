package com.sports.unity.util;

import android.content.Context;
import android.util.Log;

import com.sports.unity.messages.controller.model.PersonalMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.jivesoftware.smack.chat.Chat;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by amandeep on 26/11/15.
 */
public class FileOnCloudHandler {

    private static final String CONTENT_CLOUD_URL = "http://54.169.217.88/media?";

    public static void uploadAndSendMedia(byte[] content, String mimeType, Chat chat, long messageId, Context context){
        String checksum = uploadContent(content);

        PersonalMessaging.getInstance( context).sendMediaMessage( checksum, chat, messageId, mimeType);
    }

    public static String uploadContent(byte[] content){
        Log.i("File on cloud" , "uploading");

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(CONTENT_CLOUD_URL);

        ByteArrayInputStream byteArrayInputStream = null;
        String checksum = null;
        try {
            checksum = CommonUtil.getMD5EncryptedString(content);
            byteArrayInputStream = new ByteArrayInputStream( content, 0, content.length);

            httppost.setHeader( "Checksum", checksum);

            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(byteArrayInputStream);
            entity.setContentLength(content.length);

            entity.setContentType("binary/octet-stream");
            httppost.setEntity(entity);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httppost, responseHandler);

            Log.i("File on cloud" , content.length + " uploaded with checksum " + checksum);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( byteArrayInputStream != null ){
                try {
                    byteArrayInputStream.close();
                }catch (Exception ex){}
            }
            httpclient.getConnectionManager().shutdown();
        }

        return checksum;
    }

    public static byte[] downloadContent(String fileName){
        Log.i("File on cloud" , "downloading " + fileName);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(CONTENT_CLOUD_URL + "name="+fileName);

        ByteArrayOutputStream byteArrayOutputStream = null;

        byte[] data = null;
        try {
            HttpResponse resp = client.execute(get);
            InputStream is = resp.getEntity().getContent();
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] chunk = new byte[1024];
            int bytesRead = -1;
            while ( (bytesRead = is.read(chunk)) != -1 ) {
                byteArrayOutputStream.write( chunk, 0, bytesRead);
            }

            data = byteArrayOutputStream.toByteArray();
            String checksum = CommonUtil.getMD5EncryptedString(data);
            Log.i("File on cloud" , data.length + " downloaded " + checksum);
            Log.i("File on cloud" , "content " + String.valueOf(data));
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {

        }
        return data;
    }

}
