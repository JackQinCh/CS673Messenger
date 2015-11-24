package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * Created by jack on 15/11/19.
 */
public class GroupListAdapter extends BaseAdapter {
    private List<Messages> groupChats;
    Context context;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param groupChats  The objects to represent in the ListView.
     */
    public GroupListAdapter(Context context, List<Messages> groupChats) {
        super();
        this.context = context;
        this.groupChats = groupChats;
    }

    /**
     * ViewHolder
     */
    static class ViewHolder {
        TextView groupName;
        TextView members;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return groupChats.size();
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
        return groupChats.get(position);
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

    /**
     * getView
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Messages messages = groupChats.get(position);
        if (null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.group_list_item, null);
            holder = new ViewHolder();
            holder.groupName = (TextView) convertView.findViewById(R.id.groupNameLabel);
            holder.members = (TextView) convertView.findViewById(R.id.groupMembersLabel);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupName.setText(groupChats.get(position).getName());

        String membersStr = "You and ";
        for (Friend member:messages.getMembers()){
            membersStr += member.getProfileName() + ", ";
        }
        holder.members.setText(membersStr);

        // 5. retrn rowView
        return convertView;
    }
}
