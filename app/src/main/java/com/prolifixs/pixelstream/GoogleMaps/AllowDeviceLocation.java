package com.prolifixs.pixelstream.GoogleMaps;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Login.LoginMainActivity;
import com.prolifixs.pixelstream.Utils.FirebaseMethods;

/**
 * Created by Prolifixs on 2/13/2018.
 */


//Concept was copied from mitch.tabian video on youtube (Google-maps)...


public class AllowDeviceLocation extends AppCompatActivity {
    private static final String TAG = "AllowDeviceLocation";


    //widgets
    private Button allowLocation;

    private static final int ERROR_DIALOG_REQUEST = 9001;//checking if user has correct verion of google play services
    private static final int REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_request);

        allowLocation = (Button) findViewById(R.id.allowLocation);

        allowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: requesting permission");

                checkLocationPermission();
            }
        });


        if (isServicesOk()) {
           // buildGoogleApiClient();
           // createLocationRequest();

        }
        //displayLocation();
    }


    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: checking google services version...");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AllowDeviceLocation.this);

        if (available == ConnectionResult.SUCCESS) {
            //Everything is fine and user can make map request
            Log.d(TAG, "isServicesOk: Google play services is working...");
            return true;

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but can be resolved
            Log.d(TAG, "isServicesOk: Error occured but we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AllowDeviceLocation.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission: getting location permissions..");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {
                //Ask for permission single...
                ActivityCompat.requestPermissions(this,
                        permissions,
                        REQUEST_CODE);
            }
        } else {
            //Ask for single permission...
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called...");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length; i++) {
                        //When all permissions are declined
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed!!!");
                            Toast.makeText(this, "permission failed!!!...", Toast.LENGTH_SHORT).show();
                            finish();
                            //When all permission are granted
                        } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            if (isServicesOk()) {
                            }
                            //All permissions are granted and we can now proceed with the next activity...
                            mLocationPermissionsGranted = true;

                            Toast.makeText(this, "permission granted..", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onRequestPermissionsResult: all permissions are granted..");
                            finish();
                        }
                    }

                }
                break;
            }
        }
    }

    private void getDeviceLocation(){//does the samething as mainHomeActivity...Mitch.tabian(Geolocation video)
        Log.d(TAG, "getDeviceLocation: getting device current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if (mLocationPermissionsGranted){
                //if permission is granted, do something..
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                       if (task.isSuccessful()){
                           Log.d(TAG, "onComplete: Found location!!");

                       }else {
                           Log.d(TAG, "onComplete: couldn't find a location...");
                           Toast.makeText(AllowDeviceLocation.this, "unable to get current location...", Toast.LENGTH_SHORT).show();
                           Location currentLocation = (Location) task.getResult();
                           //currentLocation.
                       }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }
}
