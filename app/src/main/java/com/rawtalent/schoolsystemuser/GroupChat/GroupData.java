package com.rawtalent.schoolsystemuser.GroupChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.schoolsystemuser.Constants;
import com.rawtalent.schoolsystemuser.ListChats.ChatListAdapter;
import com.rawtalent.schoolsystemuser.ModelClass.ContactModel;
import com.rawtalent.schoolsystemuser.NavigationActivities.Contacts;
import com.rawtalent.schoolsystemuser.R;
import com.rawtalent.schoolsystemuser.SetProfile;
import com.rawtalent.schoolsystemuser.ShowUserProfile;
import com.squareup.picasso.Picasso;

public class GroupData extends AppCompatActivity {


    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    AlertDialog.Builder builder;
    AlertDialog progressDialog;
    FirebaseAuth mAuth;

    TextView name, description;
    ImageView icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_data);


        name = findViewById(R.id.groupName);
        description = findViewById(R.id.groupDescription);
        icon = findViewById(R.id.groupImage);

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        getGroupData();

        mrecyclerview = findViewById(R.id.admins);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(Constants.COLLECTION_TEACHERS);


        FirestoreRecyclerOptions<ContactModel> options = new FirestoreRecyclerOptions.Builder<ContactModel>().setQuery(query, ContactModel.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ContactModel, ChatListAdapter.ChatListViewHolder>(options) {
            @NonNull
            @Override
            public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(GroupData.this).inflate(R.layout.all_chats_list_items, parent, false);
                return new ChatListAdapter.ChatListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, final int position, @NonNull final ContactModel model) {

                holder.name.setText("" + model.getName());
                // holder.lastmsg.setText(""+model.getStatus());
                holder.numberofmsgs.setVisibility(View.GONE);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference().child("ProfileImages").child(getSnapshots().getSnapshot(position).getId());
                storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //       Log.d(TAG, "onSuccess: Got profile image successfully");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bitmap != null) {
                            holder.image.setImageBitmap(bitmap);
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

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount() == 0) {
                    // animationView.setVisibility(View.VISIBLE);
                } else {
                }
            }
        };
        mrecyclerview.setHasFixedSize(false);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);


    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();

    }

    public void getGroupData() {
        progressDialog.show();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.COLLECTION_GROUPS).document(Constants.GROUP_ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    name.setText(documentSnapshot.getString(Constants.GROUP_NAME));
                    description.setText(documentSnapshot.getString(Constants.GROUP_DESCRIPTION));
                    Picasso.get()
                            .load(documentSnapshot.getString(Constants.GROUP_ICON))
                            .fit()
                            .centerCrop()
                            .into(icon);
                } catch (Exception e) {

                }
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupData.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(GroupData.this);
            builder.setTitle("saving details...");

            final ProgressBar progressBar = new ProgressBar(GroupData.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }
}