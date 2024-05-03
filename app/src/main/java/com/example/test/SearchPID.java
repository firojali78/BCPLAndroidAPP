package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPID extends AppCompatActivity {
    SearchView searchView;
    ListView lv;
    int max_size;
    ArrayList<HashMap<String, String>> c_names;
    ArrayList<HashMap<String, String>> c_id;
    ArrayAdapter<String> arrayAdapter;
    String session_id = "";
    String session_username = "";
    String customer_id;
    String store;
    String description, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pid);
        lv = findViewById(R.id.lv_pid);
        searchView = findViewById(R.id.search_pid);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            session_username = extras.getString("session_username");
            session_id = extras.getString("session_id");
            customer_id = extras.getString("customer_id");
            store = extras.getString("store_name");

            for (String key: extras.keySet())
            {
                System.out.println( "Keys in bundles in SearchPID "+key + " "+extras.getString(key) );
            }
        }
        c_names = new ArrayList<>();
        try {
            c_names = say(session_id);
        } catch (AuthFailureError e) {
            e.printStackTrace();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(SearchPID.this, LoadPID.class);
              //  intent.putExtra("pid", c_names.get(i).get("id"));
                intent.putExtra("pid", parent.getItemAtPosition(i).toString());
                intent.putExtra("session_id", session_id);
                intent.putExtra("session_username", session_username);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("store_name", store);
              //  System.out.println("Clicked index "+ i + " ID "+id+ " IDTEMP " + parent.getItemAtPosition(i).toString()+" Names "+ c_names.toString() );

                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchPID.this.arrayAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchPID.this.arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });

    }

    private ArrayList<HashMap<String, String>> say(String sessionId) throws AuthFailureError {

        final String[] res = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://49.249.232.210:6262/webItemMasterListAPI";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String json;
                        json = response;
                        System.out.println("Response from SearchPID "+response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(json);
                            int max = jsonArray.length();
                            max_size = max;
                            for (int i = 0; i < max; i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                description = jsonObject.getString("description");
                                no = jsonObject.getString("no");
                                //  System.out.println(customer_id + " customer_id " + i);
                                //    System.out.println(customer_name + " customer_name " + i);
                                HashMap<String, String> cust_names = new HashMap<>();
                                cust_names.put("name", description);
                                cust_names.put("id", no);
                                c_names.add(cust_names);
                            }
                            ArrayList<HashMap<String, String>> data = null;
                            data = c_names;
                            //     System.out.println(c_names.toString()+" c_names");
                            String[] array_name = new String[data.size()];
                            // arrayAdapter.clear();
                            for (int ii = 0; ii < array_name.length; ii++)
                            {
                                array_name[ii] = c_names.get(ii).get("id");
                                //    System.out.println(array_name[ii] +" array_name ");

                            }
                            arrayAdapter = new ArrayAdapter<String>(SearchPID.this, android.R.layout.simple_list_item_1, array_name);
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
                System.out.println(error.toString()+" Error in syncing ITEMS page");

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return c_names;
    }
}