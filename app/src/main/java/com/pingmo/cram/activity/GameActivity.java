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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pingmo.cram.Cram;
import com.pingmo.cram.MyDBHelper;
import com.pingmo.cram.R;
import com.pingmo.cram.adapter.GameViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity {
    public MyDBHelper myDb;
    public SQLiteDatabase sqlDb;
    public String userName;
    
    public Cram cram = Cram.getInstance();
    Handler gameHandler;

    boolean onGaming = false;
    boolean isHost = true;

    Dialog gamePlayerDialog;
    Dialog cardPickDialog;

    TextView txtGameTitle;

    String [] players;
    TextView[] txtPlayer;

    int gamer = 0;

    int[] playerImg;
    ImageView[] imgPlayer;

    //룰렛
    Thread rollThread;
    boolean isRolling = false;
    Handler rollHandler;

    //덱 고르기
    int pickedCard = 0;
    int[] deck = new int[6];

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
        cur.close();
        sqlDb.close();

        Intent gameIntent = getIntent();
        boolean isLock = gameIntent.getBooleanExtra("isLock", false);
        String roomNum = gameIntent.getStringExtra("roomNum");
        String roomName = gameIntent.getStringExtra("roomName");
        String roomInfo = gameIntent.getStringExtra("roomInfo");
        players = gameIntent.getStringArrayExtra("players");

        txtGameTitle = findViewById(R.id.txtGameTitle);
        txtGameTitle.setText("" + roomNum + ". " + roomName + " (" + roomInfo + ")");

        ViewPager pager = findViewById(R.id.pagerGame);
        TabLayout tabLayout = findViewById(R.id.tabGame);

        GameViewAdapter adapter = new GameViewAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

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

        playerImg = new int[]{R.drawable.userimg, R.drawable.userimg, R.drawable.userimg, R.drawable.userimg,
                    R.drawable.userimg, R.drawable.userimg, R.drawable.userimg, R.drawable.userimg};

        setGamer();

        for(int i = 0; i < imgPlayer.length; i++) {
            imgPlayer[i].setTag("UnLocked");
            int num = i;

            imgPlayer[i].setOnClickListener(v -> {
                if(!onGaming) {
                    gamePlayerDialog = new Dialog(GameActivity.this);
                    gamePlayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    gamePlayerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    if(players[num].equals("") && isHost){
                        gamePlayerDialog.setContentView(R.layout.dial_player2);
                        gamePlayerDial2(num);
                    }else if(!players[num].equals("")){
                        gamePlayerDialog.setContentView(R.layout.dial_player);
                        gamePlayerDial(num);
                    }
                }
            });
        }
        rollHandler = new Handler(new Handler.Callback() {

            int cnt = 0;
            int max = gamer * 2;

            int loser = 0;

            @Override
            public boolean handleMessage(Message msg) {
                int n = (int) msg.obj;
                cnt++;
                int fin = max + loser;
                if(cnt > fin){
                    isRolling = false;
                    for (int i = 0; i < players.length; i++) {
                        imgPlayer[i].setBackgroundColor(Color.WHITE);
                    }
                    imgPlayer[loser].setBackgroundColor(Color.RED);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cnt = 0;
                            imgPlayer[n].setBackgroundColor(Color.WHITE);
                        }
                    }, 3000);
                }else {
                    for (int i = 0; i < players.length; i++) {
                        imgPlayer[i].setBackgroundColor(Color.WHITE);
                    }
                    imgPlayer[n].setBackgroundColor(Color.RED);
                }
                return true;
            }
        });

        gameHandler = new Handler(msg -> {
            try {
                JSONObject receiveData = new JSONObject(msg.obj.toString());
                if(msg.what == 200){
                    // 채팅
                    adapter.addChat(receiveData.getString("who") + " : " + receiveData.getString("chat"));
                }
                if(msg.what == 300) {
                    //덱 제출
                    int status = Integer.parseInt(receiveData.getString("status"));
                    if(status == 1){
                        if(cardPickDialog.isShowing()){
                            cardPickDialog.dismiss();
                        }
                        adapter.setDeck(deck);
                    }else{
                        Toast.makeText(getApplicationContext(), "카드 제출에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                if(msg.what == 401) {
                    // 누군가 입장
                    String roomNum1 = receiveData.getString("roomNum");
                    String roomName1 = receiveData.getString("roomName");
                    boolean isLock1 = receiveData.getString("roomPW").length() > 0;
                    String roomInfo1 = receiveData.getString("curPlayer") + "/" + receiveData.getString("maxPlayer");
                    JSONArray pData = receiveData.getJSONArray("players");
                    players = new String[pData.length()];
                    for(int i = 0; i < pData.length(); i++){
                        JSONObject player = (JSONObject) pData.get(i);
                        players[i] = player.getString(Integer.toString(i + 1));
                    }
                    txtGameTitle.setText("" + roomNum1 + ". " + roomName1 + " (" + roomInfo1 + ")");
                    setGamer();
                }

                if(msg.what == 402) {
                    // 방 나가기
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });


        txtGameTitle.setOnClickListener(v -> {
            if(!isRolling){
                isRolling = true;
            }
            cardPickDialog = new Dialog(GameActivity.this);
            cardPickDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cardPickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cardPickDialog.setContentView(R.layout.dial_cardpick);
            cardPickDialog();
        });
        rollThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true) {
                    try {
                        if(isRolling) {
                            for(int i = 0; i < players.length; i++) {
                                if(players[i].equals("Locked") || players[i].equals("")){

                                }else{
                                    gamer++;
                                }
                            }
                            for (int i = 0; i < players.length; i++) {
                                if(players[i].equals("Locked") || players[i].equals("")){

                                }else {
                                    Message msg = new Message();
                                    msg.obj = i;
                                    rollHandler.sendMessage(msg);
                                    Thread.sleep(200);
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
                }
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        cram.setHandler(gameHandler);
        rollThread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
        if(cram.isConnected()) {
            try {
                JSONObject sendData = new JSONObject();
                sendData.put("what", 402);
                cram.send(sendData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
        
         */
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    public void cardPickDialog() {
        cardPickDialog.show();
        Button btnPick = cardPickDialog.findViewById(R.id.btnPick);
        pickedCard = 0;
        ImageView [] imgPick = {
                cardPickDialog.findViewById(R.id.imgPick1),
                cardPickDialog.findViewById(R.id.imgPick2),
                cardPickDialog.findViewById(R.id.imgPick3),
                cardPickDialog.findViewById(R.id.imgPick4),
                cardPickDialog.findViewById(R.id.imgPick5),
                cardPickDialog.findViewById(R.id.imgPick6),
                cardPickDialog.findViewById(R.id.imgPick7),
                cardPickDialog.findViewById(R.id.imgPick8),
                cardPickDialog.findViewById(R.id.imgPick9),
                cardPickDialog.findViewById(R.id.imgPick10),
                cardPickDialog.findViewById(R.id.imgPick11),
                cardPickDialog.findViewById(R.id.imgPick12),
                cardPickDialog.findViewById(R.id.imgPick13),
                cardPickDialog.findViewById(R.id.imgPick14),
                cardPickDialog.findViewById(R.id.imgPick15),
        };
        for(int i = 0; i < imgPick.length; i++) {
            int num = i;
            imgPick[num].setTag("UnPicked");
            imgPick[i].setOnClickListener(v -> {
                if (imgPick[num].getTag().equals("Picked") && pickedCard > 0) {
                    imgPick[num].setTag("UnPicked");
                    imgPick[num].setColorFilter(Color.parseColor("#00000000"));
                    pickedCard--;
                } else if (pickedCard < 6){
                    imgPick[num].setTag("Picked");
                    imgPick[num].setColorFilter(R.color.mainColor);
                    pickedCard++;
                }
            });
        }
        btnPick.setOnClickListener(v -> {
            if(pickedCard == 6){
                try {
                    JSONObject sendData = new JSONObject();
                    sendData.put("what", 300);
                    int cnt = 1;
                    for(int i = 0; i < imgPick.length; i++) {
                        if(imgPick[i].getTag().equals("Picked")){
                            sendData.put(String.valueOf(cnt), Integer.toString(i + 1));
                            deck[cnt - 1] = i;
                            cnt++;
                        }
                    }
                    cram.send(sendData.toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "카드를 6장 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
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
        txtPlayerDetail.setText(players[num]);

        if(isHost){
            btnPlayerKick.setVisibility(View.VISIBLE);
        }else{
            btnPlayerKick.setVisibility(View.INVISIBLE);
        }

        btnPlayerKick.setOnClickListener(v -> {
            //서버 측 강퇴
            imgPlayer[num].setImageResource(R.drawable.layoutshape);
            imgPlayer[num].setTag("UnLocked");
            txtPlayer[num].setText("");
            gamePlayerDialog.dismiss();
        });

        btnPlayerExit.setOnClickListener(v -> gamePlayerDialog.dismiss());
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

        btnAddBot.setOnClickListener(v -> {
            // 서버 봇 추가
            imgPlayer[num].setImageResource(R.drawable.bot);
            imgPlayer[num].setTag("Bot");
            txtPlayer[num].setText("Bot" + (num + 1));
            gamePlayerDialog.dismiss();
        });

        btnPlayerLock.setOnClickListener(v -> {
            // 이미지 바꾸고 서버 측 숫자 수정
            if(btnPlayerLock.getText().toString().equals("잠그기")) {
                imgPlayer[num].setImageResource(R.drawable.locked);
                imgPlayer[num].setTag("Locked");
            }else{
                imgPlayer[num].setImageResource(R.drawable.layoutshape);
                imgPlayer[num].setTag("UnLocked");
            }
            txtPlayer[num].setText("");

            gamePlayerDialog.dismiss();
        });
        btnPlayerEixt2.setOnClickListener(v -> gamePlayerDialog.dismiss());
    }

    // 유저 설정
    public void setGamer(){
        for(int i = 0; i < players.length; i++) {
            Toast.makeText(getApplicationContext(), "" + players[i] + " " + players.length, Toast.LENGTH_SHORT).show();
            if(players[i].equals("Bot")){
                txtPlayer[i].setText("Bot" + (i + 1));
                imgPlayer[i].setImageResource(R.drawable.bot);
                imgPlayer[i].setTag("Bot");
            }else if(players[i].equals("Locked")){
                txtPlayer[i].setText("");
                imgPlayer[i].setImageResource(R.drawable.locked);
                imgPlayer[i].setTag("Locked");
            }else if(players[i].equals("")){
                txtPlayer[i].setText("");
                imgPlayer[i].setImageResource(R.drawable.layoutshape);
                imgPlayer[i].setTag("UnLocked");
            } else {
                txtPlayer[i].setText(players[i]);
                imgPlayer[i].setImageResource(playerImg[i]);
                imgPlayer[i].setTag("Player");
            }
        }
    }
}
