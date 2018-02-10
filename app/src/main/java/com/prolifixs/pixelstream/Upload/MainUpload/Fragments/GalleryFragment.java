package com.prolifixs.pixelstream.Upload.MainUpload.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileAccountSettings.AccountSettingsActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Upload.MainUpload.NextActivities.NextActivity;
import com.prolifixs.pixelstream.Upload.MainUpload.UploadActivity;
import com.prolifixs.pixelstream.Utils.FilePaths;
import com.prolifixs.pixelstream.Utils.FileSearch;
import com.prolifixs.pixelstream.Utils.GridImageAdapter;
import com.prolifixs.pixelstream.Utils.Permissions;

import java.util.ArrayList;

/**
 * Created by Prolifixs on 1/3/2018.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //vars
    private ArrayList<String> directories;
    private  String mAppend = "file:/";
    private String mSelectedImage;


    //Widgets
    private GridView mGridView;
    private ImageView galleryImage;
    private Spinner directorySpinner;
    private ImageView mClose;
    private TextView mNext;
    private ImageView cameraLayout;
    private ProgressBar mProgressBar;

    //Constants
    private static final int GALLERY_FRAGMENT_NUM = 0;
    private static final int VIDEO_FRAGMENT_NUM = 1;
    private static final int CAMERA_REQUEST_CODE = 5;
    private static final int NUM_GRID_COLOUMNS = 4;//if you change this, layout grid should also be changed...


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_upload_gallery, container, false);
        Log.d(TAG, "onCreateView: Started.");
        cameraLayout = (ImageView) view.findViewById(R.id.galleryCameraView);
        galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mGridView = (GridView)view.findViewById(R.id.griView);
        directorySpinner = (Spinner) view.findViewById(R.id.spinnerDirectory);
        directories = new ArrayList<>();
        mClose = (ImageView) view.findViewById(R.id.ivCLoseUpload);
        mNext = (TextView) view.findViewById(R.id.tvNext);
        mProgressBar.setVisibility(View.GONE);


        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the gallery fragment");
                getActivity().finish();
            }
        });


        /*
        * logics are made here since activities, fragments use the same Gallery fragment, if request comes from
        * different areas, we write a logic to differentiate them
        * */
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to the 'NextActivity' screen");

                if (isRootTaks()){//means it is from its original calling..
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }


            }
        });

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
                        getActivity().finish();
                    }
                }
            }
        });

        //Method calling.....
        init();
        return view;
    }
    /*
     * logics are made here since activities, fragments use the same Gallery fragment, if request comes from
     * different areas, we write a logic to differentiate them
     * */
    private boolean isRootTaks(){
        if (((UploadActivity)getActivity()).getTask() == 0){//268435456 is not a root task.(EditProfileFragment)
            return true;
        }
        else {
            return false;
        }
    }

    //Camera Data Captured
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: Photo Captured successfully");
            Log.d(TAG, "onActivityResult: Attempting to navigate to the next step");
            ////////////Navigating  to the the next screen to publish photo.

            Bitmap bitmap;
            bitmap =(Bitmap) data.getExtras().get("data");
            if (isRootTaks()){//means it is from its original calling..
                //setting the profile image
                try{
                    Log.d(TAG, "onActivityResult: Recieved new bitmap from camera..." + bitmap);
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    startActivity(intent);
                }catch (NullPointerException e){
                    Log.e(TAG, "NullPointerException: " + e.getMessage());
                }

            }else {
                try{
                    Log.d(TAG, "onActivityResult: Recieved new bitmap from camera..." + bitmap);
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }catch (NullPointerException e){
                    Log.e(TAG, "NullPointerException: " + e.getMessage());
                }
            }
        }
    }



//-----------------------------------------On video fragment, copy this and just change diectories to video--------------------
    private void init(){
        FilePaths filePaths = new FilePaths();
        //checking for other folders inside "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null){// <== change PICTURES to others that suit your needs
        //make that into list of directories
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);//<== change PICTURES to others that suit your needs
        }

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++){
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index).replace("/", " ");
            directoryNames.add(string);
        }

        directories.add(filePaths.CAMERA);//adds the directory for camera
        //directories.add(filePaths.GALLLERY);

        //setting the directory spinner drop-down
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //Setup imageGrid For Directory chosen.......
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: Directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //setting the grid coloumn widths...
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLOUMNS;
        mGridView.setColumnWidth(imageWidth);

        //Use the grid image Adapter to adapt images to gridView.
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        mGridView.setAdapter(adapter);


        //setImage(imgURLs.get(0), galleryImage, mAppend);  //set image display by default.(comment to disable.)
        //mSelectedImage = imgURLs.get(0);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));
                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }
    //setting the single selected image //setting the first image to be shown by default when the activity is first inflated....
    private void setImage(String imURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image.");

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }



}
