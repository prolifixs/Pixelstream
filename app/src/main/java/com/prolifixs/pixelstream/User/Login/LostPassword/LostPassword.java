package com.prolifixs.pixelstream.User.Login.LostPassword;

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
import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Login.Fragment.PasswordChangedSuccess;

/**
 * Created by Prolifixs on 1/5/2018.
 */

public class LostPassword extends AppCompatActivity {
    private static final String TAG = "LostPassword";

    //FireBase
    private FirebaseAuth mAuth;


    //setup widgets
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail;
    private TextView mPleaseWait;
    private Button mNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostpassword);
        Log.d(TAG, "onCreate: Starting Activity");

        mContext = LostPassword.this;
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmail = (EditText) findViewById(R.id.inputEmail);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);


        mAuth = FirebaseAuth.getInstance();

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);


        init();
    }


    //Simple method for checking empty strings ---------------------
    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: Checking string if null.");
        if (string.equals("")){
            return true;
        }else {
            return false;
        }
    }

    private void init() {
        mNext = (Button) findViewById(R.id.btn_next);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to send email notification");

                //Hide soft keyboard-----------------------------------
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != LostPassword.this.getCurrentFocus())
                    imm.hideSoftInputFromWindow(LostPassword.this.getCurrentFocus()
                            .getApplicationWindowToken(), 0);


                String email = mEmail.getText().toString();

                if (isStringNull(email)){
                    Toast.makeText(mContext, "Enter your registered Email", Toast.LENGTH_SHORT).show();
                }else{

                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Intent intent = new Intent(mContext, PasswordChangedSuccess.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(mContext, "Failed to reset email", Toast.LENGTH_SHORT).show();
                            }
                            mProgressBar.setVisibility(View.GONE);
                            mPleaseWait.setVisibility(View.GONE);
                        }
                    });

                }

            }
        });


    }




}
