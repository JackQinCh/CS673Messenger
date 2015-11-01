package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.ChattingListFragment;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.FriendsListFragment;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.GroupChatFragment;

/**
 * Created by jack on 10/30/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ChattingListFragment.newInstance(position+1);
            case 1:
                return new FriendsListFragment();
            case 2:
                return new GroupChatFragment();
            default:
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Recent";
            case 1:
                return "Friend";
            case 2:
                return "Group";
        }
        return null;
    }

}
