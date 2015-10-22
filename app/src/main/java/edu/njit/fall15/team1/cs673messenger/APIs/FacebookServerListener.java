package edu.njit.fall15.team1.cs673messenger.APIs;

/**
 * Created by jack on 10/21/15.
 */
public interface FacebookServerListener {
    void facebookConnected(Boolean isConnected);
    void facebookLogined(Boolean isLogin);
}
