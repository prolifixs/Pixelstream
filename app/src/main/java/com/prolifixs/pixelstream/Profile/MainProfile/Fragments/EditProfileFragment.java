package com.prolifixs.pixelstream.Profile.MainProfile.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.UniversalImageLoader;

import java.util.ArrayList;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    //Widgets
    private ImageView mProfilePhoto;
    private ImageView mChangePhoto;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_editprofile, container, false);

        mProfilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        mChangePhoto = (ImageView) view.findViewById(R.id.changePhoto);
        seProfileImage();



        //Back arrow for navigating back to profile activity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back to profile activity");

                getActivity().finish();
            }
        });

        return view;

    }

    private void seProfileImage(){
        Log.d(TAG, "seProfileImage: setting up profile photo");
        String imgURL = "www.gipsypixel.com/wp-content/uploads/2017/11/Wallpaper-Hd-Of-Full-Hdcars-Desktop-Backgrounds-Car-Background-Screen-High-Quality.jpg";
        UniversalImageLoader.setSingleImage(imgURL, mProfilePhoto, null, "http://");


    }




}
