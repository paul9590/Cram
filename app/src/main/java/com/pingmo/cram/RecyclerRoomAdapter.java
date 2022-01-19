package com.pingmo.cram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private ArrayList<RecyclerRoomList> mData;
    private ArrayList<RecyclerRoomList> filterList;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public RecyclerRoomAdapter(ArrayList<RecyclerRoomList> data) {
        mData = data;
        filterList = data;
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
        return filterList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void populateItemRows(RoomViewHolder holder, int position) {
        RecyclerRoomList item = filterList.get(position);
        if(item.getImgLock()){
            (holder).imgLock.setVisibility(View.VISIBLE);
        }else{
            (holder).imgLock.setVisibility(View.INVISIBLE);
        }
        (holder).txtRoomName.setText(item.getRoomName());
        (holder).txtRoomInfo.setText(item.getRoomInfo());
    }


    private void showLoadingView(LoadingViewHolder holder, int position) {

    }

    // getItemCount : 전체 데이터의 개수를 리턴
    @Override public int getItemCount() {
        return filterList == null ? 0 : filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();
                if (str.isEmpty()) {
                    filterList = mData;
                } else {
                    ArrayList<RecyclerRoomList> filteringList = new ArrayList<>();
                    for(RecyclerRoomList item : mData) {
                        if(item.getRoomName().toUpperCase().contains(str.toUpperCase()) ||
                                item.getRoomName().toUpperCase().contains(str.toLowerCase())){
                            filteringList.add(item);
                        }
                    }
                    filterList = filteringList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (ArrayList<RecyclerRoomList>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLock;
        TextView txtRoomName;
        TextView txtRoomInfo;

        RoomViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            imgLock = itemView.findViewById(R.id.imgLock);
            txtRoomName = itemView.findViewById(R.id.txtRoomName);
            txtRoomInfo = itemView.findViewById(R.id.txtRoomInfo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 방 번호 받아서 실행 비밀번호 확인 및 인원수 체크
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