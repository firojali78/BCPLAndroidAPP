package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Catagory extends AppCompatActivity {
    TextView t1,t2;
    ListView lv;
    int max_size;
    String catagory_name="";
    String catagory_id="";
    int catagory_size=0;
    ArrayList<HashMap<String,String>> catagories;
    ArrayAdapter<String> arrayAdapter;

    String session_id = "C1";
    String session_username = "Akash";
    String store="";
    String customer_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);
        t1 = findViewById(R.id.tv_catagory_customer);
        t2 = findViewById(R.id.tvcategory);
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

            t1.setText("Hello "+session_username +" ("+ session_id+ " - "+ customer_id+ " )");

            //The key argument here must match that used in the other activity
            for (String key: extras.keySet())
            {
                System.out.println( "Keys in bundles in Catagory Page "+key + " "+extras.getString(key) );
            }
        }

        catagories = new ArrayList<>();
        try {
            catagories = say();
        } catch (AuthFailureError e) {
            e.printStackTrace();
        }
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Catagory.this, "Downloading", Toast.LENGTH_SHORT).show();
                fetchdatacsv(session_id, session_username, store);
            }
        });

        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Catagory.this,
                        SearchPID.class);

                intent.putExtra("session_username", session_username);
                intent.putExtra("session_id", session_id);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("store_name", store);
                startActivity(intent);

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(Integer.parseInt(catagories.get(i).get("size"))<=0)
                {
                    Toast.makeText(Catagory.this, "No items available in this category", Toast.LENGTH_SHORT).show();
                }
                else {


                   // Toast.makeText((Context) Catagory.this, catagories.get(i).get("id"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Catagory.this,
                            ItemViewDetail.class);

                    intent.putExtra("catagory_id", catagories.get(i).get("id"));
                    intent.putExtra("catagory_pid", catagories.get(i).get("name"));
                    intent.putExtra("session_username", session_username);
                    intent.putExtra("session_id", session_id);
                    intent.putExtra("customer_id", customer_id);
                    intent.putExtra("store_name", store);
                    intent.putExtra("counter", catagories.get(i).get("size"));

                    //Intent is used to switch from one activity to another.
                    startActivity(intent);
                }
            }
        });
    }

    private void fetchdatacsv(String sessionId, String session_Username, String store_name) {

            DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri;
            String filename;
            if(store_name.length() <1)
            {
                filename = sessionId;
            }
            else{
                filename = session_Username;
            }
            uri = Uri.parse("http://49.249.232.210:6262/getusercsv/"+filename);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(filename);
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            //request.setDestinationUri(Uri.parse("file://" + folderName + "/myfile.mp3"));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());

            downloadmanager.enqueue(request);
    }



    private ArrayList<HashMap<String, String>> say() throws AuthFailureError {

        final String[] res = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://49.249.232.210:6262/webitemcategoryapp";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String json;
                        json = response;
                        System.out.println("Response from Catagory "+response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(json);
                            int max = jsonArray.length();
                            max_size = max;
                            for (int i = 0; i < max; i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                catagory_name = jsonObject.getString("catagory_code");
                                catagory_id = jsonObject.getString("catagory_Name");
                                catagory_size = jsonObject.getInt("ItemCount");
                                //System.out.println(catagory_id + " category name " + i);
                             //   System.out.println(catagory_name + " category name " + i);
                                HashMap<String, String> cust_names = new HashMap<>();
                                cust_names.put("name", catagory_name);
                                cust_names.put("id", catagory_id);
                                cust_names.put("size", String.valueOf(catagory_size));
                                catagories.add(cust_names);
                            }
                            ArrayList<HashMap<String, String>> data = null;
                            data = catagories;
                          //  System.out.println(catagories.toString()+" c_category");
                            String[] array_name = new String[data.size()];
                            // arrayAdapter.clear();
                            for (int ii = 0; ii < array_name.length; ii++)
                            {
                                array_name[ii] = catagories.get(ii).get("name");
                                System.out.println(array_name[ii] +" array_name ");

                            }


                            ListAdapter listAdapter =  new SimpleAdapter(
                                    Catagory.this,
                                    data,
                                    R.layout.row_items,
                                    new String[] {"name", "id","size"},
                                    new int[]{R.id.tv_c_name, R.id.tv_c_id,R.id.tv_c_size}
                            );
                            lv.setAdapter(listAdapter);


                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()+" Error in HItting API");

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return catagories;
    }



}