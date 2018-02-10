package com.prolifixs.pixelstream.Utils;

import android.os.Environment;

/**
 * Created by Prolifixs on 2/5/2018.
 */

/////////////////////////////////location where media are found in phone.
public class FilePaths {

    //"Storage/emulated/0"  --for external storage
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    //public String GALLLERY = Environment.getRootDirectory().getPath();

    //"Storage/emulated/0"  --for internal storage
    public String ROOT_DIR_2 = Environment.getExternalStorageDirectory().getPath();

    //"Storage/emulated/0/camera"
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String CAMERA2 = ROOT_DIR + "DCIM/Camera";


    //"Storage/emulated/0/pictures"
    public String PICTURES = ROOT_DIR + "/pictures";

    //"Storage/emulated/0/videos"
    public String VIDEOS = ROOT_DIR + "/Videos";


    //--------------------------------F  I  R  E  B  A  S  E  -----------------------
    public String FIREBASE_IMAGE_STORAGE = "photos/users";

    //-----> Test mode
    public String FIREBASE_IMAGE_STORAGE_TEST = "photos";
}
