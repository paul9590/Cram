package com.pingmo.cram.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pingmo.cram.Cram;
import com.pingmo.cram.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeckFragment extends Fragment {

    ImageView [] imgDeck;
    int [] deck = new int[6];
    Cram cram = Cram.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeckFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeckFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeckFragment newInstance(String param1, String param2) {
        DeckFragment fragment = new DeckFragment();
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
        View rootView =  inflater.inflate(R.layout.fragment_deck, container, false);

        imgDeck = new ImageView[]{
                rootView.findViewById(R.id.imgDeck1),
                rootView.findViewById(R.id.imgDeck2),
                rootView.findViewById(R.id.imgDeck3),
                rootView.findViewById(R.id.imgDeck4),
                rootView.findViewById(R.id.imgDeck5),
                rootView.findViewById(R.id.imgDeck6),
        };
        // 액티비티 301 호출 핸드 제출
        for(int i = 0; i < imgDeck.length; i++) {
            int num = i;
            imgDeck[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cram.isConnected()) {
                        imgDeck[num].setColorFilter(R.color.secondColor);
                        Toast.makeText(getActivity(), "" + deck[num] + "을 냈습니다.", Toast.LENGTH_SHORT).show();
                        for(int j = 0; j < imgDeck.length; j++){
                            imgDeck[j].setClickable(false);
                        }
                        try {
                            JSONObject sendData = new JSONObject();
                            sendData.put("what", 301);
                            sendData.put("pop", Integer.toString(deck[num]));
                            cram.send(sendData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getActivity(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return rootView;
    }

    public void setDeck(int [] deck) {
        this.deck = deck;
        int card [] = {
                R.drawable.layoutshape,
                R.drawable.card1,
                R.drawable.card2,
                R.drawable.card3,
                R.drawable.card4,
                R.drawable.card5,
                R.drawable.card6,
                R.drawable.card7,
                R.drawable.card8,
                R.drawable.card9,
                R.drawable.card10,
                R.drawable.card11,
                R.drawable.card12,
                R.drawable.card13,
                R.drawable.card14,
                R.drawable.card15,
        };

        for(int i = 0; i < imgDeck.length; i++) {
            imgDeck[i].setClickable(false);
            imgDeck[i].setColorFilter(Color.parseColor("#00000000"));
            imgDeck[i].setImageResource(card[0]);
        }

        for(int i = 0; i < deck.length; i++) {
            if(deck[i] > 0) {
                imgDeck[i].setClickable(true);
            }
            imgDeck[i].setImageResource(card[deck[i]]);
        }
    }
    public void setClickable(boolean [] deckClick){
        for(int i = 0; i < deckClick.length; i++) {
            imgDeck[i].setColorFilter(Color.parseColor("#00000000"));
            imgDeck[i].setClickable(deckClick[i]);
            if(!deckClick[i]){
                imgDeck[i].setColorFilter(R.color.mainColor);
            }
        }
    }
}