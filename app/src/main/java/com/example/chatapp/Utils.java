package com.example.chatapp;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.chatapp.JsonMy.JsonAccessToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String getAccessToken(){
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
            // 遍历所有的响应头字段
            ///System.out.println("1");
            Log.i("测试","1");
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            Log.i("测试","2");
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            Log.i("测试",in.toString());
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            //System.out.println(3);
            //System.out.println(result.toString());
            Log.i("测试1",result.toString());

            System.err.println("result:" + result);
            // JsonParser parser = new JsonParser();

            //String jsonData = "[{\"username\":\"arthinking\",\"userId\":001},{\"username\":\"Jason\",\"userId\":002}]";

            Gson gson = new Gson();
            JsonAccessToken jsonContent = gson.fromJson(result, JsonAccessToken.class);
            //System.out.println(jsonContent.getAccess_token());
            access_token = jsonContent.getAccess_token();



//            JSONObject jsonObject = new JSONObject(result);
//            String access_token = jsonObject.getString("access_token");
//            System.out.println("chenggong");
//            System.out.println(access_token);
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            access_token = "shibai";
            e.printStackTrace(System.err);
        }
        return access_token;
    }


    //bitmap转为base64
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = android.util.Base64.encodeToString(bitmapBytes, android.util.Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }



}
