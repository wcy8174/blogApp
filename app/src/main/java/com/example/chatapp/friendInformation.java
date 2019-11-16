package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class friendInformation extends AppCompatActivity implements View.OnClickListener {

    private TextView friendInformation;
    private Button lookBlog;
    private Button deleteFriend;


    private String friendName;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_information);

        friendInformation = findViewById(R.id.friendInformation);
        lookBlog = findViewById(R.id.lookBlog);
        deleteFriend = findViewById(R.id.deleteFriend);


        lookBlog.setOnClickListener(this);
        deleteFriend.setOnClickListener(this);


        //获得当前好友的名字
        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendInformation");
        //更改UI
        friendInformation.setText(friendName);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lookBlog:
                Intent intent = new Intent(friendInformation.this,FriendBlog.class);
                intent.putExtra("friendName",friendName);
                startActivity(intent);
                break;
            case R.id.deleteFriend:
                delete();
                break;
        }
    }

    private void addFriend() {
    }

    //删除好友
    private void delete() {
        new Thread(){
            public void run(){
                String url = "http://114.55.33.227:8000/deleteFriends";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", Logining.name)
                        .addFormDataPart("friendName",friendName)
                        //filename:avatar,originname:abc.jpg
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    String result;
                    result =  response.body().string();
                    if (result.equals("deleteSuccess")){
                        for (int i = 0; i < Logining.friendsList.size(); i++) {
                            if (Logining.friendsList.get(i).equals(friendName)) {
                                Logining.friendsList.remove(i);
                                i--;
                            }
                        }
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(friendInformation.this,friendName+"已经成功删除",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();

                        //进行页面跳转
                        Intent intent = new Intent(friendInformation.this,HomeActivity.class);
                        intent.putExtra("index",2);
                        startActivity(intent);

                    }else{
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(friendInformation.this,"请重试",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                    }

                    //String error_msg = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
