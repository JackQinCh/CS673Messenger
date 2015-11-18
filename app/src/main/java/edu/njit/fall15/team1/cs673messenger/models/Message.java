package edu.njit.fall15.team1.cs673messenger.models;

import java.util.Date;

/**
 * Created by jack on 10/25/15.
 */
public final class Message {
    private Friend friend;
    private int type;
    private Date time;
    private String message;

    public final static int MESSAGE_FROM = 0;
    public final static int MESSAGE_TO   = 1;

    public Message(int type, Friend friend, Date time, String message) {
        this.friend = friend;
        this.type = type;
        this.time = time;
        this.message = message;
    }

    public Friend getFriend() {
        return friend;
    }

    public int getType() {
        return type;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String messageString;
        messageString = type+","+
                friend.getProfileName()+","+
                time.toString()+","+
                message.toString();
        return messageString;
    }
}
