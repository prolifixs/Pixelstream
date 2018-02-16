package com.prolifixs.pixelstream.User.Register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolifixs.pixelstream.GoogleMaps.AllowDeviceLocation;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.FirebaseMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class RegisterMainActivity extends AppCompatActivity {
    private static final String TAG = "RegisterMainActivity";

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //FireStore
    private FirebaseFirestore mFirestore;


    private String append = "";

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
        Log.d(TAG, "checkInputs: checking inputs for null vakues");//////////////////ASSIGN MORE LOGIC HERE ELSE APP CRASHES
        if (email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(mContext, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return false;
        }
        return true;

    }

    //Initializing registration button
    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Creating a new user");


                //Hide soft keyboard-----------------------------------
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != RegisterMainActivity.this.getCurrentFocus())
                    imm.hideSoftInputFromWindow(RegisterMainActivity.this.getCurrentFocus()
                            .getApplicationWindowToken(), 0);

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
        /*
    * To go back to login screen.
    * */
        mBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back to login screen.");
                finish();
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
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    //User is Signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());

                    //checking current state of database if anything is changed--------
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {////on success
                            checkIfUsernameExists(username);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {///on error

                        }
                    });
                    finish();
                }else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    /*
* checking if username already exists (firestore version)******-----------------------------------------------
*
* */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if the " + username + "Exists");

        String userID = mAuth.getCurrentUser().getUid();
        //FireStore
        mFirestore = FirebaseFirestore.getInstance();

        //--Reference to all_users node--
        final CollectionReference docRef = mFirestore.collection("all_users");

        docRef.whereEqualTo("username", username).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null){
                    Log.w(TAG, "onEvent: ", e);
                    return;
                }

                for (DocumentSnapshot doc : documentSnapshots){
                    if (doc.get("username").equals(username)){
                        //when username exists
                        Log.d(TAG, "FOUND A MATCH: Appending digits to username");

                        //if username exists, append a random string
                        append = myRef.push().getKey().substring(3, 10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name"
                                + append);
                    }
                }
                //Appened username now is
                String mUsername = "";
                mUsername = username + append;

                //add new user to the database--------------------------------------
                //find the Main Code in 'firebasemethods.java'
                firebaseMethods.addNewUser(email, mUsername, "", "", "");

                Toast.makeText(mContext, "Signup successful, sending verification email", Toast.LENGTH_SHORT).show();
                mAuth.signOut();//until user verify email.


                //Before any further advancement, request device location--------------------------------------------------
                Intent intent = new Intent(mContext, AllowDeviceLocation.class);
                startActivity(intent);

            }
        });
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
