package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Presence;

import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.AsyncImageLoader;
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


    /**
     * ViewHolder
     */
    static class ViewHolder {
        ImageView photo;
        TextView name;
        TextView status;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Friend friend = friends.get(position);
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.friendlistitem, null);
            holder = new ViewHolder();
            holder.photo = (ImageView) convertView.findViewById(R.id.friendPhotoImg);
            holder.name = (TextView) convertView.findViewById(R.id.profileName);
            holder.status = (TextView) convertView.findViewById(R.id.activeStatus);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 4. Set the text for textView
        loadBitmap(friend.getUser(), holder.photo);

        holder.name.setText(friend.getProfileName());
        Presence.Type friendStatus = friend.getStatus();
        if (friend.getStatus().equals(Presence.Type.unavailable)){
            holder.status.setTextColor(Color.RED);
        }else if (friend.getStatus().equals(Presence.Type.available)){
            holder.status.setTextColor(Color.GREEN);
        }
        holder.status.setText(friendStatus.toString());

        // 5. retrn rowView
        return convertView;
    }

    private final int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取当前应用程序所分配的最大内存
    private final int cacheSize = maxMemory / 5;//只分5分之一用来做图片缓存
    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(
            cacheSize) {
        protected int sizeOf(String key, Bitmap bitmap) {//复写sizeof()方法
            // replaced by getByteCount() in API 12
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024; //这里是按多少KB来算
        }
    };

    /**
     *
     * @param urlStr 所需要加载的图片的url，以String形式传进来，可以把这个url作为缓存图片的key
     * @param image ImageView 控件
     */
    private void loadBitmap(String urlStr, ImageView image) {
        System.out.println(urlStr);
        AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache);//什么一个异步图片加载对象
        Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);//首先从内存缓存中获取图片
        if (bitmap != null) {
            image.setImageBitmap(bitmap);//如果缓存中存在这张图片则直接设置给ImageView
        } else {
            image.setImageResource(R.drawable.blank_profile);//否则先设置成默认的图片
            asyncLoader.execute(urlStr);//然后执行异步任务AsycnTask 去网上加载图片
        }
    }
}
