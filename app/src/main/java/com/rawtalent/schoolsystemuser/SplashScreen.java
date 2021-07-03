package com.rawtalent.schoolsystemuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.UserProfile;
import com.rawtalent.schoolsystemuser.NavigationActivities.DashBoard;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    //User is logged in
                    try {
                        if (user.getDisplayName().equals("") || user.getDisplayName().isEmpty()) {
                            //User has not set profile yet
                            gotoSetProfile();
                        } else {
                            //User has set his profile go to dashboard directly
                            gotoDashBoard();
                        }
                    } catch (Exception e) {
                        gotoSetProfile();
                    }

                } else {
                    //No user is logged in go to Login Page
                    gotoLoginPage();
                }

            }
        }, 4000);
    }


    public void gotoDashBoard() {

        UserProfile profile=UserSharedPreference.retrieveData(SplashScreen.this);
        if(profile==null || profile.getImage().equals("")){
            Log.d("APPTEST", "gotoDashBoard: no profile image");
            getImage();
        }
        Intent myIntent = new Intent(SplashScreen.this, DashBoard.class);
        SplashScreen.this.startActivity(myIntent);
        SplashScreen.this.finish();
    }

    public void gotoLoginPage() {
        Intent myIntent = new Intent(SplashScreen.this, LoginActivity.class);
        SplashScreen.this.startActivity(myIntent);
        SplashScreen.this.finish();
    }

    public void gotoSetProfile() {
        Intent myIntent = new Intent(SplashScreen.this, SetProfile.class);
        SplashScreen.this.startActivity(myIntent);
        SplashScreen.this.finish();
    }


    public void getImage(){

        Log.d("APPTEST", "getting image from firebase..");

        //showProgressDialogWithTitle("Fetching the image...");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseAuth mAuth2 = FirebaseAuth.getInstance();
        StorageReference storageReference = storage.getReference().child(Constants.STORAGE_PROFILEIMAGES).child(mAuth2.getCurrentUser().getUid());
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //       Log.d(TAG, "onSuccess: Got profile image successfully");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    UserSharedPreference.storeImage(SplashScreen.this,""+UserSharedPreference.encodeTobase64(bitmap));
                    Log.d("APPTEST", "Image downloaded and saved!");
                } else {

                }
                // UserSharedPreference.storeBackgroundImage(backgroundImage.this, encodeTobase64(bitmap));
                //  hideProgressDialogWithTitle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log.d(TAG, "onFailure: failed to get image "+e.getMessage());

            }
        });
    }
}