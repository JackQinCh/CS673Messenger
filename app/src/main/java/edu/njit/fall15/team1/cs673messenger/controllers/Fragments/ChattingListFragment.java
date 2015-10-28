package edu.njit.fall15.team1.cs673messenger.controllers.Fragments;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Activities.ChattingWindowActivity;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.RecentListAdapter;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;

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

        RecentListAdapter adapter = new RecentListAdapter(this.getContext(), getData());
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RecentListAdapter)getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RecentChatsManager.getInstance().removeListener(this);
    }

    private List<MessageModels> getData(){
        return RecentChatsManager.getInstance().getRecentChats();
    }

    @Override
    public void receivedMessage(MessageModel messageModel) {
        ((RecentListAdapter)getListAdapter()).notifyDataSetChanged();
        //Ring
        try{
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), notification);
            ringtone.play();
        }catch (Exception e){
            e.printStackTrace();
        }

        //Other notifications
        //Intent notificationIntent = new Intent(context, MainActivity.class);
        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(),
                0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        Resources res = getContext().getResources();
        Notification.Builder builder = new Notification.Builder(getContext());
        //Notification.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.stat_sys_download)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.facebook_icon))
                .setContentTitle("CS673 App: You have new message!")
                .setContentText(messageModel.getMessage()); // Text from message

        Notification notification = builder.build();

        //Add Clolor Lights
        notification.ledARGB = Color.BLUE;
        notification.ledOffMS = 0;
        notification.ledOnMS = 1;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

        //Add vibrations
        long[] vibrate = new long[] { 1000, 1000};
        notification.vibrate = vibrate;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(673, notification);
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
