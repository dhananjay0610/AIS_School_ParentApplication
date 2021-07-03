package com.rawtalent.schoolsystemuser.NavigationActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.UserProfile;
import com.rawtalent.schoolsystemuser.Constants;
import com.rawtalent.schoolsystemuser.ListChats.ChatListAdapter;
import com.rawtalent.schoolsystemuser.LoginActivity;
import com.rawtalent.schoolsystemuser.ModelClass.ContactModel;
import com.rawtalent.schoolsystemuser.R;
import com.rawtalent.schoolsystemuser.ShowUserProfile;
import com.rawtalent.schoolsystemuser.UserSharedPreference;

public class Contacts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private ChangeNavigationActivities mChangeActivityFromNavigation;
    private Context mContext;


    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    AlertDialog.Builder builder;
    AlertDialog progressDialog;
    FirebaseAuth mAuth;


    LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mContext =getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mChangeActivityFromNavigation = new ChangeNavigationActivities();

        emptyView=findViewById(R.id.emptyview);
        mrecyclerview=findViewById(R.id.contacts_recyclerview);
        
        mAuth=FirebaseAuth.getInstance();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);



        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(Constants.COLLECTION_TEACHERS);
        // .orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ContactModel> options = new FirestoreRecyclerOptions.Builder<ContactModel>().setQuery(query, ContactModel.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ContactModel, ChatListAdapter.ChatListViewHolder>(options) {
            @NonNull
            @Override
            public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(Contacts.this).inflate(R.layout.all_chats_list_items, parent, false);
                return new ChatListAdapter.ChatListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, final int position, @NonNull final ContactModel model) {

                holder.name.setText(""+model.getName());
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
                            }else {

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log.d(TAG, "onFailure: failed to get image "+e.getMessage());

                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(Contacts.this, ShowUserProfile.class);
                            intent.putExtra("uid",getSnapshots().getSnapshot(position).getId());
                            startActivity(intent);
                        }
                    });

            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount()==0){
                    // animationView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                    mrecyclerview.setVisibility(View.GONE);
                }else{

                    emptyView.setVisibility(View.GONE);
                    mrecyclerview.setVisibility(View.VISIBLE);
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
        setNavigation();
        firestoreRecyclerAdapter.startListening();

    }


    private void setNavigation() {
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(1).setChecked(true);
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
              //  mDrawerLayout.closeDrawer(GravityCompat.START);
              //  mChangeActivityFromNavigation.startContactsActivity(mContext);
        } else if (id == R.id.nav_home) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startHomeActivity(mContext);
        }else if (id==R.id.nav_profile){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startProfileActivity(mContext);
        }else if (id==R.id.nav_logout){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(Contacts.this);
            builder.setMessage("Are you sure you want to logout ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Contacts.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("processing...");

            final ProgressBar progressBar = new ProgressBar(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }

}