package com.example.chatapp.JsonMy;

import java.util.List;

public class JsonIdentifyResult {
    private int error_code;
    private String error_msg;
    private long log_id;
    private int timestamp;
    private int cached;
    private JsonResult result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }


    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }


    public JsonResult getResult() {
        return result;
    }

    public void setResult(JsonResult result) {
        this.result = result;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getCached() {
        return cached;
    }

    public void setCached(int cached) {
        this.cached = cached;
    }


    public static class JsonResult {
        private  String face_token;
        private  List<UserResultList> user_list;

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
    }

    public static class UserResultList {
        private String group_id;
        private String user_id;
        private String user_info;
        private Double score;

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_info() {
            return user_info;
        }

        public void setUser_info(String user_info) {
            this.user_info = user_info;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }
    }




}
