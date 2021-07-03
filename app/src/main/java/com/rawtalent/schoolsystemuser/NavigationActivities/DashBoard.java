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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.UserProfile;
import com.rawtalent.schoolsystemuser.Constants;
import com.rawtalent.schoolsystemuser.GroupChat.GroupChat;
import com.rawtalent.schoolsystemuser.ListChats.ChatListAdapter;
import com.rawtalent.schoolsystemuser.LoginActivity;
import com.rawtalent.schoolsystemuser.ModelClass.ModelContacts;
import com.rawtalent.schoolsystemuser.PersonalChat.PersonalChatActivity;
import com.rawtalent.schoolsystemuser.R;
import com.rawtalent.schoolsystemuser.UserSharedPreference;

import java.util.HashMap;
import java.util.Map;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private ChangeNavigationActivities mChangeActivityFromNavigation;
    private Context mContext;

    Button opengrp;

    FirebaseAuth mAuth;

    Map<String,Bitmap> profileImages;

    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        profileImages=new HashMap<>();

        initialize();

        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(Constants.COLLECTION_PARENTS).document(mAuth.getCurrentUser().getUid()).collection(Constants.PARENTS_CONTACTS).whereEqualTo(Constants.CONTACTS_STATUS,1).orderBy("lastmsgdate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ModelContacts> options = new FirestoreRecyclerOptions.Builder<ModelContacts>().setQuery(query, ModelContacts.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ModelContacts, ChatListAdapter.ChatListViewHolder>(options) {
            @NonNull
            @Override
            public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(DashBoard.this).inflate(R.layout.all_chats_list_items, parent, false);
                return new ChatListAdapter.ChatListViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, final int position, @NonNull final ModelContacts model) {

                holder.name.setText(""+model.getName());
                holder.lastmsg.setText(""+model.getLastMessage());

                if (model.getUnseenMessages()>0) {
                    holder.numberofmsgs.setVisibility(View.VISIBLE);
                    holder.numberofmsgs.setText("" + model.getUnseenMessages());
                }else{
                    holder.numberofmsgs.setVisibility(View.GONE);
                }

                if (profileImages.containsKey(getSnapshots().getSnapshot(position).getId()) &&
                        profileImages.get(getSnapshots().getSnapshot(position).getId())!=null){
                        holder.image.setImageBitmap(profileImages.get(getSnapshots().getSnapshot(position).getId()));
                }else {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference().child(Constants.STORAGE_PROFILEIMAGES).child(getSnapshots().getSnapshot(position).getId());
                    storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //       Log.d(TAG, "onSuccess: Got profile image successfully");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bitmap != null) {
                                try {
                                    profileImages.put(getSnapshots().getSnapshot(position).getId(), bitmap);
                                }catch (Exception e){
                                    Log.d("TESTERROR", "error: "+e.getMessage());
                                }
                                holder.image.setImageBitmap(bitmap);
                            }
                        }
                    });

                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(DashBoard.this, PersonalChatActivity.class);
                        intent.putExtra("uid",getSnapshots().getSnapshot(position).getId());
                        intent.putExtra("chatID",model.getChatID());
                        intent.putExtra("name",model.getName());
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
        mNavigationView.getMenu().getItem(0).setChecked(true);
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
        //    mDrawerLayout.closeDrawer(GravityCompat.START);
        //    mChangeActivityFromNavigation.startHomeActivity(mContext);
        }else if (id==R.id.nav_profile){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startProfileActivity(mContext);
        }else if (id==R.id.nav_logout){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
            builder.setMessage("Are you sure you want to logout ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DashBoard.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }


    public void initialize(){

        mContext =getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mChangeActivityFromNavigation = new ChangeNavigationActivities();

        opengrp = findViewById(R.id.opengrpbtn);

        mAuth=FirebaseAuth.getInstance();
        mrecyclerview=(RecyclerView)findViewById(R.id.show_all_chats);
        emptyView=(LinearLayout) findViewById(R.id.emptyview2);

        opengrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashBoard.this, GroupChat.class);
                startActivity(intent);
            }
        });

    }


}