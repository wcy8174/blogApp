package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener {
    private Button addFriendBtn1;
    private EditText addFriendName;
    private String friendName;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);

        addFriendBtn1 = findViewById(R.id.addFriendBtn1);
        addFriendName = findViewById(R.id.addFriendName);


        addFriendBtn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFriendBtn1:
                addFriend();
                break;
        }
    }

    private void addFriend() {
        new Thread(){
            public void run(){
                //
                String url = "http://114.55.33.227:8000/addFriends";
                friendName = addFriendName.getText().toString();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", Logining.name)
                        .addFormDataPart("friendName", friendName)
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
                    if(result.equals("invalidName")){
                        //没有该用户
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(AddFriendActivity.this,"该用户不存在",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                    }else if(result.equals("alreadyExistence")){
                        //跳转到好友详情页面
                        Intent intent = new Intent(AddFriendActivity.this, friendInformation.class);
                        intent.putExtra("friendInformation",friendName);
                        startActivity(intent);

                    }else if(result.equals("addSuccess")){
                        //添加成功
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(AddFriendActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                        Intent intent = new Intent(AddFriendActivity.this,HomeActivity.class);
                        intent.putExtra("index",2);
                        startActivity(intent);
                    }else {
                        //发生系统错误
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(AddFriendActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
