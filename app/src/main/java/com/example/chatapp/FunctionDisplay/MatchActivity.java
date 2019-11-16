package com.example.chatapp.FunctionDisplay;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.Nullable;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.Utils;
import com.google.gson.Gson;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatchActivity extends AppCompatActivity  implements View.OnClickListener  {

    private Button matchFaceBtn4;
    private ImageView imageIv41;
    private ImageView imageIv42;
    private TextView showResult4;
    private Uri imgUri4;
    private Uri photoUri4;
    private Bitmap bitmapUpLodeFirst4;
    private Bitmap bitmapUpLodeSecond4;
    String imageBase64First4;
    String imageBase64Second4;
    private String uploadFileNameFirst4;
    private String uploadFileNameSecond4;
    private byte[] fileBufFirst4;
    private byte[] fileBufSecond4;
    public OkHttpClient client4 = new OkHttpClient();
    public String accessToken4;
    Bitmap bitmap41;
    Bitmap bitmap42;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    //存请求json
    List<Map<String,String>> list=new ArrayList<Map<String,String>>();
    Map<String,String> map=new HashMap<String, String>();


    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    showResult4.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        matchFaceBtn4=findViewById(R.id.matchFaceBtn4);
        imageIv41=findViewById(R.id.imageIv41);
        imageIv42=findViewById(R.id.imageIv42);
        showResult4=findViewById(R.id.showResult4);

        imageIv41.setOnClickListener(this);
        imageIv42.setOnClickListener(this);
        matchFaceBtn4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageIv41:
                selectImageFirst(v);
                break;
            case R.id.imageIv42:
                selectImageSecond(v);
                break;
            case R.id.matchFaceBtn4:
                matchFace(v);
                break;
        }
    }

    public void selectImageFirst(View view) {

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            openGalleryFirst(); //打开相册，进行选择
        }
    }

    public void selectImageSecond(View view) {

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 2);
        } else {
            openGallerySecond(); //打开相册，进行选择
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFirst();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallerySecond();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //打开相册,进行照片的选择 1
    private void openGalleryFirst() {
        Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
        intent1.setType("image/*");
        startActivityForResult(intent1, 1);
    }

    //打开相册,进行照片的选择 2
    private void openGallerySecond() {
        Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
        intent2.setType("image/*");
        startActivityForResult(intent2, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                handleSelectFirst(data);
                break;
            case 2:
                handleSelectSecond(data);
                break;

        }
    }


    //选择后照片的读取工作 1
    private void handleSelectFirst(Intent intent) {
        Cursor cursor1 = null;
        Uri uri1 = intent.getData();

        try {
            bitmapUpLodeFirst4 =  BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("源文件URI",uri1.toString());
        cursor1 = getContentResolver().query(uri1, null,        null, null, null);

//        if (cursor1.moveToFirst()) {
//            int columnIndex1 = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//            uploadFileNameFirst = cursor1.getString(columnIndex1);
//            //Log.i("选择上传文件",uploadFileName.toString());
//        }
        try {
            InputStream inputStream1 = getContentResolver().openInputStream(uri1);
            fileBufFirst4=convertToBytes(inputStream1);
            bitmap41 = BitmapFactory.decodeByteArray(fileBufFirst4, 0, fileBufFirst4.length);
            imageIv41.setImageBitmap(bitmap41);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor1.close();
    }


    //选择后照片的读取工作   2
    private void handleSelectSecond(Intent intent) {
        Cursor cursor2 = null;
        Uri uri2 = intent.getData();

        try {
            bitmapUpLodeSecond4 =  BitmapFactory.decodeStream(getContentResolver().openInputStream(uri2));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("源文件URI",uri2.toString());
        cursor2 = getContentResolver().query(uri2, null,        null, null, null);

//        if (cursor2.moveToFirst()) {
//            int columnIndex = cursor2.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//            uploadFileNameSecond = cursor2.getString(columnIndex);
//            //Log.i("选择上传文件",uploadFileName.toString());
//        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri2);
            fileBufSecond4=convertToBytes(inputStream);
            bitmap42 = BitmapFactory.decodeByteArray(fileBufSecond4, 0, fileBufSecond4.length);
            imageIv42.setImageBitmap(bitmap42);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor2.close();
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }


    //完成与百度大脑的《人脸对比》API交互，完成相应的功能
    private void matchFace(View v){
        new Thread(){
            public void run(){
                accessToken4 = Utils.getAccessToken();
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/match?access_token="+accessToken4;
                String score="相似度：";

                if (bitmapUpLodeFirst4 !=null&&bitmapUpLodeFirst4 !=null) {
                    imageBase64First4 = Utils.bitmapToBase64(bitmap41);
                    imageBase64Second4 = Utils.bitmapToBase64(bitmap42);

                } else {
                    imageBase64First4 = null;
                    imageBase64Second4=null;
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(MatchActivity.this,"图片为null",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }
//             str="[{name:'image',value:"+imageBase64First+"},{name:'image',value:"+imageBase64Second+"}]";
//                System.out.println(str);

                Map<String,String> map=new HashMap<String, String>();
                map.put("image",imageBase64First4);
                map.put("image_type","BASE64");
                list.add(map);
                Map<String,String> map1=new HashMap<String, String>();
                map1.put("image",imageBase64Second4);
                map1.put("image_type","BASE64");
                list.add(map1);

                String param=new Gson().toJson(list);
                System.out.println(param);

                RequestBody body = RequestBody.create(JSON, param);
                Request request = new Request.Builder()
                        .addHeader("Content-Type","application/json")//添加头部
                        .url(url)
                        .post(body)
                        .build();

                try {
                    Response response = client4.newCall(request).execute();
                    String result ;
                    result =  response.body().string();
                    System.out.println(result);
                    Gson gson = new Gson();
                    Map reponseMap = gson.fromJson(result,Map.class);
                    Map  resultMap = (Map) reponseMap.get("result");
                    score= score+resultMap .get("score").toString()+"%\n";

                    //information=facenum+information;
                    //System.out.println(information);
                    Message message = new Message();
                    message.what = 1;
                    message.obj=score;
                    handler.sendMessage(message);
                }catch (IOException e){
                    e.printStackTrace();
                }
            };
        }.start();
    }



}

