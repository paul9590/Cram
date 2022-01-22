package com.pingmo.cram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.cram.R;
import com.pingmo.cram.list.RecyclerChatList;

import java.util.ArrayList;

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.ChatViewHolder> {
    private ArrayList<RecyclerChatList> mData;

    public RecyclerChatAdapter(ArrayList<RecyclerChatList> data) {
        mData = data;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_chat, parent, false);
        ChatViewHolder vh = new ChatViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        RecyclerChatList item = mData.get(position);
        String txt = item.getName() + " : " + item.getChat();
        holder.txtChat.setText(txt);
    }

    @Override public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtChat;

        ChatViewHolder(View itemView) {
            super(itemView);
            txtChat = itemView.findViewById(R.id.txtChat);
        }
    }
}