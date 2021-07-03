package com.rawtalent.schoolsystemuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ShowUserProfile extends AppCompatActivity {


    Button send, cancel;
    ImageView imageView;
    TextView name, bio, status;

    FirebaseAuth mAuth;

    String uid = "";

    String mname = "";

    ConstraintLayout loaderLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user_profile);


        uid = getIntent().getStringExtra("uid");
        initialize();
        getDetails();


    }


    public void initialize() {

        mAuth = FirebaseAuth.getInstance();

        send = findViewById(R.id.send);
        cancel = findViewById(R.id.cancel);

        loaderLayout = findViewById(R.id.loaderlayout);

        imageView = findViewById(R.id.show_image);
        name = findViewById(R.id.showname);
        bio = findViewById(R.id.showbio);
        status = findViewById(R.id.showrequeststatus);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(uid);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest(uid);
            }
        });


    }


    public void getImage() {
        //showProgressDialogWithTitle("Fetching the image...");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(Constants.STORAGE_PROFILEIMAGES).child(uid);
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //       Log.d(TAG, "onSuccess: Got profile image successfully");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log.d(TAG, "onFailure: failed to get image "+e.getMessage());

            }
        });
    }

    public void getDetails() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        showLoader();

        db.collection(Constants.COLLECTION_TEACHERS).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mname = documentSnapshot.getString(Constants.USER_PROFILE_NAME);
                String mstatus = documentSnapshot.getString(Constants.USER_PROFILE_BIO);

                name.setText("" + mname);
                bio.setText("" + mstatus);
                disappearLoader();

                getStatus();
                getImage();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ShowUserProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                ShowUserProfile.this.finish();
                disappearLoader();

            }
        });
    }

    public void sendRequest(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        showLoader();

        Map<String, Object> map = new HashMap<>();
        map.put(Constants.CONTACTS_NAME, mAuth.getCurrentUser().getDisplayName());
        map.put(Constants.CONTACTS_STATUS, 0);
        map.put(Constants.CONTACTS_CHATID, "");

        Map<String, Object> map2 = new HashMap<>();
        map2.put(Constants.CONTACTS_NAME, mname);
        map2.put(Constants.CONTACTS_STATUS, 0);
        map2.put(Constants.CONTACTS_CHATID, "");

        db.collection(Constants.COLLECTION_TEACHERS).document(uid).collection(Constants.PARENTS_CONTACTS).document(mAuth.getCurrentUser().getUid()).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        db.collection(Constants.COLLECTION_PARENTS).document(mAuth.getCurrentUser().getUid()).collection(Constants.PARENTS_CONTACTS).document(uid).set(map2).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        send.setVisibility(View.GONE);
                                        cancel.setVisibility(View.VISIBLE);
                                        disappearLoader();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShowUserProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                disappearLoader();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowUserProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                disappearLoader();
            }
        });

    }

    public void cancelRequest(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        showLoader();

        db.collection(Constants.COLLECTION_TEACHERS).document(uid).collection(Constants.PARENTS_CONTACTS).document(mAuth.getCurrentUser().getUid()).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowUserProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                disappearLoader();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection(Constants.COLLECTION_PARENTS).document(mAuth.getCurrentUser().getUid()).collection(Constants.PARENTS_CONTACTS).document(uid).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                disappearLoader();
                                send.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowUserProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        disappearLoader();
                    }
                });
            }
        });
    }


    public void getStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        db.collection(Constants.COLLECTION_PARENTS).document(mAuth.getCurrentUser().getUid()).collection(Constants.PARENTS_CONTACTS).document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                try {

                    long s = documentSnapshot.getLong("status");
                    if (s == 0) {
                        status.setText("Request Pending");
                        cancel.setVisibility(View.VISIBLE);

                    } else if (s == 1) {
                        status.setText("Request Accepted");
                        status.setTextColor(Color.GREEN);


                    } else {
                        status.setText("Request Rejected");
                        status.setTextColor(Color.RED);
                        send.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    status.setText("");
                    send.setVisibility(View.VISIBLE);

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                status.setText("");
                send.setVisibility(View.VISIBLE);
            }
        });
    }


    public void showLoader() {
        loaderLayout.setVisibility(View.VISIBLE);
    }

    public void disappearLoader() {
        loaderLayout.setVisibility(View.GONE);
    }
}