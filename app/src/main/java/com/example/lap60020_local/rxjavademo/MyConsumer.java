package com.example.lap60020_local.rxjavademo;

import android.widget.ProgressBar;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

class MyConsumer implements Consumer<Integer> {
    ProgressBar progressBar;
    public MyConsumer(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void accept(Integer integer) throws Exception {
        progressBar.setProgress(integer);
    }
}
