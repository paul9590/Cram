package com.pingmo.cram.activity;

import android.graphics.drawable.Drawable;
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
import com.pingmo.cram.ShopArray;
import com.pingmo.cram.fragment.ShopFragment;

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
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    public class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> arrayList = new ArrayList<>();
        private ArrayList<String> name = new ArrayList<>();

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            ShopArray shopArray = new ShopArray();
            ShopFragment shopFr1 = new ShopFragment();
            ShopFragment shopFr2 = new ShopFragment();
            ShopFragment shopFr3 = new ShopFragment();

            Bundle bundle1 = new Bundle();
            bundle1.putStringArray("shopMain", shopArray.getShopMain1());
            bundle1.putIntArray("shopRes", shopArray.getShopRes1());
            shopFr1.setArguments(bundle1);

            Bundle bundle2 = new Bundle();
            bundle2.putStringArray("shopMain", shopArray.getShopMain2());
            bundle2.putIntArray("shopRes", shopArray.getShopRes2());
            shopFr2.setArguments(bundle2);

            Bundle bundle3 = new Bundle();
            bundle3.putStringArray("shopMain", shopArray.getShopMain3());
            bundle3.putIntArray("shopRes", shopArray.getShopRes3());
            shopFr3.setArguments(bundle3);

            arrayList.add(shopFr1);
            arrayList.add(shopFr2);
            arrayList.add(shopFr3);

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
