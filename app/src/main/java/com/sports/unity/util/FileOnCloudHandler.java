package com.sports.unity.util;

import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.PersonalMessaging;

import org.jivesoftware.smack.chat.Chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amandeep on 26/11/15.
 */
public class FileOnCloudHandler {

    public static final int STATUS_NONE = 1;

    public static final int STATUS_DOWNLOADING = 2;
    public static final int STATUS_DOWNLOAD_FAILED = 3;
    public static final int STATUS_DOWNLOADED = 4;

    public static final int STATUS_UPLOADING = 5;
    public static final int STATUS_UPLOADED = 6;
    public static final int STATUS_UPLOAD_FAILED = 7;


    private static final String CONTENT_CLOUD_URL = "http://54.169.217.88/media?";

    private static FileOnCloudHandler FILE_ON_CLOUD_HANDLER = null;

    public static FileOnCloudHandler getInstance(Context context) {
        if (FILE_ON_CLOUD_HANDLER == null) {
            FILE_ON_CLOUD_HANDLER = new FileOnCloudHandler(context);
        }
        return FILE_ON_CLOUD_HANDLER;
    }

    private Context context = null;
    private ArrayList<CloudContentRequest> requests = new ArrayList<>();
    private HashMap<String, Integer> requestMapWithStatus = new HashMap<String, Integer>();

    private Thread threadToHandleRequests = null;

    private FileOnCloudHandler(Context context) {
        this.context = context;
    }

    public void requestForUpload(byte[] content, String mimeType, Chat chat, long messageId, boolean nearByChat) {
        if (!requestMapWithStatus.containsKey(String.valueOf(messageId))) {
            CloudContentRequest request = new CloudContentRequest(true, mimeType, messageId, null, content, true, chat);
            requests.add(request);
            requestMapWithStatus.put(String.valueOf(messageId), STATUS_UPLOADING);

            processRequests(nearByChat);
        }
    }

    public void requestForUpload(String fileName, String mimeType, Chat chat, long messageId, boolean nearByChat) {
        if (!requestMapWithStatus.containsKey(String.valueOf(messageId))) {
            Log.i("File on cloud", "upload request message id " + messageId);
            CloudContentRequest request = new CloudContentRequest(true, mimeType, messageId, null, fileName, false, chat);
            requests.add(request);
            requestMapWithStatus.put(String.valueOf(messageId), STATUS_UPLOADING);

            processRequests(nearByChat);
        }
    }

