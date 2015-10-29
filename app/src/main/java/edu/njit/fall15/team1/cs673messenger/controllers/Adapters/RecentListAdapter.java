package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;

/**
 * Created by jack on 10/26/15.
 */
public class RecentListAdapter extends BaseAdapter {
    private Context context = null;
    private List<MessageModels> modelses = null;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param modelses  The objects to represent in the ListView.
     */
    public RecentListAdapter(Context context, List<MessageModels> modelses) {
        this.context = context;
        this.modelses = modelses;
    }




    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        int count = 0;
        if (null != modelses){
            count = modelses.size();
        }
        return count;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public MessageModels getItem(int position) {
        MessageModels item = null;
        if (null != modelses){
            item = modelses.get(position);
        }

        return item;
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
     * ViewHolder
     */
    private static class ViewHolder {
        TextView name;
        TextView content;
        TextView time;
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
        Log.d(getClass().getSimpleName(), "getView");
        ViewHolder holder;

        if (null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.recentlistitem, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.recentName);
            holder.content = (TextView) convertView.findViewById(R.id.recentContent);
            holder.time = (TextView) convertView.findViewById(R.id.recentTime);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(modelses.get(position).getWithWho().getProfileName());

        if (modelses.get(position).getMessages().size() != 0){
            //Set content
            MessageModel messageModel = modelses.get(position).getMessages().getLast();
            if (messageModel.getType() == MessageModel.MessageType.From){
                String contentString = ":" + messageModel.getMessage();
                holder.content.setText(contentString);
            }else{
                String contentString = "You:" + messageModel.getMessage();
                holder.content.setText(contentString);
            }
            //Set Time
            SimpleDateFormat sF = new SimpleDateFormat("yyyy MMM d");
            String currentDate = sF.format(new Date());
            String messageDate = sF.format(messageModel.getTime());

            if (!currentDate.equals(messageDate)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d");
                holder.time.setText(simpleDateFormat.format(messageModel.getTime()));
            }else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                holder.time.setText(simpleDateFormat.format(messageModel.getTime()));
            }
        }else {
            holder.content.setText("");
            holder.time.setText("");
        }

        // 5. retrn rowView
        Log.d(getClass().getSimpleName(), "getView finished.");
        return convertView;
    }

}
