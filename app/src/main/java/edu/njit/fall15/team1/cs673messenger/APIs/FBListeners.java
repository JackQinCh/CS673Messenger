package edu.njit.fall15.team1.cs673messenger.APIs;

import java.util.Date;
import java.util.LinkedList;

/**
 * LinkedList of FacebookServerListener
 * Created by jack on 10/22/15.
 */
public class FBListeners extends LinkedList<FacebookServerListener> {
    void facebookReceivedMessage(String from, String message, Date time){
        if (this.size() != 0){
            for (FacebookServerListener listener:this){
                listener.facebookReceivedMessage(from, message, time);
            }
        }
    }
    void facebookConnected(Boolean isConnected){
        if (this.size() != 0){
            for (FacebookServerListener listener:this){
                listener.facebookConnected(isConnected);
            }
        }
    }
    void facebookLogined(Boolean isLogin){
        if (this.size() != 0){
            for (FacebookServerListener listener:this){
                listener.facebookLogined(isLogin);
            }
        }
    }
}
