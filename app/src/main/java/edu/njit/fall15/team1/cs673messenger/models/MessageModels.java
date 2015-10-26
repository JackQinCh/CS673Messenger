package edu.njit.fall15.team1.cs673messenger.models;

import android.util.Log;

import java.util.LinkedList;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;

/**
 * Created by jack on 10/25/15.
 */
public class MessageModels {
    public LinkedList<MessageModel> getMessages() {
        return messages;
    }

    private LinkedList<MessageModel> messages = new LinkedList<>();
    private Friend withWho;

    public MessageModels(Friend withWho) {
        this.withWho = withWho;
    }

    public Friend getWithWho() {
        return withWho;
    }

    public void addMessage(MessageModel m){
        messages.add(m);
        if (m.getType().equals(MessageModel.MessageType.To)){
            FacebookServer.getInstance().sendMessage(m.getFriend(), m.getMessage());
            Log.d("Jack","Sending message to "+m.getFriend().getProfileName()+":"+m.getMessage());
        }
    }
}
