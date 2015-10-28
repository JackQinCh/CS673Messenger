package edu.njit.fall15.team1.cs673messenger.controllers.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Activities.ChattingWindowActivity;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.FriendListItemAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Friends;

/**
 * Created by jack on 10/14/15.
 */
public class FriendsListFragment extends ListFragment {
    private Friends friendsFactory = new Friends();
    private List<Friend> friends;

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

        FriendListItemAdapter adapter = new FriendListItemAdapter(this.getContext(), getFriendsData());
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
        TextView numOfFriend = (TextView)getActivity().findViewById(R.id.numberOfFriendLabel);
        numOfFriend.setText("Your friends("+friendsFactory.getFriends().size()+")");
        ((FriendListItemAdapter)getListAdapter()).notifyDataSetChanged();
        friends = getFriendsData();
    }

    /**
     * ListView Adapter get data Method
     * @return List<String> ListView Datas
     */
    private List<Friend> getFriendsData() {

        return friendsFactory.getFriends();
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
        intent.putExtra("FriendUser",friend.getUser());
        intent.setClass(this.getActivity(), ChattingWindowActivity.class);
        startActivity(intent);
    }
}
