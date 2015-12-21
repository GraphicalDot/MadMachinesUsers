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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by amandeep on 26/11/15.
 */
public class FileOnCloudHandler {

    private static final String CONTENT_CLOUD_URL = "http://54.169.217.88/media?";

    private static final int STATUS_NONE = 1;
    private static final int STATUS_DOWNLOADING = 2;
    private static final int STATUS_DOWNLOAD_FAILED = 3;
    private static final int STATUS_DOWNLOADED = 4;

    private static final int STATUS_UPLOADING = 5;
    private static final int STATUS_UPLOADED = 6;

    private static FileOnCloudHandler FILE_ON_CLOUD_HANDLER = null;

    public static FileOnCloudHandler getInstance(Context context){
        if( FILE_ON_CLOUD_HANDLER == null ){
            FILE_ON_CLOUD_HANDLER = new FileOnCloudHandler(context);
        }
        return FILE_ON_CLOUD_HANDLER;
    }

    private Context context = null;
    private PriorityQueue<CloudContentRequest> requests = new PriorityQueue<>();
    private HashMap<Long, Integer> requestMapWithStatus = new HashMap<>();

    private Thread threadToHandleRequests = null;

    private FileOnCloudHandler(Context context){
        this.context = context;
    }

    public void requestForUpload(byte[] content, String mimeType, Chat chat, long messageId){
        if( ! requestMapWithStatus.containsKey(messageId) ) {
            CloudContentRequest request = new CloudContentRequest(true, mimeType, messageId, null, content, true, chat);
            requests.add(request);
            requestMapWithStatus.put(messageId, STATUS_UPLOADING);

            processRequests();
        }
    }

    public void requestForUpload(String fileName, String mimeType, Chat chat, long messageId){
        if( ! requestMapWithStatus.containsKey(messageId) ) {
            CloudContentRequest request = new CloudContentRequest(true, mimeType, messageId, null, fileName, false, chat);
            requests.add(request);
            requestMapWithStatus.put(messageId, STATUS_UPLOADING);

            processRequests();
        }
    }

    public void requestForDownload(String checksum, String mimeType, long messageId) {
        if( ! requestMapWithStatus.containsKey(messageId) ) {
            boolean isContentInBytes = true;
            if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
                isContentInBytes = false;
            }

            CloudContentRequest request = new CloudContentRequest(false, mimeType, messageId, checksum, null, isContentInBytes, null);
            requests.add(request);
            requestMapWithStatus.put(messageId, STATUS_DOWNLOADING);

            processRequests();
        }
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
        if(request.isContentBytes) {
            String checksum = uploadContent((byte[]) request.getContent());
            PersonalMessaging.getInstance(context).sendMediaMessage(checksum, (Chat) request.extra, request.messageId, request.mimeType);

            sendActionToCorrespondingActivityListener(0, ActivityActionHandler.CHAT_SCREEN_KEY);

            requests.remove(request);
            requestMapWithStatus.remove(request.getMessageId());
        } else {
            String checksum = uploadContent((String)request.getContent());
            PersonalMessaging.getInstance(context).sendMediaMessage(checksum, (Chat) request.extra, request.messageId, request.mimeType);

            sendActionToCorrespondingActivityListener(0, ActivityActionHandler.CHAT_SCREEN_KEY);

            requests.remove(request);
            requestMapWithStatus.remove(request.getMessageId());
        }
    }

    private void handleDownloadRequest( CloudContentRequest request, Context context){
        if(request.isContentBytes){
            byte[] content = downloadContent(request.checksum);

            String fileName = DBUtil.getUniqueFileName(context, request.mimeType);
            DBUtil.writeContentToExternalFileStorage(context, fileName, content);
            SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

            sendActionToCorrespondingActivityListener(2, ActivityActionHandler.CHAT_SCREEN_KEY, request.mimeType, fileName, content);

            requests.remove(request);
            requestMapWithStatus.remove(request.getMessageId());
        } else {
            String fileName = DBUtil.getUniqueFileName(context, request.mimeType);
            downloadContentFromFile(request.checksum, fileName);

            SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

            sendActionToCorrespondingActivityListener( 0, ActivityActionHandler.CHAT_SCREEN_KEY);

            requests.remove(request);
            requestMapWithStatus.remove(request.getMessageId());
        }
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

    private String uploadContent(String fileName){
        Log.i("File on cloud" , "uploading file");

        HttpURLConnection httpURLConnection = null;
        FileInputStream fileInputStream = null;
        String checksum = null;
        try {
            checksum = CommonUtil.getMD5EncryptedString(context, fileName);

            File file = new File ( DBUtil.getFilePath(context, fileName));
            fileInputStream = new FileInputStream(file);

            URL url = new URL(CONTENT_CLOUD_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setChunkedStreamingMode(4096);

            httpURLConnection.setRequestProperty("Checksum", checksum);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            byte chunk[] = new byte[4096];
            int read = 0;
            while( (read = fileInputStream.read(chunk)) != -1 ){
                outputStream.write(chunk, 0, read);
            }

            if( httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                Log.i("File on cloud" , "uploaded with checksum " + checksum);
            } else {
                //TODO
                Log.i("File on cloud" , "failed to load file content");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            }catch (Exception ex){}
            try {
                httpURLConnection.disconnect();
            }catch (Exception ex){}
        }

        return checksum;
    }

    private byte[] downloadContent(String cloudFileName){
        Log.i("File on cloud" , "downloading " + cloudFileName);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(CONTENT_CLOUD_URL + "name="+cloudFileName);

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

            //TODO validate content

            Log.i("File on cloud" , data.length + " downloaded " + checksum);
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {

        }
        return data;
    }

    private void downloadContentFromFile(String cloudFileName, String fileName){
        Log.i("File on cloud" , "downloading file from " + cloudFileName);

        HttpURLConnection httpURLConnection = null;
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File ( DBUtil.getFilePath(context, fileName));
            fileOutputStream = new FileOutputStream(file);

            URL url = new URL(CONTENT_CLOUD_URL + "name="+cloudFileName);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setChunkedStreamingMode(4096);

            InputStream inputStream = httpURLConnection.getInputStream();

            byte chunk[] = new byte[4096];
            int read = 0;
            while( (read = inputStream.read(chunk)) != -1 ){
                fileOutputStream.write(chunk, 0, read);
            }

            //TODO validate file content

            if( httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                Log.i("File on cloud" , "downloaded");
            } else {
                //TODO
                Log.i("File on cloud" , "failed to download file content");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            }catch (Exception ex){}
            try {
                httpURLConnection.disconnect();
            }catch (Exception ex){}
        }
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

    private boolean sendActionToCorrespondingActivityListener(int id, String key) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleAction(id);
            success = true;
        }
        return success;
    }

    public static class CloudContentRequest {

        private boolean uploadRequest = false; // upload - true, download - false

        private String mimeType = null;
        private long messageId = 0;

        private String checksum = null;

        private boolean isContentBytes = false;
        private Object content = null;

        private Object extra = null;

        public CloudContentRequest(boolean uploadRequest, String mimeType, long messageId, String checksum, Object content, boolean isContentBytes, Object extra){
            this.uploadRequest = uploadRequest;
            this.mimeType = mimeType;
            this.messageId = messageId;
            this.checksum = checksum;
            this.content = content;
            this.isContentBytes = isContentBytes;
            this.extra = extra;
        }

        public String getMimeType() {
            return mimeType;
        }

        public Object getContent() {
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

        public boolean isContentBytes() {
            return isContentBytes;
        }
    }

}
