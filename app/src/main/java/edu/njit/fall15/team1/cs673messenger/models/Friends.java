package edu.njit.fall15.team1.cs673messenger.models;

import android.util.Log;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import java.util.LinkedList;
import java.util.Collection;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;

/**
 * Created by jack on 10/22/15.
 */
public class Friends {

    public LinkedList<Friend> initFriends(){
        LinkedList<Friend> friends = new LinkedList<>();

        Roster roster = FacebookServer.getInstance().getRoster();

        Collection<RosterEntry> entries = roster.getEntries();
        Log.d("Jack", "Your Friend List detected: " + entries.size() + " Friends :");

        for (RosterEntry entry : entries) {
            friends.add(new Friend(
                    entry.getStatus(),
                    entry.getUser(),
                    entry.getName(),
                    entry.getType()));
        }
        return friends;
    }

    public LinkedList<Friend> getFriends() {
        return initFriends();
    }
}
