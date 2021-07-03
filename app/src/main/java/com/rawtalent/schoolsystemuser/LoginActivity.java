package com.rawtalent.schoolsystemuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rawtalent.schoolsystemuser.NavigationActivities.DashBoard;

public class LoginActivity extends AppCompatActivity {


    private Button login;
    private EditText email, password;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    private TextView forgetPass;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        initializeViews();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithCredentials();
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordResetDialog dialog=new PasswordResetDialog(LoginActivity.this);
                dialog.show(getSupportFragmentManager(), "Reset Password");
            }
        });
    }


    public void initializeViews() {
        email = (EditText) findViewById(R.id.email_et);
        password = (EditText) findViewById(R.id.password_et);
        forgetPass = (TextView) findViewById(R.id.forgetpassword_tv);

        login = (Button) findViewById(R.id.login_btn);
    }


    public void loginWithCredentials() {

        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();

        if (mEmail.equals("") || mEmail.isEmpty()) {
            Toast.makeText(this, "Please enter EMAIL", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPassword.equals("") || mEmail.isEmpty()) {
            Toast.makeText(this, "Please enter PASSWORD", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                updateUI();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "login failure: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Checking details...");

            final ProgressBar progressBar = new ProgressBar(LoginActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }




    public void updateUI() {
        check();
    //    Toast.makeText(this, "login success", Toast.LENGTH_SHORT).show();
    }

    public void check(){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser mUser=auth.getCurrentUser();

        try {

            String name=mUser.getDisplayName();

            if (name.equals("")||name.isEmpty()){
                Intent intent=new Intent(LoginActivity.this, SetProfile.class);
                progressDialog.dismiss();
                startActivity(intent);
                LoginActivity.this.finish();
            }else{
                Intent intent=new Intent(LoginActivity.this, DashBoard.class);
                progressDialog.dismiss();
                startActivity(intent);
                LoginActivity.this.finish();
            }

        }catch (Exception e){
            Intent intent=new Intent(LoginActivity.this, SetProfile.class);
            progressDialog.dismiss();
            startActivity(intent);
            LoginActivity.this.finish();
        }



    }



}