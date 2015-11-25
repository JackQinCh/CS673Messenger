package edu.njit.fall15.team1.cs673messenger.controllers.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Activities.ChattingWindowActivity;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.FriendListItemAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;

/**
 * Created by jack on 10/14/15.
 */
public class FriendsListFragment extends ListFragment {
    private List<Friend> friends = new ArrayList<>();
    private FriendListItemAdapter adapter;

    public FriendsListFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friends = getFriendsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlistfragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new FriendListItemAdapter(getActivity(), friends);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshFriends();
    }

    /**
     * Refresh friends data
     */
    private void refreshFriends(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                friends = getFriendsData();
                TextView numOfFriendLabel = (TextView) getActivity().findViewById(R.id.numberOfFriendLabel);
                numOfFriendLabel.setText("Your friends(" + friends.size() + ")");
                adapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * ListView Adapter get data Method
     * @return List<String> ListView Datas
     */
    private List<Friend> getFriendsData() {

        return FriendsManager.getFriends();
    }

    /**
     * List view touch event
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Bug dangers
        Friend friend = friends.get(position);
        //Change to other page
        Intent intent = new Intent();
        intent.putExtra(getActivity().getString(R.string.chat_id),friend.getUser());
        intent.setClass(this.getActivity(), ChattingWindowActivity.class);
        startActivity(intent);
    }
}
