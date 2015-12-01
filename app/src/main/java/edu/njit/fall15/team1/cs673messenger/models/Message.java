package edu.njit.fall15.team1.cs673messenger.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FriendsManager;

/**
 * Created by jack on 10/25/15.
 */
public final class Message {
    private final String name;

    public String getName() {
        return name;
    }

    private final String chatID;

    public String getChatID() {
        return chatID;
    }

    private final List<Friend> friends;
    private final int direction;
    private final Date time;
    private final String messageContent;
    private final int command;
    private final int type;
    private final String extra;
    private final String packetString;

    public String getPacketString() {
        return packetString;
    }

    public final static int DIRECTION_FROM = 0;
    public final static int DIRECTION_TO   = 1;

    public final static int TYPE_CHAT  = 0;
    public final static int TYPE_GROUP = 1;

    public final static int COMMAND_NONE = 0;
    public final static int COMMAND_CREATE_GROUP = 1;
    public final static int COMMAND_GROUP_CHAT = 2;
    public final static int COMMAND_CREATE_TASK = 3;
    public final static int COMMAND_REMOVE_TASK = 4;
    public final static int COMMAND_SHARE_LOCATION = 5;
    public final static int COMMAND_CREATE_EVENT = 6;

    public final static String HEADER_TYPE = "<TYPE:";
    public final static String HEADER_NAME = "<NAME:";
    public final static String HEADER_ID = "<ID:";
    public final static String HEADER_COMMAND = "<COMMAND:";
    public final static String HEADER_MEMBERS = "<MEMBERS:";
    public final static String HEADER_EXTRA = "<EXTRA:";
    public final static String HEADER_END = ">";

    private Message(String name, String chatID, int type, int command, int direction, List<Friend> friends, Date time, String messageContent, String extra) {
        this.name = name;
        this.chatID = chatID;
        this.type = type;
        this.command = command;
        this.direction = direction;
        this.friends = friends;
        this.time = time;
        this.messageContent = messageContent;
        this.extra = extra;

        //Packet string
        if (type == TYPE_CHAT)
            packetString = messageContent;
        else if (type == TYPE_GROUP){
            String membersStr = "";
            for (Friend f:friends)
                membersStr += f.getUser() + ",";
            packetString = HEADER_TYPE + type + HEADER_END
                    + HEADER_NAME + name + HEADER_END
                    + HEADER_ID + chatID + HEADER_END
                    + HEADER_COMMAND + command + HEADER_END
                    + HEADER_MEMBERS + membersStr + HEADER_END
                    + HEADER_EXTRA + extra + HEADER_END
                    + messageContent;
        }else packetString = "";
    }

    public static Message createPersonalMessage(int direction, Friend friend, String messageContent){
        List<Friend> friends = new ArrayList<>();
        friends.add(friend);
        String messageText = messageContent;
        String extra = "";
        if (messageContent.contains("https")) {
            int index = messageContent.indexOf("https");
            String imageURL = messageContent.substring(index, messageContent.length());
            messageText = messageContent.substring(0, index);
            extra = imageURL;
        }

        Message message = new Message(
                friend.getProfileName(),
                friend.getUser(),
                TYPE_CHAT,
                COMMAND_NONE,
                direction,
                friends,
                new Date(),
                messageText,
                extra);
        return message;
    }

    public static Message createGroupMessage(String name,String chatID, int command, int direction, List<Friend> friends, Date time, String messageContent, String extra){
        Message message = new Message(
                name,
                chatID,
                TYPE_GROUP,
                command,
                direction,
                friends,
                time,
                messageContent,
                extra);
        Log.d(Message.class.getSimpleName(), message.toString());
        return message;

    }


    public String getExtra() {
        return extra;
    }

