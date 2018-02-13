package com.prolifixs.pixelstream.Upload.MainUpload.NextActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.Utils.FirebaseMethods;
import com.prolifixs.pixelstream.Utils.FirebaseStorageMethods;
import com.prolifixs.pixelstream.Utils.UniversalImageLoader;

/**
 * Created by Prolifixs on 2/6/2018.
 */

public class NextActivity extends AppCompatActivity{
    private static final String TAG = "NextActivity";

    //Firebase setup
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseStorageMethods mFirebaseStorageMethods;


    //FireStore
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference myUser;
    private String userID;

    //widgets
    private ImageView mBack;
    private TextView mShare;
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private String randomString = "";
    private int imageCount = 0;
    private String imgUrl;
    private Intent intent;
    private Bitmap bitmap;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Log.d(TAG, "onCreate: Got the selected image" + getIntent().getStringExtra(getString(R.string.selected_image)));
        setupFirebaseAuth();
        setImage();

        mBack = (ImageView) findViewById(R.id.ivBackArrow);
        mShare = (TextView) findViewById(R.id.tvShare);
        mCaption = (EditText) findViewById(R.id.description);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mFirebaseStorageMethods = new FirebaseStorageMethods(NextActivity.this);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });

        //-------------------------------------F I R E S T O R E   U P L O A D---------------------------------------------------------
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to upload media");
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();

                if (intent.hasExtra(getString(R.string.selected_image))){//coming from gallery
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseStorageMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl, null);

                }else if (intent.hasExtra(getString(R.string.selected_bitmap))){//coming from camera
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseStorageMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, bitmap);

                }
                //-------------------------------------F I R E S T O R E   U P L O A D--------------------------------------------


                //Uploading the image to firebase...

            }
        });

    }
    /*
    * Gets the image url from the incoming intent and displays the chosen image....
    * */
    private void setImage(){

        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        if (intent.hasExtra(getString(R.string.selected_image))){//coming from gallery
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            UniversalImageLoader.setSingleImage(imgUrl, image, null, mAppend);
            Log.d(TAG, "setImage: got new selected image" + imgUrl);

        }else if (intent.hasExtra(getString(R.string.selected_bitmap))){//coming from camera
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);

        }

    }

    //-----------------------------------------------F I R E   B A S E------------------------------------------------------------



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mFirebaseFirestore = FirebaseFirestore.getInstance();
                myUser = mFirebaseFirestore.collection("all_users");
                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                myUser.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        Log.d(TAG, "onEvent: initilization");

                    }
                });

                //------------------------------------------------------------


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
    //--------------------------------------------------------------------------------------------------------------------------------
}
