package com.pingmo.cram.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pingmo.cram.Cram;
import com.pingmo.cram.MyDBHelper;
import com.pingmo.cram.R;

public class RegisterActivity extends AppCompatActivity {

    private MyDBHelper myDb;
    private SQLiteDatabase sqlDB;

    Handler registerHandler;
    Cram cram = Cram.getInstance();
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        EditText editUser = (EditText) findViewById(R.id.editUser);

        Intent registerIntent = getIntent();
        String UID = registerIntent.getStringExtra("UID");
        String [] filter = getResources().getStringArray(R.array.filter);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                userName = editUser.getText().toString();
                Boolean confirm = true;

                for(int i = 0; i < filter.length; i++) {
                    if(userName.contains(filter[i])){
                        Toast.makeText(getApplicationContext(), "건전한 닉네임을 생각해 주세요..", Toast.LENGTH_SHORT).show();
                        editUser.requestFocus();
                        confirm = false;
                    }
                }

                // 회원 가입
                if(userName.equals("")){
                    Toast.makeText(getApplicationContext(), "닉네임을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    editUser.requestFocus();
                    confirm = false;

                }

                if(confirm){
                    // 서버로 유저 이름 보내기
                    if(cram.isConnected()) {
                        cram.send(userName);
                    }else {
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerHandler = new Handler(msg -> {
            // isValidate?
            if(true) {
                myDb = new MyDBHelper(getApplicationContext());
                sqlDB = myDb.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO userTB (userName) VALUES ('" + userName + "');");
                sqlDB.close();
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "이미 사용 중인 이름입니다.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        cram.setHandler(registerHandler);
    }

}