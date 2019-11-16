package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapp.JsonMy.JsonTypeAddface;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private Button finishRegisterBtn;//完成注册

    private EditText accountRegisterText;//数据库用户名
    private EditText passwordRegisterText;//数据库密码
    private Button accountRegisterBtn;

    private EditText faceRegisterText;
    private Button faceRegisterBtn;
    private ImageView faceImage;
    private Button cameraBtn;

    private Uri imgUri;
    private Bitmap bitmapUpLode; //用于上传百度库的bitmap文件
    OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        finishRegisterBtn = findViewById(R.id.finishRegisterBtn);
        accountRegisterBtn = findViewById(R.id.accountRegisterBtn);
        faceRegisterBtn = findViewById(R.id.faceRegisterBtn);
        cameraBtn = findViewById(R.id.cameraBtn);

        accountRegisterText = findViewById(R.id.accountRegisterText);
        passwordRegisterText = findViewById(R.id.passwordRegisterText);
        faceRegisterText = findViewById(R.id.faceRegisterText);
        faceImage = findViewById(R.id.faceImage);

        finishRegisterBtn.setOnClickListener(this);
        accountRegisterBtn.setOnClickListener(this);
        faceRegisterBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.finishRegisterBtn:
                Intent intent1 = new Intent(Register.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.accountRegisterBtn:
                accountRegister();
                break;
            case R.id.cameraBtn:
                camera();
                break;
            case R.id.faceRegisterBtn:
                faceAdd();
                break;
        }
    }
    public void accountRegister(){
        new Thread(){
            public void run(){
                final String user_id = accountRegisterText.getText().toString();
                String password = passwordRegisterText.getText().toString();
                if(user_id.isEmpty()){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(Register.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        }
                    }.start();
                }else if(password.isEmpty()){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(Register.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        }
                    }.start();
                }else {
                    String url = "http://114.55.33.227:8000/register";
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
                        if(error_msg.equals("registerSuccess")){
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(Register.this,user_id+"成功注册",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        }else if(error_msg.equals("invalidName")){
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(Register.this,user_id+"用户名已经存在",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        }else {
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(Register.this,"发生错误，请重试",Toast.LENGTH_SHORT).show();
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


    //启动相机来获得上传的照片
    public void camera() {

        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"关联目录"，无需！
        File outImg=new File(getExternalCacheDir(),"temp.jpg");
        if(outImg.exists()) outImg.delete();

        try {
            outImg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //复杂的Uri创建方式
        if(Build.VERSION.SDK_INT>=24)
            //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
            imgUri= FileProvider.getUriForFile(this,"com.example.chatapp.fileprovider",outImg);
        else
            imgUri= Uri.fromFile(outImg);

        //利用actionName和Extra,启动《相机Activity》


        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,1);
        //到此，启动了相机，等待用户拍照
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode==RESULT_OK){
                    try {
                        //利用ContentResolver,查询临时文件，并使用BitMapFactory,从输入流中创建BitMap
                        //同样需要配合Provider,在Manifest.xml中加以配置
                        Bitmap map= BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                        //得到上传百度库的map
                        bitmapUpLode = map;
                        Log.i("图片","map");

                        //显示照片
                        faceImage.setImageBitmap(map);

                        //photo.setImageBitmap(map);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public  void faceAdd(){
        new Thread(){
            public void run(){
                String token = Utils.getAccessToken();

                Log.i("获取token",token);
                // OkHttpClient client = new OkHttpClient();

                String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token="+token;

                final String userName = faceRegisterText.getText().toString();
                if(userName.isEmpty()){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(Register.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        }
                    }.start();
                }
                String imageBase64;
                if (bitmapUpLode !=null) {
                    imageBase64 = Utils.bitmapToBase64(bitmapUpLode);
                } else {
                    imageBase64 = null;
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(Register.this,"图片为null",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }


                if (!userName.isEmpty()&&bitmapUpLode !=null) {
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("Content-Type","application/json")
                            .addFormDataPart("image",imageBase64)
                            .addFormDataPart("image_type", "BASE64")
                            .addFormDataPart("group_id","normal")
                            .addFormDataPart("user_id",userName)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        //Log.i("数据", response.body().string() + "....");
                        //Log.i("上传toast","2");
                        String result = response.body().string();
                        Gson gson = new Gson();
                        JsonTypeAddface resultFaceAdd = gson.fromJson(result,JsonTypeAddface.class);
                        JsonTypeAddface.Result faceResult = resultFaceAdd.getResult();
                        final String error_msg = resultFaceAdd.getError_msg();

                        if (error_msg.equals("SUCCESS")) {
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(Register.this,userName+"注册成功",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        }else{
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(Register.this,"注册失败"+error_msg,Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                };
                            }.start();
                        }
                        // System.out.println(response.body().string());
                    } catch (IOException e) {
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(Register.this,"上传失败,请重试",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            };
                        }.start();
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }



}
