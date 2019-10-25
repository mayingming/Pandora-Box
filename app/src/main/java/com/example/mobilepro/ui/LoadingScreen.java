package com.example.mobilepro.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilepro.MainActivity;
import com.example.mobilepro.R;

public class LoadingScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);super.onCreate(savedInstanceState);

//        finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

//        new Handler().postDelayed(new Runnable(){
//
//            public void run(){
//                Intent homeIntent = new Intent(LoadingScreen.this, MainActivity.class);
//                startActivity(homeIntent);
//                finish();
//            }
//
//        }, SPLASH_TIME_OUT);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
