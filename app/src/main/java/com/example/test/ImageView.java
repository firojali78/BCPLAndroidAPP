package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

public class ImageView extends AppCompatActivity {
    android.widget.ImageView iv1;
    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        iv1 = findViewById(R.id.iv_full);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            url = extras.getString("url");

            for (String key: extras.keySet())
            {
                System.out.println( "Keys in bundles in ImageView Page "+key + " "+extras.getString(key) );
            }


            //The key argument here must match that used in the other activity
        }

        Glide.with(this).load(url).error(R.drawable.ic_launcher_background).
                into((android.widget.ImageView) iv1);


        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}