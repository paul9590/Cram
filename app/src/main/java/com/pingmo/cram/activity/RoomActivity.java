package com.pingmo.cram.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.cram.Cram;
import com.pingmo.cram.R;
import com.pingmo.cram.adapter.RecyclerRoomAdapter;
import com.pingmo.cram.list.RecyclerRoomList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoomActivity extends AppCompatActivity {

    Cram cram = Cram.getInstance();

    RecyclerView viewRoom = null;
    RecyclerRoomAdapter mAdapter = null;
    ArrayList<RecyclerRoomList> mList;
    Boolean isLoading = false;

    Handler roomHandler;
    Dialog roomDialog;

    final int MAX_PLAYER = 8;
    final int MIN_PLAYER = 4;
    int roomMax = MIN_PLAYER;
    final int PW_LENGTH = 8;
    final int ROOM_LENGTH = 20;
    int roomCnt = 0;

    String [] filter;
    String roomName;
    JSONObject requestRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        viewRoom = findViewById(R.id.viewRoom);
        SearchView srhRoom = findViewById(R.id.srhRoom);
        Button btnAddRoom = findViewById(R.id.btnAddRoom);

        filter = getResources().getStringArray(R.array.filter);

        requestRoom = new JSONObject();
        try {
            requestRoom.put("what", 403);
            requestRoom.put("roomCnt", Integer.toString(roomCnt));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        roomHandler = new Handler(msg -> {
            try {
                JSONObject receiveData = new JSONObject(msg.obj.toString());
                if(msg.what == 400) {
                    int status = Integer.parseInt(receiveData.getString("status"));
                    if(status == 1){
                        Intent gameIntent = new Intent(RoomActivity.this, GameActivity.class);
                        gameIntent.putExtra("roomName", roomName);
                        gameIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(gameIntent);
                        if(roomDialog.isShowing()) {
                            roomDialog.dismiss();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "방 생성에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(msg.what == 403) {
                    roomCnt += 5;
                    JSONArray datas = receiveData.getJSONArray("detail");
                    for(int i = 0; i < datas.length(); i++) {
                        JSONObject data = (JSONObject) datas.get(i);
                        boolean isLock = data.getString("roomPW").length() > 0;
                        String roomName = data.getString("roomName");
                        String roomInfo = data.getString("curPlayer") + "/" + data.getString("maxPlayer");
                        addItem(isLock, roomName, roomInfo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomDialog = new Dialog(RoomActivity.this);
                roomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                roomDialog.setContentView(R.layout.dial_room);
                roomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                roomDial();
            }
        });

        srhRoom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        mList = new ArrayList<>();
        mAdapter = new RecyclerRoomAdapter(mList);
        viewRoom.setAdapter(mAdapter);
        viewRoom.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        viewRoom.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));
        viewRoom.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!isLoading && !recyclerView.canScrollVertically(1)) {
                    loadMore();
                    isLoading = true;
                }
            }
        });
        // 언젠가 지울거임
        addItem(false, "hwang", "1/8");
    }

    @Override
    public void onStart() {
        super.onStart();
        cram.setHandler(roomHandler);
        // 초기 방 목록 10개 정도 불러오기
        if(cram.isConnected()) {
            cram.send(requestRoom.toString());
        }else{
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cram.switchHandler();
    }

    private void addItem(Boolean isLock, String roomName, String roomInfo) {
        RecyclerRoomList item = new RecyclerRoomList();
        item.setImgLock(isLock);
        item.setRoomName(roomName);
        item.setRoomInfo(roomInfo);
        mList.add(item);
        mAdapter.notifyDataSetChanged();
    }

    private void loadMore() {
        mList.add(null);
        mAdapter.notifyItemInserted(mList.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.remove(mList.size() - 1);
                int scrollPosition = mList.size();
                mAdapter.notifyItemRemoved(scrollPosition);
                cram.send(requestRoom.toString());
                Collections.sort(mList, new Comparator<RecyclerRoomList>() {
                    @Override
                    public int compare(RecyclerRoomList o1, RecyclerRoomList o2) {
                        // 1 - 2 이 방에 사람이 적은 순서
                        return o1.getRoomInfo().compareTo(o2.getRoomInfo());

                    }
                });

                mAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void roomDial() {
        roomDialog.show();
        EditText editRoomName = roomDialog.findViewById(R.id.editRoomName);
        EditText editRoomPw = roomDialog.findViewById(R.id.editRoomPw);

        ImageButton imbRoomCntUp = roomDialog.findViewById(R.id.imbRoomCntUp);
        ImageButton imbRoomCntDown = roomDialog.findViewById(R.id.imbRoomCntDown);

        TextView txtRoomMax = roomDialog.findViewById(R.id.txtRoomMax);

        CheckBox chkRoomLock = roomDialog.findViewById(R.id.chkRoomLock);

        Button btnRoomDialYes = roomDialog.findViewById(R.id.btnRoomDialYes);
        Button btnRoomDialNo = roomDialog.findViewById(R.id.btnRoomDialNo);

        txtRoomMax.setText("" + roomMax);
        editRoomPw.setVisibility(View.INVISIBLE);

        chkRoomLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editRoomPw.setVisibility(View.VISIBLE);
                }else {
                    editRoomPw.setVisibility(View.INVISIBLE);
                }
            }
        });
        imbRoomCntUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomMax < MAX_PLAYER) {
                    roomMax++;
                }else{
                    Toast.makeText(getApplicationContext(), "입장 가능한 최대 인원은 '" + MAX_PLAYER + "' 입니다." , Toast.LENGTH_SHORT).show();
                }
                txtRoomMax.setText("" + roomMax);
            }
        });
        imbRoomCntDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomMax > MIN_PLAYER) {
                    roomMax--;
                }else{
                    Toast.makeText(getApplicationContext(), "입장 가능한 최소 인원은 '" + MIN_PLAYER + "' 입니다." , Toast.LENGTH_SHORT).show();
                }
                txtRoomMax.setText("" + roomMax);
            }
        });


        btnRoomDialYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomName = editRoomName.getText().toString();
                String roomPW = editRoomPw.getText().toString();
                boolean confirm = true;

                if(roomName.equals("")){
                    Toast.makeText(getApplicationContext(), "방 이름을 작성해 주세요.", Toast.LENGTH_SHORT).show();
                    confirm = false;
                }
                if (roomName.length() > ROOM_LENGTH){
                    Toast.makeText(getApplicationContext(), "방 이름은 " + ROOM_LENGTH + "자리를 넘길 수 없습니다.", Toast.LENGTH_SHORT).show();
                    confirm = false;
                }

                for(int i = 0; i < filter.length; i++) {
                    if (roomName.contains(filter[i])){
                        confirm = false;
                    }
                }
                if(chkRoomLock.isChecked()){
                    if(roomPW.equals("")){
                        Toast.makeText(getApplicationContext(), "비밀번호를 작성해 주세요.", Toast.LENGTH_SHORT).show();
                        confirm = false;
                    }
                    if(roomPW.length() > PW_LENGTH){
                        Toast.makeText(getApplicationContext(), "비밀번호는 " + PW_LENGTH + "자리를 넘길 수 없습니다.", Toast.LENGTH_SHORT).show();
                        confirm = false;
                    }
                }

                if(confirm) {
                    try {
                        JSONObject sendData = new JSONObject();
                        sendData.put("what", 400);
                        sendData.put("roomName", roomName);
                        sendData.put("maxPlayer", txtRoomMax.getText().toString());
                        if(chkRoomLock.isChecked()) {
                            roomPW = "";
                        }
                        sendData.put("roomPW", roomPW);
                        cram.send(sendData.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        btnRoomDialNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomDialog.dismiss();
            }
        });

    }
}
