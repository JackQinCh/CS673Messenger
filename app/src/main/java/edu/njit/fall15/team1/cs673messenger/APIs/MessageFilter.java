package edu.njit.fall15.team1.cs673messenger.APIs;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by jack on 15/11/17.
 */
public final class MessageFilter implements PacketFilter {
    private final Message.Type[] types;

    public MessageFilter(Message.Type... types) {
        this.types = types;
    }

    @Override
    public boolean accept(Packet packet) {
        if (types.length != 0){
            if (packet instanceof Message){
                Message message = (Message)packet;
                for (Message.Type type:types){
                    if (type == message.getType()){
                        return true;
                    }
                }
            }else return false;
        }
        return false;
    }
}
