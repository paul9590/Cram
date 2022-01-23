package com.pingmo.cram.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.cram.R;
import com.pingmo.cram.adapter.RecyclerNoticeAdapter;
import com.pingmo.cram.list.RecyclerNoticeList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NoticeActivity extends AppCompatActivity {

    Handler noticeHandler;
    RecyclerView viewNotice = null;
    RecyclerNoticeAdapter mAdapter = null;
    ArrayList<RecyclerNoticeList> mList;
    final int NOTICE = 1;

    class Notice{
        String title;
        String body;
        String date;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        Button btnNoticeExit = findViewById(R.id.btnNoticeExit);
        viewNotice = findViewById(R.id.viewNotice);

        mList = new ArrayList<>();
        mAdapter = new RecyclerNoticeAdapter(mList);
        viewNotice.setAdapter(mAdapter);
        viewNotice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        viewNotice.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

        btnNoticeExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        noticeHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(msg.what == NOTICE) {
                    RecyclerNoticeList item = new RecyclerNoticeList();
                    Notice notice = (Notice) msg.obj;
                    item.setTitle(notice.title);
                    item.setBody(notice.body);
                    item.setDate(notice.date);
                    mList.add(item);
                    mAdapter.notifyDataSetChanged();
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


                    for(int i = arr.length() - 1; i >= 0; i--) {
                        JSONObject tmp = (JSONObject) arr.get(i);

                        Message msg = new Message();
                        msg.what = NOTICE;
                        Notice notice = new Notice();
                        notice.title = tmp.getString("title");
                        notice.body = tmp.getString("body");
                        notice.date = tmp.getString("date");
                        msg.obj = notice;
                        noticeHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    Log.e("에러임", e.toString());
                }
            }
        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}
