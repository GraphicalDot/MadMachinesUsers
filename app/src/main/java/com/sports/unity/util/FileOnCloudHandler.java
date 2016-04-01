package com.sports.unity.util;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.model.PubSubMessaging;

import org.jivesoftware.smack.chat.Chat;
import org.json.JSONObject;

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


    private static final String CONTENT_CLOUD_URL = "http://" + XMPPClient.SERVER_HOST + "/media?";
    private static final String CONTENT_PRESENT_URL = "http://" + XMPPClient.SERVER_HOST + "/media_present?";

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

//    public void requestForUpload(byte[] content, String mimeType, Chat chat, long messageId, boolean nearByChat) {
//        boolean handleRequest = shouldHandleRequest(messageId);
//
//        if ( handleRequest ) {
//            CloudContentRequest request = new CloudContentRequest(true, mimeType, messageId, null, content, true, chat);
//            requests.add(request);
//            requestMapWithStatus.put(String.valueOf(messageId), STATUS_UPLOADING);
//
//            processRequests(nearByChat);
//        }
//    }

    public void requestForUpload(String fileName, String thumbnailImage, String mimeType, Chat chat, int messageId, boolean nearByChat, boolean isGroupChat, String toJid) {
        boolean handleRequest = shouldHandleRequest(messageId);

        if (handleRequest) {
            Log.i("File on cloud", "upload request message id " + messageId);
            CloudContentRequest request = new CloudContentRequest(true, mimeType, messageId, null, fileName, thumbnailImage, chat, toJid);
            requests.add(request);
            requestMapWithStatus.put(String.valueOf(messageId), STATUS_UPLOADING);

            processRequests(nearByChat, isGroupChat, toJid);
        }
    }

    public void requestForDownload(String checksum, String mimeType, int messageId, String fromJid) {
        boolean handleRequest = shouldHandleRequest(messageId);

        if (handleRequest) {
            CloudContentRequest request = new CloudContentRequest(false, mimeType, messageId, checksum, null, null, null, fromJid);
            requests.add(request);
            requestMapWithStatus.put(String.valueOf(messageId), STATUS_DOWNLOADING);

            processRequests(false, false, null);
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

    private boolean downloadContentDirectToFile(String mimeType) {
        boolean directToFile = false;
        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            directToFile = true;
        }
        return directToFile;
    }

    private boolean shouldHandleRequest(long messageId) {
        boolean handleRequest = false;
        if (requestMapWithStatus.containsKey(String.valueOf(messageId))) {
            int status = requestMapWithStatus.get(String.valueOf(messageId));
            if (status == STATUS_DOWNLOAD_FAILED || status == STATUS_UPLOAD_FAILED) {
                handleRequest = true;
                requestMapWithStatus.remove(String.valueOf(messageId));
            } else {
                handleRequest = false;
            }
        } else {
            handleRequest = true;
        }
        return handleRequest;
    }

    private void processRequests(final boolean nearByChat, final boolean isGroupChat, final String groupServerId) {
        if (threadToHandleRequests != null && threadToHandleRequests.isAlive()) {

        } else {
            threadToHandleRequests = new Thread(new Runnable() {

                @Override
                public void run() {

                    while (isThereMoreRequestsToServe()) {
                        CloudContentRequest request = getContentRequestToBeServed();
                        if (request.isUploadRequest()) {
                            handleUploadRequest(request, context, nearByChat, isGroupChat, groupServerId);
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

    private void handleUploadRequest(CloudContentRequest request, Context context, boolean nearByChat, boolean isGroupChat, String jid) {
        String checksum = uploadContent((String) request.getFileName(), request.mimeType);
        if (checksum != null) {
            if (!isGroupChat) {
                PersonalMessaging.getInstance(context).sendMediaMessage(checksum, request.thumbnailImage, (Chat) request.extra, request.messageId, request.mimeType, nearByChat);
            } else {
                PubSubMessaging.getInstance().sendMediaMessage(context, checksum, request.thumbnailImage, request.messageId, request.mimeType, jid);
            }

            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY, request.jid);

            requests.remove(request);
            requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
        } else {
            requests.remove(request);
            requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_UPLOAD_FAILED);
        }
    }

    private void handleDownloadRequest(CloudContentRequest request, Context context) {
        if (!downloadContentDirectToFile(request.mimeType)) {
            byte[] content = downloadContent(request.checksum);
            if (content != null) {
                String fileName = DBUtil.getUniqueFileName(request.mimeType, UserUtil.isSaveIncomingMediaToGallery());
                DBUtil.writeContentToExternalFileStorage(context, fileName, content, request.mimeType);
                SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

                ActivityActionHandler.getInstance().dispatchDownloadCompletedEvent(ActivityActionHandler.CHAT_SCREEN_KEY, request.jid, request.mimeType, fileName, content);

                requests.remove(request);
                requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
            } else {
                requests.remove(request);
                requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_DOWNLOAD_FAILED);
            }
        } else {
            String fileName = DBUtil.getUniqueFileName(request.mimeType, UserUtil.isSaveIncomingMediaToGallery());
            boolean success = downloadContentFromFile(request.checksum, fileName, request.mimeType);

            if (success) {
                SportsUnityDBHelper.getInstance(context).updateMediaMessage_ContentDownloaded(request.messageId, fileName);

                ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY, request.jid);

                requests.remove(request);
                requestMapWithStatus.remove(String.valueOf(request.getMessageId()));
            } else {
                requests.remove(request);
                requestMapWithStatus.put(String.valueOf(request.getMessageId()), STATUS_DOWNLOAD_FAILED);
            }
        }
    }

//    private String uploadContent(byte[] content) {
//        Log.i("File on cloud", "uploading");
//
//        HttpURLConnection httpURLConnection = null;
//        ByteArrayInputStream byteArrayInputStream = null;
//        String checksum = null;
//        try {
//            checksum = CommonUtil.getMD5EncryptedString(content);
//
//            if( checkIfContentAlreadyExist(checksum)){
//                //nothing
//            } else {
//
//                byteArrayInputStream = new ByteArrayInputStream(content, 0, content.length);
//
//                URL url = new URL(CONTENT_CLOUD_URL);
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setConnectTimeout(15000);
//                httpURLConnection.setReadTimeout(60000);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setRequestMethod("POST");
//
//                httpURLConnection.setRequestProperty("Checksum", checksum);
//
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//
//                byte chunk[] = new byte[4096];
//                int read = 0;
//                while ((read = byteArrayInputStream.read(chunk)) != -1) {
//                    outputStream.write(chunk, 0, read);
//                }
//
//                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                    Log.i("File on cloud", "uploaded with checksum " + checksum);
//                } else {
//                    checksum = null;
//                    Log.i("File on cloud", "failed to load file content");
//                }
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            checksum = null;
//        } finally {
//            try {
//                byteArrayInputStream.close();
//            } catch (Exception ex) {
//            }
//            try {
//                httpURLConnection.disconnect();
//            } catch (Exception ex) {
//            }
//        }
//
//        return checksum;
//    }

    private String uploadContent(String fileName, String mimeType) {
        Log.i("File on cloud", "uploading file");

        HttpURLConnection httpURLConnection = null;
        FileInputStream fileInputStream = null;
        String checksum = null;
        try {
            checksum = CommonUtil.getMD5EncryptedString(context, mimeType, fileName);

            if (checkIfContentAlreadyExist(checksum)) {
                //nothing
            } else {

                File file = new File(DBUtil.getFilePath(context, mimeType, fileName));
                fileInputStream = new FileInputStream(file);

                URL url = new URL(CONTENT_CLOUD_URL + Constants.REQUEST_PARAMETER_KEY_APK_VERSION + "=" + CommonUtil.getBuildConfig() +
                        "&" + Constants.REQUEST_PARAMETER_KEY_UDID + "=" + CommonUtil.getDeviceId(context));
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

            }

        } catch (Exception e) {
            e.printStackTrace();
            checksum = null;
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
            URL url = new URL(CONTENT_CLOUD_URL + "name=" + cloudFileName + "&" + Constants.REQUEST_PARAMETER_KEY_APK_VERSION + "=" + CommonUtil.getBuildConfig() +
                    "&" + Constants.REQUEST_PARAMETER_KEY_UDID + "=" + CommonUtil.getDeviceId(context));
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
            data = null;
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

    private boolean downloadContentFromFile(String cloudFileName, String fileName, String mimeType) {
        Log.i("File on cloud", "downloading file from " + cloudFileName);

        boolean success = false;

        HttpURLConnection httpURLConnection = null;
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(DBUtil.getFilePath(context, mimeType, fileName));
            fileOutputStream = new FileOutputStream(file);

            URL url = new URL(CONTENT_CLOUD_URL + "name=" + cloudFileName + "&" + Constants.REQUEST_PARAMETER_KEY_APK_VERSION + "=" + CommonUtil.getBuildConfig() +
                    "&" + Constants.REQUEST_PARAMETER_KEY_UDID + "=" + CommonUtil.getDeviceId(context));
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
                String checksum = CommonUtil.getMD5EncryptedString(context, mimeType, fileName);
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

    private boolean checkIfContentAlreadyExist(String checksum) {
        boolean existOnServer = false;

        HttpURLConnection httpURLConnection = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] data = null;
        try {
            URL url = new URL(CONTENT_PRESENT_URL + "name=" + checksum + "&" + Constants.REQUEST_PARAMETER_KEY_APK_VERSION + "=" + CommonUtil.getBuildConfig() +
                    "&" + Constants.REQUEST_PARAMETER_KEY_UDID + "=" + CommonUtil.getDeviceId(context));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(20000);
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

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(data));
                    if (jsonObject.getInt("status") == 200) {
                        existOnServer = true;
                        Log.i("File on cloud", "Check for existence of content on server, Exist ");
                    } else {
                        Log.i("File on cloud", "Check for existence of content on server, Don't Exist ");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Log.i("File on cloud", "Check for existence of content on server, failed as per response code ");
            }

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

        return existOnServer;
    }

    public static class CloudContentRequest {

        private boolean uploadRequest = false; // upload - true, download - false

        private String mimeType = null;
        private int messageId = 0;

        private String checksum = null;
        private String fileName = null;
        private String thumbnailImage = null;

        private String jid = null;

        private Object extra = null;

        public CloudContentRequest(boolean uploadRequest, String mimeType, int messageId, String checksum, String fileName, String thumbnailImage, Object extra, String jid) {
            this.uploadRequest = uploadRequest;
            this.mimeType = mimeType;
            this.messageId = messageId;
            this.checksum = checksum;
            this.fileName = fileName;
            this.thumbnailImage = thumbnailImage;
            this.extra = extra;
            this.jid = jid;
        }

        public String getJid() {
            return jid;
        }

        public String getMimeType() {
            return mimeType;
        }

        public long getMessageId() {
            return messageId;
        }

        public Object getExtra() {
            return extra;
        }

        public String getFileName() {
            return fileName;
        }

        public boolean isUploadRequest() {
            return uploadRequest;
        }

        public String getThumbnailImage() {
            return thumbnailImage;
        }

        public String getChecksum() {
            return checksum;
        }

//        public void setContent(byte[] content) {
//            this.content = content;
//        }
//
//        public boolean isContentBytes() {
//            return isContentBytes;
//        }

    }

}
