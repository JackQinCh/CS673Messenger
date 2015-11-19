package edu.njit.fall15.team1.cs673messenger.controllers.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.GroupListAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 10/14/15.
 */
public class GroupChatFragment extends ListFragment implements RecentChatsListener {
    protected boolean isCreated = false;
    private GroupListAdapter adapter;
    private List<Messages> groupChats = new LinkedList<>();
    private View rootView;

    public GroupChatFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecentChatsManager.INSTANCE.addListener(this);
        isCreated = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.groupchatfragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null){
            adapter = new GroupListAdapter(getActivity(), groupChats);
            setListAdapter(adapter);
        }
    }

    /**
     * Detect page visibility
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isCreated) {
            return;
        }
        if (isVisibleToUser) {
            pageStart();
        }else {
            pageEnd();
        }
    }

    /**
     * pageStart
     */
    private void pageStart() {
        updateData();
    }

    /**
     * pageEnd
     */
    private void pageEnd() {
    }

    private void updateData() {
        if (rootView !=null && adapter != null){
            List<Messages> list = RecentChatsManager.INSTANCE.getGroupChats();
            TextView groupNumber = (TextView)rootView.findViewById(R.id.numberOfGroupLabel);
            groupNumber.setText("Your groups(" + list.size() + ")");
            groupChats.clear();
            groupChats.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void receivedMessage(Message message) {

    }
}
