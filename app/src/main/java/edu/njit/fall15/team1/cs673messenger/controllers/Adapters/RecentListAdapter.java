package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class RecentListAdapter extends ArrayAdapter<MessageModels> {
    private final Context context;
    private final List<MessageModels> modelses;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param modelses  The objects to represent in the ListView.
     */
    public RecentListAdapter(Context context, List<MessageModels> modelses) {
        super(context, 0, modelses);

        this.context = context;
        this.modelses = modelses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.recentlistitem, parent, false);

        // 3. Get the two text view from the rowView
        TextView name = (TextView) rowView.findViewById(R.id.recentName);
        TextView content = (TextView) rowView.findViewById(R.id.recentContent);
        TextView time = (TextView) rowView.findViewById(R.id.recentTime);

        // 4. Set the text for textView

        name.setText(modelses.get(position).getWithWho().getProfileName());
        if (modelses.get(position).getMessages().size() != 0){
            //Set content
            MessageModel messageModel = modelses.get(position).getMessages().getLast();
            if (messageModel.getType() == MessageModel.MessageType.From){
                String contentString = ":" + messageModel.getMessage();
                content.setText(contentString);
            }else{
                String contentString = "You:" + messageModel.getMessage();
                content.setText(contentString);
            }
            //Set Time
            SimpleDateFormat sF = new SimpleDateFormat("yyyy MMM d");
            String currentDate = sF.format(new Date());
            String messageDate = sF.format(messageModel.getTime());

            if (!currentDate.equals(messageDate)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d");
                time.setText(simpleDateFormat.format(messageModel.getTime()));
            }else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                time.setText(simpleDateFormat.format(messageModel.getTime()));
            }
        }else {
            content.setText("");
            time.setText("");
        }

        // 5. retrn rowView
        return rowView;
    }
}
