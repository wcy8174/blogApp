package com.example.chatapp;

import android.util.Log;

import com.example.chatapp.JsonMy.JsonAccessToken;
import com.example.chatapp.JsonMy.JsonFindUserBlog;
import com.example.chatapp.JsonMy.JsonFriends;
import com.example.chatapp.ListView.BlogContent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public  void initFriends(){

                String url = "http://114.55.33.227:8000/findFriends";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name", "wcy")
                        //filename:avatar,originname:abc.jpg
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();

                    String result;
                    result =  response.body().string();
                    Gson gson = new Gson();
                    JsonFriends friendResult = gson.fromJson(result,JsonFriends.class);

                    System.out.println("1");
                    Logining.friendsList  = friendResult.getFriends();
                    System.out.println(Logining.friendsList.toString());
                    System.out.println("2");
                    //Log.i("test",Logining.friendsList.toString());

                    //String error_msg = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    @Test
    public void timetest() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        System.out.println(time);

//        SimpleDateFormat format = new SimpleDateFormat(formatType);
//
//         date = format.parse(time);
        java.text.SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyyMMddHHmm");
        date = formatter.parse(time);
        System.out.println(date);


    }

    @Test
    public void resultTest(){
        BlogContent blogContent = new BlogContent();
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
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String result;
            result =  response.body().string();

           // Log.i("测试结果2",result);
            if(result.equals("nullBlog")){
                //获取一个时间
            }else{
                Gson gson = new Gson();
                Type listType = new TypeToken<LinkedList<JsonFindUserBlog>>(){}.getType();
                LinkedList<JsonFindUserBlog> blogs = gson.fromJson(result, listType);
                for (Iterator iterator = blogs.iterator(); iterator.hasNext();) {
                    JsonFindUserBlog blog = (JsonFindUserBlog) iterator.next();
                    System.out.println(blog.getName());
                }

                //最外层信息
//                JsonFindUserBlog jsonContent = gson.fromJson(result,JsonFindUserBlog.class);
//                List<JsonFindUserBlog.result> blogRecord = jsonContent.getRecord();
//
//                for(int i =0;i<blogRecord.size();i++){
//                    BlogContent blogContent1 = new BlogContent();
//                    String time = blogRecord.get(i).getTime();
//                    String content = blogRecord.get(i).getContent();
//                    blogContent1.setTime(time);
//                    blogContent1.setContent(content);
//                    //添加到数组
//                    blogContents.add(blogContent1);
//                    Log.i("测试结果4",blogContents.get(i).getContent());
//                    //发消息完成
//                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void subBlog() {


                //获取当前时间，转化为字符串
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                Date date = new Date(System.currentTimeMillis());
                String time = format.format(date);
                //获取发表内容
                String content;
                content = "测试2";
                //
                String url = "http://114.55.33.227:8000/submitBlog";

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Content-Type","application/x-www-form-urlencoded")
                        .addFormDataPart("name","wcy")
                        .addFormDataPart("time", time)
                        .addFormDataPart("content", content)
                        //filename:avatar,originname:abc.jpg
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();

                    String result;
                    result =  response.body().string();
                    System.out.println(1);
                    System.out.println(result);
                    System.out.println(2);
                    if(result.equals("submitSuccess")){
                        //清空内容
                        //发消息更改UI

                        //传入adapter
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

    }

    @Test
    public  void getAccessToken(){
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + "1ii4cGNSh1jz4Ue9aOHnKmKW"
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + "q6xIT2TrfvoQEK6YjGAaEWGUR5TSaTqL";
        String access_token;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            //Log.i("测试","2");
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //Log.i("测试",in.toString());
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            System.err.println("result:" + result);


            Gson gson = new Gson();
            JsonAccessToken jsonContent = gson.fromJson(result, JsonAccessToken.class);
            //System.out.println(jsonContent.getAccess_token());
            access_token = jsonContent.getAccess_token();

        } catch (Exception e) {
            System.err.printf("获取token失败！");
            access_token = "shibai";
            e.printStackTrace(System.err);
        }
        System.out.println(access_token);
    }
}