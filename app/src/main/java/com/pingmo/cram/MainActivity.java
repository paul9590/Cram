package com.pingmo.cram;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private Dialog setDialog;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String TAG = "mainTag";

    private myDBHelper myDb;
    private SQLiteDatabase sqlDB;

    private String [] userInfo = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = (Button) findViewById(R.id.btnRoom);
        Button btnShop = (Button) findViewById(R.id.btnShop);
        Button btnSetting = (Button) findViewById(R.id.btnSetting);
        ImageView imgUser = (ImageView) findViewById(R.id.imgUser);
        imgUser.setImageResource(R.drawable.userimg);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roomIntent = new Intent(getApplicationContext(), RoomActivity.class);
                roomIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(roomIntent);
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

        CheckLogin();

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

    }

    @Override
    public void onRestart() {
        super.onRestart();
        CheckLogin();
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
    private void CheckLogin(){
        myDb = new myDBHelper(this);
        sqlDB = myDb.getReadableDatabase();
        Cursor cur = sqlDB.rawQuery("SELECT * FROM userTB", null);

        if(cur.getCount() > 0){
            cur.moveToFirst();
            userInfo[0] = cur.getString(cur.getColumnIndexOrThrow("userName"));
            cur.close();
            sqlDB.close();

        } else {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(loginIntent);
        }
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
