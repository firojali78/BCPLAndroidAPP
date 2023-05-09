package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ItemViewDetail extends AppCompatActivity {
    EditText e1,e2,e3,e4,e5,e6;
    Button b1;
    View iv1;
    TextView t_name;
    SharedPreferences sharedPreferences;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    int max_size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view_detail);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        init_layout();

        sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("json_val", 0);
        edit.commit();

        SharedPreferences sh = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        int index = sh.getInt("json_val",0);

        String name, url,pid;
        JSONObject jsonObject = loadJson(index);
        try {
            name = jsonObject.getString("name");
            url = jsonObject.getString("imgUrl");
            pid = jsonObject.getString("pid");
            edit.putString("pid", pid);
            edit.commit();
            setView(name,url);
            enableEditText(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pid="";
                SharedPreferences sh = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                pid = sh.getString("pid","1");
                int s,m,l,xl,xxl,xxxl;
                if(e1.getText().toString().equals(""))
                {
                    s =0;
                }
                else
                {
                    s = Integer.parseInt(e1.getText().toString());
                }
                if(e2.getText().toString().equals(""))
                {
                    m =0;
                }
                else
                {
                    m = Integer.parseInt(e2.getText().toString());
                }
                if(e3.getText().toString().equals(""))
                {
                    l =0;
                }
                else if(e3.getText().toString().equals("40"))
                {
                    Toast.makeText(ItemViewDetail.this, "Out of Stock", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    l = Integer.parseInt(e3.getText().toString());
                }
                if(e4.getText().toString().equals(""))
                {
                    xl =0;
                }
                else
                {
                    xl = Integer.parseInt(e4.getText().toString());
                }
                if(e5.getText().toString().equals(""))
                {
                    xxl =0;
                }
                else
                {
                    xxl = Integer.parseInt(e5.getText().toString());
                }
                if(e6.getText().toString().equals(""))
                {
                    xxxl =0;
                }
                else
                {
                    xxxl = Integer.parseInt(e6.getText().toString());
                }
                Toast.makeText(ItemViewDetail.this, "Data Updated for PID "+ pid, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setView(String name, String url)
    {
        e1.setEnabled(false);
        e2.setEnabled(false);
        e3.setEnabled(false);
        e4.setEnabled(false);
        e5.setEnabled(false);
        e6.setEnabled(false);

        t_name.setText(name);
        Glide.with(this).load(url).into((ImageView) iv1);
    }


    private JSONObject loadJson(int i)
    {
        try {
            InputStream inputStream = getAssets().open("data.json");
            int size = inputStream.available();
            byte [] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json;
            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            int max = jsonArray.length();
            max_size = max;
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            return  jsonObject;
        }
        catch (Exception e)
        {
            Log.d("TAG", "loadJson err "+e);
        }
        return null;
    }


    private void init_layout() {
        e1  = findViewById(R.id.et_s);
        e2 = findViewById(R.id.et_m);
        e3 = findViewById(R.id.et_l);
        e4 = findViewById(R.id.et_xl);
        e5 = findViewById(R.id.et_xxl);
        e6 = findViewById(R.id.et_xxxl);
        b1 = findViewById(R.id.btn_submit);
        iv1 = findViewById(R.id.p_images);
        t_name = findViewById(R.id.p_name);
    }

    private void enableEditText(JSONObject jsonObject) throws JSONException {
        String size;
        size = jsonObject.getString("size");
        String size_arr[] = size.split(",");
        for(int i =0;i<size_arr.length;i++)
        {
            System.out.println("Size available is "+size_arr[i]);
            if(size_arr[i].equals("S"))
            {
                e1.setEnabled(true);
            }
            if(size_arr[i].equals("M"))
            {
                e2.setEnabled(true);
            }
            if(size_arr[i].equals("L"))
            {
                e3.setEnabled(true);
            }
            if(size_arr[i].equals("XL"))
            {
                e4.setEnabled(true);
            }
            if(size_arr[i].equals("XXL"))
            {
                e5.setEnabled(true);
            }
            if(size_arr[i].equals("XXXL"))
            {
                e6.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        SharedPreferences sh = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                        int index = sh.getInt("json_val",0);
                        String name, url, pid="1";
                        if(index == 0)
                        {
                            Toast.makeText(this, "This is first item", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            index = index - 1;
                            JSONObject jsonObject = loadJson(index);
                            try {
                                name = jsonObject.getString("name");
                                url = jsonObject.getString("imgUrl");
                                pid = jsonObject.getString("pid");
                                setView(name, url);
                                enableEditText(jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putInt("json_val", index);
                        edit.putString("pid", pid);
                        edit.commit();
                   //     Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                    }

                    // Right to left swipe action
                    else
                    {

                        SharedPreferences sh = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                        int index = sh.getInt("json_val",0);
                        System.out.println(max_size + " " + index);
                        if(index == max_size-1)
                        {
                            Toast.makeText(this, "This is Last item", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            index = index + 1;
                            String name, url, pid = "1";
                            JSONObject jsonObject = loadJson(index);
                            try {
                                name = jsonObject.getString("name");
                                url = jsonObject.getString("imgUrl");
                                pid = jsonObject.getString("pid");
                                setView(name, url);
                                enableEditText(jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putInt("json_val", index);
                            edit.putString("pid", pid);
                            edit.commit();
                        }

                       // Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}