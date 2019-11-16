package com.example.chatapp.ListView;

import android.app.Activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.AlterBlog;
import com.example.chatapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    class MyHolder extends RecyclerView.ViewHolder{
        View friendsView;
        TextView timeBlog;
        TextView contentBlog;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            friendsView = itemView;
            timeBlog = itemView.findViewById(R.id.timeBlog);
            contentBlog=itemView.findViewById(R.id.contentBlog);

        }
    }
    private List<BlogContent> blogContents;
    private Activity activity;

    public BlogAdapter(List<BlogContent> blogContents,Activity activity){
        this.blogContents = blogContents;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BlogAdapter.MyHolder holder = null;
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_listview_item,parent,false);
        holder = new BlogAdapter.MyHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BlogAdapter.MyHolder holder1 = (BlogAdapter.MyHolder)holder;
        final BlogContent blogContent = blogContents.get(position);

        Date date  = new Date();
        java.text.SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyyMMddHHmm");
        try {
            date = formatter.parse(blogContent.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        holder1.timeBlog.setText(format.format(date));
        holder1.contentBlog.setText(blogContent.getContent());
       // holder1.contentBlog.setMovementMethod(ScrollingMovementMethod.getInstance());

        holder1.friendsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AlterBlog.class);
                intent.putExtra("blogTime",blogContent.getTime());
                intent.putExtra("blogContent",blogContent.getContent());
                activity.startActivity(intent);
                //Toast.makeText(v.getContext(),"click",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogContents.size();
    }


}
