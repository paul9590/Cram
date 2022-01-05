package com.pingmo.cram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<RecyclerShopItem> mData = null;
    public RecyclerViewAdapter(ArrayList<RecyclerShopItem> data) {
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_shop, parent, false);
        RecyclerViewAdapter.ViewHolder vh = new RecyclerViewAdapter.ViewHolder(view);
        return vh;
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerShopItem item = mData.get(position);
        holder.imageView1.setBackground(item.getImgShop1());
        holder.mainText1.setText(item.getMainText1());
        holder.subText1.setText(item.getSubText1());

        holder.imageView2.setBackground(item.getImgShop2());
        holder.mainText2.setText(item.getMainText2());
        holder.subText2.setText(item.getSubText2());

        holder.imageView3.setBackground(item.getImgShop3());
        holder.mainText3.setText(item.getMainText3());
        holder.subText3.setText(item.getSubText3());

        holder.imageView4.setBackground(item.getImgShop4());
        holder.mainText4.setText(item.getMainText4());
        holder.subText4.setText(item.getSubText4());
    }

    // getItemCount : 전체 데이터의 개수를 리턴
    @Override public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;
        TextView mainText1;
        TextView subText1;

        ImageView imageView2;
        TextView mainText2;
        TextView subText2;

        ImageView imageView3;
        TextView mainText3;
        TextView subText3;

        ImageView imageView4;
        TextView mainText4;
        TextView subText4;
        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            imageView1 = itemView.findViewById(R.id.imgShop1);
            mainText1 = itemView.findViewById(R.id.txtShopMain1);
            subText1 = itemView.findViewById(R.id.txtShopSub1);

            imageView2 = itemView.findViewById(R.id.imgShop2);
            mainText2 = itemView.findViewById(R.id.txtShopMain2);
            subText2 = itemView.findViewById(R.id.txtShopSub2);

            imageView3 = itemView.findViewById(R.id.imgShop3);
            mainText3 = itemView.findViewById(R.id.txtShopMain3);
            subText3 = itemView.findViewById(R.id.txtShopSub3);

            imageView4 = itemView.findViewById(R.id.imgShop4);
            mainText4 = itemView.findViewById(R.id.txtShopMain4);
            subText4 = itemView.findViewById(R.id.txtShopSub4);
        }
    }
}