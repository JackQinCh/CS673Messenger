package edu.njit.fall15.team1.cs673messenger.XMPP;

import android.os.AsyncTask;
import android.os.Build;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
                        Log.d("DENIS", "Connected as user  " + connection.getUser());
                        // Set the status to available
                        Presence presence = new Presence(Presence.Type.available);
                        // Set the status to unavailable
                        //Presence presence = new Presence(Presence.Type.unavailable);
                        connection.sendPacket(presence);

                        Roster roster = connection.getRoster();
                        Collection<RosterEntry> entries = roster.getEntries();
                        Log.d("DENIS", "Your Friend List detected: " + "\n\n" + entries.size() + " Friends :");

                        // shows first time onliners---->
                        String temp[] = new String[50];
                        int i = 0;
                        for (RosterEntry entry : entries) {
                            String user = entry.getUser();
                            String name = entry.getName();
                            Log.i("DENIS", "Friend's username is " + name + " [" + entry.getStatus() + "] " + user + " Type: " + entry.getType());
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
                                    if (message.getBody() != null) {
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