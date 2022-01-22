package com.pingmo.cram.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pingmo.cram.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeckFragment extends Fragment {

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

        ImageView [] imgDeck = {
                (ImageView) rootView.findViewById(R.id.imgDeck1),
                (ImageView) rootView.findViewById(R.id.imgDeck2),
                (ImageView) rootView.findViewById(R.id.imgDeck3),
                (ImageView) rootView.findViewById(R.id.imgDeck4),
                (ImageView) rootView.findViewById(R.id.imgDeck5),
                (ImageView) rootView.findViewById(R.id.imgDeck6),
        };
        for(int i = 0; i < imgDeck.length; i++) {
            imgDeck[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "?", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return rootView;
    }
}