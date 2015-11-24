package edu.njit.fall15.team1.cs673messenger.APIs;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Message;
import edu.njit.fall15.team1.cs673messenger.models.Messages;

/**
 * RecentChatsManager is a Singleton, manages recent chat lists.
 * Created by jack on 10/25/15.
 */
public enum  RecentChatsManager implements FacebookServerListener{
    INSTANCE;

    private List<Messages> recentChats = new LinkedList<>();
    private List<RecentChatsListener> listeners = new LinkedList<>();

    RecentChatsManager() {
        FacebookServer.INSTANCE.addListeners(this);
    }

    public List<Messages> getRecentChats() {
        List<Messages> list = new ArrayList<>();
        if (recentChats.size() != 0)
            for (Messages messages:recentChats){
                if (messages.getType() == Messages.PERSONAL_CHAT)
                    list.add(messages);
            }
        return list;
    }

    public List<Messages> getGroupChats(){
        List<Messages> list = new ArrayList<>();
        if (recentChats.size() != 0)
            for (Messages messages:recentChats){
                if (messages.getType() == Messages.GROUP_CHAT)
                    list.add(messages);
            }
        return list;
    }

    public void addListener(RecentChatsListener listener) {
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public void removeListener(RecentChatsListener listener){
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    /**
     * Add messages into recent chats.
     * @param messages
     */
    public void addMessages(Messages messages){
        recentChats.add(messages);
        Log.i(getClass().getSimpleName(), toString());
    }
    /**
     * Get Messgaes with chatId.
     * @param type
     * @param chatId
     * @return
     */
    public synchronized Messages getMessages(int type, String chatId){
        if (recentChats.size() != 0) {
            for (Messages messages : recentChats) {
                if (messages.checkId(chatId)) {
                    return messages;
                }
            }
        }
        Log.d(getClass().getSimpleName(),"Didn't find the messages with "+chatId);
        if (type == Messages.PERSONAL_CHAT){
            Friend friend = FriendsManager.checkFriend(chatId);
            if (friend != null){
                Messages messages = Messages.newPersonalMessages(friend);
                recentChats.add(messages);
                return messages;//copy
            }
        }else if (type == Messages.GROUP_CHAT){
            Log.d(getClass().getSimpleName(), "New Group_chat");
            return null;
        }
        return null;
    }

    /**
     * Add a message into Recent messages list.
     * @param message
     */
    public synchronized void addMessage(Message message){
        if (recentChats.size() != 0){//If recent chats is not empty, find the chat and add the message into it.
            for (Messages ms:recentChats){
                if(ms.getChatId().equals(message.getChatID())){
                    ms.addMessage(message);
                    if (message.getDirection() == Message.DIRECTION_TO){
                        FacebookServer.INSTANCE.sendMessage(message);
                    }
                    Log.i(getClass().getSimpleName(),toString());
                    return;
                }
            }
        }//If recent chats is empty or didn't find the chat, create a chat. When receive a message.
        Log.d(getClass().getSimpleName() + "->addMessage():", "Didn't find the chat: " + message.getChatID());
        Messages messages = null;
        if (message.getType() == Messages.PERSONAL_CHAT){
            messages = Messages.newPersonalMessages(message.getFriend().get(0));
        }else if (message.getType() == Messages.GROUP_CHAT){
            messages = Messages.newGroupMessages(message.getName(), message.getChatID(), message.getFriend());
        }
        messages.addMessage(message);
        recentChats.add(messages);
        if (message.getDirection() == Message.DIRECTION_TO){
            FacebookServer.INSTANCE.sendMessage(message);
        }
        Log.i(getClass().getSimpleName(),toString());
    }

    @Override
    public void facebookReceivedMessage(String from, String messageText, Date time) {
        Log.d(getClass().getSimpleName(), "Received a Message from FacebookServer.");

        Friend friend = FriendsManager.checkFriend(from);
        if (friend != null){
            Message message = Message.createWithReceivedMessage(messageText, friend, time);
            if (message.getType() == Messages.PERSONAL_CHAT){
                addMessage(message);
                if (listeners.size() != 0) {
                    Log.d(getClass().getSimpleName(), "Notify GUI listeners.");
                    for (RecentChatsListener listener : listeners)
                        listener.receivedMessage(message);
                }
            }else if (message.getType() == Messages.GROUP_CHAT){
                Log.e(getClass().getSimpleName(),"To be continue...");
            }

        }else{
            Log.e(getClass().getSimpleName(),"Didn't find this friend.");
        }
    }

    @Override
    public String toString() {
        String recentChatsString = "Recent chats list:\n";
        if (recentChats.size() != 0){
            for (Messages messages:recentChats){
                recentChatsString += messages.getChatId()+"\n";
                for (Friend f:messages.getMembers()){
                    recentChatsString += f.getProfileName() + ", ";
                }
                recentChatsString += "\n";
                if (messages.getMessages().size() != 0){
                    for (Message message:messages.getMessages()){
                        recentChatsString += message.toString() +", "+ message.getTime() + "\n";
                    }
                }
            }
        }
        return recentChatsString;
    }

    @Override
    public void facebookConnected(Boolean isConnected) {

    }

    @Override
    public void facebookLogined(Boolean isLogin) {

    }
}
