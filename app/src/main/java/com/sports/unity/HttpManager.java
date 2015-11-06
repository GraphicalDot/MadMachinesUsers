package com.sports.unity;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by madmachines on 4/11/15.
 */
public class HttpManager {

    private static HttpManager httpManager;

    private HttpManager() {

    }

    synchronized public static HttpManager getInstance() {
        if (httpManager == null) {
            httpManager = new HttpManager();
            return httpManager;
        } else {
            return httpManager;
        }
    }


    public class PostImageRequest extends Thread {

        private byte[] userimage;
        File imagefile;

        public PostImageRequest(byte[] image, File imageFile) {
            this.userimage = image;
            this.imagefile = imageFile;
        }

        @Override
        public void run() {
            super.run();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://54.169.217.88/media");

            httppost.setEntity(new FileEntity(new File(String.valueOf(imagefile)), "hello"));

            try {
                HttpResponse response = httpClient.execute(httppost);
                Log.i("httppoststatus", String.valueOf(response.getStatusLine()));
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    public void HttpPostImageRequest(byte[] image, File imageFile) {
        new PostImageRequest(image, imageFile).start();
    }

    public void HttpGetImageRequest(MessageDigest digest) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://54.169.217.88:5222/media");
    }
}
