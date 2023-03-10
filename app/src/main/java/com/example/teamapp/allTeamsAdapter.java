package com.example.teamapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class allTeamsAdapter extends FragmentPagerAdapter {
    private long baseId = 0;
    ArrayList<Fragment> fragmentAdapterArrayList;

    public allTeamsAdapter(FragmentManager fm, ArrayList<Fragment> fragmentArrayList) {
        super(fm);
        fragmentAdapterArrayList = fragmentArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return fragmentAdapterArrayList.get(0);
            case 1:
                return fragmentAdapterArrayList.get(1);
            case 2:
                return fragmentAdapterArrayList.get(2);
            case 3:
                return fragmentAdapterArrayList.get(3);
            case 4:
                return fragmentAdapterArrayList.get(4);
            default:
                return null;
        }



//            return fragmentAdapterArrayList.get(position).getmFragment();


    }

    @Override
    public int getCount() {
        return fragmentAdapterArrayList.size();
    }


    //this is called when notifyDataSetChanged() is called
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return allTeamsAdapter.POSITION_NONE;
    }


    @Override
    public long getItemId(int position) {
        // give an ID different from position when position has been changed
        return baseId + position;
    }

    /**
     * Notify that the position of a fragment has been changed.
     * Create a new ID for each position to force recreation of the fragment
     *
     * @param n number of items which have been changed
     */
    public void notifyChangeInPosition(int n) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += getCount() + n;
    }


    public void removeTabPage(int position) {
        if (!fragmentAdapterArrayList.isEmpty() && position<fragmentAdapterArrayList.size()) {
            fragmentAdapterArrayList.remove(position);
            notifyDataSetChanged();
        }
    }



    /**
     * //     * This method returns the title of the tab according to the position.
     * //
     */



//    @Override
//    public CharSequence getPageTitle(int position) {
//        return fragmentAdapterArrayList.get(position).getmCatName();
//    }
}
