package edu.njit.fall15.team1.cs673messenger.APIs;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by jack on 15/11/24.
 */
public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {
    private ImageView image;
    private LruCache<String, Bitmap> lruCache;
//    private int width;
//    private int height;
    public AsyncImageLoader(ImageView image, LruCache<String, Bitmap> lruCache) {
        super();
        this.image = image;
        this.lruCache = lruCache;
//        this.width=width;
//        this.height=width;
    }
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            if (params[0].startsWith("-")){
                bitmap = FacebookServer.INSTANCE.getFriendPhoto(params[0]);
            }else {
                bitmap = FacebookServer.INSTANCE.getUserPhoto();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addBitmapToMemoryCache(params[0], bitmap);
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }
    //调用LruCache的put 方法将图片加入内存缓存中，要给这个图片一个key 方便下次从缓存中取出来
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            lruCache.put(key, bitmap);
        }
    }
    //调用Lrucache的get 方法从内存缓存中去图片
    public Bitmap getBitmapFromMemoryCache(String key) {
        return lruCache.get(key);
    }
}
