package com.pingmo.cram.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pingmo.cram.adapter.RecyclerNoticeAdapter;
import com.pingmo.cram.fragment.ChatFragment;
import com.pingmo.cram.fragment.DeckFragment;
import com.pingmo.cram.R;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    Dialog gamePlayerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent gameIntent = getIntent();
        String roomName = gameIntent.getStringExtra("roomName");

        TextView txtGameTitle = findViewById(R.id.txtGameTitle);

        ViewPager pager = findViewById(R.id.pagerGame);
        TabLayout tabLayout = findViewById(R.id.tabGame);

        ImageView imgPlayer [] = {
                findViewById(R.id.imgPlayer1),
                findViewById(R.id.imgPlayer2),
                findViewById(R.id.imgPlayer3),
                findViewById(R.id.imgPlayer4),
                findViewById(R.id.imgPlayer5),
                findViewById(R.id.imgPlayer6),
                findViewById(R.id.imgPlayer7),
                findViewById(R.id.imgPlayer8),
        };
        for(int i = 0; i < imgPlayer.length; i++) {
            imgPlayer[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "" + v.getX() + " " + v.getY(), Toast.LENGTH_SHORT).show();
                    gamePlayerDialog = new Dialog(GameActivity.this);
                    gamePlayerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    gamePlayerDialog.setContentView(R.layout.dial_gameplayer);
                    Window window = gamePlayerDialog.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.x = (int) v.getX();
                    params.y = (int) v.getY();
                    window.setAttributes(params);
                    gamePlayerDial();
                }
            });
        }

        txtGameTitle.setText(roomName);

        GameActivity.ViewpagerAdapter adapter = new GameActivity.ViewpagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    public static class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> arrayList = new ArrayList<>();
        private ArrayList<String> name = new ArrayList<>();

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            arrayList.add(new ChatFragment());
            arrayList.add(new DeckFragment());

            name.add("채팅");
            name.add("카드");
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return name.get(position);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

    }
    public void gamePlayerDial(){
        gamePlayerDialog.show();
        ListView listGamePlayer = gamePlayerDialog.findViewById(R.id.listGamePlayer);

        ArrayList<String> list = new ArrayList<>();
        list.add("호에에");
        list.add("호에에");
        list.add("호에에");
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, R.layout.listlayout, list);
        listGamePlayer.setAdapter(mAdapter);
    }
}
