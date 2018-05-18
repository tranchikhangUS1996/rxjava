package com.example.lap60020_local.rxjavademo;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

class MyEmitter implements ObservableOnSubscribe<Integer> {
    String url;
    public MyEmitter(String url)
    {
        this.url = url;
    }
    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL downloadURL = new URL(url);
            connection = (HttpURLConnection) downloadURL.openConnection();
            connection.connect();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                emitter.onComplete();
                return;
            }
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            String[] fileNames = url.split("/");
            String fileName = fileNames[fileNames.length-1];
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File f = new File(path,fileName);
            output = new FileOutputStream(f);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                  if(emitter.isDisposed()) {
                      input.close();
                      return;
                  }
                  long pretotal = total;
                  int pre_percent = (int) (((float) pretotal/fileLength) *100);
                  total += count;
                  int cur_percent = (int) (((float) total/fileLength)*100);
                if(pre_percent<cur_percent) {
                    emitter.onNext(cur_percent);
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            emitter.onComplete();
        } finally {
            try {
                    output.close();
                    input.close();
            } catch (IOException ignored) {

            }
            if(connection!=null) {
                connection.disconnect();
            }
        }
    }
}