    /**
     * Static Generation Factory with received message
     * @param s String
     * @param friend Friend
     * @param time Date
     * @return Message
     */
    public static Message createWithReceivedMessage( String s, Friend friend, Date time ){
        String packetStr = s;
        Log.d(Message.class.getSimpleName(), packetStr);
        List<Friend> friends = new ArrayList<>();
        friends.add(friend);
        if (friend == null)
            return null;
        String name = null;
        String ID = null;
        String messageText = "";
        int command = COMMAND_NONE;
        String extra = "";
        //Type
        if (packetStr.startsWith(HEADER_TYPE+TYPE_GROUP+HEADER_END)){//Type: group chat
            packetStr = packetStr.replace(HEADER_TYPE+TYPE_GROUP+HEADER_END, "");
            Log.d(Message.class.getSimpleName(), packetStr);
        }else{//Type: chat
            messageText = packetStr;
            return createPersonalMessage(Message.DIRECTION_FROM, friend, messageText);
        }
        //Name
        if (packetStr.startsWith(HEADER_NAME)){
            String nameStr = packetStr.substring(0, packetStr.indexOf(HEADER_END)+1);
            name = nameStr.substring(6, nameStr.length()-1);
            Log.d(Message.class.getSimpleName(),"Group name:"+name);
            packetStr = packetStr.replace(nameStr, "");
            Log.d(Message.class.getSimpleName(), packetStr);
        }
        //Chat Id
        if (packetStr.startsWith(HEADER_ID)){
            String IDStr = packetStr.substring(0, packetStr.indexOf(HEADER_END)+1);
            ID = IDStr.substring(4, IDStr.length()-1);
            Log.d(Message.class.getSimpleName(),"Group ID:"+ID);
            packetStr = packetStr.replace(IDStr, "");
            Log.d(Message.class.getSimpleName(), packetStr);
        }
        //Command
        if (packetStr.startsWith(HEADER_COMMAND)){
            String commandStr = packetStr.substring(0, packetStr.indexOf(HEADER_END)+1);
            command = Integer.parseInt(commandStr.substring(9, commandStr.length()-1));
            Log.d(Message.class.getSimpleName(),"Command:"+command);
            packetStr = packetStr.replace(commandStr,"");
            Log.d(Message.class.getSimpleName(), packetStr);
        }
        //Members in create group command.
        String memberStr = packetStr.substring(0, packetStr.indexOf(HEADER_END)+1);
        if (command == Message.COMMAND_CREATE_GROUP){
            String[] membersStr = memberStr.substring(9, memberStr.length()-2).split(",");
            Friend f;
            for (String userID:membersStr){
                Log.d(Message.class.getSimpleName(),"str:"+userID);
                f = FriendsManager.checkFriend(userID);
                if (f != null){
                    friends.add(f);
                    Log.d(Message.class.getSimpleName(),"Member:"+f.getProfileName());
                }
            }
        }

        packetStr = packetStr.replace(memberStr,"");
        //Extra
        if (packetStr.startsWith(HEADER_EXTRA)){
            String extraStr = packetStr.substring(0, packetStr.indexOf(HEADER_END)+1);
            extra = extraStr.substring(7, extraStr.length()-1);
            Log.d(Message.class.getSimpleName(),"Extra:"+extra);
            packetStr = packetStr.replace(extraStr, "");
            Log.d(Message.class.getSimpleName(), packetStr);
        }
        //Message Text
        messageText = packetStr;
        Log.d(Message.class.getSimpleName(), packetStr);
        return createGroupMessage(name, ID, command, DIRECTION_FROM, friends, time, messageText, extra);
    }

    /**
     * Static factory
     * @param name String
     * @param chatID String
     * @param type int
     * @param command int
     * @param direction int
     * @param friends List of Friend
     * @param time Date
     * @param message String
     * @param extra String
     * @return Message
     */
    public static Message createMessage(String name,String chatID, int type, int command, int direction, List<Friend> friends, Date time, String message, String extra){
        return new Message(name, chatID, type, command, direction, friends, time, message, extra);
    }

    /**
     * get Friends
     * @return List of Friend
     */
    public List<Friend> getFriend() {
        List<Friend> list = new ArrayList<>();
        list.addAll(friends);
        return list;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return messageContent;
    }

    public int getDirection() {
        return direction;
    }

    public int getCommand() {
        return command;
    }

    public int getType() {
        return type;
    }

    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        String messageString;
        String commandStr, directionStr, typeStr;
        switch (type){
            case TYPE_CHAT:
                typeStr = "Type: Chat";
                break;
            case TYPE_GROUP:
                typeStr = "Type: Group chat";
                break;
            default:
                typeStr = "null";
        }

        switch (command){
            case COMMAND_CREATE_GROUP:
                commandStr = "Command: Create group";
                break;
            case COMMAND_GROUP_CHAT:
                commandStr = "Command: Normal group chat";
                break;
            case COMMAND_CREATE_TASK:
                commandStr = "Command: Create group task";
                break;
            case COMMAND_REMOVE_TASK:
                commandStr = "Command: Remove group task";
                break;
            case COMMAND_SHARE_LOCATION:
                commandStr = "Command: Share location";
                break;
            case COMMAND_CREATE_EVENT:
                commandStr = "Command: Create event";
                break;
            default:
                commandStr = "null";
        }

        switch (direction){
            case DIRECTION_FROM:
                directionStr = "Direction: From";
                break;
            case DIRECTION_TO:
                directionStr = "Direction: To";
                break;
            default:
                directionStr = "null";
        }
        String withWho = "With: ";
        for (Friend f:friends){
            withWho += f.getProfileName() + ",";
        }
        String timeStr = "Time: " + time.toString();
        String messageStr = "Content: " + messageContent;
        messageString =
                typeStr + ", " +
                commandStr + ", " +
                directionStr + ", " +
                withWho + ", " +
                timeStr + ", " +
                messageStr;
        return messageString;
    }
}
