package com.pingmo.cram;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Shop1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Shop1Fragment extends Fragment {

    RecyclerView mRecyclerView = null;
    RecyclerViewAdapter mAdapter = null;
    ArrayList<RecyclerShopItem> mList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Shop1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Shop1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Shop1Fragment newInstance(String param1, String param2) {
        Shop1Fragment fragment = new Shop1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shop1, container, false);

        Drawable mImageDrawable [];
        String mMainText [];
        String mSubText [];

        mRecyclerView = rootView.findViewById(R.id.viewShop1);

        mList = new ArrayList<>();
        mAdapter = new RecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mImageDrawable = new Drawable[4];
        mMainText = new String[4];
        mSubText = new String[4];

        // 이부분 건들여서 서버 측 소스 코드 받아 올 것
        for(int i = 0; i < 4; i++) {
            mImageDrawable[i] = ResourcesCompat.getDrawable(getResources(), R.drawable.help, null);
            mMainText[i] = "Crocus";
            mSubText[i] = "www.crocus.co.kr";
        }

        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        addItem(mImageDrawable, mMainText, mSubText);
        mAdapter.notifyDataSetChanged();

        return rootView;
    }

    private void addItem(Drawable icon [], String mainText [], String subText []) {
        RecyclerShopItem item = new RecyclerShopItem();
        item.setImgShop1(icon[0]);
        item.setMainText1(mainText[0]);
        item.setSubText1(subText[0]);

        item.setImgShop2(icon[1]);
        item.setMainText2(mainText[1]);
        item.setSubText2(subText[1]);

        item.setImgShop3(icon[2]);
        item.setMainText3(mainText[2]);
        item.setSubText3(subText[2]);

        item.setImgShop4(icon[3]);
        item.setMainText4(mainText[3]);
        item.setSubText4(subText[3]);
        mList.add(item);
    }
}