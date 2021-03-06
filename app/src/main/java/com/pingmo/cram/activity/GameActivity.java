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

import java.util.HashSet;

public class GameActivity extends AppCompatActivity {
    public MyDBHelper myDb;
    public SQLiteDatabase sqlDb;
    
    public Cram cram = Cram.getInstance();
    Handler gameHandler;

    boolean onGaming = false;
    boolean isHost = true;

    Dialog gamePlayerDialog;
    Dialog cardPickDialog;

    TextView txtGameTitle;
    Button btnGameStart;
    ImageView imgGameLock;

    String title = "";

    String [] players;
    TextView[] txtPlayer;

    String userName;
    int userNum;

    int gamer = 0;

    int[] playerImg;
    ImageView[] imgPlayer;

    //룰렛
    Thread rollThread;
    boolean isRolling = false;
    Handler rollHandler;

    int loser = 0;
    String winner = "";

    //덱 고르기
    int pickedCard = 0;
    int[] deck = new int[6];

    HashSet<String> lastChat = new HashSet<>();

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
        int host = gameIntent.getIntExtra("host", 0);
        players = gameIntent.getStringArrayExtra("players");

        imgGameLock = findViewById(R.id.imgGameLock);
        if(isLock) {
            imgGameLock.setVisibility(View.VISIBLE);
        }else{
            imgGameLock.setVisibility(View.INVISIBLE);
        }
        txtGameTitle = findViewById(R.id.txtGameTitle);
        title = roomNum + ". " + roomName + " (" + roomInfo + ")";
        txtGameTitle.setText(title);
        btnGameStart = findViewById(R.id.btnGameStart);

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

        setGamer(host);

