package com.prolifixs.pixelstream.Upload.MainUpload.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Upload.MainUpload.UploadActivity;
import com.prolifixs.pixelstream.Utils.Permissions;

/**
 * Created by Prolifixs on 1/3/2018.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //vars
    ImageView cameraLayout;
    ProgressBar mProgressBar;
    //Constants
    private static final int GALLERY_FRAGMENT_NUM = 0;
    private static final int VIDEO_FRAGMENT_NUM = 1;
    private static final int CAMERA_REQUEST_CODE = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_upload_gallery, container, false);
        Log.d(TAG, "onCreateView: Started.");
        cameraLayout = (ImageView) view.findViewById(R.id.galleryCameraView);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: launching camera.");
                //We now know the viewPager is coming from UploadActivity and we want to start camera.
                //This is because we want to reuse the same upload system for different purposes
                //In the future if your coming from profileActivity, e.t.c, change this direction.
                if (((UploadActivity)getActivity()).getCurrentTabNumber() == GALLERY_FRAGMENT_NUM){

                    //first checking if permission is granted.
                    if (((UploadActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])){
                        Log.d(TAG, "onClick: starting camera.");
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }else {
                        //navigate back to upload activity but clear all recent saved information
                        //so that permission request will re-fire...
                        Intent intent = new Intent(getActivity(), UploadActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        return view;
    }

    //Camera Data Captured
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: Photo Captured successfully");
            Log.d(TAG, "onActivityResult: Attempting to navigate to the next step");
            ////////////Navigating  to the the next screen to publish photo.
        }
    }
}
