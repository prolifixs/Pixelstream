package com.prolifixs.pixelstream.Home.MainHome.Message.Models.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.prolifixs.pixelstream.R;

/**
 * Created by Prolifixs on 1/3/2018.
 */

public class MainMessagesFragment extends Fragment {
    private static final String TAG = "MainMessagesFragment";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Widgets
    private Toolbar mtoolbar;
    private ViewPager mViewPager;
    private TabLayout mTablayout;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_messages, container, false);
        return view;


    }


}
