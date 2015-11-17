package edu.njit.fall15.team1.cs673messenger.models;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;

/**
 * Created by jack on 10/25/15.
 */
public class Messages {

    private LinkedList<Message> messages = new LinkedList<>();
    private List<Friend> members = new LinkedList<>();
    private Friend withWho;
    private String chatId;
    private int type;

    public final static int PERSONAL_CHAT = 0;
    public final static int GROUP_CHAT = 1;

    public Messages(Friend withWho) {
        this.withWho = withWho;
    }

    public Friend getWithWho() {
        return withWho;
    }

    public void addMessage(Message m){
        messages.add(m);
        if (m.getType().equals(Message.MessageType.To)){
            FacebookServer.getInstance().sendMessage(m.getFriend(), m.getMessage());
            Log.d(getClass().getSimpleName(),"Sending message to "+m.getFriend().getProfileName()+":"+m.getMessage());
        }
    }
    public LinkedList<Message> getMessages() {
        return messages;
    }
}
