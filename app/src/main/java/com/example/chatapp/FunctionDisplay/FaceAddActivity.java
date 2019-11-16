package com.example.chatapp.FunctionDisplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import com.example.chatapp.R;
import com.example.chatapp.Utils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceAddActivity extends AppCompatActivity  implements View.OnClickListener {

    private Button selectBtn1;
    private Button takePhotoBtn1;
    private Button faceAddBtn1;
    private ImageView faceAddPhoto1;
    private EditText userId1;
    private String uploadFileName1;
    private byte[] fileBuf1;
    private Uri imgUri1;
    private Bitmap bitmapUpLode1;

    public OkHttpClient client1 = new OkHttpClient();
    public String accessToken1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_add);

        selectBtn1 = findViewById(R.id.selectBtn1);
        takePhotoBtn1 = findViewById(R.id.photoBtn1);
        userId1 = findViewById(R.id.getUserId1);
        faceAddBtn1=findViewById(R.id.faceAddBtn1);
        faceAddPhoto1=findViewById(R.id.faceAddPhoto1);

        selectBtn1.setOnClickListener(this);
        takePhotoBtn1.setOnClickListener(this);
        faceAddBtn1.setOnClickListener(this);

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectBtn1:
                select(v);
                break;
            case R.id.photoBtn1:
                camera(v);
                break;
            case R.id.faceAddBtn1:
                faceAdd(v);
                break;

        }
    }



    public void select(View view) {

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            openGallery(); //打开相册，进行选择
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //打开相册,进行照片的选择
    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    public void camera(View view) {
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
            imgUri1= FileProvider.getUriForFile(this,"com.example.chatapp.fileprovider",outImg);
        else
            imgUri1= Uri.fromFile(outImg);

        //利用actionName和Extra,启动《相机Activity》
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri1);
        startActivityForResult(intent,2);
        //到此，启动了相机，等待用户拍照
    }

    //选择后照片的读取工作
    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        Uri uri1 = intent.getData();

        try {
            bitmapUpLode1 =  BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("源文件URI",uri1.toString());
        cursor = getContentResolver().query(uri1, null,        null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName1 = cursor.getString(columnIndex);
            //Log.i("选择上传文件",uploadFileName.toString());
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri1);
            fileBuf1=convertToBytes(inputStream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf1, 0, fileBuf1.length);
            faceAddPhoto1.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                handleSelect(data);
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    try {
                        //利用ContentResolver,查询临时文件，并使用BitMapFactory,从输入流中创建BitMap
                        //同样需要配合Provider,在Manifest.xml中加以配置
                        Bitmap map= BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri1));
                        //得到上传百度库的map
                        bitmapUpLode1 = map;
                        //Log.i("图片","map");
                        faceAddPhoto1.setImageBitmap(map);

                        //更新上传文件所需要的 uploadFileName,fileBuf
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date(System.currentTimeMillis());
                        uploadFileName1 = format.format(date)+".jpg";
                        InputStream inputStream = getContentResolver().openInputStream(imgUri1);
                        try {
                            fileBuf1=convertToBytes(inputStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //创建文件夹
                        String sd = "sdcard/";
                        String fileName = sd +uploadFileName1;
                        File file = new File(fileName);
                        FileOutputStream fos = new FileOutputStream(file);
                        //把文件写入流 fos
                        map.compress(Bitmap.CompressFormat.JPEG,100,fos);
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //以广播的形式通知扫描sd卡，
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(file));
                        sendBroadcast(intent);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

        }
    }



    public  void faceAdd(View view){
        new Thread(){
            public void run(){
                accessToken1 = Utils.getAccessToken();
                //Log.i("获取token",accessToken);
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token="+accessToken1;
                String userName = userId1.getText().toString();
                if(userName.isEmpty()){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(FaceAddActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }
                String imageBase64;
                if (bitmapUpLode1 !=null) {
                    imageBase64 = Utils.bitmapToBase64(bitmapUpLode1);
                } else {
                    imageBase64 = null;
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(FaceAddActivity.this,"图片为null",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }

                if (!userName.isEmpty()&&bitmapUpLode1 !=null) {
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
                        Response response = client1.newCall(request).execute();
//               Log.i("数据", response.body().string() + "....");
//                Log.i("数据",accessToken1);
                        // Log.i("上传toast","2");
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(FaceAddActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            };
                        }.start();
                        //System.out.println(response.body().string());
                    } catch (IOException e) {

                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(FaceAddActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            };
                        }.start();
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }


}
