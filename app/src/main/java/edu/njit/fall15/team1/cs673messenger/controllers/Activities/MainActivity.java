package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Adapters.ViewPagerAdapter;

/**
 * Created by jack on 10/5/15.
 */
public class MainActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_interface);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    /**
     * Create menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Menu select action
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id){
            case R.id.action_signout:
                FacebookServer.INSTANCE.disConnect();
                Intent intentSignout = new Intent();
                intentSignout.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intentSignout);
                FacebookServer.INSTANCE.disConnect();
                this.finish();
                return true;
            case R.id.action_setting:
                Intent intentSetting = new Intent();
                intentSetting.setClass(MainActivity.this, AppPreferencesActivity.class);
                startActivity(intentSetting);
                return true;
            default:
                break;
        }
        return false;
    }

    public void onCreateGroupChat(View v){
        Log.d(getClass().getSimpleName(),"onCreateGroupChat");
    }

}
