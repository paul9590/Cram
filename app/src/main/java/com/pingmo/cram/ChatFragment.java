package com.pingmo.cram;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    RecyclerView mRecyclerView = null;
    RecyclerChatAdapter mAdapter = null;
    ArrayList<RecyclerChatList> mList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        EditText editChat = (EditText) rootView.findViewById(R.id.editChat);
        Button btnChatSend = (Button) rootView.findViewById(R.id.btnChatSend);


        editChat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    if (!editChat.getText().toString().trim().equals("")) {
                        Message msg = new Message();
                        msg.obj = editChat.getText().toString();
                        ((GameActivity)getActivity()).client.writeHandler.sendMessage(msg);
                    }
                    return true;
                }
                return false;
            }
        });
        btnChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editChat.getText().toString().trim().equals("")) {
                    Message msg = new Message();
                    msg.obj = editChat.getText().toString();
                    ((GameActivity) getActivity()).client.writeHandler.sendMessage(msg);
                }
            }
        });

        mRecyclerView = rootView.findViewById(R.id.viewChat);

        mList = new ArrayList<>();
        mAdapter = new RecyclerChatAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        // 이부분 건들여서 서버 측 소스 코드 받아 올 것
        String name;
        String chat;
        for(int i = 0; i < 100; i++) {
            name = "paul";
            chat = "호에에";
            addItem(name, chat);
        }
        return rootView;
    }

    private void addItem(String name, String chat) {
        RecyclerChatList item = new RecyclerChatList();
        item.setName(name);
        item.setChat(chat);
        mList.add(item);
    }
}