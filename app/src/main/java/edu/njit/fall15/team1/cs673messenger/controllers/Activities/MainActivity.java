package edu.njit.fall15.team1.cs673messenger.controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.APIs.FacebookServer;
import edu.njit.fall15.team1.cs673messenger.R;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.ChattingListFragment;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.FriendsListFragment;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.GroupChatFragment;

/**
 * Created by jack on 10/5/15.
 */
public class MainActivity extends AppCompatActivity{
//    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_interface);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);//Avoid destroying fragment in viewpager.
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChattingListFragment(), getString(R.string.chatlist));
        adapter.addFragment(new FriendsListFragment(), getString(R.string.friend));
        adapter.addFragment(new GroupChatFragment(), getString(R.string.groupchat));
        viewPager.setAdapter(adapter);
    }

    /**
     * ViewPagerAdapter Class
     */
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
                FacebookServer.getInstance().disConnect();
                Intent intentSignout = new Intent();
                intentSignout.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intentSignout);
                FacebookServer.getInstance().disConnect();
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
}
