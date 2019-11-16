package com.example.chatapp.ListView;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.friendInformation;

import java.util.List;

public class FriendAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    class MyHolder extends RecyclerView.ViewHolder{
        View friendsView;
        ImageView friend_image;
        TextView friend_name;
        Activity test;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            friendsView = itemView;
            friend_image = itemView.findViewById(R.id.friend_image);
            friend_name=itemView.findViewById(R.id.friend_name);

        }
    }

    private List<Friends> friends ;
    private Activity activity;

    public FriendAdapter(List<Friends> friends,Activity activity) {
        this.friends = friends;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyHolder holder=null;
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends,parent,false);
        holder=new MyHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MyHolder holder1=(MyHolder)holder;
        final Friends friend = friends.get(position);

        holder1.friend_image.setImageResource(friend.getImageId());
        holder1.friend_name.setText(friend.getName());


        holder1.friendsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"click"+friend.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, friendInformation.class);
                intent.putExtra("friendInformation",friend.getName());
                activity.startActivity(intent);
            }

        });


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
