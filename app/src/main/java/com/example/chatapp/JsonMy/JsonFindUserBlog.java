package com.example.chatapp.JsonMy;

import java.util.List;

public class JsonFindUserBlog {
    private String _id;
    private String name;
    private List<result> record;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<result> getRecord() {
        return record;
    }

    public void setRecord(List<result> record) {
        this.record = record;
    }


    public static class result{
        private String time;
        private String content;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
