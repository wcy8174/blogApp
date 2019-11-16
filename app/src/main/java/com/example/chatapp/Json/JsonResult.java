package com.example.chatapp.Json;

import java.util.List;

public class JsonResult {
    //search
    private  String face_token;
    public   List<UserResultList> user_list;
    //detect
    public    List<FaceResultList> face_list;
    private  String face_num;

    //search
    public  String getFace_token() {
        return face_token;
    }

    public  void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    public  List<UserResultList> getUserList() {
        return user_list;
    }

    public  void setUserList(List<UserResultList> userList) {
        this.user_list = userList;
    }

    //detect


    public void setFace_list(List<FaceResultList> face_list) { this.face_list = face_list; }

    public void setFace_num(String face_num) { this.face_num = face_num; }

    public List<FaceResultList> getFace_list() { return face_list; }

    public String getFace_num() { return face_num; }

}
