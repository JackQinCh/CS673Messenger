package edu.njit.fall15.team1.cs673messenger.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.njit.fall15.team1.cs673messenger.R;

/**
 * Created by jack on 10/14/15.
 */
public class FriendsListFragment extends Fragment {
    public FriendsListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friendlistfragment, container, false);
    }
}
