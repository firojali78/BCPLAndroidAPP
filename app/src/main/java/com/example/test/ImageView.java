package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

public class ImageView extends AppCompatActivity {
    android.widget.ImageView iv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        iv1 = findViewById(R.id.iv_full);
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        iv1.setImageBitmap(bitmap);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}