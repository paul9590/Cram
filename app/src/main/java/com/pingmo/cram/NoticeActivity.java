package com.pingmo.cram;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NoticeActivity extends AppCompatActivity {

    TextView tmp;
    TextView tmp2;
    Handler noticeHandler;
    final int TITLE = 1;
    final int BODY = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        tmp = findViewById(R.id.textView);
        tmp2 = findViewById(R.id.textView2);

        noticeHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(msg.what == TITLE) {
                    tmp.setText(msg.obj.toString());
                }else {
                    tmp2.setText(msg.obj.toString());
                }
                return true;
            }
        });
        new Thread() {
            @Override
            public void run() {
                super.run();
                try{
                    URL url = new URL("http://pingmo.co.kr/notice.txt");
                    InputStream is = url.openStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String s;
                    StringBuilder sb = new StringBuilder();
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                    }
                    JSONObject root = new JSONObject(sb.toString());
                    JSONArray arr = root.getJSONArray("Board");


                    for(int i = 0; i < arr.length(); i++) {
                        JSONObject tmp = (JSONObject) arr.get(i);

                        Message msgTitle = new Message();
                        msgTitle.what = TITLE;
                        msgTitle.obj = tmp.getString("title");
                        noticeHandler.sendMessage(msgTitle);

                        Message msgBody = new Message();
                        msgBody.what = BODY;
                        msgBody.obj = tmp.getString("body");
                        noticeHandler.sendMessage(msgBody);
                    }

                }catch (Exception e){
                    Log.e("에러임", e.toString());
                }
            }
        }.start();

    }
}
