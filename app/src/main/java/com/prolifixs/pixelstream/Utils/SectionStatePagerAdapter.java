package com.prolifixs.pixelstream.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {


    /*
    * IMPORTANT : DO NOT TOUCH THIS.....
    * */
    //trying to get the fragment name or number or even the fragment itself
    private final List<Fragment> mfragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();


    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mfragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentList.size();
    }

    public void addFragmentToList(Fragment fragment, String fragmentName){
        mfragmentList.add(fragment);
        mFragments.put(fragment, mfragmentList.size()-1);
        mFragmentNumbers.put(fragmentName, mfragmentList.size()-1);
        mFragmentNames.put(mfragmentList.size()-1, fragmentName);
    }

    /*
    *
    * Returns the fragment with the name @params
    * */
    public Integer getFragmentNumber(String fragmentName){
        if (mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        }else{
            return null;
        }
    }

    /*
   *
   * Returns the fragment with the name @params
   * */
    public Integer getFragmentNumber(Fragment fragmen){
        if (mFragmentNumbers.containsKey(fragmen)){
            return mFragmentNumbers.get(fragmen);
        }else{
            return null;
        }
    }

    /*
*
* Returns the fragment with the name @params
* */
    public String getFragmentName(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        }else{
            return null;
        }
    }

}
