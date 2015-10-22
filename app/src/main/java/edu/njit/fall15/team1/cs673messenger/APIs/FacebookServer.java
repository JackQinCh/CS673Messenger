package edu.njit.fall15.team1.cs673messenger.APIs;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;

/**
 * Singleton of Facebook Server
 * Created by jack on 10/21/15.
 */
public class FacebookServer {

    /**
     * Instance holder
     */
    private static class FacebookHolder{
        private static FacebookServer instance = new FacebookServer();
    }

    /**
     * Private Constructor
     */
    private FacebookServer(){}

    /**
     * Get FacebookServer Singleton Instance
     * @return FacebookServer
     */
    public static FacebookServer getInstance(){
        return FacebookHolder.instance;
    }

    /**
     * Contains all information and methods about the management of the connection.
     */
    public XMPPConnection connection = null;

    /**
     * Messages
     */
    private ArrayList<String> messages = new ArrayList();
    /**
     * Delegate Pattern
     */
    FacebookServerListener connectionListener = null;

    /**
     * Facebook server Connect
     * @param serverAddress
     */
    public void connect(String serverAddress){
        connection = new XMPPConnection(setConnectConfig(serverAddress));

        final AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    connection.connect();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
            //isConnectSuccess
                Log.d("Jack","Facebook connect:"+result);
                if (connectionListener != null)
                    connectionListener.facebookConnected(result);
            }
        };
        asyncTask.execute();
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
                    }

                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (connectionListener != null)
                    connectionListener.facebookLogined(result);
            }
        };
        asyncTask.execute(username, password);

    }



    /**
     * Set ConnectionListener
     * @param listener
     */
    public void setConnectionListener(FacebookServerListener listener){
        this.connectionListener = listener;
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

}
