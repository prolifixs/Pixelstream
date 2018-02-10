package com.prolifixs.pixelstream.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Prolifixs on 2/5/2018.
 */

//////////////////////Searching for all directories inside phone memory and setting them to a list.

    /*
    * Returns list of all directories contained in the location
    * */
public class FileSearch {
    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathsArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                pathsArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathsArray;
    }

    /*
    * Returns list of all files contained in the location
    * */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathsArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isFile()) {
                pathsArray.add(listFiles[i].getAbsolutePath());

            }
        }
        return pathsArray;
    }


    //-------------------------------------------use in the future (not added yet)-----------------------------
/*    public static ArrayList<String> getRecentFilePaths(String directory){
        ArrayList<String> pathsArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isAbsolute()) {
                pathsArray.add(listFiles[i].getAbsolutePath());

            }
        }
        return pathsArray;
    }*/
}
