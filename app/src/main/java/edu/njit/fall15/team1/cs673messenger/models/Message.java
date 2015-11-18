package edu.njit.fall15.team1.cs673messenger.models;

import java.util.Date;

/**
 * Created by jack on 10/25/15.
 */
public final class Message {
    private Friend friend;
    private MessageType type;
    private Date time;
    private String message;

    public enum MessageType{
        From(0), To(1);

        private int _value;

        MessageType(int value){
            _value = value;
        }

        public int value(){
            return _value;
        }
    }

    public Message(MessageType type, Friend friend, Date time, String message) {
        this.friend = friend;
        this.type = type;
        this.time = time;
        this.message = message;
    }

    public Friend getFriend() {
        return friend;
    }

    public MessageType getType() {
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
        messageString = type.toString()+","+
                friend.getProfileName()+","+
                time.toString()+","+
                message.toString();
        return messageString;
    }
}
