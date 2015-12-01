package edu.njit.fall15.team1.cs673messenger.APIs;

import android.util.Log;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import java.util.LinkedList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.models.Friend;

/**
 * Created by jack on 10/22/15.
 */
public final class FriendsManager {
    private FriendsManager() {
    }

    public static Friend checkFriend(String userid){
        Log.d(FriendsManager.class.getSimpleName(), "Checking:"+userid);
        List<Friend> friends = getFriends();
        for(Friend friend:friends){
            if (friend.getUser().equals(userid)){
                Log.d(FriendsManager.class.getSimpleName(), "Found the friend "+friend);
                return friend;
            }
        }
        Log.d(FriendsManager.class.getSimpleName(),"Didn't find the friend "+userid);
        return null;
    }

    /**
     * Get friends data from FacebookServer.getInstance().getRoster()
     * Convert Roster to Friend
     * @return LinkedList of Friend
     */
    public static List<Friend> getFriends(){
        LinkedList<Friend> friends = new LinkedList<>();
        Roster roster = FacebookServer.INSTANCE.getRoster();

        //Log
        try{
            int num = roster.getEntries().size();
            if (num == 0){
                //No friend
                Log.i(FriendsManager.class.getSimpleName(), "No friend has been detected.");
            }else{
                Log.i(FriendsManager.class.getSimpleName(), "There are " + num + " friends have been detected.");
                for (RosterEntry entry : roster.getEntries()) {
                    Presence friendPresence = roster.getPresence(entry.getUser());
                    friends.add(new Friend(
                            friendPresence.getType(),
                            entry.getUser(),
                            entry.getName(),
                            entry.getType()));
                    Log.i(FriendsManager.class.getSimpleName(),
                            entry.getName() + "," + entry.getUser() + "," + entry.getType() + "," + friendPresence.getType());
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return friends;
    }
}
