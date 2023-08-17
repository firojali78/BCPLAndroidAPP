package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserPage extends AppCompatActivity {
    TextView t1;
    boolean isFitToScreen;

    SearchView searchView;
    ListView lv;
    int max_size;
    String customer_name, customer_id;
    ArrayList<HashMap<String, String>> c_names;
    ArrayAdapter<String> arrayAdapter;

    String session_id = "";
    String session_username = "";

    //return orderid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        t1 = findViewById(R.id.tv1_userpage);
        lv = findViewById(R.id.lv_customer);
        searchView = findViewById(R.id.search_user);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            session_username = extras.getString("session_username");
            t1.setText("Hello " + session_username);
            session_id = extras.getString("session_id");

            for (String key: extras.keySet())
            {
                System.out.println( "Keys in bundles in UserPage "+key + " "+extras.getString(key) );
            }
        }


        c_names = new ArrayList<>();
        try {
            c_names = say(session_id);
        } catch (AuthFailureError e) {
            e.printStackTrace();
        }


        //System.out.println(data.toString()+" Hi");
        String[] array_name = new String[c_names.size()];




        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(UserPage.this, Store_Name.class);
                intent.putExtra("customer_id", c_names.get(i).get("id"));
                intent.putExtra("session_id", customer_id);
                intent.putExtra("session_username", session_username);


                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                UserPage.this.arrayAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                UserPage.this.arrayAdapter.getFilter().filter(s);
                return false;
            }
        });


    }



    private ArrayList<HashMap<String, String>> say(String username) throws AuthFailureError {

        final String[] res = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://49.249.232.210:6262/webwebuserexportdp?UserID="+username;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String json;
                        json = response;
                        System.out.println("Response from UserPage "+response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(json);
                            int max = jsonArray.length();
                            max_size = max;
                            for (int i = 0; i < max; i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                customer_name = jsonObject.getString("Description");
                                customer_id = jsonObject.getString("No");
                               // System.out.println(customer_id + " customer_id " + i);
                             //   System.out.println(customer_name + " customer_name " + i);
                                HashMap<String, String> cust_names = new HashMap<>();
                                cust_names.put("name", customer_name);
                                cust_names.put("id", customer_id);
                                c_names.add(cust_names);
                            }
                                ArrayList<HashMap<String, String>> data = null;
                                data = c_names;
                           //     System.out.println(c_names.toString()+" c_names");
                                String[] array_name = new String[data.size()];
                               // arrayAdapter.clear();
                                for (int ii = 0; ii < array_name.length; ii++)
                                {
                                    array_name[ii] = c_names.get(ii).get("name");
                                //    System.out.println(array_name[ii] +" array_name ");

                                }
                            arrayAdapter = new ArrayAdapter<String>(UserPage.this, android.R.layout.simple_list_item_1, array_name);
                            lv.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();


                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()+" Error in syncing user page");

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return c_names;
    }

    @Override
    protected void onStart() {

        super.onStart();
    }
}