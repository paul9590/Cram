package com.pingmo.cram;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Dialog setDialog;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String TAG = "mainTag";

    private MyDBHelper myDb;
    private SQLiteDatabase sqlDB;

    ProfileFragment profileFragment;
    String userName = "로그인을 해주세요.";
    String userInfo = "점수 : 0\n캐시 : 0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new MyDBHelper(this);

        Button btnStart = (Button) findViewById(R.id.btnRoom);
        Button btnShop = (Button) findViewById(R.id.btnShop);
        Button btnSetting = (Button) findViewById(R.id.btnSetting);
        ImageView imgUser = (ImageView) findViewById(R.id.imgUser);
        imgUser.setImageResource(R.drawable.userimg);

        DrawerLayout drawLay = (DrawerLayout) findViewById(R.id.drawLay);
        View drawView = (View) findViewById(R.id.viewProf);

        profileFragment = new ProfileFragment();

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawLay.openDrawer(drawView);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewProf, profileFragment).commit();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLogin()) {
                    Intent roomIntent = new Intent(getApplicationContext(), RoomActivity.class);
                    roomIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(roomIntent);
                }else {
                    Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(loginIntent);
                }
            }
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shopIntent = new Intent(getApplicationContext(), ShopActivity.class);
                shopIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(shopIntent);
            }
        });

        // 구글 로그인 정보 불러오기
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog = new Dialog(MainActivity.this);
                setDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                setDialog.setContentView(R.layout.dial_setting);
                setDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                SetDial();
            }
        });

        sqlDB = myDb.getReadableDatabase();
        Cursor cur = sqlDB.rawQuery("SELECT * FROM userTB", null);
        if(cur.getCount() > 0){
            cur.moveToFirst();
            int n = cur.getColumnIndex("userName");
            userName = cur.getString(n);
            StringBuilder sb = new StringBuilder();
            sb.append("점수 : ");
            sb.append(1000);
            sb.append("\n캐시 : ");
            sb.append(1000);
            userInfo = sb.toString();
        }
        cur.close();
        sqlDB.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sqlDB = myDb.getReadableDatabase();
        userName = "로그인을 해주세요.";
        userInfo = "점수 : 0\n캐시 : 0";
        Cursor cur = sqlDB.rawQuery("SELECT * FROM userTB", null);
        if(cur.getCount() > 0){
            cur.moveToFirst();
            int n = cur.getColumnIndex("userName");
            userName = cur.getString(n);
            StringBuilder sb = new StringBuilder();
            sb.append("점수 : ");
            sb.append(1000);
            sb.append("\n캐시 : ");
            sb.append(1000);
            userInfo = sb.toString();
        }
        cur.close();
        sqlDB.close();
        profileFragment = new ProfileFragment();
    }

    private void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "로그 아웃 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void revokeAccess() {
        mAuth.signOut();

        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "로그 아웃 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void DeleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User account deleted.");
                    }
                }
            });
            signOut();
        }
    }

    // 초기 로그인 정보 확인
    private boolean checkLogin(){
        myDb = new MyDBHelper(this);
        sqlDB = myDb.getReadableDatabase();
        Cursor cur = sqlDB.rawQuery("SELECT * FROM userTB", null);

        if(cur.getCount() == 0){
            cur.close();
            sqlDB.close();
            return false;
        }
        cur.close();
        sqlDB.close();
        return true;
    }

    private void SetDial(){
        setDialog.show();

        Button btnChange = (Button) setDialog.findViewById(R.id.btnChange);
        Button btnOut = (Button) setDialog.findViewById(R.id.btnOut);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                setDialog.dismiss();
                sqlDB = myDb.getWritableDatabase();
                sqlDB.execSQL("DROP TABLE IF EXISTS userTB");
                sqlDB.execSQL("CREATE TABLE userTB (userID VARCHAR(10) PRIMARY KEY, userName VARCHAR(20), friend VARCHAR(20), rank Integer);");
                sqlDB.close();
                onRestart();
            }
        });

        // 서버 측에도 삭제 요청 넣어야댐
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteUser();
                setDialog.dismiss();
                sqlDB = myDb.getWritableDatabase();
                sqlDB.execSQL("DROP TABLE IF EXISTS userTB");
                sqlDB.execSQL("CREATE TABLE userTB (userID VARCHAR(10) PRIMARY KEY, userName VARCHAR(20), friend VARCHAR(20), rank Integer);");
                sqlDB.close();
                onRestart();
            }
        });
    }
}
