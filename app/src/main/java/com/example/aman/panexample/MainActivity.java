package com.example.aman.panexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    private ImageSurfaceViewClass imageSurfaceViewClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageSurfaceViewClass = (ImageSurfaceViewClass) findViewById(R.id.imgSurfaceView);
    }
}
