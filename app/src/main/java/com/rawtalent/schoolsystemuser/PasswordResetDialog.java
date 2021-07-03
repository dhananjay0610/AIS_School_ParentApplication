package com.rawtalent.schoolsystemuser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetDialog extends AppCompatDialogFragment {

    EditText email;
    Button reset;
    Context context;
    FirebaseAuth mAuth;


    public PasswordResetDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_reset_dialog, null);

        builder.setView(view);
        email = view.findViewById(R.id.emailet);
        reset = view.findViewById(R.id.resetPassword);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail=email.getText().toString();
                if (mEmail.equals("")||mEmail.isEmpty()){
                    Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show();
                }else{
                    resetEmail(mEmail);
                }

            }
        });


        return builder.create();
    }



    public void resetEmail(String email){
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "New password is successfully sent to your email!", Toast.LENGTH_LONG).show();
                PasswordResetDialog.this.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
