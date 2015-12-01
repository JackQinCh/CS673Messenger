package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.R;

/**
 * Created by jack on 15/11/28.
 */
public class WelcomeActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_face);

        ImageView photo = (ImageView) findViewById(R.id.welPhoto);
        try {
            FacebookServer.INSTANCE.loadBitmap(FacebookServer.INSTANCE.getUserID(), photo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView name = (TextView) findViewById(R.id.welName);

        name.setText("Hello "+FacebookServer.INSTANCE.getUserName()+"!");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 3000);
    }
}
