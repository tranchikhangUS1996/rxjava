package com.example.lap60020_local.rxjavademo;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class MyAdapter extends ArrayAdapter<MyType>{

    private ArrayList<MyType> MyLinks;
    private Context context;
    public MyAdapter(Context context) {
        super(context,R.layout.list_layout);
        this.context = context;
        MyLinks = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return MyLinks.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = MyLinks.get(position).card;
        ProgressBar progressBar = MyLinks.get(position).progressBar;
        ImageButton imageButton = null;
        if(convertView==null) {
            // tao lan dau
            convertView = layoutInflater.inflate(R.layout.list_layout, parent, false);
            // luu View lai
            MyLinks.get(position).card = convertView;
            progressBar = convertView.findViewById(R.id.progressbar);
            progressBar.setMax(100);
            MyLinks.get(position).progressBar = progressBar;
            TextView tv = convertView.findViewById(R.id.Name);
            tv.setText(MyLinks.get(position).URL);
            imageButton = convertView.findViewById(R.id.cancel);
        }
        //ImageButton imageButton = convertView.findViewById(R.id.cancel);
        Disposable disposable;
        if(!MyLinks.get(position).isLoaded) {
            MyLinks.get(position).isLoaded = true;
            disposable = taixuong(MyLinks.get(position).URL,progressBar);
            if(imageButton!=null) imageButton.setOnClickListener(new MyOnClickListener(disposable,progressBar));
        };
        return convertView;
    }

    private Disposable taixuong(String url, ProgressBar progressBar) {
        Disposable disposable = null;
        disposable = Observable.create(new MyEmitter(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyConsumer(progressBar));
        return disposable;
    }

    private class MyOnClickListener implements View.OnClickListener {
        ProgressBar progressBar;
        Disposable disposable;
        public MyOnClickListener(Disposable disposable ,ProgressBar progressBar) {
            this.progressBar = progressBar;
            this.disposable = disposable;
        }

        @Override
        public void onClick(View v) {
            disposable.dispose();
        }
    }

    public void load(ArrayList<String> URLs) {
        for(String s:URLs) {
            MyType myType = new MyType(s,false,null,null);
            MyLinks.add(myType);
        }
        this.notifyDataSetChanged();
//        Flowable.fromIterable(URLs).map(s->{
//            MyType myType = new MyType(s,false);
//            return myType;
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(data->{
//                    MyLinks.add(data);
//                    this.notifyDataSetChanged();
//                });
    }

    private String download(String url, ProgressBar progressBar) {
        HttpURLConnection connection;
        OutputStream outputStream;
        InputStream inputStream;
        try {
            URL downloadURL = new URL(url);
            connection = (HttpURLConnection) downloadURL.openConnection();
            connection.connect();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                return connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();
            String[] fileNames = url.split("/");
            String fileName = fileNames[fileNames.length-1];
            File path = Environment.getExternalStoragePublicDirectory("Downloads");
            File f = new File(path,fileName);
            outputStream = new FileOutputStream(f);
            inputStream = connection.getInputStream();

            byte[] data = new byte[4096];
            int count = 0;
            while((count  = inputStream.read(data))!=-1) {
                outputStream.write(data,0,count);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
