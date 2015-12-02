package com.sports.unity.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by amandeep on 26/11/15.
 */
public class FileOnCloudHandler {

    private static final String CONTENT_CLOUD_URL = "http://54.169.217.88/media?";

    private static final int TIME_OUT = 20000;

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

            Log.i("File on cloud" , "uploaded with checksum " + checksum);
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

//    public static void uploadContent(byte[] content){
//        HttpURLConnection connection = null;
//
//        ByteArrayOutputStream byteArrayOutputStream = null;
//        try {
//            String checksum = CommonUtil.getMD5EncryptedString(content);
//
//            URL url = new URL(CONTENT_CLOUD_URL);
//
//            // Open a HTTP  connection to  the URL
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true); // Allow Inputs
//            connection.setDoOutput(true); // Allow Outputs
//            connection.setUseCaches(false); // Don't use a Cached Copy
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Connection", "Keep-Alive");
//            connection.setRequestProperty("Content-Type", "binary/octet-stream");
//            connection.setConnectTimeout(30000);
//            connection.setReadTimeout(30000);
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("filename", checksum));
//
//            OutputStream os = connection.getOutputStream();
//            os.write(getQuery(params).getBytes());
//            os.write( "\n".getBytes());
//            os.write(content);
//
//            os.flush();
//            os.close();
//
//            String response = null;
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                byte[] chunk = new byte[1024];
//                int read = 0;
//
//                InputStream in = connection.getInputStream();
//                byteArrayOutputStream = new ByteArrayOutputStream();
//
//                while( (read = in.read(chunk)) != -1 ) {
//                    byteArrayOutputStream.write( chunk, 0, read);
//                }
//
//                response = String.valueOf(byteArrayOutputStream.toByteArray());
//            } else {
//                response = "";
//            }
//
//            Log.i("Content upload on cloud" , response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if( byteArrayOutputStream != null ){
//                try {
//                    byteArrayOutputStream.close();
//                }catch (Exception ex){}
//            }
//
//        }
//    }
//
//    private void uploadVideo(byte[] content) throws ParseException, IOException {
//        String checksum = CommonUtil.getMD5EncryptedString(content);
//
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost(CONTENT_CLOUD_URL);
//
//        ByteArrayBody fileContentBody = new ByteArrayBody( content, checksum);
//        StringBody title = new StringBody(checksum);
//        StringBody description = new StringBody("This is a description of the content");
//
//        MultipartEntity reqEntity = new MultipartEntity();
//        reqEntity.addPart("content", fileContentBody);
//        reqEntity.addPart("title", title);
//        reqEntity.addPart("description", description);
//        httppost.setEntity(reqEntity);
//
//        // DEBUG
//        System.out.println( "executing request " + httppost.getRequestLine( ) );
//        HttpResponse response = httpclient.execute( httppost );
//        HttpEntity resEntity = response.getEntity( );
//
//        // DEBUG
//        System.out.println( response.getStatusLine( ) );
//        if (resEntity != null) {
//            System.out.println( EntityUtils.toString(resEntity) );
//        } // end if
//
//        if (resEntity != null) {
//            resEntity.consumeContent( );
//        } // end if
//
//        httpclient.getConnectionManager( ).shutdown( );
//    }

//    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        for (NameValuePair pair : params)
//        {
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
//        }
//
//        return result.toString();
//    }

    public static byte[] downloadContent(String fileName){
        Log.i("File on cloud" , "downloading");

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(CONTENT_CLOUD_URL);

        byte[] data = null;
        try {
            BasicHttpParams params = new BasicHttpParams();
            params.setParameter("name", fileName);
            get.setParams(params);

            HttpResponse resp = client.execute(get);

            InputStream is = resp.getEntity().getContent();
            int contentSize = (int) resp.getEntity().getContentLength();
            BufferedInputStream bis = new BufferedInputStream(is, 512);

            data = new byte[contentSize];
            int bytesRead = 0;
            int offset = 0;

            while (bytesRead != -1 && offset < contentSize) {
                bytesRead = bis.read(data, offset, contentSize - offset);
                offset += bytesRead;
            }

            Log.i("File on cloud" , "downloaded");
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return data;
    }

}
