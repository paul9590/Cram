package com.pingmo.cram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pingmo.cram.fragment.ChatFragment;
import com.pingmo.cram.fragment.DeckFragment;
import com.pingmo.cram.R;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {


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
                    Toast.makeText(getApplicationContext(), "" + v.getX(), Toast.LENGTH_SHORT).show();
                    v.getY();
                }
            });
        }

        txtGameTitle.setText(roomName);

        GameActivity.ViewpagerAdapter adapter = new GameActivity.ViewpagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

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
}
