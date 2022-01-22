package com.pingmo.cram.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pingmo.cram.R;
import com.pingmo.cram.fragment.Shop1Fragment;
import com.pingmo.cram.fragment.Shop2Fragment;
import com.pingmo.cram.fragment.Shop3Fragment;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        ViewPager pager = findViewById(R.id.pagerShop);
        TabLayout tabLayout = findViewById(R.id.tabShop);
        Button btnShopExit = findViewById(R.id.btnShopExit);

        btnShopExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

    }

    public static class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> arrayList = new ArrayList<>();
        private ArrayList<String> name = new ArrayList<>();

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            arrayList.add(new Shop1Fragment());
            arrayList.add(new Shop2Fragment());
            arrayList.add(new Shop3Fragment());

            name.add("1번 탭");
            name.add("2번 탭");
            name.add("3번 탭");
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
