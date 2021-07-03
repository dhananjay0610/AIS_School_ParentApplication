package com.rawtalent.schoolsystemuser.GroupChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.UploadTask;
import com.rawtalent.schoolsystemuser.Constants;
import com.rawtalent.schoolsystemuser.ModelClass.ChatModel;
import com.rawtalent.schoolsystemuser.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity {


    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    ImageButton send,cancel,attach,imgbtn,pdfbtn;
    TextView filename;
    EditText message;

    LinearLayout filelayout,typelayout;

    boolean isVisible=false;

    Uri file;
    int type=1;


    boolean showFile=false;

    String nameOfFile="";

    TextView name;
    CircleImageView icon;
    ImageButton fileButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        initialize();
        mAuth=FirebaseAuth.getInstance();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("Groups").document("maingroup").collection("Chats")
         .orderBy("timeLong", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ChatModel, ChatViewHolder>(options) {
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(GroupChat.this).inflate(R.layout.chat_item, parent, false);
                return new ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position, @NonNull final ChatModel model) {






                if (model.getUid().equals(mAuth.getCurrentUser().getUid())){
                    //This message was posted by you

                    holder.cardView.setVisibility(View.GONE);
                    holder.cardView2.setVisibility(View.VISIBLE);

                    holder.time2.setVisibility(View.GONE);
                    holder.time.setVisibility(View.VISIBLE);



                    holder.name2.setText("You");
                    holder.message2.setText(""+model.getMessage());
                    holder.date2.setText(""+model.getDate());
                    holder.time.setText(""+model.getTime());
                    holder.filename2.setText(""+model.getFileName());

                    if (model.getTag().equals("pdf")){
                        holder.fileLayout2.setVisibility(View.VISIBLE);
                        holder.imageDisplay2.setVisibility(View.GONE);
                    }else if (model.getTag().equals("img")){
                        holder.fileLayout2.setVisibility(View.GONE);
                        holder.imageDisplay2.setVisibility(View.VISIBLE);

                        Picasso.get()
                                .load(model.getUrl())
                                .fit()
                                .centerCrop().placeholder(R.drawable.loginbackground)
                                .into(holder.imageDisplay2);
                    }
                    else{
                        holder.fileLayout2.setVisibility(View.GONE);
                        holder.imageDisplay2.setVisibility(View.GONE);
                    }

                }else{


                    holder.cardView.setVisibility(View.VISIBLE);
                    holder.cardView2.setVisibility(View.GONE);

                    holder.time.setVisibility(View.GONE);
                    holder.time2.setVisibility(View.VISIBLE);


                    holder.name.setText(""+model.getName());
                    holder.message.setText(""+model.getMessage());
                    holder.date.setText(""+model.getDate());
                    holder.time2.setText(""+model.getTime());
                    holder.filename.setText(""+model.getFileName());

                    if (model.getTag().equals("pdf")){
                        holder.fileLayout.setVisibility(View.VISIBLE);
                        holder.imageDisplay.setVisibility(View.GONE);
                    }else if (model.getTag().equals("img")){
                        holder.fileLayout.setVisibility(View.GONE);
                        holder.imageDisplay.setVisibility(View.VISIBLE);

                        Picasso.get()
                                .load(model.getUrl())
                                .fit()
                                .placeholder(R.drawable.loginbackground)
                                .into(holder.imageDisplay);

                    }
                    else{
                        holder.fileLayout.setVisibility(View.GONE);
                        holder.imageDisplay.setVisibility(View.GONE);
                    }

                }

                holder.imageDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(GroupChat.this,ShowImage.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getFileName());
                        startActivity(intent);

                    }
                });

                holder.imageDisplay2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(GroupChat.this,ShowImage.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getFileName());
                        startActivity(intent);

                    }
                });




                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {

                            DownloadManager downloadManager=(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri=Uri.parse(model.getUrl());

                            DownloadManager.Request request=new DownloadManager.Request(uri);
                            request.setTitle("File Download");
                            request.setDescription("downloading file...");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(GroupChat.this,"CollegeChatApp",""+model.getFileName());
                            downloadManager.enqueue(request);

                        }catch (Exception e){
                            Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.download2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            DownloadManager downloadManager=(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri=Uri.parse(model.getUrl());

                            DownloadManager.Request request=new DownloadManager.Request(uri);
                            request.setTitle("File Download");
                            request.setDescription("downloading file...");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(GroupChat.this,"CollegeChatApp",""+model.getFileName());
                            downloadManager.enqueue(request);

                        }catch (Exception e){
                            Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                   /*     DownloadManager downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri=Uri.parse(model.getUrl());

                        DownloadManager.Request request=new DownloadManager.Request(uri);
                        request.setTitle("File Download");
                        request.setDescription("downloading file...");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                        request.setVisibleInDownloadsUi(false);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"MyApp/files");

                        downloadManager.enqueue(request);


                    */
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount()==0){
                   // animationView.setVisibility(View.VISIBLE);

                }else{

                    mrecyclerview.smoothScrollToPosition(firestoreRecyclerAdapter.getItemCount()-1);

                    //  animationView.setVisibility(View.GONE);
                    //  emptyView.setVisibility(View.GONE);
                }
            }
        };
        mrecyclerview.setHasFixedSize(false);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(GroupChat.this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);


        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFile=!showFile;
                if (showFile){
                    Query query = firebaseFirestore.collection("Groups").document("maingroup").collection("Chats")
                            .whereNotEqualTo("tag","str");
                    FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
                    firestoreRecyclerAdapter.updateOptions(options);

                }else{
                    Query query = firebaseFirestore.collection("Groups").document("maingroup").collection("Chats")
                            .orderBy("timeLong", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
                    firestoreRecyclerAdapter.updateOptions(options);

                }
            }
        });

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

    public void initialize(){
        mrecyclerview=findViewById(R.id.chatrecyclerview);
        send=findViewById(R.id.sendbtn);
        cancel=findViewById(R.id.cancelfile);
        attach=findViewById(R.id.attachfile);
        filename=findViewById(R.id.filenametv);
        message=findViewById(R.id.message_et);
        imgbtn=findViewById(R.id.img);
        pdfbtn=findViewById(R.id.pdf);
        filelayout=findViewById(R.id.fileshowlayout);
        typelayout=findViewById(R.id.selectfilelayout);

        name = findViewById(R.id.nameOfGroup);
        fileButton = findViewById(R.id.filebutton);
        icon = findViewById(R.id.groupicon);

        getGroupData();

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent=new Intent(GroupChat.this,GroupData.class);
               startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCancel();

            }
        });

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectFile(1);
            }
        });

        pdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectFile(2);

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttach();
            }
        });
    }


    public void sendMessage(){

        String msg=message.getText().toString();
        if ((msg.equals("")||msg.isEmpty())&&file==null){
            return;
        }

        progressDialog.show();

        if (file!=null){

            saveFile(msg);
        }else{
            postMessage(msg,"",0);
        }



    }

    public void saveFile(String msg){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String uuid=mAuth.getCurrentUser().getUid();

        StorageReference mStorageReference= FirebaseStorage.getInstance().getReference();
        StorageReference reference;

        if (type==1){
            reference = mStorageReference.child("Group/Images" + "/" + nameOfFile+""+System.currentTimeMillis());
        }else{
             reference = mStorageReference.child("Group/PDFs" + "/" + nameOfFile+""+System.currentTimeMillis());
        }
        reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        postMessage(msg,uri.toString(),type);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postMessage(String msg,String url,int t){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String uuid=mAuth.getCurrentUser().getUid();

        Map<String,Object> map=new HashMap<>();
        map.put("name",mAuth.getCurrentUser().getDisplayName());
        map.put("uid",uuid);
        map.put("message",msg);
        map.put("url",url);
        map.put("fileName",nameOfFile);


        if (t==0){
            map.put("tag","str");
        }else if (t==1){
            map.put("tag","img");
        }else if (t==2){
            map.put("tag","pdf");
        }

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm:ss");

        String dateString=dateFormat.format(date);
        String timeString=timeformat.format(date);

        long timeInLong=date.getTime();

        map.put("time",timeString);
        map.put("date",dateString);
        map.put("timeLong",timeInLong);


        db.collection("Groups").document("maingroup").collection("Chats").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                message.setText("");

                setCancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             progressDialog.dismiss();
                Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setCancel(){

        file=null;
        nameOfFile="";
        filelayout.setVisibility(View.GONE);
    }

    public void setAttach(){


        if (isVisible){
            typelayout.setVisibility(View.GONE);
        }else{
            typelayout.setVisibility(View.VISIBLE);
        }

        isVisible=(!isVisible);

    }

    private void selectFile(int t) {
        Intent intent = new Intent();

        if (t==1){
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }else{
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 2);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            file = data.getData();
            //imageView.setVisibility(View.VISIBLE);
            try {

                Cursor cursor=getContentResolver().query(file,null,null,null,null);
                int nameIndex=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();

                nameOfFile=cursor.getString(nameIndex);
                filename.setText(""+cursor.getString(nameIndex));

                cursor.close();
                type=1;
                setAttach();
                filelayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            file = data.getData();
            //imageView.setVisibility(View.VISIBLE);
            try {

                Cursor cursor=getContentResolver().query(file,null,null,null,null);
                int nameIndex=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                nameOfFile=cursor.getString(nameIndex);
               filename.setText(""+cursor.getString(nameIndex));

                cursor.close();

                type=2;
               setAttach();
                filelayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(GroupChat.this);
            builder.setTitle("processing...");

            final ProgressBar progressBar = new ProgressBar(GroupChat.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;

    }


    public void getGroupData() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.COLLECTION_GROUPS).document(Constants.GROUP_ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    name.setText(documentSnapshot.getString(Constants.GROUP_NAME));
                    Picasso.get()
                            .load(documentSnapshot.getString(Constants.GROUP_ICON))
                            .fit()
                            .centerCrop()
                            .into(icon);
                } catch (Exception e) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

}