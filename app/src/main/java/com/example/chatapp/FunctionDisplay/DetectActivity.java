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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatapp.Json.FaceResultList;
import com.example.chatapp.Json.JsonBean;
import com.example.chatapp.Json.JsonResult;
import com.example.chatapp.R;
import com.example.chatapp.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetectActivity extends AppCompatActivity implements View.OnClickListener {

    private String uploadFileName2;
    private byte[] fileBuf2;
    private ImageView photo2;
    private Button detectFaceBtn2;
    private TextView takePhoto2;
    private TextView information2;
    private Uri imgUri2;
    private Bitmap bitmapUpLode2;
    public OkHttpClient client2 = new OkHttpClient();
    public String accessToken2;


    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    information2.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        photo2=findViewById(R.id.photo2);
        detectFaceBtn2=findViewById(R.id.detectFaceBtn2);
        takePhoto2=findViewById(R.id.takePhoto2);
        information2=findViewById(R.id.information2);

        photo2.setOnClickListener(this);
        takePhoto2.setOnClickListener(this);
        detectFaceBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo2:
                select(v);
                break;
            case R.id.takePhoto2:
                camera(v);
                break;
            case R.id.detectFaceBtn2:
                detectFace(v);
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
            imgUri2= FileProvider.getUriForFile(this,"com.example.chatapp.fileprovider",outImg);
        else
            imgUri2= Uri.fromFile(outImg);

        //利用actionName和Extra,启动《相机Activity》
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri2);
        startActivityForResult(intent,2);
        //到此，启动了相机，等待用户拍照
    }

    //选择后照片的读取工作
    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();

        try {
            bitmapUpLode2 =  BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("源文件URI",uri.toString());
        cursor = getContentResolver().query(uri, null,        null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName2 = cursor.getString(columnIndex);
            //Log.i("选择上传文件",uploadFileName.toString());
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            fileBuf2=convertToBytes(inputStream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf2, 0, fileBuf2.length);
            photo2.setImageBitmap(bitmap);
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
                        Bitmap map= BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri2));
                        //得到上传百度库的map
                        bitmapUpLode2 = map;
                        //Log.i("图片","map");
                        photo2.setImageBitmap(map);

                        //更新上传文件所需要的 uploadFileName,fileBuf
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date(System.currentTimeMillis());
                        uploadFileName2 = format.format(date)+".jpg";
                        InputStream inputStream = getContentResolver().openInputStream(imgUri2);
                        try {
                            fileBuf2=convertToBytes(inputStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //创建文件夹
                        String sd = "sdcard/";
                        String fileName = sd +uploadFileName2;
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


    //完成与百度大脑的《人脸检测》API交互，完成相应的功能
    private void detectFace(View v){
        new Thread(){
            public void run(){
                accessToken2 = Utils.getAccessToken();
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token="+accessToken2;
                String imageBase64;
                float addScore;
                if (bitmapUpLode2 !=null) {
                    imageBase64 = Utils.bitmapToBase64(bitmapUpLode2);
                } else {
                    imageBase64 = null;
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(DetectActivity.this,"图片为null",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/json")
                        .addFormDataPart("image",imageBase64)
                        .addFormDataPart("image_type", "BASE64")
                        .addFormDataPart("face_field","age,beauty,,face_shape,gender,emotion")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client2.newCall(request).execute();
                    String result ;
                    String information="";
                    result =  response.body().string();
                    System.out.println(result);
                    Gson gson = new Gson();
                    Type type = new TypeToken<JsonBean>() {}.getType();
                    JsonBean jsonContent = gson.fromJson(result, type);
                    JsonResult jsonResult = jsonContent.getResult();
                    List<FaceResultList> faceResultList =  jsonResult.getFace_list();
                    FaceResultList faceResult = faceResultList.get(0);
                    String age=faceResult.getAge();
                    information=information+"年龄;"+age;

                    addScore=Float.parseFloat(faceResult.getBeauty())+30;
                    if(addScore>=100) {
                        if (addScore >= 100 && addScore < 110) {
                            addScore = addScore - 10;
                        } else if (addScore >= 110 && addScore < 120) {
                            addScore = addScore - 20;
                        } else {
                            addScore = addScore - 30;
                        }
                    }
                    String beauty=Float.toString(addScore);
                    information=information+"  美貌度;"+beauty;

                    String gender=faceResult.getGender();
                    information=information+"  性别;"+gender;

                    String faceshape=faceResult.getFaceShape();
                    information=information+"  脸型;"+faceshape;

                    String emotion = faceResult.getEmotion();
                    information=information+"  表情;"+emotion;

                    System.out.println(information);

                    Message message = new Message();
                    message.what = 1;
                    message.obj=information;
                    handler.sendMessage(message);

                }catch (IOException e){
                    e.printStackTrace();
                }
            };
        }.start();
    }



}
