package edu.njit.fall15.team1.cs673messenger.XMPP;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
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
import java.util.ArrayList;
import java.util.Collection;

import edu.njit.fall15.team1.cs673messenger.R;

/**
 * Responsible for establishing a connection between a client and an XMPP server.
 * Provides authentication and connection methods, and callbacks for handling
 * connection failures.
 * Library used: aSmack 4.0.0
 *
 * @author Richard Lopes
 * @version %I%, %G%
 * @since 1.0
 */

public class XMPP {
    /**
     * IP address or Domain address from the XMPP Server that will be connected to.
     */
    private String serverAddress;

    /**
     * Contains all information and methods about the management of the connection.
     */
    private XMPPConnection connection;

    /**
     * User login name without domain name: {userName}@{domainName}
     */
    private String loginUser;

    /**
     * User password
     */
    private String passwordUser;

    private ArrayList<String> messages = new ArrayList();


    public XMPP(String serverAddress, String loginUser, String passwordUser) {
        //this.serverAddress = serverAddress;
        //this.loginUser = loginUser;
        //this.passwordUser = passwordUser;
        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
    }

    /**
     * Creates an AsyncTask to starts a connection usign serverAddress attribute from this class.
     * It also attach a listener  to handle with changes on connection, like fall down.
     */
    public void XMPPconnect() {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... arg0) {
                boolean isConnected = false;

                ConnectionConfiguration config = new ConnectionConfiguration(serverAddress);
                config.setReconnectionAllowed(true);

                config.setSASLAuthenticationEnabled(true);
                //config.setCompressionEnabled(true);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    config.setTruststoreType("AndroidCAStore");
                    config.setTruststorePassword(null);
                    config.setTruststorePath(null);
                } else {
                    config.setTruststoreType("BKS");
                    String path = System.getProperty("javax.net.ssl.trustStore");
                    if (path == null)
                        path = System.getProperty("java.home") + File.separator + "etc"
                                + File.separator + "security" + File.separator
                                + "cacerts.bks";
                    config.setTruststorePath(path);
                }


                connection = new XMPPConnection(config);


                //			XMPPConnectionListener connectionListener = new XMPPConnectionListener();
                //			connection.addConnectionListener(connectionListener);

