package com.prolifixs.pixelstream.Profile.MainProfile.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prolifixs.pixelstream.R;

/**
 * Created by Prolifixs on 2/3/2018.
 */

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";

    //interfaces
    public interface OnConfirmPasswordListener{//because fragment would be reusable, interface is chosen
        public void onConfirmPassword(String password);
    }

    //vars
    OnConfirmPasswordListener onConfirmPasswordListener;
    TextView cancelDailog, confirmDialog, mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_editprofile_confirmpassword, container, false);

        cancelDailog = (TextView)view.findViewById(R.id.dialogCancel);
        confirmDialog = (TextView)view.findViewById(R.id.dialogConfirm);
        mPassword = (TextView)view.findViewById(R.id.confirm_password);

        cancelDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });
        
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: capturing password and confirming");

                String password = mPassword.getText().toString();
                if (!password.equals("")){
                    onConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }else {
                    Toast.makeText(getActivity(), "please type password", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            onConfirmPasswordListener = (OnConfirmPasswordListener)getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException" + e.getMessage() );
        }
    }
}
