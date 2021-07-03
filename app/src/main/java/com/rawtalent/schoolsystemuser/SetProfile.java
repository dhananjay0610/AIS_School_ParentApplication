package com.rawtalent.schoolsystemuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rawtalent.schoolsystemuser.NavigationActivities.DashBoard;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetProfile extends AppCompatActivity {


    Button save;
    EditText name, status;
    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    CircleImageView imageView;


    private Uri mImageHolder;
    public Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        getSupportActionBar().setTitle("Set Profile");
        initializeViews();

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSave();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void setSave() {

        String mName = name.getText().toString();
        String mStatus = status.getText().toString();

        if (mName.equals("") || mName.isEmpty()) {
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        if (mImageHolder == null) {
            saveDetails(mName, mStatus, "");
        } else {
            saveImage(mName, mStatus);
        }


    }

    public void saveDetails(String mName, String mStatus, String url) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uuid = mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.USER_PROFILE_NAME, mName);
        map.put(Constants.USER_PROFILE_BIO, mStatus);
        map.put(Constants.USER_PROFILE_PROFILEIMAGE, url);


        db.collection(Constants.COLLECTION_PARENTS).document(uuid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressDialog.dismiss();

                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mName).build();
                user.updateProfile(profileChangeRequest);

                Toast.makeText(SetProfile.this, "Successfully Saved !", Toast.LENGTH_SHORT).show();

                saveDataToSharedPref(mName,mStatus);

                Intent intent = new Intent(SetProfile.this, DashBoard.class);
                startActivity(intent);
                SetProfile.this.finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void saveImage(String mName, String mStatus) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uuid = mAuth.getCurrentUser().getUid();

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        StorageReference reference = mStorageReference.child(Constants.STORAGE_PROFILEIMAGES + "/" + uuid);
        reference.putFile(mImageHolder).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                saveDetails(mName, mStatus, uuid);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SetProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(SetProfile.this);
            builder.setTitle("saving details...");

            final ProgressBar progressBar = new ProgressBar(SetProfile.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }

    public void initializeViews() {
        name = (EditText) findViewById(R.id.setusername_et);
        status = (EditText) findViewById(R.id.setstatus_et);

        imageView = (CircleImageView) findViewById(R.id.profile_image);

        save = (Button) findViewById(R.id.save_btn);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageHolder = data.getData();
            //imageView.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder);
                imageView.setImageBitmap(bitmap);
                imageBitmap = bitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitmap.recycle();
                //      byteArray = stream.toByteArray();
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveDataToSharedPref(String name, String status) {
        String image = "";
        if (imageBitmap != null) {
            image = UserSharedPreference.encodeTobase64(imageBitmap);
        }


        UserSharedPreference.storeData(this,name,image,status);
    }
}