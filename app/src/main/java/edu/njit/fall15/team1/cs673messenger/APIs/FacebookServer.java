package edu.njit.fall15.team1.cs673messenger.APIs;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import edu.njit.fall15.team1.cs673messenger.models.Messages;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

/**
 * Singleton of Facebook Server
 * Created by jack on 10/21/15.
 */
public enum FacebookServer implements PacketListener, ConnectionCreationListener{
    INSTANCE;

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
        if (connection != null)
            connection.disconnect();
    }

    public void sendMessage(edu.njit.fall15.team1.cs673messenger.models.Message message){
        if (connection != null){
            AsyncTask<edu.njit.fall15.team1.cs673messenger.models.Message, Void, Void> asyncTask = new AsyncTask<edu.njit.fall15.team1.cs673messenger.models.Message, Void, Void>() {
                @Override
                protected Void doInBackground(edu.njit.fall15.team1.cs673messenger.models.Message... params) {
                    String to = params[0].getFriend().getUser();
                    String messageText = params[0].getMessage();
                    Message msg = new Message(to, Message.Type.chat);
                    msg.setBody(messageText);
                    connection.sendPacket(msg);
                    return null;
                }
            };
            asyncTask.execute(message);
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
            Log.d(getClass().getSimpleName(), "Receive a mesage:"+message);
            if (listeners.size() != 0){
                Log.d(getClass().getSimpleName(), "Notify the message:"+message);
                for (FacebookServerListener listener:listeners){
                    if (msg.getType() == Message.Type.chat){
                        listener.facebookReceivedMessage(Messages.PERSONAL_CHAT, from, message, new Date());
                    }else if (msg.getType() == Message.Type.groupchat){
                        listener.facebookReceivedMessage(Messages.GROUP_CHAT, from, message, new Date());
                    }
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
            Log.d(getClass().getSimpleName(),connection.getUser());
            return connection.getUser().replaceAll("@chat.facebook.com/Smack","");//Not true Username.
        }
        else
            return null;
    }


}
