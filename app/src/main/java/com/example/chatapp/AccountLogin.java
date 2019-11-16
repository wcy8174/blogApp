package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountLogin extends AppCompatActivity implements View.OnClickListener {
    private TextView accountText;
    private TextView passwordText;
    private Button cancelBtn;
    private Button loginBtn;
    private ProgressBar progress_circular;

    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    if(progress_circular.getVisibility() != View.GONE){
                        progress_circular.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }

    };
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login);

        accountText = findViewById(R.id.accountText);
        passwordText = findViewById(R.id.passwordText);
        cancelBtn = findViewById(R.id.cancelBtn);
        loginBtn = findViewById(R.id.loginBtn);
        progress_circular = findViewById(R.id.progress_circular);

        cancelBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        if(progress_circular.getVisibility() != View.GONE){
            progress_circular.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelBtn:
                Intent intent = new Intent(AccountLogin.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.loginBtn:
                if(progress_circular.getVisibility() != View.VISIBLE){
                    progress_circular.setVisibility(View.VISIBLE);
                }
                login();
                //Log.i("测试",Logining.friendsList.toString());
                break;
        }
    }


    public void login(){
        new Thread(){
            public void run(){
                final String user_id = accountText.getText().toString();
                String password = passwordText.getText().toString();
                String url = "http://114.55.33.227:8000/login";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", user_id)
                        .addFormDataPart("password",password)
                        //filename:avatar,originname:abc.jpg
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                //System.out.println("1");

                try {
                    Response response = client.newCall(request).execute();
                    String error_msg = response.body().string();
                    if(error_msg.equals("loginSuccess")){
                        Logining.name = user_id;
                        //初始化信息

                        //发消息更改UI
                        mHandler.sendEmptyMessage(0);

                        Intent intent2 = new Intent(AccountLogin.this,HomeActivity.class);
                        startActivity(intent2);

                    }else if(error_msg.equals("invalidName")){
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(AccountLogin.this,user_id+"用户名不存在",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                    }else if(error_msg.equals("invalidPassword")){
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(AccountLogin.this,"密码不正确",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                    }else {
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(AccountLogin.this,"发生错误，请重试",Toast.LENGTH_SHORT).show();
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
