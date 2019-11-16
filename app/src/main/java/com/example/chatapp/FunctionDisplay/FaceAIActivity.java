package com.example.chatapp.FunctionDisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.content.Intent;

import com.example.chatapp.R;


public class FaceAIActivity extends AppCompatActivity implements View.OnClickListener {

    private Button faceAdd;
    private Button detectFace;
    private Button multiFace;
    private Button matchFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_ai);


        faceAdd = findViewById(R.id.faceAdd);
        detectFace = findViewById(R.id.detectFace);
        multiFace = findViewById(R.id.multiFace);
        matchFace = findViewById(R.id.matchFace);

        faceAdd.setOnClickListener(this);
        detectFace.setOnClickListener(this);
        multiFace.setOnClickListener(this);
        matchFace.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.faceAdd:
                Intent intent1 = new Intent(FaceAIActivity.this,FaceAddActivity.class);
                startActivity(intent1);
                break;
            case R.id.detectFace:
                Intent intent2 = new Intent(FaceAIActivity.this,DetectActivity.class);
                startActivity(intent2);
                break;
            case R.id.multiFace:
                Intent intent3 = new Intent(FaceAIActivity.this,MultiSearchActivity.class);
                startActivity(intent3);
                break;
            case R.id.matchFace:
                Intent intent4 = new Intent(FaceAIActivity.this,MatchActivity.class);
                startActivity(intent4);
        }
    }




}

