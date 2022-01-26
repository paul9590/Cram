package com.pingmo.cram;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public boolean isUser(Context context){
        MyDBHelper myDb = new MyDBHelper(context);
        SQLiteDatabase sqlDb;
        sqlDb = myDb.getReadableDatabase();
        Cursor cur = sqlDb.rawQuery("SELECT * FROM userTB", null);
        boolean check = cur.getCount() > 0;
        cur.close();
        sqlDb.close();
        return check;
    }

    public User getUser(Context context) {
        MyDBHelper myDb = new MyDBHelper(context);
        SQLiteDatabase sqlDb;
        sqlDb = myDb.getReadableDatabase();
        User curUser = new User();
        Cursor cur = sqlDb.rawQuery("SELECT * FROM userTB", null);
        if(cur.getCount() > 0){
            cur.moveToFirst();
            int [] arr = {
                    cur.getColumnIndex("userName"),
                    cur.getColumnIndex("cash"),
                    cur.getColumnIndex("rank")
            };
            curUser.setName(cur.getString(arr[0]));
            curUser.setCash(cur.getInt(arr[1]));
            curUser.setRank(cur.getInt(arr[2]));
        }else {
            curUser.setName("로그인을 해주세요.");
            curUser.setRank(0);
            curUser.setCash(0);
        }
        cur.close();
        sqlDb.close();

        return curUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        Intent intent = new Intent(context, ClientService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

}
    