package com.pingmo.cram.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.pingmo.cram.activity.ShopActivity;
import com.pingmo.cram.list.RecyclerNoticeList;
import com.pingmo.cram.list.RecyclerShopList;

import java.util.ArrayList;

public class RecyclerShopAdapter extends RecyclerView.Adapter<RecyclerShopAdapter.ShopViewHolder> {
    private ArrayList<RecyclerShopList> mData;
    private Dialog shopDialog;
    public RecyclerShopAdapter(ArrayList<RecyclerShopList> data) {
        mData = data;
    }
    Context context;
    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_shop, parent, false);
        ShopViewHolder vh = new ShopViewHolder(view);
        return vh;
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        RecyclerShopList item = mData.get(position);
        holder.txtShopMain.setText(item.getShopMain());
        holder.imgShop.setImageResource(item.getShopRes());
    }

    // getItemCount : 전체 데이터의 개수를 리턴
    @Override public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ShopViewHolder extends RecyclerView.ViewHolder {
        ImageView imgShop;
        TextView txtShopMain;
        ShopViewHolder(View itemView) {
            super(itemView);
            txtShopMain = itemView.findViewById(R.id.txtShopMain);
            imgShop = itemView.findViewById(R.id.imgShop);
            imgShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        RecyclerShopList item = mData.get(pos);
                        shopDialog = new Dialog(context);
                        shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        shopDialog.setContentView(R.layout.dial_shop);
                        shopDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        shopDial(item.getShopMain(), item.getShopRes());
                    }
                }
            });
        }
    }

    private void shopDial(String shopTxt, int shopImg) {
        shopDialog.show();
        Button btnShopBuy = (Button) shopDialog.findViewById(R.id.btnShopBuy);
        Button btnShopExit2 = (Button) shopDialog.findViewById(R.id.btnShopExit2);

        ImageView imgShopDetail = (ImageView) shopDialog.findViewById(R.id.imgShopDetail);
        TextView txtShopDetail = (TextView) shopDialog.findViewById(R.id.txtShopDetail);
        imgShopDetail.setImageResource(shopImg);
        txtShopDetail.setText(shopTxt);

        btnShopBuy.setOnClickListener(v -> {
            // 서버 측 연결로 구매 날려야댐
        });

        btnShopExit2.setOnClickListener(v -> shopDialog.dismiss());
    }
}