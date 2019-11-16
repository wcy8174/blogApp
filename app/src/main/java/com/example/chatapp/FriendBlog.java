package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatapp.JsonMy.JsonFindUserBlog;
import com.example.chatapp.ListView.BlogAdapter;
import com.example.chatapp.ListView.BlogContent;
import com.example.chatapp.ListView.FriendBlogAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendBlog extends AppCompatActivity {
    private TextView friendBlogName;
    private RecyclerView friendBlogRV;
    private String friendName;

    //存储adapter需要的数据
    private List<BlogContent> blogContents = new ArrayList<>();
    FriendBlogAdapter blogAdapter;
    Activity contactActivity;
    //private Boolean flag = false;
    OkHttpClient client = new OkHttpClient();

    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //flag = true;
                    //Toast.makeText(getActivity(),"ceshi"+flag.toString()+blogContents.size(),Toast.LENGTH_SHORT).show();
                    for(int i=0;i<blogContents.size();i++){
                        Log.i("测试6",blogContents.get(i).getContent());
                        Log.i("测试7",""+i);
                    }

                    blogAdapter.notifyDataSetChanged();

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_blog);

        friendBlogName = findViewById(R.id.friendBlogName);
        friendBlogRV = findViewById(R.id.friendBlogRV);
        friendName = getIntent().getStringExtra("friendName");

        //设置名字
        friendBlogName.setText(friendName);

        initFriendBlogContent();

        //RecyclerView rv=view.findViewById(R.id.rv);
        blogAdapter=new FriendBlogAdapter(blogContents,contactActivity);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        // manager.setOrientation(RecyclerView.Orientation.);
        friendBlogRV.setLayoutManager(manager);
        friendBlogRV.setAdapter(blogAdapter);
    }

    private void initFriendBlogContent() {
        new Thread(){
            public void run(){
                BlogContent blogContent = new BlogContent();
                String name = friendName;
                String url = "http://114.55.33.227:8000/findBlog";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", name)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result;
                    result =  response.body().string();

                    if(result.equals("nullBlog")){
                        //获取一个时间
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = new Date(System.currentTimeMillis());
                        String time = format.format(date);
                        //赋值
                        blogContent.setTime(time);
                        blogContent.setContent("第一条微博");
                        blogContents.add(blogContent);

                    }else{
                        Gson gson = new Gson();
                        //最外层信息
                        JsonFindUserBlog jsonContent = gson.fromJson(result,JsonFindUserBlog.class);
                        List<JsonFindUserBlog.result> blogRecord = jsonContent.getRecord();

                        for(int i =0;i<blogRecord.size();i++){
                            BlogContent blogContent1 = new BlogContent();
                            String time = blogRecord.get(i).getTime();
                            String content = blogRecord.get(i).getContent();
                            blogContent1.setTime(time);
                            blogContent1.setContent(content);
                            //添加到数组
                            blogContents.add(blogContent1);
                            //发消息完成
                        }
                    }
                    Collections.sort(blogContents);
                    mHandler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
