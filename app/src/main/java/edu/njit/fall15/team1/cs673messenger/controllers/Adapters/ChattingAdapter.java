package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Message;

/**
 * Created by jack on 10/25/15.
 */
public class ChattingAdapter extends BaseAdapter {
    private Context context;

    private List<Message> chatMessages;   //关联数据
    //析构函数
    public ChattingAdapter(Context context, List<Message> messages) {
        super();
        this.context = context;
        this.chatMessages = messages;

    }

    @Override
    public int getCount() {                 //返回数据源中总的记录数目
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {   //获得选择的数据源中某个项目的数据
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {   //获取数据源中的索引值
        return position;
    }

    @Override                               //获取要展示的项目View对象
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Message message = chatMessages.get(position);
        if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != message.getDirection()) {

            holder = new ViewHolder();
            if (message.getDirection() == Message.DIRECTION_FROM) {
                holder.flag = Message.DIRECTION_FROM;
                convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);

            } else {
                holder.flag = Message.DIRECTION_TO;
                convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to, null);
            }

            holder.text = (TextView) convertView.findViewById(R.id.chatting_content_itv);
            convertView.setTag(holder);
        }
        holder.text.setText(message.getMessage());

        return convertView;
    }
    //优化listview的Adapter
    static class ViewHolder {
        TextView text;
        int flag;
    }

}
