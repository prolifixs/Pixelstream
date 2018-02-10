package com.prolifixs.pixelstream.Upload.MainUpload;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Upload.MainUpload.Fragments.GalleryFragment;
import com.prolifixs.pixelstream.Upload.MainUpload.Fragments.VideoFragment;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;
import com.prolifixs.pixelstream.Utils.Permissions;
import com.prolifixs.pixelstream.Utils.SectionsPagerAdapter;

/**
 * Created by Prolifixs on 1/3/2018.
 */

    public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "UploadActivity";

    //Constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int ACTIVITY_NUM = 2;

    //vars
    private ViewPager mViewPager;

    private Context mContext = UploadActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_upload);
        Log.d(TAG, "onCreate: Starting activity");

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();

        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

        //setupBottomNavigationView();
    }

    /*
    * Returns the current tab number
    * 0 = Gallery-fragment
    * 1 = Video-fragment
    * */
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /*
    * Setting up viewPager for managing tabs
    * */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new VideoFragment());
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.video));
    }

    /*
    *
    * */
    public int getTask(){
        //Log.d(TAG, "getTask: " + getIntent().getFlags());
        return getIntent().getFlags();
    }
    /*
    * Permissions verification ---------------------------------------
    * */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                UploadActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
    /*
    * Checking an array of permission.
    * */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /*
    * Checking a single permission if it has been verified.
    * */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(UploadActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);

            return true;
        }
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
