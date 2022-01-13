package com.pingmo.cram;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    public Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        TextView txtGameTitle = findViewById(R.id.txtGameTitle);
        //서버 통신으로 받아 올 것

        ViewPager pager = findViewById(R.id.pagerGame);
        TabLayout tabLayout = findViewById(R.id.tabGame);

        GameActivity.ViewpagerAdapter adapter = new GameActivity.ViewpagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        client = new Client(getString(R.string.SERVERIP), Integer.parseInt(getString(R.string.PORT)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.flagConnection = false;
        client.isConnected = false;

        if (client.socket != null) {
            client.flagRead = false;
            client.writeHandler.getLooper().quit();
            try {
                client.bout.close();
                client.bin.close();
                client.socket.close();
            } catch (IOException e) {
            }
        }
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
