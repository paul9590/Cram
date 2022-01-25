package com.pingmo.cram.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.cram.R;
import com.pingmo.cram.User;
import com.pingmo.cram.activity.MainActivity;
import com.pingmo.cram.adapter.RecyclerFriendAdapter;
import com.pingmo.cram.list.RecyclerFriendList;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    RecyclerView mRecyclerView = null;
    RecyclerFriendAdapter mAdapter = null;
    ArrayList<RecyclerFriendList> mList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mRecyclerView = rootView.findViewById(R.id.viewFriend);

        ImageView imgUser2 = rootView.findViewById(R.id.imgUser2);

        TextView txtUser = rootView.findViewById(R.id.txtUser);
        TextView txtUserInfo = rootView.findViewById(R.id.txtUserInfo);

        Button btnLogOut = rootView.findViewById(R.id.btnLogOut);

        imgUser2.setImageResource(R.drawable.help);

        User curUser = ((MainActivity)getActivity()).curUser;

        txtUser.setText(curUser.getName());
        StringBuilder sb = new StringBuilder();
        sb.append("점수 : " + curUser.getRank() + "\n");
        sb.append("캐시 : " + curUser.getCash());
        txtUserInfo.setText(sb.toString());
        DrawerLayout drawLay = getActivity().findViewById(R.id.drawLay);

        mList = new ArrayList<>();

        Boolean isOnline;
        String name;

        // 이부분 건들여서 서버 측 소스 코드 받아 올 것
        for(int i = 0; i < 100; i++) {
            isOnline = true;
            name = "paul";
            addItem(isOnline, name);
        }

        mAdapter = new RecyclerFriendAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    drawLay.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                }else{
                    drawLay.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).signOut();
                drawLay.closeDrawers();
            }
        });

        return rootView;
    }

    private void addItem(Boolean isOnline, String name) {
        RecyclerFriendList item = new RecyclerFriendList();
        item.setOnline(isOnline);
        item.setName(name);
        mList.add(item);
    }
}