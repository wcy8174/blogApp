package com.example.chatapp.FunctionDisplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.example.chatapp.R;
import com.example.chatapp.Utils;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MultiSearchActivity extends AppCompatActivity  implements View.OnClickListener {
    private String uploadFileName3;
    private byte[] fileBuf3;
    private ImageView photo3;
    private Button multiFaceBtn3;
    private TextView takePhoto3;
    private TextView information3;
    private Uri imgUri3;
    private Bitmap bitmapUpLode3;
    public OkHttpClient client3 = new OkHttpClient();
    public String accessToken3;
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    information3.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_search);


        photo3=findViewById(R.id.photo3);
        multiFaceBtn3=findViewById(R.id.multiFaceBtn3);
        information3=findViewById(R.id.information3);

        photo3.setOnClickListener(this);
        multiFaceBtn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo3:
                select(v);
                break;
            case R.id.multiFaceBtn3:
                multiFace(v);
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

    //选择后照片的读取工作
    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();

        try {
            bitmapUpLode3 =  BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("源文件URI",uri.toString());
        cursor = getContentResolver().query(uri, null,        null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName3 = cursor.getString(columnIndex);
            //Log.i("选择上传文件",uploadFileName.toString());
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            fileBuf3=convertToBytes(inputStream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf3, 0, fileBuf3.length);
            photo3.setImageBitmap(bitmap);
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

    //打开相册,进行照片的选择
    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                handleSelect(data);
                break;
        }

    }



    //利用《人脸搜索》完成基于合影照片的人脸搜索并显示
    private void multiFace(View v){
        new Thread(){
            public void run(){
                accessToken3 = Utils.getAccessToken();
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search?access_token="+accessToken3;
                String imageBase64;
                String information="与库中匹配的人脸如下：\n";
                String userGroupId;
                String userName;
                String score;
                String facenum="总人脸数： ";
                int count=1;
                if (bitmapUpLode3 !=null) {
                    imageBase64 = Utils.bitmapToBase64(bitmapUpLode3);
                } else {
                    imageBase64 = null;
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(MultiSearchActivity.this,"图片为null",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        };
                    }.start();
                }
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/json")
                        .addFormDataPart("image",imageBase64)
                        .addFormDataPart("group_id_list","normal")
                        .addFormDataPart("image_type", "BASE64")
                        .addFormDataPart("max_face_num","10")
                        .addFormDataPart("max_user_num","10")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client3.newCall(request).execute();
                    String result ;
                    result =  response.body().string();
                    // System.out.println(result);
                    Gson gson = new Gson();
                    Map reponseMap = gson.fromJson(result,Map.class);
                    Map  resultMap = (Map) reponseMap.get("result");
                    List<Map> face_list =  (List<Map>)resultMap.get("face_list");
                    facenum=facenum+resultMap .get("face_num").toString()+"\n";
                    for (Map map : face_list){
                        List<Map> user_list =  (List<Map>)map.get("user_list");
                        for(Map user : user_list){
                            userGroupId=user.get("group_id").toString();
                            userName=user.get("user_id").toString();
                            score=user.get("score").toString();
                            information=information+"用户"+count+":\n"+"组号："
                                    +userGroupId+"\n用户名："+userName
                                    +"\n相似度："+score+"%\n\n";
                            count++;

                        }
                    }
                    information=facenum+information;
                    //System.out.println(information);
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
