package com.prolifixs.pixelstream.Profile.MainProfile.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Login.LoginMainActivity;
import com.prolifixs.pixelstream.Utils.BottomNavigationViewHelper;

/**
 * Created by Prolifixs on 1/4/2018.
 */


public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";
    private static final int ACTIVITY_NUM = 4;

    //private Context mContext = SignOutFragment.this;

    //FireBase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Widgets
    private ProgressBar mProgressBar;
    private TextView tvSignOut, tvSigningOut;
    private Button btnConfirmSignout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_signout, container, false);

        tvSignOut = (TextView) view.findViewById(R.id.tv_confirmSignout);
        tvSigningOut = (TextView)view.findViewById(R.id.tvSigningOut);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnConfirmSignout = (Button) view.findViewById(R.id.btnConfirmSignout);

        setupFirebaseAuth();

        mProgressBar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);
        
        btnConfirmSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Signing out of app");
                mProgressBar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);

                mAuth.signOut();
                getActivity().finish();

            }
        });
        return view;
    }

    /*
    * Setting up BottomNavigationView
    * */
    /*private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }*/


    //-----------------------------------------------F I R E   B A S E--------------------------------------------------

     /*
    * IMPORTANT : DO NOT TOUCH.
    * Setting up firebase Auth for the first time. this is the beginning of authentication.
    * */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null){
                    //User is Signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                }else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "onAuthStateChanged: Navigating back to login screen");
                    Intent intent = new Intent(getActivity(), LoginMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
