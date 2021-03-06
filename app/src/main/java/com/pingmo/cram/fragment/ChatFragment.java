package com.pingmo.cram.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingmo.cram.Cram;
import com.pingmo.cram.R;
import com.pingmo.cram.adapter.RecyclerChatAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    RecyclerView mRecyclerView = null;
    RecyclerChatAdapter mAdapter = null;
    public ArrayList<String> mList;
    Cram cram = Cram.getInstance();

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

        EditText editChat = rootView.findViewById(R.id.editChat);
        Button btnChatSend = (Button) rootView.findViewById(R.id.btnChatSend);
        mRecyclerView = rootView.findViewById(R.id.viewChat);

        editChat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    if (!editChat.getText().toString().trim().equals("")) {
                        if(cram.isConnected()) {
                            try {
                                JSONObject sendData = new JSONObject();
                                sendData.put("what", 200);
                                sendData.put("chat", editChat.getText().toString());
                                cram.send(sendData.toString());
                                editChat.setText("");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getActivity(), "????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        }
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
                    if(cram.isConnected()) {
                        try {
                            JSONObject sendData = new JSONObject();
                            sendData.put("what", 200);
                            sendData.put("chat", editChat.getText().toString());
                            cram.send(sendData.toString());
                            editChat.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getActivity(), "????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mList = new ArrayList<>();
        mAdapter = new RecyclerChatAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        return rootView;
    }

    public void addChat(String s){
        mList.add(s);
        mAdapter.notifyDataSetChanged();
    }
}