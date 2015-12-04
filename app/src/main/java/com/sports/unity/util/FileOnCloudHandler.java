package com.sports.unity.util;

import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.network.ContentRequest;

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

    private static FileOnCloudHandler FILE_ON_CLOUD_HANDLER = null;

    public static FileOnCloudHandler getInstance(Context context){
        if( FILE_ON_CLOUD_HANDLER == null ){
            FILE_ON_CLOUD_HANDLER = new FileOnCloudHandler(context);
        }
        return FILE_ON_CLOUD_HANDLER;
    }

    private Context context = null;
    private PriorityQueue<CloudContentRequest> requests = new PriorityQueue<>();

    private Thread threadToHandleRequests = null;

    private FileOnCloudHandler(Context context){
        this.context = context;
    }

    public void requestForUpload(byte[] content, String mimeType, Chat chat, long messageId){
        CloudContentRequest request = new CloudContentRequest( true, mimeType, messageId, null, content, chat);
        requests.add(request);

        processRequests();
    }

    public void requestForDownload(String checksum, String mimeType, long messageId){
        CloudContentRequest request = new CloudContentRequest( false, mimeType, messageId, checksum, null, null);
        requests.add(request);

        processRequests();
    }

    private void processRequests(){
        if( threadToHandleRequests != null && threadToHandleRequests.isAlive() ){

        } else {
            threadToHandleRequests = new Thread(new Runnable() {

                @Override
                public void run() {

                    while( isThereMoreRequestsToServe() ){
                        CloudContentRequest request = getContentRequestToBeServed();
                        if( request.isUploadRequest() ){
                            handleUploadRequest( request, context);
                        } else {
                            handleDownloadRequest( request, context);
                        }
                    }

                }

            });
            threadToHandleRequests.start();
        }
    }

    private CloudContentRequest getContentRequestToBeServed(){
        return requests.poll();
    }

    private boolean isThereMoreRequestsToServe(){
        return requests.size() > 0;
    }

    private void handleUploadRequest( CloudContentRequest request, Context context){
        String checksum = uploadContent(request.getContent());

        PersonalMessaging.getInstance( context).sendMediaMessage(checksum, (Chat)request.extra, request.messageId, request.mimeType);

        requests.remove(request);
    }

    private void handleDownloadRequest( CloudContentRequest request, Context context){
        byte[] content = downloadContent(request.checksum);

        String fileName = String.valueOf(System.currentTimeMillis());
        DBUtil.writeContentToFile(context, fileName, content, false);
        SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

        sendActionToCorrespondingActivityListener( 2, ActivityActionHandler.CHAT_SCREEN_KEY, request.mimeType, fileName, content);

        requests.remove(request);
    }

    private String uploadContent(byte[] content){
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

    private byte[] downloadContent(String fileName){
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
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {

        }
        return data;
    }

    private boolean sendActionToCorrespondingActivityListener(int id, String key, String mimeType, Object messageContent, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent( id, mimeType, messageContent, mediaContent);
            success = true;
        }
        return success;
    }

    public static class CloudContentRequest {

        private boolean uploadRequest = false; // upload - true, download - false

        private String mimeType = null;
        private long messageId = 0;

        private String checksum = null;
        private byte[] content = null;

        private Object extra = null;

        public CloudContentRequest(boolean uploadRequest, String mimeType, long messageId, String checksum, byte[] content, Object extra){
            this.uploadRequest = uploadRequest;
            this.mimeType = mimeType;
            this.messageId = messageId;
            this.checksum = checksum;
            this.content = content;
            this.extra = extra;
        }

        public String getMimeType() {
            return mimeType;
        }

        public byte[] getContent() {
            return content;
        }

        public long getMessageId() {
            return messageId;
        }

        public String getChecksum() {
            return checksum;
        }

        public Object getExtra() {
            return extra;
        }

        public boolean isUploadRequest() {
            return uploadRequest;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }

    }

}
