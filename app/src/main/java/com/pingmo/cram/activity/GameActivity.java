package com.pingmo.cram.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pingmo.cram.MyDBHelper;
import com.pingmo.cram.adapter.RecyclerNoticeAdapter;
import com.pingmo.cram.fragment.ChatFragment;
import com.pingmo.cram.fragment.DeckFragment;
import com.pingmo.cram.R;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    public MyDBHelper myDb;
    public SQLiteDatabase sqlDb;
    public String userName;

    boolean onGaming = false;
    boolean isHost = true;

    Dialog gamePlayerDialog;
    String playerName [];
    ImageView imgPlayer [];
    TextView txtPlayer [];

    int playerImg [];

    Thread rollThread;
    boolean isRolling = true;
    Handler rollHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        myDb = new MyDBHelper(this);
        sqlDb = myDb.getReadableDatabase();
        Cursor cur = sqlDb.rawQuery("SELECT * FROM userTB", null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int n = cur.getColumnIndex("userName");
            userName = cur.getString(n);
        }

        Intent gameIntent = getIntent();
        String roomName = gameIntent.getStringExtra("roomName");

        TextView txtGameTitle = findViewById(R.id.txtGameTitle);

        ViewPager pager = findViewById(R.id.pagerGame);
        TabLayout tabLayout = findViewById(R.id.tabGame);

        txtPlayer = new TextView[] {
                findViewById(R.id.txtPlayer1),
                findViewById(R.id.txtPlayer2),
                findViewById(R.id.txtPlayer3),
                findViewById(R.id.txtPlayer4),
                findViewById(R.id.txtPlayer5),
                findViewById(R.id.txtPlayer6),
                findViewById(R.id.txtPlayer7),
                findViewById(R.id.txtPlayer8),
        };

        imgPlayer = new ImageView[]{
                findViewById(R.id.imgPlayer1),
                findViewById(R.id.imgPlayer2),
                findViewById(R.id.imgPlayer3),
                findViewById(R.id.imgPlayer4),
                findViewById(R.id.imgPlayer5),
                findViewById(R.id.imgPlayer6),
                findViewById(R.id.imgPlayer7),
                findViewById(R.id.imgPlayer8),
        };


        playerName = new String[]{"paul", "pool", "poly", "holy",
                "ppap", "poul", "p", ""};

        playerImg = new int[]{R.drawable.help, R.drawable.userimg, R.drawable.help, R.drawable.userimg,
                        R.drawable.help, R.drawable.userimg, R.drawable.help, R.drawable.layoutshape};

        for(int i = 0; i < playerName.length; i++) {
            txtPlayer[i].setText(playerName[i]);
            imgPlayer[i].setImageResource(playerImg[i]);
        }

        for(int i = 0; i < imgPlayer.length; i++) {
            imgPlayer[i].setTag("UnLocked");
            int num = i;

            imgPlayer[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!onGaming) {
                        gamePlayerDialog = new Dialog(GameActivity.this);
                        gamePlayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        gamePlayerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        if(playerName[num].equals("") && isHost){
                            gamePlayerDialog.setContentView(R.layout.dial_player2);
                            gamePlayerDial2(num);
                        }else if(playerName[num].length() > 0){
                            gamePlayerDialog.setContentView(R.layout.dial_player);
                            gamePlayerDial(num);
                        }
                    }
                }
            });
        }

        rollThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(isRolling) {
                    try {
                        for(int i = 0; i < playerName.length; i++) {
                            Message msg = new Message();
                            msg.obj = i;
                            rollHandler.sendMessage(msg);
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        rollThread.start();

        rollHandler = new Handler(new Handler.Callback() {

            int cnt = 0;
            int max = playerName.length * 2;
            int loser = 0;
            int fin = max + loser;

            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int n = (int) msg.obj;
                cnt++;
                if(cnt > fin){
                    isRolling = false;
                    for (int i = 0; i < playerName.length; i++) {
                        imgPlayer[i].setBackgroundColor(Color.WHITE);
                    }
                    imgPlayer[loser].setBackgroundColor(Color.RED);
                }else {
                    for (int i = 0; i < playerName.length; i++) {
                        imgPlayer[i].setBackgroundColor(Color.WHITE);
                    }
                    imgPlayer[n].setBackgroundColor(Color.RED);
                }
                return true;
            }
        });

        txtGameTitle.setText(roomName);

        GameActivity.ViewpagerAdapter adapter = new GameActivity.ViewpagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    public static class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> arrayList = new ArrayList<>();
        private ArrayList<String> name = new ArrayList<>();

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            arrayList.add(new ChatFragment());
            arrayList.add(new DeckFragment());

            name.add("채팅");
            name.add("카드");
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return name.get(position);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

    }
    public void gamePlayerDial(int num){
        gamePlayerDialog.show();
        TextView txtPlayerDial = gamePlayerDialog.findViewById(R.id.txtPlayerDial);
        ImageView imgPlayerDetail = gamePlayerDialog.findViewById(R.id.imgPlayerDetail);
        TextView txtPlayerDetail = gamePlayerDialog.findViewById(R.id.txtPlayerDetail);
        Button btnPlayerKick = gamePlayerDialog.findViewById(R.id.btnPlayerKick);
        Button btnPlayerExit = gamePlayerDialog.findViewById(R.id.btnPlayerExit);
        
        txtPlayerDial.setText("" + (num  + 1) + "번 플레이어");
        imgPlayerDetail.setImageResource(playerImg[num]);
        txtPlayerDetail.setText(playerName[num]);

        if(isHost){
            btnPlayerKick.setVisibility(View.VISIBLE);
        }else{
            btnPlayerKick.setVisibility(View.INVISIBLE);
        }

        btnPlayerKick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버 측 강퇴
                imgPlayer[num].setImageResource(R.drawable.layoutshape);
                imgPlayer[num].setTag("UnLocked");
                txtPlayer[num].setText("");
                gamePlayerDialog.dismiss();
            }
        });

        btnPlayerExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamePlayerDialog.dismiss();
            }
        });
    }
    public void gamePlayerDial2(int num){
        gamePlayerDialog.show();
        TextView txtPlayerDial2 = gamePlayerDialog.findViewById(R.id.txtPlayerDial2);
        Button btnAddBot = gamePlayerDialog.findViewById(R.id.btnAddBot);
        Button btnPlayerLock = gamePlayerDialog.findViewById(R.id.btnPlayerLock);
        Button btnPlayerEixt2 = gamePlayerDialog.findViewById(R.id.btnPlayerExit2);

        txtPlayerDial2.setText("" + (num + 1) + "번 플레이어 자리를 어떻게 할까요?");

        if(imgPlayer[num].getTag().equals("Bot")){
            btnAddBot.setVisibility(View.INVISIBLE);
        }else{
            btnAddBot.setVisibility(View.VISIBLE);
        }

        if(imgPlayer[num].getTag().equals("Locked")){
            btnPlayerLock.setText("풀기");
        }

        btnAddBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버 봇 추가
                imgPlayer[num].setImageResource(R.drawable.ic_launcher_background);
                imgPlayer[num].setTag("Bot");
                txtPlayer[num].setText("Bot" + (num + 1));
                gamePlayerDialog.dismiss();

            }
        });
        btnPlayerLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 바꾸고 서버 측 숫자 수정
                if(btnPlayerLock.getText().toString().equals("잠그기")) {
                    imgPlayer[num].setImageResource(R.drawable.ic_launcher_foreground);
                    imgPlayer[num].setTag("Locked");
                }else{
                    imgPlayer[num].setImageResource(R.drawable.layoutshape);
                    imgPlayer[num].setTag("UnLocked");
                }
                txtPlayer[num].setText("");
                gamePlayerDialog.dismiss();
            }
        });
        btnPlayerEixt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamePlayerDialog.dismiss();
            }
        });
    }
}
