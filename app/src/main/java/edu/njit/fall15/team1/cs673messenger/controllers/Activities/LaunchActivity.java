package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServerListener;
import edu.njit.fall15.team1.cs673messenger.APIs.RecentChatsManager;
import edu.njit.fall15.team1.cs673messenger.R;

/**
 * Created by jack on 10/27/15.
 */
public class LaunchActivity extends Activity implements FacebookServerListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(),"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchinterface);

        //Display the current version number
        PackageManager pm = getPackageManager();
        try{
            PackageInfo pi = pm.getPackageInfo("edu.njit.fall15.team1.cs673messenger", 0);
            TextView versionNumber = (TextView) findViewById(R.id.launchVersion);
            versionNumber.setText("Version " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                init();
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FacebookServer.getInstance().removeListeners(this);
        Log.d(getClass().getSimpleName(),"onDestroy");
    }

    private void init() {
        FacebookServer.getInstance().addListeners(this);
        RecentChatsManager.getInstance();
        FacebookServer.getInstance().connect(getString(R.string.facebook_server));
    }

    @Override
    public void facebookConnected(Boolean isConnected) {
        Log.d(this.getClass().getSimpleName(), isConnected.toString());
        if (isConnected.booleanValue()){
            checkUser();
        }else{
            Toast.makeText(this, "Failed to connect server!",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUser() {
        launch();
    }

    private void launch() {

        Intent intent = new Intent();
        intent.setClass(LaunchActivity.this,LoginActivity.class);
        startActivity(intent);

        this.finish();
    }


    @Override
    public void facebookLogined(Boolean isLogin) {
    }

    //Useless
    @Override
    public void facebookReceivedMessage(String from, String message, Date time) {
    }
}
