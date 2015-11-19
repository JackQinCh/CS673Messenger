package edu.njit.fall15.team1.cs673messenger.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;

/**
 * Created by jack on 10/25/15.
 */
public final class Messages {

    private List<Message> messages = new LinkedList<>();
    private List<Friend> members = new LinkedList<>();
    private final String chatId;
    private final int type;
    private String name;

    public final static int PERSONAL_CHAT = 0;
    public final static int GROUP_CHAT = 1;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getType() {
        return type;
    }

    public String getChatId() {
        return chatId;
    }

    private Messages(int type, String chatId) {
        this.type = type;
        this.chatId = chatId;

    }

    /**
     * Static Factory generates a Personal Messages package.
     * @param withWho
     * @return
     */
    public static Messages newPersonalMessages(Friend withWho){
        Messages ms = new Messages(PERSONAL_CHAT, withWho.getUser());
        ms.addMember(withWho);
        ms.setName(withWho.getProfileName());
        return ms;
    }

    /**
     * Static Factory generates a Group Messages package.
     * @return
     */
    public static Messages newGroupMessages(String name, List<Friend> members){
        Date date = new Date();
        Messages messages = new Messages(GROUP_CHAT, date.toString());
        messages.setName(name);
        if (members.size() != 0){
            for (Friend f:members)
                messages.addMember(f);
        }
        return messages;
    }

    public boolean checkId(String id){
        return chatId.equals(id);
    }

    public void addMember(Friend f){
        members.add(f);
    }

    public void removeMember(Friend f){
        if (members.size() != 0){
            for (Friend friend:members) {
                if (friend.isEquals(f))
                    members.remove(friend);
            }
        }
    }

    public List<Friend> getMembers() {
        List<Friend> friends = new ArrayList<>();
        friends.addAll(members);
        return friends;
    }

    public void addMessage(Message m){
        messages.add(m);
        if (m.getDirection() == Message.DIRECTION_TO){
            FacebookServer.INSTANCE.sendMessage(m);
            Log.d(getClass().getSimpleName(),"Sending message to "+m.getFriend().getProfileName()+":"+m.getMessage());
        }
    }
    public List<Message> getMessages() {
        List<Message> ms = new ArrayList<>();
        ms.addAll(messages);
        return ms;
    }
}
