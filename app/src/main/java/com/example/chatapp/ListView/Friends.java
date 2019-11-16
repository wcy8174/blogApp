package com.example.chatapp.ListView;

public class Friends implements Comparable<Friends>{
    private int imageId;
    private String name;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Friends o) {
        int i = this.getName().compareTo(o.getName());
        return i;
    }
}
