package com.prolifixs.pixelstream.Profile.MainProfile.ProfileAccountSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.Profile.MainProfile.Fragments.EditProfileFragment;
import com.prolifixs.pixelstream.Profile.MainProfile.Fragments.SignOutFragment;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;
import com.prolifixs.pixelstream.Utils.SectionStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext;

    private SectionStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: ");

        mViewPager = (ViewPager) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        setupSettingsLists();
        setupFragments();
        setupToolbar();
        setupBottomNavigationView();


    }


    /*IMPORTANT : Room for new lists.....
    * Current Lists of data added into account settings: to add a new to this list, 1. create new fragment (in profile_fragment),
    * 2. create new String name (in String.xml), 3. add new String to "setupSettingsLists()' and place newly
    * added list into 'SetupFragment()'
    * */
    private void setupFragments(){
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmentToList(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
        pagerAdapter.addFragmentToList(new SignOutFragment(), getString(R.string.sign_out_fragment));
    }

    private void setupViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);//Goes away and makes only selected fragment to show up
        Log.d(TAG, "setupViewPager: navigating to fragment number:" + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsLists(){
        Log.d(TAG, "setupSettingsLists: initializing account settings list");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));//fragment 0
        options.add(getString(R.string.sign_out_fragment));    //fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: navigating to fragment:" + position);
                setupViewPager(position);
            }
        });
    }




    ////    Back button for navigating back to user profile activity.
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back to profile activity");

                finish();

                //alternative method :
                /*Intent intent = new Intent(mContext, ProfileActivity.class);
                startActivity(intent);*/
            }
        });
    }

    /*
    * Setting up BottomNavigationView
    * */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
