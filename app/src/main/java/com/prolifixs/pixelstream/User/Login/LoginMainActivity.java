package com.prolifixs.pixelstream.User.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prolifixs.pixelstream.GoogleMaps.AllowDeviceLocation;
import com.prolifixs.pixelstream.Home.MainHome.MainHomeActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Upload.MainUpload.UploadActivity;
import com.prolifixs.pixelstream.User.Login.LostPassword.LostPassword;
import com.prolifixs.pixelstream.User.Register.RegisterMainActivity;
import com.prolifixs.pixelstream.Utils.Permissions;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class LoginMainActivity extends AppCompatActivity {
    private static final String TAG = "LoginMainActivity";

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //setup widgets
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait, mForgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_login);


        Log.d(TAG, "onCreate: Starting activity");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mEmail = (EditText) findViewById(R.id.inputEmail);
        mPassword = (EditText) findViewById(R.id.inputPassword);
        mForgotPassword = (TextView) findViewById(R.id.forgotPassword);
        mContext = LoginMainActivity.this;

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();

        init();

    }

    //Simple method for checking empty strings -- method is used in 'init'
    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: Checking string if null.");
        if (string.equals("")){
            return true;
        }else {
            return false;
        }
    }


    //-----------------------------------------------F I R E   B A S E--------------------------------------------------


    /*
    * Method for handling the login
    * */

    private void init(){
        //Initializing the button for logging in.
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnRegister = (Button) findViewById(R.id.btn_register);


        //For the login-----------------------------------------------------------
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to login");

                //Hide soft keyboard-----------------------------------
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != LoginMainActivity.this.getCurrentFocus())
                    imm.hideSoftInputFromWindow(LoginMainActivity.this.getCurrentFocus()
                            .getApplicationWindowToken(), 0);




                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password)){
                    Toast.makeText(mContext, "you must fill out all fields", Toast.LENGTH_SHORT).show();
                }else if (!isStringNull(email) && isStringNull(password)){
                    Toast.makeText(mContext, "Password filled cannot be empty", Toast.LENGTH_SHORT).show();
                }else if (isStringNull(email) && !isStringNull(password)){
                    Toast.makeText(mContext, "Email field is empty", Toast.LENGTH_SHORT).show();

                }else if (checkPermissionsArray(Permissions.LOCATION_PERMISSIONS)){
                    //nothing happens here as checking and authorization is done within "checkpermissionArray" method
                }
                else{
                    //before logging in, check if user permission is ok....login is been moved below//////////////////
                    Intent intent = new Intent(mContext, AllowDeviceLocation.class);
                    startActivity(intent);


                    /*mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginMainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        //checking if user is verified so it can login.
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        try {
                                            if (user.isEmailVerified()){
                                                Log.d(TAG, "onComplete: successful. navigating to home activity");
                                                Intent intent = new Intent(mContext, MainHomeActivity.class);
                                                startActivity(intent);

                                            }else{
                                                //keyboard
                                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                if (null != LoginMainActivity.this.getCurrentFocus())
                                                    imm.hideSoftInputFromWindow(LoginMainActivity.this.getCurrentFocus()
                                                            .getApplicationWindowToken(), 0);

                                                //Error message
                                                Toast.makeText(mContext, "Check your inbox for verification email", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }

                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException" + e.getMessage() );

                                        }

                                        //Navigating to home activity

                                        Intent intent = new Intent(mContext, MainHomeActivity.class);
                                        startActivity(intent);
                                        //updateUI(user);
                                    } else {

                                        // If sign in fails, display a message to the user.
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginMainActivity.this, "Incorrect email or password",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });*/
                }
            }
        });
        //REGISTER NEW USER BUTTON-----------------------------------------------
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to register activity");

                Intent intent = new Intent(mContext, RegisterMainActivity.class);
                startActivity(intent);
            }
        });

        //---FORGOT PASSWORD?--------------------------------------------------------
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Retrieving lost password");

                if (mAuth.getCurrentUser() == null) {

                    Intent intent = new Intent(mContext, LostPassword.class);
                    startActivity(intent);
                }
            }
        });

        //if log_in is successful, navigating to homeActivity-------------------------------------
        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(mContext, MainHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    //Hide keyboard------------------------------------------------------------------------------
    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != LoginMainActivity.this.getCurrentFocus())
            imm.hideSoftInputFromWindow(LoginMainActivity.this.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }



    //------------------------------V E R I F Y I N G   P E R M I S S I O N S---------------------------------

    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }

        //******************************************* ACCOUNT -LOGIN ***************************************************************************
        mProgressBar.setVisibility(View.VISIBLE);
        mPleaseWait.setVisibility(View.VISIBLE);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginMainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //checking if user is verified so it can login.
                            FirebaseUser user = mAuth.getCurrentUser();

                            try {
                                if (user.isEmailVerified()){
                                    Log.d(TAG, "onComplete: successful. navigating to home activity");
                                    Intent intent = new Intent(mContext, MainHomeActivity.class);
                                    startActivity(intent);

                                }else{
                                    //keyboard
                                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (null != LoginMainActivity.this.getCurrentFocus())
                                        imm.hideSoftInputFromWindow(LoginMainActivity.this.getCurrentFocus()
                                                .getApplicationWindowToken(), 0);

                                    //Error message
                                    Toast.makeText(mContext, "Check your inbox for verification email", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    mPleaseWait.setVisibility(View.GONE);
                                    mAuth.signOut();
                                }

                            }catch (NullPointerException e){
                                Log.e(TAG, "onComplete: NullPointerException" + e.getMessage() );

                            }

                            //Navigating to home activity

                            Intent intent = new Intent(mContext, MainHomeActivity.class);
                            startActivity(intent);
                            finish();
                            //updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            mProgressBar.setVisibility(View.GONE);
                            mPleaseWait.setVisibility(View.GONE);
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginMainActivity.this, "Incorrect email or password",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
        return true;
    }

    /*
    * Checking a single permission if it has been verified.
    * */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(LoginMainActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            mAuth.signOut();
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);

            return true;
        }
    }//------------------------------V E R I F Y I N G   P E R M I S S I O N S---------------------------------


    
    
    
    
    
    
     /*
    * IMPORTANT : DO NOT TOUCH.
    * Setting up firebase Auth for the first time. this is the beginning of authentication.
    * */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null){
                    //User is Signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                }else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    //--------------------------------------------Exiting application on back pressed------------------------------
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

}
