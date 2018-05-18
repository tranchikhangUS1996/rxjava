package com.example.lap60020_local.rxjavademo;

import android.view.View;
import android.widget.ProgressBar;

class MyType {
    public String URL;
    public boolean isLoaded;
    public View card;
    public ProgressBar progressBar;
    MyType(String URL,Boolean isLoaded,View card,ProgressBar progressBar) {
        this.URL = URL;
        this.isLoaded = isLoaded;
        this.card = card;
        this.progressBar = progressBar;
    }
}
