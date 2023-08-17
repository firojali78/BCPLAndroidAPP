package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemViewDetail extends AppCompatActivity {
    EditText e1,e2,e3,e4,e5,e6;
    Button b1;
    View iv1;
    TextView t_name, t_page_count;
    SharedPreferences sharedPreferences;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    int max_size;
    int counter_items=1;
    int counter_items_total = 0;

    String session_id = "C1";
    String session_username = "Akash";
    String store="";
    String customer_id="";
    String category_id ="";
    String catagory_pid = "";
    String json="";
    String url_to_zoom="";
    ProgressBar pb;


    public interface VolleyCallback{
        void onSuccess(String result);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view_detail);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        init_layout();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            session_username = extras.getString("session_username");
            session_id = extras.getString("session_id");
            store = extras.getString("store_name");
            customer_id = extras.getString("customer_id");
            category_id = extras.getString("catagory_id");
            catagory_pid = extras.getString("catagory_pid");
            counter_items_total = Integer.parseInt(extras.getString("counter"));
            for (String key: extras.keySet())
            {
                System.out.println( "Keys in bundles in ItemDetails Page "+key + " "+extras.getString(key) );
            }


            //The key argument here must match that used in the other activity
        }


        SharedPreferences sh = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        int index = sh.getInt("json_val",0);


        try {
            say(catagory_pid,new VolleyCallback(){
                @Override
                public void onSuccess(String result){
                    System.out.println("After callback "+result);
                    json = result;
                    loadJson(index);
                }
            });
        } catch (AuthFailureError e) {
            throw new RuntimeException(e);
        }
        System.out.println("Starting activity with json value "+json);




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
//                else if(e3.getText().toString().equals("40"))
//                {
//                    Toast.makeText(ItemViewDetail.this, "Out of Stock", Toast.LENGTH_SHORT).show();
//                }
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
                try {
                    hitsubmit(pid,s,m,l,xl,xxl,xxxl);
                } catch (AuthFailureError e) {
                    throw new RuntimeException(e);
                }
            //    Toast.makeText(ItemViewDetail.this, "Data Updated for PID "+ pid, Toast.LENGTH_SHORT).show();
            }
        });

        /*
        iv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent(ItemViewDetail.this, com.example.test.ImageView.class);
                i.putExtra("url", url_to_zoom);
                System.out.println("Passing URL "+ url_to_zoom);
                startActivity(i);
                return false;
            }
        });*/


    }

    private void hitsubmit(String pid,int s, int m, int l, int xl, int xxl, int xxxl) throws AuthFailureError {
        String url = "http://49.249.232.210:6262/webCreateSOMobileApp?&CustomerNo="+session_id.toString()+"&ItemNo="+pid.toString()+"&ItemSize="+s+","+m+","+l+","+xl+","+xxl+","+xxxl+"&Remark=&DocumentNo=&BillToCustomer=&SellToCustomer=&UserID="+session_username+"&StoreName="+store+"&LocationCode=&ItemCategoryCode="+category_id;
        System.out.println("url request "+url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                response = response.replaceAll("\\\\","");
                System.out.println("Response on submit button ItemviewDetail "+ response);

                if(!response.contains("Success\":\"False"))
                {
                    reset_editText();
                }
                else
                {
                    System.out.println("Not in IF Itemviewdetails");
                }
                int f = response.indexOf(",\"Message\":");
                String res = response.substring(f+12, response.length()-3);
                System.out.println("Res "+res);
                Toast.makeText(ItemViewDetail.this, res, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ItemViewDetail.this, error.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
                Log.d("ItemViewDetail", "Error: " + error
                        + "\nStatus Code " + error.networkResponse.statusCode
                        + "\nResponse Data " + error.networkResponse.data
                        + "\nCause " + error.getCause()
                        + "\nmessage" + error.getMessage());

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // params.put("Content-Type", "application/json");
                params.put("Accept","*/*");
                params.put("Accept-Encoding","gzip, deflate, br");
                params.put("Connection","keep-alive");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return getParams().toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String sizes= s+","+m+","+l+","+xl+","+xxl+","+xxxl;
                Map<String,String>params=new HashMap<>();
                params.put("CustomerNo",session_id);
                params.put("ItemNo",pid);
                params.put("ItemSize", sizes);
                params.put("ItemCategoryCode", category_id);
                params.put("Remark", "");
                params.put("DocumentNo", "");
                params.put("BillToCustomer", "");
                params.put("SellToCustomer", "");
                params.put("UserID", session_id);
                params.put("StoreName", store);
                params.put("LocationCode", "");

                System.out.println(params.toString()+"Payload for Itemviewdetails");

                return params;
            }

        };
        Volley.newRequestQueue(this).add(request);
        Log.i("ItemViewDetail", "Request body: " + new String(request.getBodyContentType() +"--~--"+ new String(request.getHeaders().toString()))+"--~--"+ new String(request.getBody()));

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
        t_page_count.setText(counter_items+" / "+counter_items_total);
        iv1.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        Glide.with(this).load(url).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        iv1.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                   //     Intent i = new Intent(Intent.ACTION_VIEW);
                     //   i.setData(Uri.parse(url));
                       // startActivity(i);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        iv1.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        return false;
                    }
                })//.apply(new RequestOptions().override(150, 150))
                .error(R.drawable.ic_launcher_background).
                into((ImageView) iv1);
    }


    private JSONObject loadJson(int i)
    {
        JSONObject jsonObject =null;
        try {
            System.out.println("calling load "+ json);
            JSONArray jsonArray = new JSONArray(json);
            int max = jsonArray.length();
            max_size = max;
            sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("json_val", 0);
            edit.commit();
            String name, url,pid;
            jsonObject = jsonArray.getJSONObject(i);
            try {
                name = jsonObject.getString("Name");
                url = jsonObject.getString("Image_URL");
                pid = jsonObject.getString("Item_No");
                edit.putString("pid", pid);
                edit.commit();
                setView(name+"\n"+pid,url);
                url_to_zoom = url;
                enableEditText(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            Log.d("TAG", "loadJson err "+e);
        }
        return jsonObject;

    }

    private String say(String category_code, final VolleyCallback callback) throws AuthFailureError {

        final String[] res = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://49.249.232.210:6262/webitemmasterapp?catagory_code="+category_code.trim();
        System.out.println("Printing connection URL "+url+" .");

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("Response from Fetch API itemviewdetail "+response);
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()+" Please check your internet");

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        if(stringRequest.hasHadResponseDelivered()) {
            return res[0];
        }
        else {
            return null;
        }
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
        t_page_count = findViewById(R.id.p_counter);
        pb = findViewById(R.id.p_progress_bar);
    }

    private void reset_editText() {
        e1.setText("");
        e2.setText("");
        e3.setText("");
        e4.setText("");
        e5.setText("");
        e6.setText("");

    }

    private void enableEditText(JSONObject jsonObject) throws JSONException {
        String size;
        size = jsonObject.getString("Item_Size");
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
                        System.out.println("Current index "+sh.getInt("json_val",0));


                        int index = sh.getInt("json_val",0);

                        String name, url, pid="1";
                        if(index == 0)
                        {
                            Toast.makeText(this, "This is first item", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            counter_items--;
                            t_page_count.setText(counter_items+" / "+counter_items_total);

                            index = index - 1;

                            JSONObject jsonObject = loadJson(index);
                            try {
                                name = jsonObject.getString("Name");
                                url = jsonObject.getString("Image_URL");
                                pid = jsonObject.getString("Item_No");
                                setView(name+"\n"+pid, url);
                                url_to_zoom = url;
                                enableEditText(jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putInt("json_val", index);
                        edit.putString("pid", pid);
                        edit.commit();

                    }

                    // Right to left swipe action
                    else
                    {

                        SharedPreferences sh = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                        System.out.println("Current index "+sh.getInt("json_val",0));


                        int index = sh.getInt("json_val",0);

                        if(index == max_size-1)
                        {
                            Toast.makeText(this, "This is Last item", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            counter_items++;
                            t_page_count.setText(counter_items+" / "+counter_items_total);
                            index = index + 1;

                            String name, url, pid = "1";
                            JSONObject jsonObject = loadJson(index);
                            try {
                                name = jsonObject.getString("Name");
                                url = jsonObject.getString("Image_URL");
                                pid = jsonObject.getString("Item_No");
                                setView(name+"\n"+pid, url);
                                url_to_zoom = url;
                                enableEditText(jsonObject);

                            } catch (JSONException e) {
                                System.out.println("Error in "+ json +"with index = "+index);
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putInt("json_val", index);
                            edit.putString("pid", pid);
                            edit.commit();
                        }


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