package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;

/**
 * Created by jack on 10/26/15.
 */
public class FriendListItemAdapter extends ArrayAdapter<Friend> {
    private final Context context;
    private final List<Friend> friends;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param friends  The objects to represent in the ListView.
     */
    public FriendListItemAdapter(Context context, List<Friend> friends) {
        super(context, 0, friends);

        this.context = context;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.friendlistitem, parent, false);

        // 3. Get the two text view from the rowView
        TextView profileName = (TextView) rowView.findViewById(R.id.profileName);
        TextView activeStatus = (TextView) rowView.findViewById(R.id.activeStatus);

        // 4. Set the text for textView
        profileName.setText(friends.get(position).getProfileName());
        activeStatus.setText(friends.get(position).getStatus().toString());

        // 5. retrn rowView
        return rowView;
    }
}
