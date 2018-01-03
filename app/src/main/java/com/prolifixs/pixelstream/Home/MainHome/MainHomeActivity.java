package com.prolifixs.pixelstream.Home.MainHome;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.Home.MainHome.Fragments.CameraFragment;
import com.prolifixs.pixelstream.Home.MainHome.Fragments.HomeFragment;
import com.prolifixs.pixelstream.Home.MainHome.Fragments.MessagesFragment;
import com.prolifixs.pixelstream.Home.MainHome.Fragments.SectionsPagerAdapter;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;

/**
 * Created by Prolifixs on 1/1/2018.
 */

public class MainHomeActivity extends AppCompatActivity {
    private static final String TAG = "MainHomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = MainHomeActivity.this;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Log.d(TAG, "onCreate: Starting Main home activity");

        setupBottomNavigationView();
        setupViewPager();
    }


    /*
    * Responsible for adding the 3 tabs: camera, home and messaging.
    * */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment());       //index0
        adapter.addFragment(new HomeFragment());         //index 1
        adapter.addFragment(new MessagesFragment());     //index 2
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsTop);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fragment_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_home_fragment_message);

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
