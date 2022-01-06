package com.pingmo.cram;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    RecyclerView viewRoom = null;
    RecyclerRoomAdapter mAdapter = null;
    ArrayList<RecyclerRoomList> mList;
    Boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        viewRoom = findViewById(R.id.viewRoom);

        mList = new ArrayList<>();
        mAdapter = new RecyclerRoomAdapter(mList);
        viewRoom.setAdapter(mAdapter);
        viewRoom.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
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

        // 이부분 건들여서 서버 측 소스 코드 받아 올 것
        Boolean isLock = true;
        String roomName =  "paul";
        String roomInfo = "1/8";

        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);
        addItem(isLock, roomName, roomInfo);

        mAdapter.notifyDataSetChanged();

    }

    private void addItem(Boolean isLock, String roomName, String roomInfo) {
        RecyclerRoomList item = new RecyclerRoomList();
        item.setImgLock(isLock);
        item.setRoomName(roomName);
        item.setRoomInfo(roomInfo);
        mList.add(item);
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
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    // 여기서 방 불러오는 서버 코드
                    addItem(false, "핑모에오", "8/8");
                    currentSize++;
                }

                mAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }
}
