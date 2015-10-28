package edu.njit.fall15.team1.cs673messenger.XMPP;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Akhmetov on 10/28/2015.
 */
public class BackendTEST extends Application {
    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DENIS", "START");
        XMPP test = new XMPP("chat.facebook.com", "denis.infodba", "DUMMY");
        test.XMPPconnect();
        Log.d("DENIS", "GetContext starts");
        sContext = getApplicationContext();
    }
    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }
}
