package com.pingmo.cram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoticeInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeinfo);
        Intent noticeInfoIntent = getIntent();
        Button btnNoticeExit2 = findViewById(R.id.btnNoticeExit2);

        TextView txtNoticeTitle2 = findViewById(R.id.txtNoticeTitle2);
        TextView txtNoticeBody = findViewById(R.id.txtNoticeBody);
        TextView txtNoticeDate2 = findViewById(R.id.txtNoticeDate2);

        txtNoticeTitle2.setText(noticeInfoIntent.getStringExtra("title"));
        txtNoticeBody.setText(noticeInfoIntent.getStringExtra("body"));
        txtNoticeDate2.setText(noticeInfoIntent.getStringExtra("date"));
        btnNoticeExit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
