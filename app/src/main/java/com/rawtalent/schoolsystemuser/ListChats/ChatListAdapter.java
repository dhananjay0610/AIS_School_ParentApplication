package com.rawtalent.schoolsystemuser.ListChats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rawtalent.schoolsystemuser.ModelClass.AllChats;
import com.rawtalent.schoolsystemuser.R;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    /* ArrayList<String> mteamName;
     ArrayList<String> mSrno;
     ArrayList<String> mNRR;
     ArrayList<String> mPoints;
     ArrayList<String> mjsonData;

     */
    Context context;
    ArrayList<AllChats> chatList;

    public ChatListAdapter(Context context, ArrayList<AllChats> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.all_chats_list_items, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, final int position) {


        holder.name.setText(""+chatList.get(position).getName());
        holder.lastmsg.setText(""+chatList.get(position).getLastmsg());
        holder.numberofmsgs.setText(""+chatList.get(position).getSize());





    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {

       public TextView name, lastmsg, numberofmsgs;
       public ImageView image;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.profile_name_item);
            image = itemView.findViewById(R.id.profile_image_item);
            lastmsg = itemView.findViewById(R.id.last_message);
            numberofmsgs = itemView.findViewById(R.id.number_of_notifications);


        }
    }


}
