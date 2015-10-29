package edu.njit.fall15.team1.cs673messenger.models;

import android.util.Log;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import java.util.LinkedList;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;

/**
 * Created by jack on 10/22/15.
 */
public class Friends {

    /**
     * LinkedList of Friend  getter
     * @return LinkedList of Friend
     */
    public LinkedList<Friend> getFriends() {
        return initFriends();
    }

    public Friend checkFriend(String userid){
        for(Friend friend:getFriends()){
            if (friend.getUser().equals(userid)){
                return friend;
            }
        }
        return null;
    }

    /**
     * Get friends data from FacebookServer.getInstance().getRoster()
     * Convert Roster to Friend
     * @return LinkedList of Friend
     */
    private LinkedList<Friend> initFriends(){
        LinkedList<Friend> friends = new LinkedList<>();
        Roster roster = FacebookServer.getInstance().getRoster();

        //Log
        try{
            int num = roster.getEntries().size();
            if (num == 0){
                //No friend
                Log.i(Friends.class.getSimpleName(), "No friend has been detected.");
            }else{
                Log.i(Friends.class.getSimpleName(), "There are " + num + " friends have been detected.");
                for (RosterEntry entry : roster.getEntries()) {
                    Presence friendPresence = roster.getPresence(entry.getUser());
                    friends.add(new Friend(
                            friendPresence.getType(),
                            entry.getUser(),
                            entry.getName(),
                            entry.getType()));
                    Log.i(Friends.class.getSimpleName(),
                            entry.getName() + "," + entry.getUser() + "," + entry.getType() + "," + friendPresence.getType());
                }
            }
        }catch (NullPointerException e){
            Log.e(initFriends().getClass().getSimpleName(),e.getMessage());
        }
        return friends;
    }
}
