package edu.njit.fall15.team1.cs673messenger.XMPP;

import android.app.Application;
import android.util.Log;

/**
 * Created by Akhmetov on 10/28/2015.
 */
public class BackendTEST extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DENIS", "START");
        XMPP test = new XMPP("chat.facebook.com", "denis.infodba", "DUMMY");
        test.XMPPconnect();
    }
}
