package com.example.chatapp.Json;
import com.example.chatapp.Json.Emotion;
import com.example.chatapp.Json.Gender;
import com.example.chatapp.Json.FaceShape;
import com.example.chatapp.Json.UserResultList;

import java.util.List;

public class FaceResultList {
    //detectFace
    private String age;
    private String beauty;
    FaceShape face_shape=new FaceShape();
    Gender gender=new Gender();
    Emotion emotion=new Emotion();


    public void setAge(String age) {
        this.age = age;
    }

    public void setBeauty(String beauty) {
        this.beauty = beauty;
    }


    public void setFaceShape(FaceShape faceShape) {
        this.face_shape = faceShape;
    }

    public void setGendertype(Gender gender) {
        this.gender = gender;
    }

    public void setEmotiontype(Emotion emotion) {
        this.emotion = emotion;
    }

    public String getAge() {
        return age;
    }

    public String getBeauty() {
        return beauty;
    }

    public String getFaceShape() {
        return face_shape.getType();
    }

    public String getGender() {
        return gender.getType();
    }

    public String getEmotion() {
        return emotion.getType();
    }


}