        for(int i = 0; i < imgPlayer.length; i++) {
            int num = i;

            imgPlayer[i].setOnClickListener(v -> {
                if(!onGaming) {
                    gamePlayerDialog = new Dialog(GameActivity.this);
                    gamePlayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    gamePlayerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    if(userNum == num){

                    }else if((players[num].equals("Locked") || players[num].equals("Bot") || players[num].equals(""))
                            && isHost){
                        gamePlayerDialog.setContentView(R.layout.dial_player2);
                        gamePlayerDial2(num);
                    }else if(!txtPlayer[num].getText().toString().equals("")){
                        gamePlayerDialog.setContentView(R.layout.dial_player);
                        gamePlayerDial(num);
                    }
                }
            });
        }
        rollHandler = new Handler(new Handler.Callback() {

            int cnt = 0;

            @Override
            public boolean handleMessage(Message msg) {
                int n = (int) msg.obj;
                int max = gamer * 2;
                cnt++;
                int fin = max + loser;
                if(cnt >= fin){
                    isRolling = false;
                    for (int i = 0; i < players.length; i++) {
                        imgPlayer[i].setBackgroundColor(Color.WHITE);
                    }
                    imgPlayer[loser].setBackgroundColor(Color.RED);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cnt = 0;
                            loser = 0;
                            for(int i = 0; i < imgPlayer.length; i++) {
                                imgPlayer[i].setBackgroundColor(Color.WHITE);
                            }
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
                    String who = receiveData.getString("who");
                    String chat = receiveData.getString("chat");
                    if(who.equals("system")){
                        if(!lastChat.contains(chat)){
                            adapter.addChat(who + " : " + chat);
                        }
                        lastChat.add(chat);
                    }else {
                        adapter.addChat(who + " : " + chat);
                    }
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
                if(msg.what == 301){
                    // 덱 남은거 호출
                    for(int i = 0; i < deck.length; i++) {
                        deck[i] = Integer.parseInt(receiveData.getString(String.valueOf(i + 1)));
                    }
                    adapter.setDeck(deck);
                }
                if(msg.what == 302) {
                    // 게임 끝
                    onGaming = false;
                    if(isHost){
                        btnGameStart.setVisibility(View.VISIBLE);
                    }
                    String win = receiveData.getString("winner");
                    Toast.makeText(getApplicationContext(), "" + win + "님이 우승 하셨 습니다.", Toast.LENGTH_SHORT).show();
                    adapter.gameFinshed();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0; i < deck.length; i++) {
                                deck[i] = 0;
                            }
                            adapter.setDeck(deck);
                        }
                    }, 5000);

                    txtGameTitle.setText(title);
                }
                if(msg.what == 303) {
                    // 게임 시작
                    int status = Integer.parseInt(receiveData.getString("status"));
                    if(status == 1){
                        onGaming = true;
                        if(isHost){
                            btnGameStart.setVisibility(View.INVISIBLE);
                        }
                        cardPickDialog = new Dialog(GameActivity.this);
                        cardPickDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        cardPickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        cardPickDialog.setContentView(R.layout.dial_cardpick);
                        cardPickDialog();
                    }else{
                        Toast.makeText(getApplicationContext(), "게임 시작을 할수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(msg.what == 304){
                    // 덱 비교
                    int order = Integer.parseInt(receiveData.getString("order"));
                    int token = Integer.parseInt(receiveData.getString("token"));
                    // token 0 이상, 1 이하, 2 홀, 3 짝

                    boolean [] deckClickable = new boolean[deck.length];
                    if(token == 0){
                        txtGameTitle.setText("" + order + " 이상 숫자 내기");
                        for(int i = 0; i < deck.length; i++){
                            deckClickable[i] = deck[i] >= order;
                        }
                    }else if(token == 1){
                        txtGameTitle.setText("" + order + " 이하 숫자 내기");
                        for(int i = 0; i < deck.length; i++){
                            deckClickable[i] = deck[i] <= order;
                        }
                    }else if(token == 2){
                        txtGameTitle.setText("홀수 내기");
                        for(int i = 0; i < deck.length; i++){
                            deckClickable[i] = deck[i] % 2 == 1;
                        }
                    }else if(token == 3){
                        txtGameTitle.setText("짝수 내기");
                        for(int i = 0; i < deck.length; i++){
                            deckClickable[i] = deck[i] % 2 == 0;
                        }
                    }
                    int cnt = 0;
                    for(int i = 0; i < deckClickable.length; i++) {
                        if(!deckClickable[i]){
                            cnt++;
                        }
                    }
                    // 제출 못할 경우 작성 해야댐
                    if(cnt == deckClickable.length){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "제출 할 수 있는 패가 없습니다.", Toast.LENGTH_SHORT).show();
                                if(cram.isConnected()) {
                                    try {
                                        JSONObject sendData = new JSONObject();
                                        sendData.put("what", 301);
                                        sendData.put("pop", Integer.toString(0));
                                        cram.send(sendData.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 3000);
                    }
                    adapter.setClickable(deckClickable);
                }
                if(msg.what == 305){
                    // 턴 끝날 때 값
                    String tmploser = receiveData.getString("loser");
                    winner = receiveData.getString("winner");
                    for(int i = 0; i < players.length; i++) {
                        if(players[i].equals(tmploser)){
                            loser = i;
                        }
                    }
                    isRolling = true;
                    if(userName.equals(winner)){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(cram.isConnected()) {
                                    try {
                                        JSONObject sendData = new JSONObject();
                                        sendData.put("what", 306);
                                        cram.send(sendData.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 3000);
                    }
                }

                if(msg.what == 401) {
                    // 누군가 입장
                    String roomNum1 = receiveData.getString("roomNum");
                    String roomName1 = receiveData.getString("roomName");
                    boolean isLock1 = receiveData.getString("roomPW").length() > 0;
                    String roomInfo1 = receiveData.getString("curPlayer") + "/" + receiveData.getString("maxPlayer");
                    JSONArray pData = receiveData.getJSONArray("players");
                    int host1 = Integer.parseInt(receiveData.getString("host"));
                    players = new String[pData.length()];
                    for(int i = 0; i < pData.length(); i++){
                        JSONObject player = (JSONObject) pData.get(i);
                        players[i] = player.getString("player");
                    }
                    if(isLock1) {
                        imgGameLock.setVisibility(View.VISIBLE);
                    }else{
                        imgGameLock.setVisibility(View.INVISIBLE);
                    }
                    title = roomNum1 + ". " + roomName1 + " (" + roomInfo1 + ")";
                    txtGameTitle.setText(title);
                    setGamer(host1);
                }

                if(msg.what == 402) {
                    // 방 나가기
                    if(!onGaming){
                        finish();
                    }
                }

                if(msg.what == 408){
                    // 추방
                    int kickNum = Integer.parseInt(receiveData.getString("kickNum"));
                    if(userNum == kickNum){
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
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        btnGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게임 시작 요청 보내기
                for(int i = 0; i < players.length; i++) {
                    if(players[i].equals("")){
                        Toast.makeText(getApplicationContext(), "빈 자리를 채워 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                int cnt = 0;
                for(int i = 0; i < players.length; i++) {
                    if(players[i].equals("Locked")){
                        cnt++;
                    }
                }
                if(cnt == players.length - 1){
                    Toast.makeText(getApplicationContext(), "혼자서는 플레이 할 수 없습니다.\n봇을 추가해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cram.isConnected()) {
                    try {
                        JSONObject sendData = new JSONObject();
                        sendData.put("what", 303);
                        cram.send(sendData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        rollThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true) {
                    try {
                        if(isRolling) {
                            gamer = 0;
                            for(int i = 0; i < players.length; i++) {
                                if(players[i].equals("") || players[i].equals("Locked")){

                                }else{
                                    gamer++;
                                }
                            }
                            for (int i = 0; i < players.length; i++) {
                                if(players[i].equals("") || players[i].equals("Locked")){

                                }else{
                                    Message msg = new Message();
                                    msg.obj = i;
                                    rollHandler.sendMessage(msg);
                                    Thread.sleep(200);
                                }
                            }
                        }else{
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
                }
            }
        };

        rollThread.start();
    }
    @Override
    public void onStart() {
        super.onStart();
        cram.setHandler(gameHandler);
    }

    @Override
    public void onBackPressed() {
        if(!onGaming) {
            if (cram.isConnected()) {
                try {
                    JSONObject sendData = new JSONObject();
                    sendData.put("what", 402);
                    cram.send(sendData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "현재 게임이 진행 중 입니다.", Toast.LENGTH_SHORT).show();
        }
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
                            // 이 부분 i + 1 로 바꿔서 실제 숫자를 리턴할 것
                            deck[cnt - 1] = (i + 1);
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
            if(cram.isConnected()) {
                try {
                    JSONObject sendData = new JSONObject();
                    if (players[num].equals("Bot")) {
                        sendData.put("what", 407);
                        sendData.put("botNum", Integer.toString(num));
                    } else {
                        sendData.put("what", 408);
                        sendData.put("kickNum", Integer.toString(num));
                    }
                    cram.send(sendData.toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
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

        if(players[num].equals("Bot")){
            btnAddBot.setText("봇 제거");
        }

        if(players[num].equals("Locked")){
            btnPlayerLock.setText("풀기");
        }

        btnAddBot.setOnClickListener(v -> {
            // 서버 봇 추가
            if(cram.isConnected()) {
                try {
                    if (players[num].equals("Locked")) {
                        JSONObject sendData2 = new JSONObject();
                        sendData2.put("what", 405);
                        sendData2.put("lockNum", Integer.toString(num));
                        cram.send(sendData2.toString());
                    }
                    JSONObject sendData = new JSONObject();
                    if(!players[num].equals("Bot")) {
                        sendData.put("what", 406);
                    }else {
                        sendData.put("what", 407);
                    }
                    sendData.put("botNum", Integer.toString(num));
                    cram.send(sendData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
            gamePlayerDialog.dismiss();
        });

        btnPlayerLock.setOnClickListener(v -> {
            // 방 잠그기
            if (cram.isConnected()) {
                try {
                    if(players[num].equals("Bot")){
                        JSONObject sendData2 = new JSONObject();
                        sendData2.put("what", 407);
                        sendData2.put("botNum", Integer.toString(num));
                        cram.send(sendData2.toString());
                    }
                    JSONObject sendData = new JSONObject();
                    if (!players[num].equals("Locked")) {
                        sendData.put("what", 404);
                    }else {
                        sendData.put("what", 405);
                    }
                    sendData.put("lockNum", Integer.toString(num));
                    cram.send(sendData.toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
            txtPlayer[num].setText("");

            gamePlayerDialog.dismiss();
        });
        btnPlayerEixt2.setOnClickListener(v -> gamePlayerDialog.dismiss());
    }

    // 유저 설정
    public void setGamer(int host){
        for(int i = 0; i < players.length; i++) {
            if(players[i].equals(userName)){
                userNum = i;
            }
            isHost = (host == userNum);

            if(isHost) {
                btnGameStart.setVisibility(View.VISIBLE);
            }else {
                btnGameStart.setVisibility(View.INVISIBLE);
            }

            if(players[i].contains("Bot")){
                txtPlayer[i].setText("Bot" + (i + 1));
                imgPlayer[i].setImageResource(R.drawable.bot);
            }else if(players[i].equals("Locked")){
                txtPlayer[i].setText("");
                imgPlayer[i].setImageResource(R.drawable.locked);
            }else if(players[i].equals("")){
                txtPlayer[i].setText("");
                imgPlayer[i].setImageResource(R.drawable.layoutshape);
            } else {
                txtPlayer[i].setText(players[i]);
                imgPlayer[i].setImageResource(playerImg[i]);
            }
        }
    }
}
