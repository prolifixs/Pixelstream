package com.prolifixs.pixelstream.Upload.MainUpload.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolifixs.pixelstream.R;

/**
 * Created by Prolifixs on 1/3/2018.
 */

public class VideoFragment extends Fragment {
    private static final String TAG = "VideoFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_upload_video, container, false);
        Log.d(TAG, "onCreateView: Started.");
        return view;
    }
}
