package com.prolifixs.pixelstream.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Utils.StringManipulation;
import com.prolifixs.pixelstream.User.model.User;
import com.prolifixs.pixelstream.User.model.UserAccountSettings;

/**
 * Created by Prolifixs on 1/5/2018.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Context mContext;
    private String userID;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;

    public FirebaseMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();

        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /*
    * -------------------Checking if username exists-------------------------------------------------
    * */
    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkIfUsernameExists: checking if: " + username + "already exists");

        User user = new User();///User is created in User/model/user.java class.

        //loop method for searching data in firebase
        for (DataSnapshot ds: dataSnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfUsernameExists: datasnapshot" + ds);

            //retrieve information from firebase--------------------------
            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username" + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH" + user.getUsername());
                return true;
            }
        }return false;
    }

    //Registering new email and password to fireBase Authentication.------------------------------------------
    public void registerNewEmail(String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            //Send verification email.
                            sendVerificationEmail();


                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success" + userID);
                            FirebaseUser user = mAuth.getCurrentUser();

                            userID = mAuth.getCurrentUser().getUid();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    /*
    * VERIFY NEW USER EMAIL AFTER REGISTRATION TO COMPLETE SIGNUP-----------------------------------------------------
    * */

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    /*
    * ADDING A NEW USER TO THE DATABASE---------------------------------------------------
    *
    *
    * */
    public void addNewUser(String email, String username, String description, String website, String profile_photo){
            //create a user
        User user = new User( userID, 1, email, StringManipulation.condenseUsername(username));

        //------------------------------replace firebase to firestore here-----------------------------------------
        //insert data into users in database
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        //insert data into user_account_settings in database
        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                username,
                website
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);
    }







}
