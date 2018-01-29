package com.prolifixs.pixelstream.Profile.MainProfile.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileAccountSettings.AccountSettingsActivity;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.model.User;
import com.prolifixs.pixelstream.User.model.UserAccountSettings;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;
import com.prolifixs.pixelstream.Utils.FirebaseMethods;
import com.prolifixs.pixelstream.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Prolifixs on 1/23/2018.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    //FireStore
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference myUserRef;
    private CollectionReference myUserSettingsRef;
    private String userID;



    //Widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayname, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView mGridView;
    private Toolbar mToolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private LinearLayout mlinearLayout;

    private Context mContext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);
        mPosts = (TextView) view.findViewById(R.id.textPost);
        mFollowers = (TextView) view.findViewById(R.id.textFollowers);
        mFollowing = (TextView) view.findViewById(R.id.textFollowing);
        mDisplayname = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mWebsite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mGridView = (GridView) view.findViewById(R.id.gridViewTodayPost);
        mToolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mlinearLayout = (LinearLayout) view.findViewById(R.id.linLayout2);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mContext = getActivity();
        Log.d(TAG, "onCreateView: Starting fragment activity");

        //calling nethods
        setupBottomNavigationView();
        setupToolbar();
        setupFirebaseAuth();


        return view;
    }

    //setting Profile Widgets
    private void setProfileWidgets(){

        //method 2 *****All methods are written inside here*******
        Accountsettings(new MyCallBack() {
            @Override
            public void onCallback(UserAccountSettings settings) {
                Log.d(TAG, "Retrieving user information from firestore");

                mDisplayname.setText(settings.getDisplay_name());
                mUsername.setText(settings.getUsername());
                mWebsite.setText(settings.getWebsite());
                mDescription.setText(settings.getDescription());
                mPosts.setText(String.valueOf(settings.getPosts()));
                mFollowers.setText(String.valueOf(settings.getFollowers()));
                mFollowing.setText(String.valueOf(settings.getFollowing()));
                UniversalImageLoader.setSingleImage(settings.getProfile_photo(), mProfilePhoto, null, "");
                mProgressBar.setVisibility(View.GONE);
            }
        });
        //******Checking if description and website fields are empty********
        if (mDescription == null || mWebsite == null){

            mlinearLayout.setVisibility(View.GONE);

        }
    }

    //****Setting up toolbar
    private void setupToolbar(){
        ((ProfileActivity)getActivity()).setSupportActionBar(mToolbar);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to Account settings");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
            * Setting up BottomNavigationView
    * */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }



    //-----------------------------------------------F I R E   B A S E--------------------------------------------------



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mFirebaseFirestore = FirebaseFirestore.getInstance();
                myUserRef = mFirebaseFirestore.collection("users");
                myUserSettingsRef = mFirebaseFirestore.collection("user_account_settings");



                myUserSettingsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        Log.d(TAG, "onEvent: initilization");
                        setProfileWidgets();
                    }
                });

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

    //-----------------------------------------------F I R E   S T O R E--------------------------------------------------

    //*********************Retrieving user_account_settings node from fireStore.
    private void Accountsettings(final MyCallBack myCallBack){
        Log.d(TAG, "settings: Retrieving user account settings from firestore");
        final CollectionReference mSettings = mFirebaseFirestore.collection("user_account_settings");

        mSettings.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot doc: documentSnapshots){

                    UserAccountSettings settings = doc.toObject(UserAccountSettings.class);
                    myCallBack.onCallback(settings);

                }

            }
        });

    }

    public interface MyCallBack{
        void onCallback(UserAccountSettings settings);
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}

