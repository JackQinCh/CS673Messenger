package edu.njit.fall15.team1.cs673messenger.APIs;

import android.util.Log;

import java.util.Date;
import java.util.LinkedList;

import edu.njit.fall15.team1.cs673messenger.models.Friend;
import edu.njit.fall15.team1.cs673messenger.models.Friends;
import edu.njit.fall15.team1.cs673messenger.models.MessageModel;
import edu.njit.fall15.team1.cs673messenger.models.MessageModels;

/**
 * RecentChatsManager is a Singleton, manages recent chat lists.
 * Created by jack on 10/25/15.
 */
public class RecentChatsManager implements FacebookServerListener{
    private LinkedList<MessageModels> recentChats = new LinkedList<>();
    private LinkedList<RecentChatsListener> listeners = new LinkedList<>();

    public LinkedList<MessageModels> getRecentChats() {
        return recentChats;
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

    @Override
    public void facebookConnected(Boolean isConnected) {

    }

    @Override
    public void facebookLogined(Boolean isLogin) {

    }


    private static class RecentChatsManagerHolder{
        private static RecentChatsManager instance = new RecentChatsManager();
    }

    private RecentChatsManager() {
        FacebookServer.getInstance().addListeners(this);
    }

    public static RecentChatsManager getInstance(){
        return RecentChatsManagerHolder.instance;
    }

    public MessageModels getMessageModels(Friend withWho){
        if (recentChats.size() != 0){
            for(MessageModels models:recentChats){
                if (models.getWithWho().equals(withWho)){
                    return models;
                }
            }
        }else{
            MessageModels models = new MessageModels(withWho);
            recentChats.add(models);
            return models;
        }
        return null;
    }

    public void sendMessage(MessageModel messageModel){
        if (recentChats.size() != 0){
            for (MessageModels ms:recentChats){
                if(ms.getWithWho().equals(messageModel.getFriend())){
                    ms.addMessage(messageModel);
                }
            }
        }else {
            MessageModels models = new MessageModels(messageModel.getFriend());
            models.addMessage(messageModel);
            recentChats.add(models);
        }
        Log.i(getClass().getSimpleName(),this.toString());
    }

    @Override
    public void facebookReceivedMessage(String from, String message, Date time) {
        Log.d(getClass().getSimpleName(), "ReceivedMessage from FacebookServer.");
        if (recentChats.size() != 0) {//If the list is not empty, find the friend and add the message.
            for (MessageModels models : recentChats) {
                if (models.getWithWho().getUser().equals(from)) {
                    MessageModel model = new MessageModel(
                            MessageModel.MessageType.From,
                            models.getWithWho(),
                            time,
                            message);
                    models.addMessage(model);
                    if (listeners.size() != 0) {
                        Log.d(getClass().getSimpleName(), "Notify listeners.");
                        for (RecentChatsListener listener : listeners)
                            listener.receivedMessage(model);
                    }
                }
                break;
            }
        }else{//If list is empty, create a new chat history list.
            Friends friends = new Friends();
            Friend friend = friends.checkFriend(from);
            if (friend != null){
                MessageModel model = new MessageModel(
                        MessageModel.MessageType.From,
                        friend,
                        time,
                        message);
                MessageModels models = new MessageModels(friend);
                models.addMessage(model);
                recentChats.add(models);
                if (listeners.size() != 0) {
                    Log.d(getClass().getSimpleName(), "Notify listeners.");
                    for (RecentChatsListener listener : listeners)
                        listener.receivedMessage(model);
                }
            }
        }
        Log.i(getClass().getSimpleName(),this.toString());
    }

    @Override
    public String toString() {
        String recentChatsString = "";
        if (recentChats.size() != 0){
            for (MessageModels models:recentChats){
                recentChatsString += models.getWithWho().getProfileName()+"\n";
                if (models.getMessages().size() != 0){
                    for (MessageModel model:models.getMessages()){
                        recentChatsString += model.toString() + "\n";
                    }
                }
            }
        }
        return recentChatsString;
    }
}
