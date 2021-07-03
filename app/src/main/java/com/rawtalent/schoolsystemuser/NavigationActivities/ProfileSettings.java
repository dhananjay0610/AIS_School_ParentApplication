package com.rawtalent.schoolsystemuser.NavigationActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rawtalent.UserProfile;
import com.rawtalent.schoolsystemuser.Constants;
import com.rawtalent.schoolsystemuser.LoginActivity;
import com.rawtalent.schoolsystemuser.R;
import com.rawtalent.schoolsystemuser.UserSharedPreference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private ChangeNavigationActivities mChangeActivityFromNavigation;
    private Context mContext;

    Button save;
    EditText name,status;
    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    CircleImageView imageView;





    private StorageReference mStorageReference;
    private boolean hasImage = false;



    private byte[] byteArray;
    private Uri mImageHolder;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mContext =getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);



        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mChangeActivityFromNavigation = new ChangeNavigationActivities();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);


        initializeViews();

        progressDialog.show();
        setData();

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

        if (mImageHolder==null){
            saveDetails(mName,mStatus,"");
        }else {
            saveImage(mName, mStatus);
        }




    }
    public void saveDetails(String mName,String mStatus,String url){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String uuid=mAuth.getCurrentUser().getUid();
        Map<String,Object> map=new HashMap<>();
        map.put("name",mName);
        map.put("status",mStatus);


        db.collection(Constants.COLLECTION_PARENTS).document(uuid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();

                FirebaseUser user=mAuth.getCurrentUser();
                UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(mName).build();
                user.updateProfile(profileChangeRequest);

                Toast.makeText(ProfileSettings.this, "Successfully Saved !", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(ProfileSettings.this, DashBoard.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileSettings.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void saveImage(String mName,String mStatus){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String uuid=mAuth.getCurrentUser().getUid();

        StorageReference mStorageReference= FirebaseStorage.getInstance().getReference();
        StorageReference reference = mStorageReference.child(Constants.STORAGE_PROFILEIMAGES + "/" + uuid);
        reference.putFile(mImageHolder).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                saveDetails(mName,mStatus,uuid);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileSettings.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(ProfileSettings.this);
            builder.setTitle("Please wait...");

            final ProgressBar progressBar = new ProgressBar(ProfileSettings.this);
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
                imageBitmap=bitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitmap.recycle();
                //      byteArray = stream.toByteArray();
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavigation();
    }
    private void setNavigation() {
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(2).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(this);
        final TextView name=mNavigationView.getHeaderView(0).findViewById(R.id.profileName);
        final TextView email=mNavigationView.getHeaderView(0).findViewById(R.id.email_tv);

        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        try{
            name.setText(""+mAuth.getCurrentUser().getDisplayName());
            email.setText(""+mAuth.getCurrentUser().getEmail());

            final ImageView profileImage=mNavigationView.getHeaderView(0).findViewById(R.id.imageView);
            UserProfile profile= UserSharedPreference.retrieveData(this);
            if (profile!=null && !profile.getImage().equals("")){
                Bitmap bitmap=UserSharedPreference.decodeBase64(profile.getImage());
                profileImage.setImageBitmap(bitmap);
            }

        }catch (Exception e){

        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("PROA", "onNavigationItemSelected: "+id);
        if (id == R.id.nav_contacts) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startContactsActivity(mContext);
        } else if (id == R.id.nav_home) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startHomeActivity(mContext);
        }else if (id==R.id.nav_profile){
         //   mDrawerLayout.closeDrawer(GravityCompat.START);
         //   mChangeActivityFromNavigation.startProfileActivity(mContext);
        }else if (id==R.id.nav_logout){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);
            builder.setMessage("Are you sure you want to logout ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ProfileSettings.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }


    public void setData(){
        getDetails();
    }


    public void getImage(){
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
                    hasImage=true;
                    imageView.setImageBitmap(bitmap);
                } else {
                    hasImage=false;
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

    public void getDetails(){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        db.collection(Constants.COLLECTION_PARENTS).document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String mname=documentSnapshot.getString("name");
                String mstatus=documentSnapshot.getString("status");

                name.setText(""+mname);
                status.setText(""+mstatus);

                progressDialog.dismiss();


                getImage();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                ProfileSettings.this.finish();
            }
        });
    }


    public void saveDataToSharedPref(String name, String status) {
        String image = "";
        if (imageBitmap != null) {
            image = UserSharedPreference.encodeTobase64(imageBitmap);
        }
        UserSharedPreference.storeData(this,name,image,status);
    }



}