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
import edu.njit.fall15.team1.cs673messenger.controllers.Activities.FragmentListener;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.RecentListAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;

/**
 * Created by jack on 10/5/15.
 */
public class ChattingListFragment extends ListFragment implements RecentChatsListener{

    private RecentListAdapter adapter = null;
    protected boolean isCreated = false;
    private List<MessageModels> messageModelses = new LinkedList<>();
    private FragmentListener listener;

    public ChattingListFragment(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecentChatsManager.getInstance().addListener(this);
        Log.d(getClass().getSimpleName(), "onCreate");

        isCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(),"onCreateView");
        return inflater.inflate(R.layout.chatlistfragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        if (adapter == null){
            adapter = new RecentListAdapter(this.getContext(), messageModelses);
            setListAdapter(adapter);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
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
        if (listener != null){
            listener.updateFragments(0);
        }
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
        RecentChatsManager.getInstance().removeListener(this);
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
        Log.d(getClass().getSimpleName(), "pageStart");
        updateData();
    }

    /**
     * pageEnd
     */
    private void pageEnd() {
        Log.d(getClass().getSimpleName(), "pageEnd");
    }


    /**
     * Get listview data
     * @return list of MessageModels
     */
    private void updateData(){
        messageModelses.clear();
        messageModelses.addAll(RecentChatsManager.getInstance().getRecentChats());
        if (adapter != null){
            ((RecentListAdapter)getListAdapter()).notifyDataSetChanged();
        }
        Log.d(getClass().getSimpleName(), "getData for list view");
    }


    /**
     * Receive message feedback
     * @param messageModel
     */
    @Override
    public void receivedMessage(MessageModel messageModel) {
        Log.d(getClass().getSimpleName(), "receivedMessage: " + messageModel.getMessage());
        if (listener != null){
            listener.updateFragments(0);
        }
        //Check setting notify.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean notifySetting = sp.getBoolean("notifyMe", true);
        Log.d(getClass().getSimpleName(), String.valueOf(notifySetting));

        notifyMessage(notifySetting, messageModel.getMessage());
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
        Friend friend;
        if (RecentChatsManager.getInstance().getRecentChats().size() != 0){
            friend = RecentChatsManager.getInstance().getRecentChats().get(position).getWithWho();

            Intent intent = new Intent();
            intent.putExtra("FriendUser",friend.getUser());
            intent.setClass(this.getActivity(), ChattingWindowActivity.class);
            startActivity(intent);
        }
    }

    public void setListener(FragmentListener listener){
        this.listener = listener;
    }

    public void update(){
        TextView chatNumText = (TextView)getActivity().findViewById(R.id.recentChatNumber);
        int chatNum = RecentChatsManager.getInstance().getRecentChats().size();
        Log.d(getClass().getSimpleName(),"Update number of chats:"+chatNum);
        chatNumText.setText("Recent Chats(" + chatNum + ")");
        updateData();
    }
}
