package com.prolifixs.pixelstream.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolifixs.pixelstream.Home.MainHome.MainHomeActivity;
import com.prolifixs.pixelstream.Notification.MainNotification.NotificationActivity;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Search.MainSearch.SearchActivity;
import com.prolifixs.pixelstream.Upload.MainUpload.UploadActivity;

/**
 * Created by Prolifixs on 1/3/2018.
 */

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

    }

    //Setting up how navigation will execute
    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, MainHomeActivity.class);//0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_seach:
                        Intent intent2 = new Intent(context, SearchActivity.class);//1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_add:
                        Intent intent3 = new Intent(context, UploadActivity.class);//2
                        context.startActivity(intent3);
                        break;

                    case R.id.ic_notification:
                        Intent intent4 = new Intent(context, NotificationActivity.class);//3
                        context.startActivity(intent4);
                        break;

                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);//4
                        context.startActivity(intent5);
                        break;
                }
                return false;
            }
        });
    }
}
