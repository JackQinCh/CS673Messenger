package edu.njit.fall15.team1.cs673messenger.controllers;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Friends;

/**
 * Created by jack on 10/14/15.
 */
public class FriendsListFragment extends ListFragment {
    private Friends friendsFactory = new Friends();
    private LinkedList<Friend> friends;

    public FriendsListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friends = friendsFactory.getFriends();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlistfragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1,
                getFriendsData()));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Jack","Display FriendListFragment");

        friends.clear();
        friends = friendsFactory.getFriends();

        TextView textView = (TextView) getActivity().findViewById(R.id.textview);
        if (friends.size() == 0){
            textView.setText("No Friends");
            textView.setEnabled(true);
        }else{
            textView.setEnabled(false);
            ((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
        }

        //Jack test
        for (Friend friend:friends) {
            Log.d("Jack", friend.getProfileName() + "," +
                    friend.getUser() + "," +
                    friend.getStatus() + "," +
                    friend.getType() + ".");
        }
    }


    /**
     * ListView Adapter get data Method
     * @return List<String> ListView Datas
     */
    private List<String> getFriendsData() {
        List<String> data = new LinkedList<>();
        if (friends != null && friends.size() != 0){
            for (Friend friend:friends) {
                data.add(friend.getProfileName());
            }
        }

        return data;
    }
}
