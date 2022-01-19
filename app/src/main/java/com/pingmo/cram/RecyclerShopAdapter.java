package com.pingmo.cram;

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

import java.util.ArrayList;

public class RecyclerShopAdapter extends RecyclerView.Adapter<RecyclerShopAdapter.ShopViewHolder> {
    private ArrayList<RecyclerShopList> mData;
    private Dialog shopDialog;
    public RecyclerShopAdapter(ArrayList<RecyclerShopList> data) {
        mData = data;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_shop, parent, false);
        ShopViewHolder vh = new ShopViewHolder(view);

        vh.imageView1.setOnClickListener(v -> {
            shopDialog = new Dialog(context);
            shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            shopDialog.setContentView(R.layout.dial_shop);
            shopDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shopDial(vh.imageView1.getBackground(), vh.mainText1.getText().toString());
        });

        vh.imageView2.setOnClickListener(v -> {
            shopDialog = new Dialog(context);
            shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            shopDialog.setContentView(R.layout.dial_shop);
            shopDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shopDial(vh.imageView2.getBackground(), vh.mainText2.getText().toString());
        });

        vh.imageView3.setOnClickListener(v -> {
            shopDialog = new Dialog(context);
            shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            shopDialog.setContentView(R.layout.dial_shop);
            shopDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shopDial(vh.imageView3.getBackground(), vh.mainText3.getText().toString());
        });

        vh.imageView4.setOnClickListener(v -> {
            shopDialog = new Dialog(context);
            shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            shopDialog.setContentView(R.layout.dial_shop);
            shopDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shopDial(vh.imageView4.getBackground(), vh.mainText4.getText().toString());
        });


        return vh;
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        RecyclerShopList item = mData.get(position);
        holder.imageView1.setBackground(item.getImgShop1());
        holder.mainText1.setText(item.getMainText1());

        holder.imageView2.setBackground(item.getImgShop2());
        holder.mainText2.setText(item.getMainText2());

        holder.imageView3.setBackground(item.getImgShop3());
        holder.mainText3.setText(item.getMainText3());

        holder.imageView4.setBackground(item.getImgShop4());
        holder.mainText4.setText(item.getMainText4());
    }

    // getItemCount : 전체 데이터의 개수를 리턴
    @Override public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ShopViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;
        TextView mainText1;

        ImageView imageView2;
        TextView mainText2;

        ImageView imageView3;
        TextView mainText3;

        ImageView imageView4;
        TextView mainText4;
        ShopViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            imageView1 = itemView.findViewById(R.id.imgShop1);
            mainText1 = itemView.findViewById(R.id.txtShopMain1);

            imageView2 = itemView.findViewById(R.id.imgShop2);
            mainText2 = itemView.findViewById(R.id.txtShopMain2);

            imageView3 = itemView.findViewById(R.id.imgShop3);
            mainText3 = itemView.findViewById(R.id.txtShopMain3);

            imageView4 = itemView.findViewById(R.id.imgShop4);
            mainText4 = itemView.findViewById(R.id.txtShopMain4);
        }
    }

    private void shopDial(Drawable shopImg, String shopTxt) {
        shopDialog.show();
        Button btnShopBuy = (Button) shopDialog.findViewById(R.id.btnShopBuy);
        Button btnShopExit2 = (Button) shopDialog.findViewById(R.id.btnShopExit2);

        ImageView imgShopDetail = (ImageView) shopDialog.findViewById(R.id.imgShopDetail);
        TextView txtShopDetail = (TextView) shopDialog.findViewById(R.id.txtShopDetail);
        imgShopDetail.setBackground(shopImg);
        txtShopDetail.setText(shopTxt);

        btnShopBuy.setOnClickListener(v -> {
            // 서버 측 연결로 구매 날려야댐
        });

        btnShopExit2.setOnClickListener(v -> shopDialog.dismiss());
    }
}