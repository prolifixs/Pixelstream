package com.prolifixs.pixelstream.User.Utils;

/**
 * Created by Prolifixs on 1/7/2018.
 */

public class StringManipulation {
    //handling username field

    public static String expandUsername(String username){
        return username.replace(".", " " );
    }

    public static String condenseUsername(String username){
        return username.replace(" ", ".");
    }
}
