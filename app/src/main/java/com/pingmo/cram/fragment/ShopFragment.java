package com.pingmo.cram.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pingmo.cram.R;
import com.pingmo.cram.adapter.RecyclerShopAdapter;
import com.pingmo.cram.list.RecyclerShopList;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {

    RecyclerView mRecyclerView = null;
    RecyclerShopAdapter mAdapter = null;
    ArrayList<RecyclerShopList> mList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopFragment() {
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
    public static ShopFragment newInstance(String param1, String param2) {
        ShopFragment fragment = new ShopFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);


        mRecyclerView = rootView.findViewById(R.id.viewShop);

        mList = new ArrayList<>();
        mAdapter = new RecyclerShopAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        Bundle bundle = this.getArguments();
        if(bundle != null){
            addItem(bundle.getStringArray("shopMain"), bundle.getIntArray("shopRes"));
        }
        mAdapter.notifyDataSetChanged();

        return rootView;
    }

    private void addItem(String shopMain [], int shopRes []) {

        for(int i = 0; i < shopMain.length; i++) {
            RecyclerShopList item = new RecyclerShopList();
            item.setShopMain(shopMain[i]);
            item.setShopRes(shopRes[i]);
            mList.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }
}