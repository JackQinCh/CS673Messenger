package edu.njit.fall15.team1.cs673messenger.controllers.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.controllers.Activities.FragmentListener;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.ChattingListFragment;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.FriendsListFragment;
import edu.njit.fall15.team1.cs673messenger.controllers.Fragments.GroupChatFragment;

/**
 * Created by jack on 10/30/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Recent", "Friend", "Group" };
    private Context context;
    private List<String> tagList = new ArrayList<>();
    private FragmentManager fm;
    private FragmentListener listener;
//    private SparseArray<Fragment> views = new SparseArray<>();

    public ViewPagerAdapter(FragmentManager manager, Context context, FragmentListener listener) {
        super(manager);
        this.fm = manager;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChattingListFragment chattingListFragment = new ChattingListFragment();
                chattingListFragment.setListener(listener);
                return chattingListFragment;
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
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

//        Fragment root = null;
//        switch (position){
//            case 0:
//                root = new ChattingListFragment();
//                ((ChattingListFragment)root).setListener(listener);
//                break;
//            case 1:
//                root = new FriendsListFragment();
//                break;
//            case 3:
//                root = new GroupChatFragment();
//            default:
//        }
//        views.put(position, root);
//        return root;

        tagList.add(makeFragmentName(container.getId(), getItemId(position)));
        container.setTag(container.getId(),getItemId(position));
        return super.instantiateItem(container, position);
    }

        public static String makeFragmentName(int viewId, long index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public void update(int item){
        Fragment fragment = fm.findFragmentByTag(tagList.get(item));
        if(fragment != null){
            switch (item) {
                case 0:
                    ((ChattingListFragment) fragment).update();
                    break;
                case 1:
//                    ((FriendsListFragment) fragment).update();
                    break;
                case 2:
//                    ((GroupChatFragment) fragment).update();
                    break;
                default:
                    break;
            }
        }
    }


}
