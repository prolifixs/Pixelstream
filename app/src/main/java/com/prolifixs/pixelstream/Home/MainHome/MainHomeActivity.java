package com.prolifixs.pixelstream.Home.MainHome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.Home.MainHome.Camera.CameraFragment;
import com.prolifixs.pixelstream.Home.MainHome.HomeFragment.HomeFragment;
import com.prolifixs.pixelstream.Home.MainHome.Message.Models.Fragments.MainMessagesFragment;
import com.prolifixs.pixelstream.User.Login.LoginMainActivity;
import com.prolifixs.pixelstream.Utils.SectionsPagerAdapter;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;

/**
 * Created by Prolifixs on 1/1/2018.
 */

public class MainHomeActivity extends AppCompatActivity {
    private static final String TAG = "MainHomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = MainHomeActivity.this;

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Log.d(TAG, "onCreate: Starting Main home activity");

        setupFirebaseAuth();
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
        adapter.addFragment(new MainMessagesFragment());     //index 2
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







        //-----------------------------------------------F I R E   B A S E--------------------------------------------------


        //Checking if user is signed in, else navigate to login screen () - first and only setup is done here.
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");
        if (user == null){
            Intent intent = new Intent(mContext, LoginMainActivity.class);
            startActivity(intent);//method is called in 'setupFirebaseAuth'
        }
    }
     /*
    * IMPORTANT : DO NOT TOUCH.
    * Setting up firebase Auth for the first time. this is the beginning of authentication.
    * */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Checking the user if logged in
                checkCurrentUser(user);

                if (user != null){
                    //User is Signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                }else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
