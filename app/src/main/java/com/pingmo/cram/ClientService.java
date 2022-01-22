package com.pingmo.cram;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class ClientService extends Service {

    boolean flagConnection = true;
    boolean isConnected = false;
    boolean flagRead = true;

    public Handler writeHandler;

    Socket socket;
    BufferedInputStream bin;
    BufferedOutputStream bout;

    SocketThread st;
    ReadThread rt;
    WriteThread wt;

    public Handler receiveHandler;

    final String IP = "121.132.133.85";
    final int PORT = 8856;

    IBinder mBinder = new MyBinder();

    class MyBinder extends Binder {
        ClientService getService() { // 서비스 객체를 리턴
            return ClientService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        st = new SocketThread();
        st.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void disconnect () {
        flagConnection = false;
        isConnected = false;

        if (socket != null) {
            flagRead = false;
            writeHandler.getLooper().quit();
            try {
                bout.close();
                bin.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send (String s) {
        Message msg = new Message();
        msg.obj = s;
        writeHandler.sendMessage(msg);
    }

    public Boolean isConnected() {
        return isConnected;
    }

    public void setHandler(Handler handler) {
        receiveHandler = handler;
    }

    class SocketThread extends Thread {

        public void run() {
            while (flagConnection){
                try {
                    if(!isConnected) {
                        socket = new Socket();
                        SocketAddress remoteAddr = new InetSocketAddress(IP, PORT);
                        socket.connect(remoteAddr, 10000);
                        bout = new BufferedOutputStream(socket.getOutputStream());
                        bin = new BufferedInputStream(socket.getInputStream());
                        if (rt != null) {
                            flagRead = false;
                        }
                        if (wt != null) {
                            writeHandler.getLooper().quit();
                        }
                        wt = new WriteThread();
                        wt.start();

                        rt = new ReadThread();
                        rt.start();
                        isConnected = true;

                    }else {
                        SystemClock.sleep(10000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    SystemClock.sleep(10000);
                }
            }

        }
    }


    class WriteThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            writeHandler = new Handler(msg -> {
                try {
                    bout.write(((String)msg.obj).getBytes());
                    bout.flush();
                }catch (Exception e){
                    e.printStackTrace();
                    isConnected = false;
                    writeHandler.getLooper().quit();
                    try{
                        flagRead = false;
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                return true;
            });
            Looper.loop();
        }
    }

    class ReadThread extends Thread {

        @Override
        public void run() {

            byte[] buffer;
            while (flagRead){
                buffer = new byte[1024];
                try {
                    String message;
                    int size = bin.read(buffer);
                    if(size > 0){
                        message = new String(buffer, 0, size, StandardCharsets.UTF_8);
                        if(!message.equals("")){
                            Message msg = new Message();
                            msg.obj = message;
                            receiveHandler.sendMessage(msg);
                        }
                    }else {
                        flagRead = false;
                        isConnected = false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    flagRead = false;
                    isConnected = false;
                }
            }
        }
    }

}
