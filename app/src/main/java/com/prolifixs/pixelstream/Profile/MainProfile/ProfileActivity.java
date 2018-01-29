package com.prolifixs.pixelstream.Profile.MainProfile;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.Profile.MainProfile.Fragments.ProfileFragment;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileAccountSettings.AccountSettingsActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;
import com.prolifixs.pixelstream.Utils.GridImageAdapter;
import com.prolifixs.pixelstream.Utils.UniversalImageLoader;

import java.util.ArrayList;

/**
 * Created by Prolifixs on 1/3/2018.
 */

    public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private Context mContext = ProfileActivity.this;
    private ImageView mProfilePhoto;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);
        Log.d(TAG, "onCreate: Starting activity");

      /*  setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();
        seProfileImage();

        tempGridSetup();*/
      init();
    }

    private void init(){
        //all this does is to navigate to profileFragment where main code rest. this is to give room to more feature and less crowded
        Log.d(TAG, "init: inflating and navigating to" + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        android.support.v4.app.FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }


/*    private void tempGridSetup(){
        ArrayList<String> imgURL = new ArrayList<>();
        imgURL.add("https://images4.alphacoders.com/658/658896.jpg");
        imgURL.add("https://images6.alphacoders.com/594/594898.jpg");
        imgURL.add("http://avante.biz/wp-content/uploads/Tuning-Wallpapers/Tuning-Wallpapers-003.jpg");
        imgURL.add("http://www.picshouse2.com/vb/imgcache/2/18515poster.jpg");
        imgURL.add("http://wallpaper-gallery.net/images/full-hd-wallpaper-of-cars/full-hd-wallpaper-of-cars-0.jpg");

        setupImageGrid(imgURL);

    }
    //setting up 'TODAY' grid-view
    private void setupImageGrid(ArrayList<String> imgURLs){

        GridView gridView = (GridView) findViewById(R.id.gridView);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);

    }

    private void seProfileImage(){
        Log.d(TAG, "seProfileImage: setting up profile photo");
        String imgURL = "www.gipsypixel.com/wp-content/uploads/2017/11/Wallpaper-Hd-Of-Full-Hdcars-Desktop-Backgrounds-Car-Background-Screen-High-Quality.jpg";

        UniversalImageLoader.setSingleImage(imgURL, mProfilePhoto, mProgressBar, "http://");
    }

    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);
        mProfilePhoto = (ImageView) findViewById(R.id.profile_photo);

    }


    private void setupToolbar(){
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to Account settings");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    *//*
    * Setting up BottomNavigationView
    * *//*
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }*/

}
