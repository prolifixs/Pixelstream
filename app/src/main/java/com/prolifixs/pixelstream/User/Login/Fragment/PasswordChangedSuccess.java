package com.prolifixs.pixelstream.User.Login.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.prolifixs.pixelstream.R;
import com.prolifixs.pixelstream.User.Login.LoginMainActivity;

/**
 * Created by Prolifixs on 1/6/2018.
 */

public class PasswordChangedSuccess extends AppCompatActivity {
    private static final String TAG = "PasswordChangedSuccessF";


    //Widgets
    private Button btn_LoginScreen;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostpassword_success);
        Log.d(TAG, "onCreate: Starting activity");

        btn_LoginScreen = (Button) findViewById(R.id.btn_backToLogin);


        btn_LoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back to login screen.");

                Intent intent = new Intent(PasswordChangedSuccess.this, LoginMainActivity.class);
                startActivity(intent);
            }
        });

    }

}
