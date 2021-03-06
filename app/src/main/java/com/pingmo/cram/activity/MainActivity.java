package com.pingmo.cram.activity;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.pingmo.cram.Cram;
import com.pingmo.cram.MyDBHelper;
import com.pingmo.cram.R;
import com.pingmo.cram.User;
import com.pingmo.cram.fragment.ProfileFragment;
import com.pingmo.cram.fragment.SettingFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private final String TAG = "mainTag";

    public MyDBHelper myDb;
    public SQLiteDatabase sqlDb;

    ProfileFragment profileFr = new ProfileFragment();
    SettingFragment settingFr = new SettingFragment();

    public User curUser = new User();
    
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

        // ?????? ??????

        curUser = cram.getUser(this);

        //?????? ??? ?????? ???????????? ?????????
        mainHandler = new Handler(msg -> {
            try {
                JSONObject receiveData = new JSONObject(msg.obj.toString());
                if(msg.what == 103) {
                    boolean isUser = Integer.parseInt(receiveData.getString("isUser")) > 0;
                    if(isUser) {
                        String userID = receiveData.getString("userID");
                        String userName = receiveData.getString("userName");
                        int cash = receiveData.getInt("cash");
                        int rank = receiveData.getInt("rank");
                        sqlDb = myDb.getWritableDatabase();
                        sqlDb.execSQL("UPDATE userTB SET "
                                + "userID = '" + userID
                                + "', userName = '" + userName
                                + "', cash = " + cash
                                + ", rank = " + rank
                                + " WHERE userID = '" + curUser.getId()
                                + "';");
                        sqlDb.close();
                    }

                }
                if(msg.what == 102) {
                    int isDeleted = Integer.parseInt(receiveData.getString("isDeleted"));
                    if(isDeleted == 1) {
                        signOut();                        
                    }else{
                        Toast.makeText(getApplicationContext(), "?????? ????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        //?????? ?????????
        imbSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.viewSetting, settingFr).commit();
                drawLay.openDrawer(viewSetting);
            }
        });

        //????????? ?????????

        imgUser.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.viewProf, profileFr).commit();
            drawLay.openDrawer(viewProf);
        });

        btnStart.setOnClickListener(v -> {
            if(cram.isUser(this)) {
                Intent roomIntent = new Intent(getApplicationContext(), RoomActivity.class);
                roomIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(roomIntent);
            }else {
                Toast.makeText(getApplicationContext(), "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

        // ?????? ????????? ?????? ????????????
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cram.isConnected()) {
            cram.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ?????? ????????? ??????
        readThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                    if(cram.isConnected()) {
                        cram.setHandler(mainHandler);

                        sqlDb = myDb.getReadableDatabase();
                        Cursor cur = sqlDb.rawQuery("SELECT * FROM userTB", null);
                        if(cur.getCount() > 0){
                            cur.moveToFirst();
                            int a = cur.getColumnIndex("userID");
                            try {
                                JSONObject sendData = new JSONObject();
                                sendData.put("what", 103);
                                sendData.put("userID", cur.getString(a));
                                cram.send(sendData.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        cur.close();
                        sqlDb.close();
                        break;
                    }
                }catch (Exception ignored){
                }
            }
        });
        readThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        profileFr.setProfile();
        profileFr.setLogOut();
        settingFr.setQuit();
    }

    public void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> Toast.makeText(getApplicationContext(), "?????? ?????? ???????????????.", Toast.LENGTH_LONG).show());

        sqlDb = myDb.getWritableDatabase();
        myDb.truncateTB(sqlDb);
        sqlDb.close();
        onRestart();
    }

    public void revokeAccess() {
        mAuth.signOut();

        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                task -> Toast.makeText(getApplicationContext(), "?????? ?????? ???????????????.", Toast.LENGTH_LONG).show());
    }

    //?????? ????????? ?????? ?????? ????????? ???
    public void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");
                }
            });
            if(cram.isConnected()) {
                try {
                    JSONObject sendData = new JSONObject();
                    sendData.put("what", 102);
                    sendData.put("userID", user.getUid());
                    sendData.put("userName", curUser.getName());
                    cram.send(sendData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(), "????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
