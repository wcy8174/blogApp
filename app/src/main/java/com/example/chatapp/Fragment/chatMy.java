package com.example.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.chatapp.FunctionDisplay.FaceAIActivity;
import com.example.chatapp.FunctionDisplay.webViewFunction;
import com.example.chatapp.Logining;
import com.example.chatapp.R;
import java.io.IOException;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chatMy extends Fragment implements View.OnClickListener{
    private Button displayBtn;
    private TextView myInformationPage;
    private Button alterPasswordBtn;
    private String newPassword;
    private TextView extend;

    private EditText newPasswordText;
    OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_my,container,false);

        myInformationPage = view.findViewById(R.id.myInformationPage);
        displayBtn = view.findViewById(R.id.displayBtn);
        alterPasswordBtn = view.findViewById(R.id.alterPasswordBtn);
        newPasswordText = view.findViewById(R.id.newPassword);
        extend = view.findViewById(R.id.extend);
        myInformationPage.setText(Logining.name);

        extend.setOnClickListener(this);
        displayBtn.setOnClickListener(this);
        alterPasswordBtn.setOnClickListener(this);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.displayBtn:
                Intent intent = new Intent(getActivity(), FaceAIActivity.class);
                startActivity(intent);
                break;
            case R.id.alterPasswordBtn:
                alterPassword();
                break;
            case R.id.extend:
                Intent intent2 = new Intent(getActivity(), webViewFunction.class);
                startActivity(intent2);
                Toast.makeText(getContext(),"测试123",Toast.LENGTH_SHORT).show();
        }
    }
    //修改密码
    private void alterPassword() {
        new Thread(){
            public void run(){
                //
                //获得新密码
                newPassword = newPasswordText.getText().toString();
                if(newPassword.equals("")||newPassword == null){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(getContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        }
                    }.start();
                }else {
                    String url = "http://114.55.33.227:8000/alterPassword";
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                            .addFormDataPart("name", Logining.name)
                            .addFormDataPart("password", newPassword)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        String result;
                        result =  response.body().string();
                        if(result.equals("alterSuccess")){
                            //提示修改成功
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(getContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        }else if(result.equals("invalidName")){
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(getContext(),"该用户不在账户数据库中",Toast.LENGTH_SHORT).show();
                                    Looper.loop();// 进入loop中的循环，查看消息队列
                                }
                            }.start();
                        } else{
                            new Thread() {
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(getContext(),"请重试",Toast.LENGTH_SHORT).show();
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
