package com.pingmo.cram;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoomActivity extends AppCompatActivity {

    RecyclerView viewRoom = null;
    RecyclerRoomAdapter mAdapter = null;
    ArrayList<RecyclerRoomList> mList;
    Boolean isLoading = false;
    Cram cram = Cram.getInstance();
    Handler roomHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        viewRoom = findViewById(R.id.viewRoom);
        SearchView srhRoom = (SearchView) findViewById(R.id.srhRoom);

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

        // 방 목록 불러 와야함
        roomHandler = new Handler(msg -> {
            if(msg.obj != null) {
                addItem(false, "hwang", "1/8");
            }
            return true;
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        cram.setHandler(roomHandler);
        // 초기 방 목록 10개 정도 불러오기
        if(cram.isConnected()) {
            cram.send("r&1");
        }else{
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
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
                cram.send("r&1");
                /*
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    // 여기서 방 불러오는 서버 코드

                    currentSize++;
                }
                 */

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
}
