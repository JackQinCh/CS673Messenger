package edu.njit.fall15.team1.cs673messenger.APIs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;

import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.models.Friend;

/**
 * Singleton of Facebook Server
 * Created by jack on 10/21/15.
 */
public enum FacebookServer implements PacketListener, ConnectionCreationListener{
    INSTANCE;

    private String userId;
    @Override
    public void connectionCreated(Connection connection) {
        if (this.connection == connection){
            Log.d(getClass().getSimpleName(), "connectionCreated");
            if (listeners.size() != 0){
                for (FacebookServerListener listener:listeners)
                    listener.facebookConnected(true);
            }
        }
    }

    /**
     * Contains all information and methods about the management of the connection.
     */
    private XMPPConnection connection;
    /**
     * LinkedList of FacebookServerListener
     */
    private LinkedList<FacebookServerListener> listeners = new LinkedList<>();

    public void addListeners(FacebookServerListener listener){
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }

    }
    public void removeListeners(FacebookServerListener listener){
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    /**
     * Facebook server Connect
     * @param serverAddress
     */
    public void connect(String serverAddress){
        final String host = serverAddress;

        final AsyncTask<FacebookServer, Void, Void> asyncTask = new AsyncTask<FacebookServer, Void, Void>() {

            @Override
            protected Void doInBackground(FacebookServer... params) {
                PacketListener packetListener = params[0];
                ConnectionCreationListener connectionCreationListener = params[0];
                if (connection == null){
                    connection = new XMPPConnection(setConnectConfig(host));
                    connection.addPacketListener(packetListener, new MessageFilter(Message.Type.chat, Message.Type.groupchat));
                    XMPPConnection.addConnectionCreationListener(connectionCreationListener);
                }
                try {
                    connection.connect();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

        };
        asyncTask.execute(this);
    }

    /**
     * Set DisConnect
     */
    public void disConnect(){
        if (connection != null){
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    connection.disconnect();
                    return null;
                }
            };
            asyncTask.execute();
        }

    }

    public void sendMessage(final edu.njit.fall15.team1.cs673messenger.models.Message message){
        if (connection != null){
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    for (Friend f:message.getFriend()){
                        String to = f.getUser();
                        String messageText = message.getPacketString();
                        Message msg = new Message(to, Message.Type.chat);
                        msg.setBody(messageText);
                        connection.sendPacket(msg);
                    }
                    return null;
                }
            };
            asyncTask.execute();
        }
    }

    /**
     * Facebook server Login
     * @param username
     * @param password
     */
    public void login(String username, String password){
        AsyncTask<String, Void, Boolean> asyncTask = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                String username = params[0];
                String password = params[1];
                if (connection != null && connection.isConnected()){
                    try {
                        connection.login(username, password);
                        userId = getFacebookID(trimUsername(connection.getUser(), false));
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        return false;
                    } catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }else {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (listeners.size() != 0){
                    for (FacebookServerListener listener:listeners)
                        listener.facebookLogined(result);
                }
            }
        };
        asyncTask.execute(username, password);
    }

    /**
     * Set Facebook login status
     * @param status
     */
    public void setActiveStatus(boolean status){
        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... params) {
                Boolean status = params[0];
                Presence presence;
                if (status){
                    presence = new Presence(Presence.Type.available);
                    Log.i(this.getClass().getSimpleName(),"Setting active status.");
                }else {
                    presence = new Presence(Presence.Type.unavailable);
                    Log.i(this.getClass().getSimpleName(),"Setting inactive status.");
                }
                try{
                    connection.sendPacket(presence);
                }catch (IllegalStateException e){
                    return false;
                }catch (NullPointerException e){
                    return false;
                }
                return true;
            }
        };
        asyncTask.execute(status);
    }


    /**
     * Get connnection status;
     * @return
     */
    public boolean isConnected(){
        return connection.isConnected();
    }

    /**
     * Get login status;
     * @return
     */
    public boolean isLogin(){
        return connection.isAuthenticated();
    }

    /**
     * Get status;
     * @return
     */
    public boolean getStatus(){
        return connection.isSendPresence();
    }

    /**
     * Get Roster
     * @return
     */
    public Roster getRoster(){
        if (connection.isConnected() && connection.isAuthenticated()){
            return connection.getRoster();
        }
        return null;
    }

    /**
     * Set Connect Configration
     * @param host
     * @return
     */
    private ConnectionConfiguration setConnectConfig(String host){
        ConnectionConfiguration config = new ConnectionConfiguration(host);
        config.setReconnectionAllowed(true);

        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            config.setTruststoreType("AndroidCAStore");
            config.setTruststorePassword(null);
            config.setTruststorePath(null);
        } else {
            config.setTruststoreType("BKS");
            String path = System.getProperty("javax.net.ssl.trustStore");
            if (path == null)
                path = System.getProperty("java.home") +
                        File.separator +
                        "etc" +
                        File.separator +
                        "security" +
                        File.separator +
                        "cacerts.bks";
            config.setTruststorePath(path);
        }
        return config;
    }

    /**
     * PacketListener method
     * @param packet
     */
    @Override
    public void processPacket(Packet packet) {
        String from;
        String message;
        Message msg = (Message) packet;
        if (msg.getBody() != null){
            from = StringUtils.parseBareAddress(msg.getFrom());
            message = msg.getBody();
            Log.d(getClass().getSimpleName(), "Receive a message:" + message);
            if (listeners.size() != 0){
                Log.d(getClass().getSimpleName(), "Notify the message:"+message);
                for (FacebookServerListener listener:listeners){
                    listener.facebookReceivedMessage(from, message, new Date());
                }
            }
        }
    }

    /**
     * Get User Name
     * @return
     */
    public String getUserName(){
        if (connection != null && connection.isConnected()){
            String userName = trimUsername(connection.getUser(), false);
            Log.d(getClass().getSimpleName(), userName);
            return userName;//Not true Username.
        }
        else
            return null;
    }

    /**
     * Get user photo
     * @param userID
     * @return Bitmap
     * @throws MalformedURLException
     * @throws IOException
     */
    private Bitmap getFacebookProfilePicture(String userID) throws IOException {

        Log.d(getClass().getSimpleName(),"getFacebookProfilePicture:"+userID);
        String imageURL;
        Bitmap bitmap;
        imageURL = "https://graph.facebook.com/" + userID
                + "/picture?type=large";

        URL url1 = new URL(imageURL);
        HttpURLConnection ucon1 = (HttpURLConnection) url1.openConnection();
        ucon1.setInstanceFollowRedirects(false);
        InputStream in = (InputStream) new URL(imageURL).getContent();
        bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }

    /**
     * Get Friend's photo
     * @param userID
     * @return
     * @throws IOException
     */
    public Bitmap getFriendPhoto(String userID) throws IOException {
        String userId = trimUsername(userID, true);
        return getFacebookProfilePicture(userId);
    }

    /**
     * Get user's photo
     * @return
     * @throws Exception
     */
    public Bitmap getUserPhoto() throws Exception {
        return getFacebookProfilePicture(trimUsername(getUserID(), false));
    }

    public String getUserID() throws Exception {
        Log.d(getClass().getSimpleName(), "User ID: "+userId);
        return userId;
    }

    private static String trimUsername (String fullusername, Boolean trimleft)
    {
        String usrid;

        if (trimleft == true) {
            usrid = fullusername.substring(1);
        } else {
            usrid = fullusername;
        }

//        int index = usrid.lastIndexOf("@");
//        usrid = usrid.substring(0, index);

        String[] arr = usrid.split("@");
        usrid = arr[0];

//        String[] arr = usrid.split("@");
//        usrid = arr[0];
        Log.d("DENIS", usrid);
        return usrid;
    }

    private static String getFacebookID(String userName)
            throws Exception {
        String numericID = null;
        HttpURLConnection connection = null;
        //URL url = new URL("https://m.facebook.com/" + userName);
        URL url = new URL("http://findmyfbid.com");


        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(
                connection.getOutputStream());
        wr.writeBytes("url=https%3A%2F%2Fm.facebook.com%2F"+ userName);
        wr.flush();
        wr.close();
        // Get Response
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();
        String responseStr = response.toString();
        Log.d("FacebookServer Hack Server response", responseStr);

        //search value for tag code
        String start = "code>";
        String end = "</code";
        String part = responseStr.substring(responseStr.indexOf(start) + start.length());
        numericID = part.substring(0, part.indexOf(end));


        return numericID;

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
    public void loadBitmap(String urlStr, ImageView image) {
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

    public void loadBitmap(String urlStr, ImageView image, int width, int heigh) {
        System.out.println(urlStr);
        AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache, width, heigh);//什么一个异步图片加载对象
        Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);//首先从内存缓存中获取图片
        if (bitmap != null) {
            image.setImageBitmap(bitmap);//如果缓存中存在这张图片则直接设置给ImageView
        } else {
            image.setImageResource(R.drawable.blank_profile);//否则先设置成默认的图片
            asyncLoader.execute(urlStr);//然后执行异步任务AsycnTask 去网上加载图片
        }
    }
}
