package com.pingmo.cram.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.pingmo.cram.R;
import com.pingmo.cram.activity.MainActivity;
import com.pingmo.cram.activity.NoticeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    public Dialog dialogQuit;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        Button btnNotice = rootView.findViewById(R.id.btnNotice);
        Button btnAsk = rootView.findViewById(R.id.btnAsk);
        Button btnQuit = rootView.findViewById(R.id.btnQuit);


        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noticeIntent = new Intent(getActivity(), NoticeActivity.class);
                noticeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(noticeIntent);
            }
        });

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kakaoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.kakao.com/o/st82Ogpd"));
                startActivity(kakaoIntent);
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQuit = new Dialog(getActivity());
                dialogQuit.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogQuit.setContentView(R.layout.dial_quit);
                dialogQuit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialQuit();
            }
        });

        return rootView;
    }
    public void dialQuit(){
        dialogQuit.show();

        Button btnQuit = dialogQuit.findViewById(R.id.btnQuit);
        Button btnQuitExit = dialogQuit.findViewById(R.id.btnQuitExit);

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).deleteUser();
            }
        });

        btnQuitExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQuit.dismiss();
            }
        });

    }
}