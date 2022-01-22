package com.pingmo.cram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerNoticeAdapter extends RecyclerView.Adapter<RecyclerNoticeAdapter.NoticeViewHolder> {
    private ArrayList<RecyclerNoticeList> mData;
    Context context;

    public RecyclerNoticeAdapter(ArrayList<RecyclerNoticeList> data) {
        mData = data;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_notice, parent, false);
        NoticeViewHolder vh = new NoticeViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        RecyclerNoticeList item = mData.get(position);
        holder.txtNoticeTitle.setText(item.getTitle());
        holder.body = item.getBody();
        holder.txtNoticeDate.setText(item.getDate());
    }

    @Override public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView txtNoticeTitle;
        String body;
        TextView txtNoticeDate;

        NoticeViewHolder(View itemView) {
            super(itemView);
            txtNoticeTitle = itemView.findViewById(R.id.txtNoticeTitle);
            txtNoticeDate = itemView.findViewById(R.id.txtNoticeDate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        RecyclerNoticeList item = mData.get(pos);
                        Intent noticeInfoIntent = new Intent(context, NoticeInfoActivity.class);
                        noticeInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        noticeInfoIntent.putExtra("title", item.title);
                        noticeInfoIntent.putExtra("body", item.body);
                        noticeInfoIntent.putExtra("date", item.date);

                        context.startActivity(noticeInfoIntent);
                    }
                }
            });
        }
    }
}