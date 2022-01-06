package com.pingmo.cram;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecyclerRoomList> mData = null;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public RecyclerRoomAdapter(ArrayList<RecyclerRoomList> data) {
        mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_room, parent, false);
            return new RoomViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoomViewHolder) {
            populateItemRows((RoomViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void populateItemRows(RoomViewHolder holder, int position) {
        RecyclerRoomList item = mData.get(position);
        if(item.getImgLock()){
            (holder).imbLock.setVisibility(View.VISIBLE);
        }else{
            (holder).imbLock.setVisibility(View.INVISIBLE);
        }
        (holder).txtRoomName.setText(item.getRoomName());
        (holder).txtRoomInfo.setText(item.getRoomInfo());
    }


    private void showLoadingView(LoadingViewHolder holder, int position) {

    }

    // getItemCount : 전체 데이터의 개수를 리턴
    @Override public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageButton imbLock;
        TextView txtRoomName;
        TextView txtRoomInfo;

        RoomViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            imbLock = itemView.findViewById(R.id.imbLock);
            txtRoomName = itemView.findViewById(R.id.txtRoomName);
            txtRoomInfo = itemView.findViewById(R.id.txtRoomInfo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 방 번호 받아서 실행
                    Intent gameIntent = new Intent(context, GameActivity.class);
                    gameIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(gameIntent);
                }
            });
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}