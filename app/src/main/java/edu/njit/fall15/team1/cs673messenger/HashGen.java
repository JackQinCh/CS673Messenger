package edu.njit.fall15.team1.cs673messenger;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * Created by Akhmetov on 9/21/2015.
 */
public class HashGen extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrintHashKey();
        Log.d("DENIS", "START");
        XMPP test = new XMPP("chat.facebook.com","!!!TypeUsernameHere!!!","TypePasswordHere!!!");
        test.XMPPconnect();


    }
    public void PrintHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.akhmetov.sandbox28",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEY_HASH_HERE_DENIS:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


    }


}
