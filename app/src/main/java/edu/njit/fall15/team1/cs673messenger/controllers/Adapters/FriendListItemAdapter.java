package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Presence;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;

/**
 * Created by jack on 10/26/15.
 */
public class FriendListItemAdapter extends BaseAdapter {
    private final Context context;
    private final List<Friend> friends;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param friends  The objects to represent in the ListView.
     */
    public FriendListItemAdapter(Context context, List<Friend> friends) {
        super();

        this.context = context;
        this.friends = friends;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return friends.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = friends.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.friendlistitem, null);
        ImageView photo = (ImageView) convertView.findViewById(R.id.friendPhotoImg);
        TextView name = (TextView) convertView.findViewById(R.id.profileName);
        TextView status = (TextView) convertView.findViewById(R.id.activeStatus);

        FacebookServer.INSTANCE.loadBitmap(friend.getUser(), photo);

        name.setText(friend.getProfileName());
        Presence.Type friendStatus = friend.getStatus();
        if (friend.getStatus().equals(Presence.Type.unavailable)){
            status.setTextColor(Color.RED);
        }else if (friend.getStatus().equals(Presence.Type.available)){
            status.setTextColor(Color.GREEN);
        }
        status.setText(friendStatus.toString());

        return convertView;
    }

}
