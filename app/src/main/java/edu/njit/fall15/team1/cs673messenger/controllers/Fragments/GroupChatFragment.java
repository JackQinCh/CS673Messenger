package edu.njit.fall15.team1.cs673messenger.controllers.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.GroupListAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * Created by jack on 10/14/15.
 */
public class GroupChatFragment extends ListFragment implements RecentChatsListener {
    protected boolean isCreated = false;
    private GroupListAdapter adapter;
    private ArrayList<Messages> groupChats = new ArrayList<>();
    private View rootView;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public GroupChatFragment(){
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GroupChatFragment newInstance(int sectionNumber) {
        GroupChatFragment fragment = new GroupChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
            updateData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RecentChatsManager.INSTANCE.removeListener(this);
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
        Log.d(getClass().getSimpleName(), "Page start");
        updateData();
    }

    /**
     * pageEnd
     */
    private void pageEnd() {
    }

    /**
     * Update chat list data
     */
    private void updateData() {
        if (rootView != null && adapter != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Messages> list = RecentChatsManager.INSTANCE.getGroupChats();
                    TextView groupNumber = (TextView) rootView.findViewById(R.id.numberOfGroupLabel);
                    groupNumber.setText("Your groups(" + list.size() + ")");

                    groupChats.clear();
                    groupChats.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public void updateGUI() {
        Log.d(getClass().getSimpleName(), "updateGUI Update GUI list.");
        updateData();
    }

    @Override
    public void receivedMessage(Message message) {
        Log.d(getClass().getSimpleName(), "receivedMessage Update GUI list.");
        updateData();
    }
}