                try {
                    connection.connect();
                    isConnected = true;
                    Log.d("DENIS", "Is Connected = " + connection.isConnected());
                    Log.d("DENIS", "Connected to " + connection.getHost());

                    try {
                        SASLAuthentication.supportSASLMechanism("PLAIN");
                        connection.login(loginUser, passwordUser);
                        Log.d("DENIS", "Connected as user  " + connection.getUser() + " " + connection.getChatManager());
                        Log.d("DENIS", "Connected as user  " + connection.getConnectionID() + " " + connection.getChatManager());

                        Log.d("DENIS", "****************GET**LOGIN**PICTURE***********");
                        try {
                            String numericID = getFacebookID(trimUsername(connection.getUser(),false));

                            if (numericID != null) {
                                Log.i("Hack", "I got Your Facebook ID = "+ numericID + " to show user picture!");

                                try {
                                    Bitmap bitmap = getFacebookProfilePicture(numericID);

                                    if (bitmap != null) {
                                        Log.i("DENIS", "I got something to show logined user picture!");
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("DENIS", "****************GET**LOGIN**PICTURE***********");


                        // Set the status to available
                        Presence presence = new Presence(Presence.Type.available);
                        // Set the status to unavailable
                        //Presence presence = new Presence(Presence.Type.unavailable);
                        connection.sendPacket(presence);




                        Roster roster = connection.getRoster();
                        Collection<RosterEntry> entries = roster.getEntries();
                        Log.d("DENIS", "Your Friend List detected: " + "\n\n" + entries.size() + " FriendsManager :");

                        // shows first time onliners---->
                        String temp[] = new String[50];
                        int i = 0;
                        for (RosterEntry entry : entries) {
                            String user = entry.getUser();
                            String name = entry.getName();
                            //gives the availability of user.
                            Presence friendPresence = connection.getRoster().getPresence(user);
                            //friendPresence.getType();
                            Log.i("DENIS", "Friend's username is " + name + " [" + friendPresence.getType() + "] " + user + " Type: " + entry.getType());

                            Log.d("DENIS", "****************GET**USER**PICTURE***********");
                            // trim value for user to paste it in URL for Graph API
                           /* String usrid;
                            usrid = user.substring(1);
                            String[] arr = usrid.split("@");
                            usrid = arr[0];
                            Log.d("DENIS", usrid);*/
                            try {
                                Bitmap bitmap = getFacebookProfilePicture(trimUsername(user, true));

                                if (bitmap != null) {
                                    Log.i("DENIS", "I got something to show user picture!");
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                            Log.d("DENIS", "****************GET**USER**PICTURE***********");
                        }


                        Log.i("DENIS", "Message List Starts");
                        if (connection != null) {
                            Log.i("DENIS", "I have connection to work with messages!");

                            //Send TEST Message
                            String to = "-100009730428072@chat.facebook.com";   //Zhonghua Qin
                            //String to = "-100010110347263@chat.facebook.com"; //Jack Qin
                            String text = "This is a test Hello message from the code!!!";


                            Message msg = new Message(to, Message.Type.chat);
                            msg.setBody(text);
                            messages.add(connection.getUser() + ":");
                            messages.add(text);
                            //connection.sendPacket(msg);
                            Log.wtf("DENIS", "Received messages code starts");
                            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
                            PacketListener pListener = new PacketListener() {
                                public void processPacket(Packet packet) {
                                    Message message = (Message) packet;
                                    Context context = BackendTEST.getContext();
                                    if (message.getBody() != null) {
                                        Log.wtf("DENIS", "Ringtones code starts");
                                        try {
                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
                                            ringtone.play();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //Other notifications
                                        //Intent notificationIntent = new Intent(context, MainActivity.class);
                                        Intent notificationIntent = new Intent();
                                        PendingIntent contentIntent = PendingIntent.getActivity(context,
                                                0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                        Resources res = context.getResources();
                                        Notification.Builder builder = new Notification.Builder(context);
                                        //Notification.Builder builder = new NotificationCompat.Builder(context);
                                        builder.setContentIntent(contentIntent)
                                                .setSmallIcon(R.drawable.stat_sys_download)
                                                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.facebook_icon))
                                                .setContentTitle("CS673 App: You have new message!")
                                                .setContentText(message.getBody()); // Text from message

                                        Notification notification = builder.build();

                                        //Add Clolor Lights
                                        notification.ledARGB = Color.BLUE;
                                        notification.ledOffMS = 0;
                                        notification.ledOnMS = 1;
                                        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

                                        //Add vibrations
                                        long[] vibrate = new long[] { 1000, 1000};
                                        notification.vibrate = vibrate;

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                        notificationManager.notify(673, notification);
                                        //

                                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                                        Log.e("DENIS", "Got text [" + message.getBody()
                                                + "] from [" + fromName + "]");
                                        messages.add(fromName + ":");
                                        messages.add(message.getBody());

                                        // Add the incoming message to the list view
                                /*	mHandler.post(new Runnable() {
										public void run() {
											setListAdapter();
										}
									});*/
                                    }
                                }
                            };
                            Log.wtf("DENIS", "Received messages code finished");
                            connection.addPacketListener(pListener, filter);


					/*	//if (connection != null) {
							// Add a packet listener to get messages sent to us
						Log.i("DENIS", "Message List Start");
							PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
							connection.addPacketListener(new PacketListener() {
								public void processPacket(Packet packet) {
									Message message = (Message) packet;
									if (message.getBody() != null) {
										String fromName = StringUtils.parseBareAddress(message.getFrom());
										Log.i("DENIS", "Got text [" + message.getBody() + "] from [" + fromName + "]");
										messages.add(fromName + ":");
										messages.add(message.getBody());

									} else Log.i("DENIS", "GOT NOTHING!!!");
								}
							}, filter);
						//}
							Log.wtf("DENIS", "Chat code starts");
							ChatManager chatmanager = connection.getChatManager();
							Chat newChat = chatmanager.createChat(to,
									new MessageListener() {
										@Override
										public void processMessage(Chat chat, Message message) {
											try {
												chat.sendMessage(message.getBody());
												chat.sendMessage("It was your message?");
												Log.i("DENIS", "Got chat text [" + message.getBody() + "] from [" + message.getFrom() + "]");
											} catch (XMPPException e) {
												e.printStackTrace();
											}

										}
									});
							newChat.sendMessage(text);
							Log.wtf("DENIS", "Chat code finished");
							//Log.wtf("DENIS", "Messages collection code starts");*/

                        }


                    } catch (XMPPException e) {
                        Log.d("DENIS", "Failed to log in as " + loginUser);
                    }

                } //catch (IOException e){
                //}
                catch (XMPPException e) {
                    Log.d("DENIS", "Not Connected!!!");
                }

                return isConnected;
            }
        };
        connectionThread.execute();
    }

    public static String trimUsername (String fullusername, Boolean trimleft)
    {
        String usrid;

        if (trimleft == true) {
            usrid = fullusername.substring(1);
        } else {
            usrid = fullusername;
        }

        String[] arr = usrid.split("@");
        usrid = arr[0];
        Log.d("DENIS", usrid);
        return usrid;
    }
    public static Bitmap getFacebookProfilePicture(String userID)
    throws MalformedURLException, IOException  {
        String imageURL;
        Bitmap bitmap = null;
        imageURL = "https://graph.facebook.com/" + userID
                + "/picture?type=large";

            URL url1 = new URL(imageURL);
            HttpURLConnection ucon1 = (HttpURLConnection) url1.openConnection();
            ucon1.setInstanceFollowRedirects(false);
            //URL secondURL1 = new URL(ucon1.getHeaderField("Location"));
            InputStream in = (InputStream) new URL(imageURL).getContent();
            bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }

    public static String getFacebookID(String userName)
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
        //Log.d("Hack Server response", responseStr);

        //search value for tag code
        String start = "code>";
        String end = "</code";
        String part = responseStr.substring(responseStr.indexOf(start) + start.length());
        numericID = part.substring(0, part.indexOf(end));


        /*//Document document = Jsoup.connect("https://www.facebook.com/" + userName).get();
        Document document = Jsoup.parse(responseStr);
        Elements meta = document.select("meta[property]");
        if (!meta.isEmpty()) {

            for (Element element : meta) {
                final String s = element.attr("content");
                if (s != null) numericID = s;
            }

            //numericID = meta.attr("content");
        }
        else {
            numericID = "BAD RESULT ";
        }*/
        return numericID;

    }

    /**
     * Provides an authentication to the server using an username and a password.
     *
     * @param  connection    {@link connection}
     * @param  loginUser    {@link loginUser}
     * @param  passwordUser    {@link passwordUser}
     */
/*	private void login(XMPPConnection connection, final String loginUser, final String passwordUser){
		try{
			SASLAuthentication.supportSASLMechanism("PLAIN");
			connection.login(loginUser, passwordUser);
			Log.d("DENIS", "Connected as user  " + connection.getUser());

		}
		catch (NotConnectedException e){
			// If is not connected, a timer is schelude and a it will try to reconnect
			new Timer().schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					connect(loginUser, passwordUser);
				}
			}, 5 * 1000);			
		}

//		catch (SaslException e){
//		}
		catch (XMPPException e){
			Log.d("DENIS", "Failed to log in as " + loginUser);
		}
//		catch (SmackException e){
//		}
//		catch (IOException e){
//		}
	}

	/**
	 * Listener for changes in connection
	 * @see ConnectionListener from org.jivesoftware.smack
	 */
/*	public class XMPPConnectionListener implements ConnectionListener {
		@Override
		public void connected(final XMPPConnection connection){
			if(!connection.isAuthenticated())
				//login(connection, loginUser, passwordUser);
				Log.d("DENIS", "STUMB");
		}
		@Override
		public void authenticated(XMPPConnection arg0){}
		@Override
		public void connectionClosed(){}
		@Override
		public void connectionClosedOnError(Exception arg0){}
		@Override
		public void reconnectingIn(int arg0){}
		@Override
		public void reconnectionFailed(Exception arg0){}
		@Override
		public void reconnectionSuccessful(){}
	}
*/

}