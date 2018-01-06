package com.prolifixs.pixelstream.User.Register;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.FirebaseMethods;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class RegisterMainActivity extends AppCompatActivity {
    private static final String TAG = "RegisterMainActivity";

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseMethods firebaseMethods;

    //setup widgets
    private Context mContext;
    private EditText mEmail, mUsername, mPassword;
    private String email, username, password;
    private TextView mPleaseWait;
    private TextView mBackToLogin;
    private Button btnRegister;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = RegisterMainActivity.this;
        setContentView(R.layout.activity_main_register);
        firebaseMethods = new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: Starting activity");

        initWidgets();
        setupFirebaseAuth();
        init();

    }


    //Checking inputs and making sure all fields are filled before registration can be successful.--------
    private boolean checkInputs(String email, String username, String password){
        Log.d(TAG, "checkInputs: checking inputs for null vakues");
        if (email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(mContext, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return false;
        }
        return true;

    }

    //Initializing registration button
    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                if (checkInputs(email, username, password)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    //Part of this code is in 'FirebaseMethods.java'
                    firebaseMethods.registerNewEmail(email, password, username);



                }
            }
        });
    }
    /*
    * Initializing Activity widgets
    * */

    private void initWidgets(){
        Log.d(TAG, "initWidgets: initializing widgets");
        mContext = RegisterMainActivity.this;
        mEmail = (EditText) findViewById(R.id.inputEmail);
        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.inputPassword);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        mBackToLogin = (TextView) findViewById(R.id.back_to_login);

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
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
}
