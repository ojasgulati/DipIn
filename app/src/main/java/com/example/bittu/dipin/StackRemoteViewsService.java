package com.example.bittu.dipin;


import android.content.Intent;
import android.widget.RemoteViewsService;

public class StackRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext());
    }
}

