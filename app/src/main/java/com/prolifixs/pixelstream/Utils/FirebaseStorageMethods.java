package com.prolifixs.pixelstream.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prolifixs.pixelstream.Home.MainHome.MainHomeActivity;
import com.prolifixs.pixelstream.Profile.MainProfile.ProfileAccountSettings.AccountSettingsActivity;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Utils.Shard;
import com.prolifixs.pixelstream.User.Utils.StringManipulation;
import com.prolifixs.pixelstream.User.model.Photo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by Prolifixs on 2/7/2018.
 */

public class FirebaseStorageMethods {
    private static final String TAG = "FirebaseStorageMethods";

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Context mContext;
    private String userID;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private StorageReference mStorageRefernce;

    //FireStore
    private FirebaseFirestore mFirestore;

    //vara
    private double mPhotoUploadProgress = 0;
    private String append = "";

    public FirebaseStorageMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();
        mStorageRefernce = FirebaseStorage.getInstance().getReference();

        //FireStore
        mFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }

    }

    /*
    * Get image count ---(Test version - incomplete).....
    * */
    //FIXME------Get count initially was for 'upLoadNewPhoto' method for counting images instead of append. either fix this issue of find new
    //FIXME--way to count all number of photo with fireStore
    public Task<Integer> getCount(final DocumentReference ref) {
        // Sum the count of each shard in the subcollection
        return ref.collection("photos").get()
                .continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        int count = 0;
                        for (DocumentSnapshot snap : task.getResult()) {
                            Shard shard = snap.toObject(Shard.class);
                            count += shard.count;
                        }
                        return count;
                    }
                });
    }




    /*
    * Upload new photo to firestore...
    * */
    public void uploadNewPhoto(String photoType, final String caption, int count, final String imUrl, Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: Attempting to upload new photo");

        //File-paths ==>>
        FilePaths filePaths = new FilePaths();

        //CASE 1: New photo-------------------------------------------------------------------------
        if (photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: Uploading a new photo");

            /*path points to files with unique user_id's...change file path for test mode
            * */
            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //appending random string at the end of /photo to make each its own unique i.d
            append = myRef.push().getKey().substring(7, 15);
            StorageReference storageReference = mStorageRefernce
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + append);

            //convert image url to bitmap
            if (bm == null){
                bm = ImageManager.getBitMap(imUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: ");
                    //firebase download url..
                    Uri firebaseUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //-------------IMPORTANT NOTE!!!!!------------------------------------------------------------
                    //------Add the new photo to "today_posts" node and "all_photo" node.
                      //--(1)--"today_posts" node is user personal photo. originally from "all_users/today_posts", but there isn't firestore query yet!!
                        //so for now the node is on test and is been accessed from outside the user id until firebase release sub-collection query

                      //---(2)-"all_photo" node is general photo from all users that upload photos. you will see in home feed

                    //---HOME-FEED----navigating to home-feed so the user can see their newly added photo.

                    //(1),(2)  Adding the photo to "all_photos" node and "today_posts"
                    DateTimeSystem dts = new DateTimeSystem();

                    String tags = StringManipulation.getTags(caption);
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference newPhotoKey = mFirestore.collection("all_photos").document();
                    Photo photo = new Photo();
                    photo.setCaption(caption);
                    photo.setDate_created(dts.getTimeStamp());
                    photo.setTags(tags);
                    photo.setUser_id(userId);
                    photo.setPhoto_id(newPhotoKey.getId());

                    //passing every data here to post...
                    addPhotoToDatabase(caption, dts.getTimeStamp(), firebaseUri.toString(), tags, userId, newPhotoKey.getId());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: photo upload failed.");
                    Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "upload progress: " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "%done");


                    //----HOME-FEED-----(after posting is successful, go to home feed)----you can move this(success or progress) or change direction...
                    Intent intent = new Intent(mContext, MainHomeActivity.class);
                    mContext.startActivity(intent);
                }
            });

        }



        //CASE 2: New Profile Photo------------------------------------------------------------------
        else if (photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: Uploading a new profile photo");



            /*path points to files with unique user_id's...change file path for test mode
            * */
            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageRefernce
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if (bm == null){
                bm = ImageManager.getBitMap(imUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: ");
                    //firebase download url..
                    Uri firebaseUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //getting the chosen image url and setting it...
                    setProfilePhoto(firebaseUri.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: photo upload failed.");
                    Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();

                    //Setting the viewpager to navigate automatically from accountSettingsActivity to EditProfileFragment
                    ((AccountSettingsActivity)mContext).setupViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter.getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );

                    if (progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "upload progress: " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "%done");
                }
            });
        }
        //CASE 3: Cover-Page photo---------(future work......)----------------------------------------
        else if (photoType.equals(mContext.getString(R.string.cover_photo))){
            Log.d(TAG, "uploadNewPhoto: Uploading a new cover photo");
            //TODO----------using the same method for uploading profilePhoto, use it to display cover-photo
            /*
            * cover photo code goes here..(Just copy and replace from profile_photo)......
            *
            * */
        }

    }

    /*
    * Adding new photo to database
    * */
    //FIXME-------photo path is set to outside "all_users" node. when subcollection query is available, change upload path inside individual user_id
    private void addPhotoToDatabase(String caption, String date, String url, String tags, String user_id, String photo_id){
        Log.d(TAG, "addPhotoToDatabase: Adding new photo to database...");

        Map<String, Object> upload = new HashMap<>();
        upload.put("caption", caption);
        upload.put("date_created", date);
        upload.put("image_path", url);
        upload.put("tags", tags);
        upload.put("user_id", user_id);
        upload.put("photo_id", photo_id);

        mFirestore.collection("all_photos").document(photo_id).set(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully uploaded to database");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: could not upload photo to database");
            }
        });
        mFirestore.collection("today_posts").document(photo_id).set(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: successfully uploaded to database");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error uploading to database...");
            }
        });

    }

    /*
    * Adding new Profile photo to database
    *
    * //inserting gotten url into all_users/user_id/profile_photo
    * */
    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: uploading profile photo to database" + url);

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> profile_photo = new HashMap<>();
        profile_photo.put("profile_photo", url);

        mFirestore.collection("all_users").document(userID).update("profile_photo", url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error changing profile photo");
            }
        });
    }




















}
