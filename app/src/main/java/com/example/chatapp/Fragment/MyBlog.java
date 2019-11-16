package com.example.chatapp.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.FunctionDisplay.FaceAIActivity;
import com.example.chatapp.ListView.BlogAdapter;
import com.example.chatapp.ListView.BlogContent;
import com.example.chatapp.JsonMy.JsonFindUserBlog;
import com.example.chatapp.Logining;
import com.example.chatapp.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyBlog extends Fragment implements  View.OnClickListener{

    private Button submitBtn;
    private EditText blogRecord;
    private RecyclerView blogListView;
    //存储adapter需要的数据
    private List<BlogContent> blogContents = new ArrayList<>();
    BlogAdapter blogAdapter;
    Activity contactActivity;
    //private Boolean flag = false;
    OkHttpClient client = new OkHttpClient();

    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    blogRecord.setText("");
                    blogAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    //flag = true;
                    //Toast.makeText(getActivity(),"ceshi"+flag.toString()+blogContents.size(),Toast.LENGTH_SHORT).show();
                    for(int i=0;i<blogContents.size();i++){
                        Log.i("测试6",blogContents.get(i).getContent());
                        Log.i("测试7",""+i);
                    }

                    blogAdapter.notifyDataSetChanged();

                default:
                    break;
            }
        }

    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_alone,container,false);

        blogRecord = view.findViewById(R.id.blogRecord);
        submitBtn = view.findViewById(R.id.submitBtn);
        blogListView = view.findViewById(R.id.blogListView);

        submitBtn.setOnClickListener(this);

        //初试化内容
        initBlogListView();
        Collections.sort(blogContents,new Comparator<BlogContent>(){
            @Override
            public int compare(BlogContent o1, BlogContent o2) {
                int i = o2.getTime().compareTo(o1.getTime());
                return i;
            }
        });

        contactActivity = getActivity();

        //RecyclerView rv=view.findViewById(R.id.rv);
        blogAdapter=new BlogAdapter(blogContents,contactActivity);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        // manager.setOrientation(RecyclerView.Orientation.);
        blogListView.setLayoutManager(manager);
        blogListView.setAdapter(blogAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitBtn:
                subBlog();
                break;
        }
    }

    //初始化 blogListView
    private void initBlogListView() {
        new Thread(){
            public void run(){
                BlogContent blogContent = new BlogContent();
                String name = Logining.name;
                String url = "http://114.55.33.227:8000/findBlog";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", name)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result;
                    result =  response.body().string();

                    Log.i("测试结果2",result);
                    if(result.equals("nullBlog")){
                        //获取一个时间
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = new Date(System.currentTimeMillis());
                        String time = format.format(date);
                        //赋值
                        blogContent.setTime(time);
                        blogContent.setContent("第一条微博");
                        blogContents.add(blogContent);

                    }else{
                        Gson gson = new Gson();
                        //最外层信息
                        JsonFindUserBlog jsonContent = gson.fromJson(result,JsonFindUserBlog.class);
                        List<JsonFindUserBlog.result> blogRecord = jsonContent.getRecord();

                        for(int i =0;i<blogRecord.size();i++){
                            BlogContent blogContent1 = new BlogContent();
                            String time = blogRecord.get(i).getTime();
                            String content = blogRecord.get(i).getContent();
                            blogContent1.setTime(time);
                            blogContent1.setContent(content);
                            //添加到数组
                            blogContents.add(blogContent1);
                            Log.i("测试结果4",blogContents.get(i).getContent());
                            //发消息完成
                        }

                    }
                    Collections.sort(blogContents);
                    mHandler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //提交微博
    private void subBlog() {
        new Thread(){
            public void run(){
                //获取当前时间，转化为字符串
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                Date date = new Date(System.currentTimeMillis());
                String time = format.format(date);
                //获取发表内容
                String content;
                content = blogRecord.getText().toString();
                //
                String url = "http://114.55.33.227:8000/submitBlog";
                //判断是否有内容
                if(content.equals("") || content == null){
                    new Thread() {
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(getContext(),"内容不能为空",Toast.LENGTH_SHORT).show();
                            Looper.loop();// 进入loop中的循环，查看消息队列
                        }
                    }.start();
                }else{
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                            .addFormDataPart("name", Logining.name)
                            .addFormDataPart("time", time)
                            .addFormDataPart("content", content)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        String result;
                        result =  response.body().string();
                        if(result.equals("submitSuccess")){
                            BlogContent blogContent = new BlogContent();
                            blogContent.setTime(time);
                            blogContent.setContent(content);
                            blogContents.add(blogContent);
                            //清空内容
                            //发消息更改UI
                            Collections.sort(blogContents);
                            mHandler.sendEmptyMessage(0);
                            //传入adapter
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


}
