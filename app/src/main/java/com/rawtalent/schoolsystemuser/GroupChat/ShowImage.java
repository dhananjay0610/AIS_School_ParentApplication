package com.rawtalent.schoolsystemuser.GroupChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.schoolsystemuser.R;

public class ShowImage extends AppCompatActivity {

    ImageView imageView;
    Button button;
    String url,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        button=findViewById(R.id.downloadbtn);
        imageView=findViewById(R.id.image_view);

        url = getIntent().getStringExtra("url");
         name=getIntent().getStringExtra("name");

        getImage(url);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    DownloadManager downloadManager=(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri=Uri.parse(url);

                    DownloadManager.Request request=new DownloadManager.Request(uri);
                    request.setTitle("File Download");
                    request.setDescription("downloading file...");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(ShowImage.this,"CollegeChatApp",""+name);
                    downloadManager.enqueue(request);

                }catch (Exception e){
                    Toast.makeText(ShowImage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void getImage(String url){
        //showProgressDialogWithTitle("Fetching the image...");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseAuth mAuth2 = FirebaseAuth.getInstance();

//        StorageReference storageReference = storage.getReference().child("ProfileImages").child(mAuth2.getCurrentUser().getUid());

        StorageReference storageReference = storage.getReferenceFromUrl(url);

        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //       Log.d(TAG, "onSuccess: Got profile image successfully");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {

                }
                // UserSharedPreference.storeBackgroundImage(backgroundImage.this, encodeTobase64(bitmap));
                //  hideProgressDialogWithTitle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log.d(TAG, "onFailure: failed to get image "+e.getMessage());

                Toast.makeText(ShowImage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                ShowImage.this.finish();
            }
        });
    }
}