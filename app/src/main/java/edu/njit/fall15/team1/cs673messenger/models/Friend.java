package edu.njit.fall15.team1.cs673messenger.models;

import org.jivesoftware.smack.packet.RosterPacket;

/**
 * Created by jack on 10/22/15.
 */
public class Friend {
    private String user;
    private String profileName;
    private RosterPacket.ItemType type;
    private RosterPacket.ItemStatus status;

    public Friend(RosterPacket.ItemStatus status, String user, String profileName, RosterPacket.ItemType type) {
        this.status = status;
        this.user = user;
        this.profileName = profileName;
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public String getProfileName() {
        return profileName;
    }

    public RosterPacket.ItemType getType() {
        return type;
    }

    public RosterPacket.ItemStatus getStatus() {
        return status;
    }
}
