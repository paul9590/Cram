package com.pingmo.cram.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pingmo.cram.Cram;
import com.pingmo.cram.MyDBHelper;
import com.pingmo.cram.R;

import org.json.JSONException;
import org.json.JSONObject;

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
        String userID = registerIntent.getStringExtra("userID");
        myDb = new MyDBHelper(getApplicationContext());

        String [] filter = getResources().getStringArray(R.array.filter);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        try {
                            JSONObject sendData = new JSONObject();
                            sendData.put("what", 100);
                            sendData.put("userID", userID);
                            sendData.put("userName", userName);
                            cram.send(sendData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerHandler = new Handler(msg -> {
            try {
                JSONObject receiveData = new JSONObject(msg.obj.toString());
                if(msg.what == 100) {
                    int isValidate = Integer.parseInt(receiveData.getString("isValidate"));
                    if(isValidate == 1) {
                        sqlDB = myDb.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO userTB (userID, userName, cash, rank) VALUES"
                                + " ('"
                                + userID + "', '"
                                + userName + "', "
                                + 0 + ", "
                                + 0 + ");");
                        sqlDB.close();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "이미 사용 중인 이름입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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