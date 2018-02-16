package com.prolifixs.pixelstream.Home.MainHome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.prolifixs.pixelstream.Home.MainHome.Camera.CameraFragment;
import com.prolifixs.pixelstream.Home.MainHome.HomeFragment.HomeFragment;
import com.prolifixs.pixelstream.Home.MainHome.Message.Models.Fragments.MainMessagesFragment;
import com.prolifixs.pixelstream.User.Login.LoginMainActivity;
import com.prolifixs.pixelstream.Utils.SectionsPagerAdapter;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;
import com.prolifixs.pixelstream.Utils.UniversalImageLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Prolifixs on 1/1/2018.
 */

public class MainHomeActivity extends AppCompatActivity {
    private static final String TAG = "MainHomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = MainHomeActivity.this;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionsGranted = false;
    private Geocoder mGeocoder;

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //vars
    private TextView mUseerCurrentLocation;


    //FireStore
    private FirebaseFirestore mFirestore;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Log.d(TAG, "onCreate: Starting Main home activity");
        mFirestore = FirebaseFirestore.getInstance();

        setupFirebaseAuth();
        setupBottomNavigationView();
        setupViewPager();
        initImageLoader();
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
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
                    getDeviceLocation();
                }else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    //--------------------------------G E T  D E V I C E   C U R R E N T  L O C A T I O N------------------------
    //*TODO------------------------------------last stop: Attempting to get device location. working perfectly but havent uploaded long,lat to firestore
    //TODO-------------either send long,lat to user and then to fire-cloud function, OR create a 'GetAddress Geocode' to automatically get user address and country

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting device current location");
        mGeocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //if permission is granted, do something..
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found location!!");

                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: current location is: " + currentLocation);

                            //builder = new StringBuilder();
                            double latitude = currentLocation.getLatitude();
                            double longitude = currentLocation.getLongitude();

                            Log.d(TAG, "onComplete: your current longitude is at: " + longitude + " and your latitude is at: " + latitude);

                        }else {
                            Log.d(TAG, "onComplete: couldn't find a location...");
                            Toast.makeText(mContext, "unable to get current location...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }//--------------------------------G E T  D E V I C E   C U R R E N T  L O C A T I O N------------------------

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

    //------------------------------------------------------Back button to exit programme-----------------------------------------
    private boolean isUserClickedBackButton = false;

    @Override
    public void onBackPressed() {
        if (!isUserClickedBackButton){
            Toast.makeText(mContext, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
        }
        new CountDownTimer(3000, 1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                isUserClickedBackButton = false;
            }
        }.start();
    }
}
