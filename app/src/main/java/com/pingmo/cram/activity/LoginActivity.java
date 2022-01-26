package com.pingmo.cram.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.pingmo.cram.Cram;
import com.pingmo.cram.MyDBHelper;
import com.pingmo.cram.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private SignInButton btnLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private final String TAG = "mainTag";
    private FirebaseAuth mAuth;
    private final int RC_SIGN_IN = 123;

    public MyDBHelper myDb;
    public SQLiteDatabase sqlDB;

    Handler loginHandler;
    Cram cram = Cram.getInstance();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        myDb = new MyDBHelper(this);

        //개인정보 및 이용 약관
        TextView txtLink = (TextView) findViewById(R.id.txtLink);

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };

        Pattern link1 = Pattern.compile("개인 정보 처리 방침");
        Pattern link2 = Pattern.compile("이용 약관");
        Linkify.addLinks(txtLink, link1, "http://pingmo.co.kr/user.html", null, mTransform);
        Linkify.addLinks(txtLink, link2, "http://pingmo.co.kr/service.html", null, mTransform);


        btnLogin = findViewById(R.id.btnLogin);

        if(cram.isUser(this)){
            finish();
        }

        // 구글 로그인 정보 불러오기
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cram.isConnected()) {
                    signIn();
                }else{
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginHandler = new Handler(msg -> {
            // 아이디 확인 되면 메인 액티비티로
            try {
                JSONObject receiveData = new JSONObject(msg.obj.toString());
                if(msg.what == 101) {
                    int isUser = Integer.parseInt(receiveData.getString("isUser"));
                    if(isUser == 1) {
                        String userID = receiveData.getString("userID");
                        String userName = receiveData.getString("userName");
                        int cash = receiveData.getInt("cash");
                        int rank = receiveData.getInt("rank");
                        sqlDB = myDb.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO userTB (userID, userName, cash, rank) VALUES"
                                + " ('"
                                + userID + "', '"
                                + userName + "', "
                                + cash + ", "
                                + rank + ");");
                        sqlDB.close();
                        finish();
                    }else{
                        Intent RegisterIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                        RegisterIntent.putExtra("userID", user.getUid());
                        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(RegisterIntent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        cram.setHandler(loginHandler);
    }
    
    @Override
    public void onRestart() {
        super.onRestart();
        if(cram.isUser(this)){
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "구글 로그인에 실패 했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            // 만약 회원 가입에 성공한다면 RegisterActivity 띄우기
                            // 서버측에 유저 정보 확인 해야댐
                            mAuth = FirebaseAuth.getInstance();
                            user = mAuth.getCurrentUser();
                            try {
                                JSONObject sendData = new JSONObject();
                                sendData.put("what", 101);
                                sendData.put("userID", user.getUid());
                                cram.send(sendData.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}