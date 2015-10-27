package edu.njit.fall15.team1.cs673messenger.APIs;

import edu.njit.fall15.team1.cs673messenger.models.MessageModel;

/**
 * Notify interface view to change text.
 * Created by jack on 10/25/15.
 */
public interface RecentChatsListener {
    public void receivedMessage(MessageModel messageModel);
}
