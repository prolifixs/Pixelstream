package com.prolifixs.pixelstream.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolifixs.pixelstream.User.Utils.StringManipulation;
import com.prolifixs.pixelstream.User.model.User;

import java.util.HashMap;
import java.util.Map;

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

    //FireStore
    private FirebaseFirestore mFirestore;

    //FireStore
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference myuserRef;
    private CollectionReference myUserSettingRef;


    public FirebaseMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();

        //FireStore
        mFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
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



        //------------------------------------  fireStore version *****-----------------------------------------------------------


        //user-------------------------------------------

        Map<String, String> userMap = new HashMap<>();
        userMap.put("user_id", userID);
        userMap.put("phone_number", "1");
        userMap.put("email", email);
        userMap.put("username", StringManipulation.condenseUsername(username));

        mFirestore.collection("users").document(userID).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: successfully added file to firestore");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Log.d(TAG, "onFailure: error adding file to firestore");
            }
        });

        //User_account_settings------------------------------

        Map<String, String> userSettings = new HashMap<>();
        userSettings.put("description", description);
        userSettings.put("display_name", username);
        userSettings.put("followers", "0");
        userSettings.put("following", "0");
        userSettings.put("posts", "0");
        userSettings.put("profile_photo", profile_photo);
        userSettings.put("username", username);
        userSettings.put("website", website);

        mFirestore.collection("user_account_settings").document(userID).set(userSettings).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: successfully added to firestore");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Log.d(TAG, "onFailure: Error adding file to firestore");
            }
        });


    }

}