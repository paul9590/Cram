package com.pingmo.cram;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private final String TAG = "mainTag";

    public MyDBHelper myDb;
    public SQLiteDatabase sqlDB;

    ProfileFragment profileFragment;
    SettingFragment settingFragment;
    
    String userName = "로그인을 해주세요.";
    String userInfo = "점수 : 0\n캐시 : 0";
    
    Cram cram = Cram.getInstance();

    Thread readThread;
    Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new MyDBHelper(this);

        Button btnStart = (Button) findViewById(R.id.btnRoom);
        Button btnShop = (Button) findViewById(R.id.btnShop);
        ImageView imgUser = (ImageView) findViewById(R.id.imgUser);
        ImageButton imbSetting = (ImageButton) findViewById(R.id.imbSetting);

        imgUser.setImageResource(R.drawable.userimg);

        DrawerLayout drawLay = (DrawerLayout) findViewById(R.id.drawLay);
        ConstraintLayout viewProf = (ConstraintLayout) findViewById(R.id.viewProf);
        ConstraintLayout viewSetting = (ConstraintLayout) findViewById(R.id.viewSetting);

        profileFragment = new ProfileFragment();
        settingFragment = new SettingFragment();

        //서버 측 코드 받아오는 핸들러
        mainHandler = new Handler(msg -> {

            return true;
        });

        // 초기 핸들러 설정
        readThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                    if(cram.isConnected()) {
                        cram.setHandler(mainHandler);
                        break;
                    }
                }catch (Exception ignored){
                }
            }
        });
        readThread.start();

        //왼쪽 햄버거
        imbSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawLay.openDrawer(viewSetting);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewSetting, settingFragment).commit();
            }
        });

        //오른쪽 프로필
        imgUser.setOnClickListener(v -> {
            drawLay.openDrawer(viewProf);
            getSupportFragmentManager().beginTransaction().replace(R.id.viewProf, profileFragment).commit();
        });

        btnStart.setOnClickListener(v -> {
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
        });

        btnShop.setOnClickListener(v -> {
            Intent shopIntent = new Intent(getApplicationContext(), ShopActivity.class);
            shopIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(shopIntent);
        });

        // 구글 로그인 정보 불러오기
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

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
    protected void onDestroy() {
        super.onDestroy();
        cram.disconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cram.setHandler(mainHandler);
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

    public void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> Toast.makeText(getApplicationContext(), "로그 아웃 되었습니다.", Toast.LENGTH_LONG).show());

        sqlDB = myDb.getWritableDatabase();
        myDb.truncateTB(sqlDB);
        sqlDB.close();
        onRestart();
    }

    public void revokeAccess() {
        mAuth.signOut();

        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                task -> Toast.makeText(getApplicationContext(), "로그 아웃 되었습니다.", Toast.LENGTH_LONG).show());
    }

    //서버 측에도 삭제 요청 넣어야 함
    public void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");
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
}
