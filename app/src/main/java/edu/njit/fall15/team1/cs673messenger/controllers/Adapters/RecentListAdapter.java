package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class RecentListAdapter extends ArrayAdapter<Messages> {
    private Context context = null;
    private List<Messages> modelses = null;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param modelses  The objects to represent in the ListView.
     */
    public RecentListAdapter(Context context, List<Messages> modelses) {
        super(context,0,modelses);
        this.context = context;
        this.modelses = modelses;
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
            Message message = modelses.get(position).getMessages().getLast();
            if (message.getType() == Message.MessageType.From){
                String contentString = ":" + message.getMessage();
                holder.content.setText(contentString);
            }else{
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
        }else {
            holder.content.setText("");
            holder.time.setText("");
        }

        // 5. retrn rowView
        Log.d(getClass().getSimpleName(), "getView finished.");
        return convertView;
    }

}
