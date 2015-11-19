package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

import java.util.List;

/**
 * Created by jack on 15/11/19.
 */
public class GroupListAdapter extends ArrayAdapter<Messages> {
    private Context context = null;
    private List<Messages> groupChats = null;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param groupChats  The objects to represent in the ListView.
     */
    public GroupListAdapter(Context context, List<Messages> groupChats) {
        super(context, 0, groupChats);
        this.context  = context;
        this.groupChats = groupChats;
    }

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        TextView groupName;
        TextView members;
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
        ViewHolder holder;

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
        for (Friend member:groupChats.get(position).getMembers()){
            membersStr += member.getProfileName() + ", ";
        }
        holder.members.setText(membersStr);

        // 5. retrn rowView
        return convertView;
    }
}
