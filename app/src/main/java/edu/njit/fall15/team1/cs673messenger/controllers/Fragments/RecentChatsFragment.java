package edu.njit.fall15.team1.cs673messenger.controllers.Fragments;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Activities.ChattingWindowActivity;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.RecentListAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * Created by jack on 10/5/15.
 */
public class RecentChatsFragment extends ListFragment implements RecentChatsListener{
    int n = 0;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecentListAdapter adapter;
    protected boolean isCreated = false;
    private List<Messages> messages = new LinkedList<>();

    View rootView;

    public RecentChatsFragment(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecentChatsManager.INSTANCE.addListener(this);
        Log.d(getClass().getSimpleName(), "onCreate");

        isCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreateView");
        rootView = inflater.inflate(R.layout.chatlistfragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onActivityCreated");

        adapter = new RecentListAdapter(this.getContext(), messages);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy");
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
        updateData();
        Log.d(getClass().getSimpleName(), "pageStart");
    }

    /**
     * pageEnd
     */
    private void pageEnd() {
        Log.d(getClass().getSimpleName(), "pageEnd");
    }


    /**
     * Get listview data
     * @return list of Messages
     */
    private void updateData(){
        if (rootView != null && adapter != null){
            List<Messages> recentChats = RecentChatsManager.INSTANCE.getRecentChats();
            TextView retentText = (TextView)rootView.findViewById(R.id.recentChatNumber);
            retentText.setText("Recent chats(" + recentChats.size() + ")");
            messages.clear();
            messages.addAll(recentChats);
            adapter.notifyDataSetChanged();
            Log.d(getClass().getSimpleName(), "getData for list view");
        }
    }


    /**
     * Receive message feedback
     * @param message
     */
    @Override
    public void receivedMessage(Message message) {
        Log.d(getClass().getSimpleName(), "receivedMessage: " + message.getMessage());
        updateData();
        //Check setting notify.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean notifySetting = sp.getBoolean("notifyMe", true);
        Log.d(getClass().getSimpleName(), String.valueOf(notifySetting));

        notifyMessage(notifySetting, message.getMessage());
    }

    /**
     * Notify receive message
     * @param notifySetting boolean
     * @param notifyMessage String
     */
    private void notifyMessage(boolean notifySetting, String notifyMessage){
        if (notifySetting) {
            //Ring
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getContext(), notification);
                ringtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Other notifications
            //Intent notificationIntent = new Intent(context, MainActivity.class);
            Intent notificationIntent = new Intent();
            PendingIntent contentIntent = PendingIntent.getActivity(getContext(),
                    0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Resources res = getContext().getResources();
            Notification.Builder builder = new Notification.Builder(getContext());
            //Notification.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.stat_sys_download)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.facebook_icon))
                    .setContentTitle("CS673 App: You have new message!")
                    .setContentText(notifyMessage); // Text from message

            Notification notification = builder.build();

            //Add Clolor Lights
            notification.ledARGB = Color.BLUE;
            notification.ledOffMS = 0;
            notification.ledOnMS = 1;
            notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

            //Add vibrations
            long[] vibrate = new long[]{1000, 1000};
            notification.vibrate = vibrate;

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            notificationManager.notify(673, notification);
        }
    }

    /**
     * List view select action
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        List<Messages> recentChats = RecentChatsManager.INSTANCE.getRecentChats();
        if (recentChats.size() != 0){
            String chatId = recentChats.get(position).getChatId();
            Log.d(getClass().getSimpleName(), chatId);
            Intent intent = new Intent();
            intent.putExtra(getActivity().getString(R.string.chat_id),chatId);
            intent.setClass(this.getActivity(), ChattingWindowActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecentChatsFragment newInstance(int sectionNumber) {
        RecentChatsFragment fragment = new RecentChatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


}
