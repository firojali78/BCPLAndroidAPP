package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Catagory extends AppCompatActivity {
    TextView t1;
    ListView lv;
    int max_size;
    String catagory_name="";
    String catagory_id="";
    ArrayList<HashMap<String,String>> catagories;

    String session_id = "C1";
    String session_username = "Akash";
    String store="";
    String customer_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);
        t1 = findViewById(R.id.tv_catagory_customer);
        lv =findViewById(R.id.lv_catagory);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            session_username = extras.getString("session_username");
            session_id = extras.getString("session_id");
            store = extras.getString("store_name");
            customer_id = extras.getString("customer_id");

            t1.setText("Hello "+session_username);
            Toast.makeText(this, session_id, Toast.LENGTH_SHORT).show();
            //The key argument here must match that used in the other activity
        }

        catagories = new ArrayList<>();
        ArrayList<HashMap<String,String>> data = loadJson();
        ListAdapter listAdapter =  new SimpleAdapter(
                this,
                data,
                R.layout.row_items,
                new String[] {"name", "id"},
                new int[]{R.id.tv_c_name, R.id.tv_c_id}
        );
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText((Context) Catagory.this,  catagories.get(i).get("id"), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Catagory.this,
                        ItemViewDetail.class);

                intent.putExtra("catagory_id",catagories.get(i).get("id"));
                intent.putExtra("session_username",session_username);
                intent.putExtra("session_id",session_id);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("store_name",store);

                //Intent is used to switch from one activity to another.
                startActivity(intent);
            }
        });
    }


    private ArrayList<HashMap<String, String>> loadJson()
    {
        try {
            InputStream inputStream = getAssets().open("catagory.json");
            int size = inputStream.available();
            byte [] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json;
            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            int max = jsonArray.length();
            max_size = max;
            for (int i=0;i<max;i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                catagory_name = jsonObject.getString("catagory_Name");
                catagory_id = jsonObject.getString("catagory_code");
                HashMap<String, String> cust_names = new HashMap<>();
                cust_names.put("name", catagory_name);
                cust_names.put("id",catagory_id);
                catagories.add(cust_names);
            }
            return  catagories;
        }
        catch (Exception e)
        {
            Log.d("TAG", "loadJson err "+e);
        }
        return null;
    }



}