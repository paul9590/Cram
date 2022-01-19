package com.pingmo.cram;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Cram extends Application {

    private static final Cram instance = new Cram();
    public ClientService service;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClientService.MyBinder mb = (ClientService.MyBinder) service;
            instance.service = mb.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("SocketManager", "onServiceDisconnected()");
        }
    };

    public Cram() {
        Log.i("SocketManager", "SocketManager()");
    }

    public static Cram getInstance() {
        return instance;
    }

    public void send(String s) {
        service.send(s);
    }

    public void disconnect() {
        service.disconnect();
    }

    public void setHandler(Handler handler) {
        service.setHandler(handler);
    }

    public boolean isConnected() {
        return service.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();
        Intent intent = new Intent(context, ClientService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
}
    