package com.pingmo.cram.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pingmo.cram.fragment.ChatFragment;
import com.pingmo.cram.fragment.DeckFragment;

import java.util.ArrayList;

public class GameViewAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    ChatFragment chatFr = new ChatFragment();
    DeckFragment deckFr = new DeckFragment();

    public GameViewAdapter(@NonNull FragmentManager fm) {
        super(fm);
        arrayList.add(chatFr);
        arrayList.add(deckFr);

        name.add("채팅");
        name.add("카드");
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

    public void addChat(String s){
        chatFr.addChat(s);
    }

    public void setDeck(int [] deck){
        deckFr.setDeck(deck);
    }
    public void setClickable(boolean [] deckClick) {
        deckFr.setClickable(deckClick);
    }
}