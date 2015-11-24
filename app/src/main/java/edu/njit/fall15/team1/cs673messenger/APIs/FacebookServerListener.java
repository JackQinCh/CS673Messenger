package edu.njit.fall15.team1.cs673messenger.APIs;

import java.util.Date;

/**
 * Created by jack on 10/21/15.
 */
public interface FacebookServerListener {
    void facebookConnected(Boolean isConnected);
    void facebookLogined(Boolean isLogin);
    void facebookReceivedMessage(String from, String message, Date time);
}
