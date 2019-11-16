package com.example.chatapp.ListView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AllBlogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    class MyHolder extends RecyclerView.ViewHolder{
        View blogView;
        TextView userName;
        TextView blogTime;
        TextView blogContent;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            blogView = itemView;
            userName = itemView.findViewById(R.id.userNameText);
            blogTime = itemView.findViewById(R.id.userBlogTime);
            blogContent = itemView.findViewById(R.id.userBlogContentText);

        }
    }
    private List<AllBlogContent> allBlogContents;
    private Activity activity;

    public AllBlogAdapter(List<AllBlogContent> allBlogContents, Activity activity){
        this.allBlogContents = allBlogContents;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AllBlogAdapter.MyHolder holder = null;
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.allblog_listview_item,parent,false);
        holder = new AllBlogAdapter.MyHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AllBlogAdapter.MyHolder holder1 = (AllBlogAdapter.MyHolder)holder;
        final AllBlogContent allBlogContent = allBlogContents.get(position);

        Date date  = new Date();
        java.text.SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyyMMddHHmm");
        try {
            date = formatter.parse(allBlogContent.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

        holder1.userName.setText(allBlogContent.getName());
        holder1.blogTime.setText(format.format(date));
        holder1.blogContent.setText(allBlogContent.getContent());

        holder1.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return allBlogContents.size();
    }
}
