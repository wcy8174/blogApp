package com.example.chatapp.ListView;

public class AllBlogContent implements Comparable<AllBlogContent> {
    private String name;
    private String time;
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    //实现排序，按照时间逆序,名字正序
    @Override
    public int compareTo(AllBlogContent o) {
        int i = o.time.compareTo(this.time);
        if(i == 0){
            i = this.name.compareTo(o.name);
        }
        return i;
    }
}
