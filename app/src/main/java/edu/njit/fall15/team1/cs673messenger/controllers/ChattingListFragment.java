package edu.njit.fall15.team1.cs673messenger.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;
import edu.njit.fall15.team1.cs673messenger.models.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.models.RecentChatsManager;

/**
 * Created by jack on 10/5/15.
 */
public class ChattingListFragment extends ListFragment implements RecentChatsListener{

    public ChattingListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecentChatsManager.getInstance().addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chatlistfragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1,
                getData()));
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RecentChatsManager.getInstance().removeListener(this);
    }

    private List<String> getData(){
        List<String> recentChatsList = new LinkedList<>();

        if (RecentChatsManager.getInstance().getRecentChats().size() != 0){
            for(MessageModels models:RecentChatsManager.getInstance().getRecentChats()){
                String title = "Chating with " + models.getWithWho().getProfileName();
                recentChatsList.add(title);
            }
        }

        return recentChatsList;
    }

    @Override
    public void receivedMessage(MessageModel messageModel) {
        ((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Friend friend;
        if (RecentChatsManager.getInstance().getRecentChats().size() != 0){
            friend = RecentChatsManager.getInstance().getRecentChats().get(position).getWithWho();

            Intent intent = new Intent();
            intent.putExtra("FriendUser",friend.getUser());
            intent.setClass(this.getActivity(), ChattingWindowActivity.class);
            startActivity(intent);
        }
    }
}
