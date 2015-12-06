package edu.njit.fall15.team1.cs673messenger.APIs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jack on 15/11/24.
 */
public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {
    private ImageView image;
    private LruCache<String, Bitmap> lruCache;
    private int width;
    private int height;
    public AsyncImageLoader(ImageView image, LruCache<String, Bitmap> lruCache) {
        super();
        this.image = image;
        this.lruCache = lruCache;
    }

    public AsyncImageLoader(ImageView image, LruCache<String, Bitmap> lruCache, int width, int height) {
        super();
        this.image = image;
        this.lruCache = lruCache;
        this.width = width;
        this.height = height;
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
            }else if (params[0].contains("https")){
                bitmap = getImage(params[0]);
            }else {
                bitmap = FacebookServer.INSTANCE.getUserPhoto();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null){
            if(width != 0 && height != 0)
                bitmap = scaleImg(bitmap, width, height);
            addBitmapToMemoryCache(params[0], bitmap);
        }
        return bitmap;
    }

    private Bitmap scaleImg(Bitmap bitmap, int width, int height) {
        float scale = width/bitmap.getWidth();
        // 定义矩阵对象
        Matrix matrix = new Matrix();
        // 缩放原图
        matrix.postScale(scale, scale);
        //bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return dstbmp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null)
            return;
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

    public static Bitmap getImage(String urlpath)
            throws Exception {
        URL url = new URL(urlpath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        Bitmap bitmap = null;
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }
        return bitmap;
    }
    
    
}