    public void requestForDownload(String checksum, String mimeType, long messageId) {
        if (!requestMapWithStatus.containsKey(String.valueOf(messageId))) {
            boolean isContentInBytes = true;
            if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
                isContentInBytes = false;
            }

            CloudContentRequest request = new CloudContentRequest(false, mimeType, messageId, checksum, null, isContentInBytes, null);
            requests.add(request);
            requestMapWithStatus.put(String.valueOf(messageId), STATUS_DOWNLOADING);

            processRequests(false);
        }
    }

    public int getMediaContentStatus(Message message) {
        int status = STATUS_NONE;
        if (requestMapWithStatus.containsKey(String.valueOf(message.id))) {
            status = requestMapWithStatus.get(String.valueOf(message.id));
        }

        if (status == STATUS_NONE) {
            if (message.iAmSender) {
                if (message.textData.length() == 0) {
                    status = STATUS_UPLOAD_FAILED;
                } else {
                    status = STATUS_UPLOADED;
                }
            } else {
                if (message.mediaFileName == null) {
                    status = STATUS_DOWNLOAD_FAILED;
                } else {
                    status = STATUS_DOWNLOADED;
                }
            }
        }

        return status;
    }

    private void processRequests(final boolean nearByChat) {
        if (threadToHandleRequests != null && threadToHandleRequests.isAlive()) {

        } else {
            threadToHandleRequests = new Thread(new Runnable() {

                @Override
                public void run() {

                    while (isThereMoreRequestsToServe()) {
                        CloudContentRequest request = getContentRequestToBeServed();
                        if (request.isUploadRequest()) {
                            handleUploadRequest(request, context, nearByChat);
                        } else {
                            handleDownloadRequest(request, context);
                        }
                    }

                }

            });
            threadToHandleRequests.start();
        }
    }

    private CloudContentRequest getContentRequestToBeServed() {
        CloudContentRequest request = null;
        if (requests.size() > 0) {
            request = requests.get(requests.size() - 1);
        }
        return request;
    }

    private boolean isThereMoreRequestsToServe() {
        return requests.size() > 0;
    }

    private void handleUploadRequest(CloudContentRequest request, Context context, boolean nearByChat) {
        if (request.isContentBytes) {
            String checksum = uploadContent((byte[]) request.getContent());
            if (checksum != null) {
                PersonalMessaging.getInstance(context).sendMediaMessage(checksum, (Chat) request.extra, request.messageId, request.mimeType, nearByChat);

                sendActionToCorrespondingActivityListener(0, ActivityActionHandler.CHAT_SCREEN_KEY);

                requests.remove(request);
                requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
            } else {
                requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_UPLOAD_FAILED);
            }
        } else {
            String checksum = uploadContent((String) request.getContent());
            if (checksum != null) {
                PersonalMessaging.getInstance(context).sendMediaMessage(checksum, (Chat) request.extra, request.messageId, request.mimeType, nearByChat);

                sendActionToCorrespondingActivityListener(0, ActivityActionHandler.CHAT_SCREEN_KEY);

                requests.remove(request);
                requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
            } else {
                requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_UPLOAD_FAILED);
            }
        }
    }

    private void handleDownloadRequest(CloudContentRequest request, Context context) {
        if (request.isContentBytes) {
            byte[] content = downloadContent(request.checksum);
            if (content != null) {
                String fileName = DBUtil.getUniqueFileName(context, request.mimeType);
                DBUtil.writeContentToExternalFileStorage(context, fileName, content);
                SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

                sendActionToCorrespondingActivityListener(2, ActivityActionHandler.CHAT_SCREEN_KEY, request.mimeType, fileName, content);

                requests.remove(request);
                requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
            } else {
                requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_DOWNLOAD_FAILED);
            }
        } else {
            String fileName = DBUtil.getUniqueFileName(context, request.mimeType);
            boolean success = downloadContentFromFile(request.checksum, fileName);

            if (success) {
                SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

                sendActionToCorrespondingActivityListener(0, ActivityActionHandler.CHAT_SCREEN_KEY);

                requests.remove(request);
                requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
            } else {
                requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_DOWNLOAD_FAILED);
            }
        }
    }

    private String uploadContent(byte[] content) {
        Log.i("File on cloud", "uploading");

        HttpURLConnection httpURLConnection = null;
        ByteArrayInputStream byteArrayInputStream = null;
        String checksum = null;
        try {
            checksum = CommonUtil.getMD5EncryptedString(content);
            byteArrayInputStream = new ByteArrayInputStream(content, 0, content.length);

            URL url = new URL(CONTENT_CLOUD_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setRequestProperty("Checksum", checksum);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            byte chunk[] = new byte[4096];
            int read = 0;
            while ((read = byteArrayInputStream.read(chunk)) != -1) {
                outputStream.write(chunk, 0, read);
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i("File on cloud", "uploaded with checksum " + checksum);
            } else {
                checksum = null;
                Log.i("File on cloud", "failed to load file content");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (Exception ex) {
            }
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }

        return checksum;
    }

    private String uploadContent(String fileName) {
        Log.i("File on cloud", "uploading file");

        HttpURLConnection httpURLConnection = null;
        FileInputStream fileInputStream = null;
        String checksum = null;
        try {
            checksum = CommonUtil.getMD5EncryptedString(context, fileName);

            File file = new File(DBUtil.getFilePath(context, fileName));
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
            while ((read = fileInputStream.read(chunk)) != -1) {
                outputStream.write(chunk, 0, read);
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i("File on cloud", "uploaded with checksum " + checksum);
            } else {
                checksum = null;
                Log.i("File on cloud", "failed to load file content");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception ex) {
            }
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }

        return checksum;
    }

    private byte[] downloadContent(String cloudFileName) {
        Log.i("File on cloud", "downloading " + cloudFileName);

        HttpURLConnection httpURLConnection = null;

        ByteArrayOutputStream byteArrayOutputStream = null;

        byte[] data = null;
        try {
            URL url = new URL(CONTENT_CLOUD_URL + "name=" + cloudFileName);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");

            InputStream is = httpURLConnection.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] chunk = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = is.read(chunk)) != -1) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }

            data = byteArrayOutputStream.toByteArray();
            String checksum = CommonUtil.getMD5EncryptedString(data);

            if (checksum.equals(cloudFileName)) {
                //valid content
                Log.i("File on cloud", data.length + " downloaded " + checksum);
            } else {
                data = null;
                Log.i("File on cloud", "Invalid Content");
            }
        } catch (Throwable t) {
            t.printStackTrace();
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
        return data;
    }

    private boolean downloadContentFromFile(String cloudFileName, String fileName) {
        Log.i("File on cloud", "downloading file from " + cloudFileName);

        boolean success = false;

        HttpURLConnection httpURLConnection = null;
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(DBUtil.getFilePath(context, fileName));
            fileOutputStream = new FileOutputStream(file);

            URL url = new URL(CONTENT_CLOUD_URL + "name=" + cloudFileName);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setChunkedStreamingMode(4096);

            InputStream inputStream = httpURLConnection.getInputStream();

            byte chunk[] = new byte[4096];
            int read = 0;
            while ((read = inputStream.read(chunk)) != -1) {
                fileOutputStream.write(chunk, 0, read);
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String checksum = CommonUtil.getMD5EncryptedString(context, fileName);
                if (checksum.equals(cloudFileName)) {
                    success = true;
                    Log.i("File on cloud", "downloaded");
                } else {
                    Log.i("File on cloud", "Invalid Content");
                }

            } else {
                Log.i("File on cloud", "failed to download file content");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (Exception ex) {
            }
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    private boolean sendActionToCorrespondingActivityListener(int id, String key, String mimeType, Object messageContent, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(id, mimeType, messageContent, mediaContent);
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

        public CloudContentRequest(boolean uploadRequest, String mimeType, long messageId, String checksum, Object content, boolean isContentBytes, Object extra) {
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
