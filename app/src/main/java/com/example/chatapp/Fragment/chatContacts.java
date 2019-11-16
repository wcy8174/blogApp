package com.example.chatapp.Fragment;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.AddFriendActivity;
import com.example.chatapp.JsonMy.JsonFriends;
import com.example.chatapp.ListView.FriendAdapter;
import com.example.chatapp.ListView.Friends;
import com.example.chatapp.Logining;
import com.example.chatapp.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class chatContacts extends Fragment implements View.OnClickListener{
    private List<Friends> friends = new ArrayList<>();
    private FriendAdapter adapter;
    private List<String> friendList;
    private Activity contactActivity;

    private Button addFriendBtn;
    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                default:
                    break;
            }
        }

    };

    OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.friends_listview,container,false);

        addFriendBtn = view.findViewById(R.id.addFriendBtn);

        //初始化好友列表
        initFriends();

        contactActivity = getActivity();

        Collections.sort(friends);
        RecyclerView rv=view.findViewById(R.id.rv);
        adapter=new FriendAdapter(friends,contactActivity);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        // manager.setOrientation(RecyclerView.Orientation.);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        addFriendBtn.setOnClickListener(this);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFriendBtn:
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void initFriends(){
        new Thread(){
            public void run(){
                String url = "http://114.55.33.227:8000/findFriends";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", Logining.name)
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
                    JsonFriends friendResult;

                    friends.clear();

                    if(result.equals("nullFriends")){
                        Friends friend =new Friends();
                        friend.setImageId(R.drawable.my);
                        friend.setName(Logining.name);
                        friends.add(friend);
                    }else{
                        Gson gson = new Gson();
                        friendResult = gson.fromJson(result,JsonFriends.class);
                        friendList = friendResult.getFriends();
                        friendList.add(Logining.name);

                        for (int i = 0; i < friendList.size(); i++) {
                            Friends friend =new Friends();
                            friend.setImageId(R.drawable.my);
                            friend.setName(friendList.get(i));
                            friends.add(friend);
                        }

                    }
                    Collections.sort(friends);
                    mHandler.sendEmptyMessage(1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
