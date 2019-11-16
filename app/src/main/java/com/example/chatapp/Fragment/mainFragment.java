package com.example.chatapp.Fragment;



import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.JsonMy.JsonFindUserBlog;
import com.example.chatapp.ListView.AllBlogAdapter;
import com.example.chatapp.ListView.AllBlogContent;

import com.example.chatapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class mainFragment extends Fragment implements View.OnClickListener{
    private RecyclerView allBlogRV;
    //private EditText titleText;
    private Button flushContentBtn;

    private List<AllBlogContent> allBlogContents = new ArrayList<>();
    Activity contactActivity;
    AllBlogAdapter allBlogAdapter;
    OkHttpClient client = new OkHttpClient();

    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //flag = true;
                    //Toast.makeText(getActivity(),"ceshi"+flag.toString()+blogContents.size(),Toast.LENGTH_SHORT).show();
                    allBlogAdapter.notifyDataSetChanged();
                default:
                    break;
            }
        }

    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chatroom,container,false);

        allBlogRV = view.findViewById(R.id.allBlogRV);
        //titleText = view.findViewById(R.id.titleText);
        flushContentBtn = view.findViewById(R.id.flushContentBtn);

        initAllBlogContent();

        contactActivity = getActivity();

        //RecyclerView rv=view.findViewById(R.id.rv);
        allBlogAdapter=new AllBlogAdapter(allBlogContents,contactActivity);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        // manager.setOrientation(RecyclerView.Orientation.);
        allBlogRV.setLayoutManager(manager);
        allBlogRV.setAdapter(allBlogAdapter);

        flushContentBtn.setOnClickListener(this);
        //titleText.setOnClickListener(this);
        return view;
    }

    private void initAllBlogContent() {
        new Thread(){
            public void run(){
                AllBlogContent allBlogContent = new AllBlogContent();
                String url = "http://114.55.33.227:8000/findBlog";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", "all")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result;
                    result =  response.body().string();

                    allBlogContents.clear();
                    Log.i("测试结果2",result);
                    if(result.equals("nullBlog")){
                        //获取一个时间
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = new Date(System.currentTimeMillis());
                        String time = format.format(date);
                        //赋值
                        allBlogContent.setName("null");
                        allBlogContent.setTime(time);
                        allBlogContent.setContent("第一条微博");
                        allBlogContents.add(allBlogContent);

                    }else{
                        Gson gson = new Gson();
                        Type listType = new TypeToken<LinkedList<JsonFindUserBlog>>(){}.getType();
                        LinkedList<JsonFindUserBlog> blogs = gson.fromJson(result, listType);
                        for (Iterator iterator = blogs.iterator(); iterator.hasNext();) {

                            JsonFindUserBlog blog = (JsonFindUserBlog) iterator.next();
                            List<JsonFindUserBlog.result> blogRecord = blog.getRecord();
                            String name= blog.getName();

                            for(int i=0;i<blogRecord.size();i++){
                                AllBlogContent allBlogContent1 = new AllBlogContent();
                                String time = blogRecord.get(i).getTime();
                                String content = blogRecord.get(i).getContent();
                                allBlogContent1.setName(name);
                                allBlogContent1.setTime(time);
                                allBlogContent1.setContent(content);
                                allBlogContents.add(allBlogContent1);
                            }
                        }
                    }
                    Collections.sort(allBlogContents);
                    mHandler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flushContentBtn:
                initAllBlogContent();
                break;
        }
    }
}
