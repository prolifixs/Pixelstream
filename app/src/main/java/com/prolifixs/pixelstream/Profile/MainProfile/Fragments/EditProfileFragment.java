package com.prolifixs.pixelstream.Profile.MainProfile.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolifixs.pixelstream.Profile.MainProfile.dialogs.ConfirmPasswordDialog;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Upload.MainUpload.UploadActivity;
import com.prolifixs.pixelstream.User.model.Users;
import com.prolifixs.pixelstream.Utils.FirebaseMethods;
import com.prolifixs.pixelstream.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Prolifixs on 1/4/2018.
 */

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener{
    private static final String TAG = "EditProfileFragment";

    //interface
    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password:" + password);//security reason: remove log if working well

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        /////////////////////Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");
                            ////////////////checking to see if the email is not in use already
                            mAuth.fetchProvidersForEmail(mEmali.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                 if (task.isSuccessful()){
                                     try {

                                     if (task.getResult().getProviders().size() == 1){
                                         Log.d(TAG, "onComplete: that email is already in use");
                                         Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();

                                     } else if (task.getResult().getProviders().size() == 0){
                                         Log.d(TAG, "onComplete: That email is available");

                                         ///////////////////Email is available, so update it.
                                         mAuth.getCurrentUser().updateEmail(mEmali.getText().toString())
                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task) {
                                                         if (task.isSuccessful()) {
                                                             Log.d(TAG, "User email address updated.");
                                                             Toast.makeText(getActivity(), "Email changed successfully.", Toast.LENGTH_SHORT).show();
                                                             mFirebaseMethods.updateEmail(mEmali.getText().toString());
                                                         }
                                                     }
                                                 });

                                        }
                                     }catch (NullPointerException e){
                                         Log.e(TAG, "NullPointerException: " + e.getMessage());
                                     }
                                 }
                                }
                            });
                        }else {
                            Log.d(TAG, "onComplete: re-authentication failed");
                        }

                    }
                });
    }

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    //FireStore
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference myUserRef;
    private CollectionReference myUserSettingsRef;
    private String userID;

    //EditProfileFragment Widgets
    private TextView mCurrentLocation;
    private EditText  mDisplayName, mUsername, mWebsite, mDescription, mEmali, mPhoneNumber;
    private ImageView mChangePhoto;
    private ImageView mSaveChanges;
    private CircleImageView mProfilePhoto;
    private ProgressBar mProgressBar;
    private Context mContext;
    private FirebaseMethods mFirebaseMethods;

    //Constructors
    private Users mUsers;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_editprofile, container, false);

        mCurrentLocation = (TextView) view.findViewById(R.id.currentLocation);
        mDisplayName = (EditText)view.findViewById(R.id.full_name);
        mUsername = (EditText)view.findViewById(R.id.username);
        mWebsite = (EditText)view.findViewById(R.id.website);
        mDescription = (EditText)view.findViewById(R.id.description);
        mEmali = (EditText)view.findViewById(R.id.email);
        mPhoneNumber = (EditText)view.findViewById(R.id.phone_number);
        mProgressBar = (ProgressBar)view.findViewById(R.id.EditProgressBar);
        mProfilePhoto = (CircleImageView)view.findViewById(R.id.profile_photo);
        mChangePhoto = (ImageView) view.findViewById(R.id.changePhoto);
        mSaveChanges = (ImageView) view.findViewById(R.id.saveChanges);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseMethods = new FirebaseMethods(mContext);

        //method calling
        setupFirebaseAuth();


        //Back arrow for navigating back to profile activity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back to profile activity");

                getActivity().finish();
            }
        });
        
        //checkMark to save changes
        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save changes");
                saveProfileSettings();
            }
        });
        return view;
    }

    private void setProfileWidgets(){

        Users(new MyCallBack() {
            @Override
            public void onCallBack(Users settings) {
                Log.d(TAG, "Retriving data from firstore");
                mUsers = settings;

                mCurrentLocation.setText(settings.getCurrent_location());
                mDisplayName.setText(settings.getDisplay_name());
                mUsername.setText(settings.getUsername());
                mWebsite.setText(settings.getWebsite());
                mDescription.setText(settings.getDescription());
                UniversalImageLoader.setSingleImage(settings.getProfile_photo(), mProfilePhoto, null, "");
                mPhoneNumber.setText(String.valueOf(settings.getPhone_number()));
                mEmali.setText(String.valueOf(settings.getEmail()));
                mProgressBar.setVisibility(View.GONE);

            }
        });

        //Changing  profile photo.....
        mChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Changing profile photo");
                //in the future, make a dialog so the user can remove photo without changing
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//will give it a number of 268435456(a root task)
                getActivity().startActivity(intent);
                getActivity().finish();

            }
        });

    }

    /*
    * Retrieves the data contained inside the widgets and submits it to database
    * Before doing so, it checks if the username is unique or available.
    * */
    private void saveProfileSettings(){
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmali.getText().toString();
        final long phoneNumber  = Long.parseLong(mPhoneNumber.getText().toString());//////////Phone number not set yet!!!

        //(1)Case 1: When the user has made a change to their username
        if (!mUsers.getUsername().equals(username)){
            checkingIfusernameExist(username);//not working properly

        }
        //(2)Case 2: When the user has made change to their email
        if (!mUsers.getEmail().equals(email)){

            //1. Re-authenticate
                //2.1 Confirm password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);
        }
        //(3)Case 3: When the user has made changes to their display_name.
        if (!mUsers.getDisplay_name().equals(displayName)){
            //update display_name.
            mFirebaseMethods.updateUserInfo(displayName, null, null);
            Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();

        }
        //(4)Case 4: when the user has made changes to their description
        if (!mUsers.getDescription().equals(description)){
            //Update description
            mFirebaseMethods.updateUserInfo(null, description, null);
            Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
        }
        //(5)Case 5: When the user has made changes to their website
        if (!mUsers.getWebsite().equals(website)){
            //Update website
            mFirebaseMethods.updateUserInfo(null, null, website);
            Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
        }
    }

    /*
* checking if username already exists (firestore version)---------------------------
*
* */
    private void checkingIfusernameExist(final String usernameToCompare){

        //----------------------------------------------------------------
        final Query mQuery = mFirebaseFirestore.collection("all_users").whereEqualTo("username", usernameToCompare);
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "checkingIfusernameExist: checking if username exists");

                if (task.isSuccessful()){
                    for (DocumentSnapshot ds: task.getResult()){
                        String userNames = ds.getString("username");
                            if (userNames.equals(usernameToCompare)) {
                                Log.d(TAG, "checkingIfusernameExist: FOUND A MATCH -username already exists");
                                Toast.makeText(getActivity(), "username already exists", Toast.LENGTH_SHORT).show();
                            }
                    }
                }
                //checking if task contains any payload. if no, then update
                if (task.getResult().size() == 0){
                    try{

                    Log.d(TAG, "onComplete: MATCH NOT FOUND - username is available");
                    Toast.makeText(getActivity(), "username changed", Toast.LENGTH_SHORT).show();
                    //Updating new username............
                        mFirebaseMethods.updateUsername(usernameToCompare);

                    }catch (NullPointerException e){
                        Log.e(TAG, "NullPointerException: " + e.getMessage() );
                    }
                }
            }
        });

    }

    



    //-----------------------------------------------F I R E   B A S E--------------------------------------------------

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mFirebaseFirestore = FirebaseFirestore.getInstance();
                myUserSettingsRef = mFirebaseFirestore.collection("all_users");



                myUserSettingsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        Log.d(TAG, "onEvent: initilization");
                        setProfileWidgets();
                    }
                });

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
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    //---------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------F I R E   S T O R E--------------------------------------------------

    //-----------------Retrieving all_users node information from firestore
    private void Users(final MyCallBack myCallBack){
        Log.d(TAG, "settings: Retrieving user account settings from firestore");
        final DocumentReference mSettings = mFirebaseFirestore.collection("all_users").document(userID);
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        mSettings.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Users settings = documentSnapshot.toObject(Users.class);
                myCallBack.onCallBack(settings);

            }
        });
    }

    public interface MyCallBack{
        void onCallBack(Users settings);
    }
    //---------------------------------------------------------------------------------------------------------------------------




}
