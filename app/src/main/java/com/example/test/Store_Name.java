package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Store_Name extends AppCompatActivity {
    EditText e1;
    Button b1;


    String session_id = "";
    String session_username = "";
    String customer_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_name);
        e1= findViewById(R.id.et_store_name);
        b1 = findViewById(R.id.btn_submit_store);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            session_username = extras.getString("session_username");
            session_id = extras.getString("session_id");
            customer_id = extras.getString("customer_id");

        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String store = e1.getText().toString().trim();
                if(store.equals(""))
                {
                    Toast.makeText(Store_Name.this, "Please enter the value", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(Store_Name.this,
                            Catagory.class);
                    i.putExtra("session_username", session_username);
                    i.putExtra("session_id", session_id);
                    i.putExtra("customer_id", customer_id);
                    i.putExtra("store_name", store);
                    //Intent is used to switch from one activity to another.
                    startActivity(i);
                }
            }
        });

    }
}