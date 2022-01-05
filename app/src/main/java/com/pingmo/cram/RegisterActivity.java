package com.pingmo.cram;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private myDBHelper myDb;
    private SQLiteDatabase sqlDB;

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
                String userName = editUser.getText().toString();
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
                    /*
                    RegisterRequest rR = new RegisterRequest(getString(R.string.SERVERIP), 8856, sb.toString());
                    rR.start();

                    Toast.makeText(getApplicationContext(), ""+rR.isCompleted(), Toast.LENGTH_SHORT).show();
                    if(rR.isCompleted().equals("1")) {
                        myDb = new myDBHelper(getApplicationContext());
                        sqlDB = myDb.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO userTB (userName) VALUES ('" + userName + "');");
                        sqlDB.close();
                        finish();
                    }

                     */
                    myDb = new myDBHelper(getApplicationContext());
                    sqlDB = myDb.getWritableDatabase();
                    sqlDB.execSQL("INSERT INTO userTB (userName) VALUES ('" + userName + "');");
                    sqlDB.close();
                    finish();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}