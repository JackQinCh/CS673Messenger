package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * Created by jack on 10/26/15.
 */
public class RecentListAdapter extends BaseAdapter {
    private List<Messages> recentChats;
    private Context context;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param recentChats  The objects to represent in the ListView.
     */
    public RecentListAdapter(Context context, List<Messages> recentChats) {
        super();
        this.context = context;
        this.recentChats = recentChats;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return recentChats.size();
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
        return recentChats.get(position);
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

        Messages messages = recentChats.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.recentlistitem, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.recentName);
            holder.content = (TextView) convertView.findViewById(R.id.recentContent);
            holder.time = (TextView) convertView.findViewById(R.id.recentTime);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(recentChats.get(position).getName());

        if (recentChats.get(position).getMessages().size() != 0){
            //Set content
            Message message = messages.getMessages().get(recentChats.get(position).getMessages().size()-1);
            if (message.getDirection() == Message.DIRECTION_FROM){
                String contentString = ":" + message.getMessage();
                holder.content.setText(contentString);
            }else if (message.getDirection() == Message.DIRECTION_TO){
                String contentString = "You:" + message.getMessage();
                holder.content.setText(contentString);
            }
            //Set Time
            SimpleDateFormat sF = new SimpleDateFormat("yyyy MMM d");
            String currentDate = sF.format(new Date());
            String messageDate = sF.format(message.getTime());

            if (!currentDate.equals(messageDate)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d");
                holder.time.setText(simpleDateFormat.format(message.getTime()));
            }else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                holder.time.setText(simpleDateFormat.format(message.getTime()));
            }
        }

        // 5. retrn rowView
        return convertView;
    }

    /**
     * ViewHolder
     */
    static class ViewHolder {
        TextView name;
        TextView content;
        TextView time;
    }

}
