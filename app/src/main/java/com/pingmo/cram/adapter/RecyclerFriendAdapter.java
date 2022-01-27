package com.pingmo.cram.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.cram.R;
import com.pingmo.cram.list.RecyclerFriendList;

import java.util.ArrayList;

public class RecyclerFriendAdapter extends RecyclerView.Adapter<RecyclerFriendAdapter.FriendViewHolder> {
    private ArrayList<RecyclerFriendList> mData;
    private Dialog friendDialog;
    Context context;

    public RecyclerFriendAdapter(ArrayList<RecyclerFriendList> data) {
        mData = data;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_friend, parent, false);
        FriendViewHolder vh = new FriendViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        RecyclerFriendList item = mData.get(position);

        if(item.getOnline()){
            holder.imgOnline.setImageResource(R.drawable.online);
        }else{
            holder.imgOnline.setImageResource(R.drawable.offline);
        }
        holder.txtFriend.setText(item.getName());
    }

    @Override public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imgOnline;
        TextView txtFriend;

        FriendViewHolder(View itemView) {
            super(itemView);
            imgOnline = itemView.findViewById(R.id.imgOnline);
            txtFriend = itemView.findViewById(R.id.txtFriend);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendDialog = new Dialog(context);
                    friendDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    friendDialog.setContentView(R.layout.dial_friend);
                    friendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    friendDial(txtFriend.getText().toString());
                }
            });
        }
    }

    private void friendDial(String txtFriend) {
        friendDialog.show();

        Button btnShopBuy = (Button) friendDialog.findViewById(R.id.btnShopBuy);
        Button btnShopExit2 = (Button) friendDialog.findViewById(R.id.btnShopExit2);

        TextView txtShopDetail = (TextView) friendDialog.findViewById(R.id.txtShopDetail);
        txtShopDetail.setText(txtFriend);

        btnShopBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버 측 연결로 구매 날려야댐
            }
        });

        btnShopExit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendDialog.dismiss();
            }
        });
    }
}