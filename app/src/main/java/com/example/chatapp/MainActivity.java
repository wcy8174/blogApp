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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatapp.JsonMy.JsonIdentifyResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button faceLoginBtn;
    private Button accountLoginBtn;
    private Button registerBtn;
    private ProgressBar progress_main;

    private Bitmap bitmapUpLode; //用于上传百度库的bitmap文件
    public OkHttpClient client = new OkHttpClient();

    private Uri imgUri;
    private String group_id;
    private String name_id;
    private double score;
    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    String data = (String)msg.obj;
                    if(progress_main.getVisibility() != View.GONE){
                        progress_main.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceLoginBtn = findViewById(R.id.faceLoginBtn);
        accountLoginBtn = findViewById(R.id.accountLoginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        progress_main = findViewById(R.id.progress_main);

        faceLoginBtn.setOnClickListener(this);
        accountLoginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        if(progress_main.getVisibility() != View.GONE){
            progress_main.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.faceLoginBtn:
                if(progress_main.getVisibility() != View.VISIBLE){
                    progress_main.setVisibility(View.VISIBLE);
                }
                camera();
                break;
            case R.id.accountLoginBtn:
                Intent intent1 = new Intent(MainActivity.this,AccountLogin.class);
                startActivity(intent1);
                break;
            case R.id.registerBtn:
                Intent intent2 = new Intent(MainActivity.this,Register.class);
                startActivity(intent2);
                break;
        }
    }

    public void camera() {

        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"关联目录"，无需！

        //发消息更改UI
        //mHandler.sendEmptyMessage(0);
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

                        if(bitmapUpLode != null){
                            identify();
                        }else{
                            Log.i("click","照片为空");
                            Toast.makeText(MainActivity.this,"照片为null",Toast.LENGTH_SHORT).show();
                        }
                        //调用识别函数
                        //显示照片
                        //photo.setImageBitmap(map);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void identify() {
        new Thread(){
            public void run(){
                String token = Utils.getAccessToken();
                //String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token="+token;
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token="+token;
                String imageBase64;
                if (bitmapUpLode !=null) {
                    imageBase64 = Utils.bitmapToBase64(bitmapUpLode);
                } else {
                    imageBase64 = null;
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,"图片为null",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/json")
                        .addFormDataPart("image",imageBase64)
                        .addFormDataPart("image_type", "BASE64")
                        .addFormDataPart("group_id_list","normal")
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
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<JsonIdentifyResult>() {}.getType();
                    //最外层信息
                    JsonIdentifyResult jsonContent = gson.fromJson(result, type);
                    //获得用户信息外层
                    JsonIdentifyResult.JsonResult jsonResult = jsonContent.getResult();
                    //用户信息数组，存的是是对象
                    List<JsonIdentifyResult.UserResultList> userResultList =  jsonResult.getUserList();
                    //数组的第一条数据，本应该也只有一条数据
                    JsonIdentifyResult.UserResultList userResult = userResultList.get(0);

                    int error_code = jsonContent.getError_code();
                    String error_msg = jsonContent.getError_msg();
                    group_id = userResult.getGroup_id();
                    name_id= userResult.getUser_id();
                    score = userResult.getScore();

                    if(error_msg.equals("SUCCESS")){
                        if(score>85){
                            Logining.name = name_id;
                            //发消息更改UI
                            mHandler.sendEmptyMessage(0);
                            //name_id 识别成功
                            Intent intent1 = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent1);
                        }
                        else {
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this,"该用户不存在",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                            mHandler.sendEmptyMessage(0);
                        }
                    }else {
                        Log.i("测试人脸","12e3");
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this,"未检测到人脸",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                        }.start();
                        mHandler.sendEmptyMessage(0);
                    }

                    //String userInfo = userGroupId+":"+userName;
                    //String face_token = jsonResult.getFace_token();
                    //System.out.println(face_token);
                    //userId.setText(userInfo);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




}
