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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Utils.StringManipulation;
import com.prolifixs.pixelstream.User.model.Users;

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


    /*
    * Updating new username (got a null pointer when referenced to EditProfileFragment(checkIfUsernameExists method))
    * method is directly used inside fragment class instead. this method is suspended for now till issue is resolved.
    * */

    public void updateUsername(final String username) {
        Log.d(TAG, "updateUsername: Updating username to:" + username);
        
        mFirestore.collection("all_users").document(userID).update("username", username).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Username successfully updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: error updating username");
            }
        });

    }

 

    /*
    * Updating email address for existing user
    * @param updateEmail
    * */
    public void updateEmail(String email){
        mFirestore.collection("all_users").document(userID).update("email", email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Email successfully updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: could not update email");
            }
        });
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
                                Toast.makeText(mContext, "could not send verification email.", Toast.LENGTH_SHORT).show();
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
        //Users user = new Users( userID, 1, email, StringManipulation.condenseUsername(username));



        //------------------------------------  fireStore version *****-----------------------------------------------------------

        //----------------------all users (updated version)-----------------------------------------
        Map<String, Object> usersMap = new HashMap<>();
        usersMap.put("description", description);
        usersMap.put("display_name", username);
        usersMap.put("followers", 0);
        usersMap.put("following", 0);
        usersMap.put("posts", 0);
        usersMap.put("profile_photo", profile_photo);
        usersMap.put("username", StringManipulation.condenseUsername(username));
        usersMap.put("website", website);
        usersMap.put("user_id", userID);
        usersMap.put("phone_number", 1);
        usersMap.put("email", email);

        mFirestore.collection("all_users").document(userID).set(usersMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Sucessfully created users");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error uploading datas");
            }
        });

    }

}