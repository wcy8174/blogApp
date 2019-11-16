package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlterBlog extends AppCompatActivity implements View.OnClickListener {

    private TextView alterBlogTime;
    private EditText alterBlogText;
    private Button deleteBlogBtn;
    private Button submitAlterBtn;
    private String time;
    private String content;

    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_blog);

        alterBlogText = findViewById(R.id.alterBlogText);
        alterBlogTime = findViewById(R.id.alterBlogTime);
        deleteBlogBtn = findViewById(R.id.deleteBlogBtn);
        submitAlterBtn = findViewById(R.id.submitAlterBtn);

        deleteBlogBtn.setOnClickListener(this);
        submitAlterBtn.setOnClickListener(this);
        //设置时间和内容
        time = getIntent().getStringExtra("blogTime");
        content = getIntent().getStringExtra("blogContent");
        Date date  = new Date();
        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        alterBlogTime.setText(format.format(date));
        alterBlogText.setText(content);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deleteBlogBtn:
                deleteBlog();
                break;
            case R.id.submitAlterBtn:
                submitAlter();
                break;
        }
    }
    //删除微博
    private void deleteBlog() {
        new Thread(){
            public void run(){
                //
                String url = "http://114.55.33.227:8000/deleteBlog";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", Logining.name)
                        .addFormDataPart("time", time)
                        .addFormDataPart("content", content)
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
                    if(result.equals("deleteSuccess")){
                        //跳转到自己微博主页面
                        Intent intent = new Intent(AlterBlog.this,HomeActivity.class);
                        intent.putExtra("index",1);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //修改博客
    private void submitAlter() {
        new Thread(){
            public void run(){
                //获取发表内容
                String contentNew;
                contentNew = alterBlogText.getText().toString();
                //
                String url = "http://114.55.33.227:8000/alterBlog";

                if(contentNew.equals("")||content == null){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(AlterBlog.this,"博客内容不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        }
                    }.start();
                }else{
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                            .addFormDataPart("name", Logining.name)
                            .addFormDataPart("time", time)
                            .addFormDataPart("content", contentNew)
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
                        Log.i("测试修改1",result);
                        if(result.equals("alterSuccess")){
                            //提示修改成功
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(AlterBlog.this,"博客修改成功",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                            Intent intent = new Intent(AlterBlog.this,HomeActivity.class);
                            intent.putExtra("index",1);
                            startActivity(intent);
                        }else {
                            //提示修改失败
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(AlterBlog.this,"博客修改失败",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

}
