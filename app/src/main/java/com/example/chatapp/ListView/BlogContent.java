package com.example.chatapp.ListView;

public class BlogContent implements Comparable<BlogContent> {
    private String time;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int compareTo(BlogContent o) {

        int i = o.getTime().compareTo(this.getTime());//
        return i;
        //return 0;
    }
}
