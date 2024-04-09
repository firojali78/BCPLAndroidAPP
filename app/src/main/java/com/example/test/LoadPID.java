package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoadPID extends AppCompatActivity {
    String session_id = "";
    String session_username = "";
    String pid="";
    String customer_id = "";
    String store = "";
    String json = "";
    String categoryid = "";
    EditText e1,e2,e3,e4,e5,e6;
    Button b1;
    TextView t_name;
    View iv1;
    ProgressBar pb;

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pid);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        init_layout();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            session_username = extras.getString("session_username");
            session_id = extras.getString("session_id");
            pid = extras.getString("pid");
            customer_id = extras.getString("customer_id");
            store = extras.getString("store_name");

            for (String key: extras.keySet())
            {
                System.out.println( "Keys in bundles in LOADPID "+key + " "+extras.getString(key) );
            }
        }
        int index =0;
        try {
            say(pid,new VolleyCallback(){
                @Override
                public void onSuccess(String result){
                    System.out.println("After callback  LOADPID "+result);
                    json = result;
                    loadJson();
                }
            });
        } catch (AuthFailureError e) {
            throw new RuntimeException(e);
        }
        System.out.println("Starting activity with json value "+json);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                int arr [] = {s,m,l,xl,xxl,xxxl};
                for(int i:arr)
                    System.out.println(i);
                try {
                    //TODO validate stock on submit
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(json);
                    String stock = jsonObject.getString("Stock");
                    String size = jsonObject.getString("Item_Size");
                    System.out.println("Debugging bc "+stock + " "+size);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if(maxConsecutiveOnes(arr)<0)
                {
                    System.out.println("Non Eligible with Maximum Non Zero "+ maxConsecutiveOnes(arr));
                    Toast.makeText(LoadPID.this, "Please enter atleast one size to submit", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        hitsubmit(pid,s,m,l,xl,xxl,xxxl,categoryid);
                    } catch (AuthFailureError e) {
                        throw new RuntimeException(e);
                    }
                    //    Toast.makeText(ItemViewDetail.this, "Data Updated for PID "+ pid, Toast.LENGTH_SHORT).show();
                }}
        });
    }


    private void init_layout() {
        e1  = findViewById(R.id.et_s_loadpid);
        e2 = findViewById(R.id.et_m_loadpid);
        e3 = findViewById(R.id.et_l_loadpid);
        e4 = findViewById(R.id.et_xl_loadpid);
        e5 = findViewById(R.id.et_xxl_loadpid);
        e6 = findViewById(R.id.et_xxxl_loadpid);
        b1 = findViewById(R.id.btn_submit_loadpid);
        iv1 = findViewById(R.id.p_images_loadpid);
        t_name = findViewById(R.id.p_name_loadpid);
        pb = findViewById(R.id.p_progress_bar_loadpid);
    }
    private void reset_editText() {
        e1.setText("");
        e2.setText("");
        e3.setText("");
        e4.setText("");
        e5.setText("");
        e6.setText("");
        e1.setHintTextColor(Color.GREEN);
        e2.setHintTextColor(Color.GREEN);
        e3.setHintTextColor(Color.GREEN);
        e4.setHintTextColor(Color.GREEN);
        e5.setHintTextColor(Color.GREEN);
        e6.setHintTextColor(Color.GREEN);
    }
    private void setView(String name, String url)
    {
        e1.setEnabled(false);
        e2.setEnabled(false);
        e3.setEnabled(false);
        e4.setEnabled(false);
        e5.setEnabled(false);
        e6.setEnabled(false);
        e1.setHintTextColor(Color.GREEN);
        e2.setHintTextColor(Color.GREEN);
        e3.setHintTextColor(Color.GREEN);
        e4.setHintTextColor(Color.GREEN);
        e5.setHintTextColor(Color.GREEN);
        e6.setHintTextColor(Color.GREEN);
        System.out.println("Printing for debugging name "+name+", url "+url +" pid ");

        t_name.setText(name);
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
    private JSONObject loadJson()
    {
        JSONObject jsonObject =null;
        try {
            System.out.println("calling load "+ json);
            jsonObject = new JSONObject(json);

            String name, url,pid;


            try {
                name = jsonObject.getString("Name");
                url = jsonObject.getString("Image_URL");
                pid = jsonObject.getString("Item_No");
                categoryid = jsonObject.getString("Catagory_Code");

                setView(name+"\n"+pid,url);
                enableEditText(jsonObject);

            } catch (JSONException e) {
                System.out.println("Error in fetching items "+ e.toString());
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            Log.d("TAG", "loadJson err "+e);
            e.printStackTrace();
        }
        return jsonObject;

    }
    private void enableEditText(JSONObject jsonObject) throws JSONException {
        String size, stock;
        size = jsonObject.getString("Item_Size");
        try {
            stock = jsonObject.getString("Stock");
        }
        catch (Exception e)
        {
            stock ="";
        }

        String size_arr[] = size.split(",");
        String stock_arr[] = stock.split(",");
        for(int i =0;i<size_arr.length;i++)
        {
            System.out.println("Size available is "+size_arr[i] + " and stock is "+stock_arr[i]);
            if(size_arr[i].equals("S"))
            {
                e1.setEnabled(true);
                try {
                    if(stock_arr[i].equals("0"))
                    {
                        // e1.setBackgroundColor(Color.RED);
                        e1.setHintTextColor(Color.RED);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Error","No value in Stock");
                }

            }
            if(size_arr[i].equals("M"))
            {
                e2.setEnabled(true);
                try {
                    if(stock_arr[i].equals("0"))
                    {
                        e2.setHintTextColor(Color.RED);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Error","No value in Stock");
                }
            }
            if(size_arr[i].equals("L"))
            {
                e3.setEnabled(true);
                try {
                    if(stock_arr[i].equals("0"))
                    {
                        e3.setHintTextColor(Color.RED);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Error","No value in Stock");
                }
            }
            if(size_arr[i].equals("XL"))
            {
                e4.setEnabled(true);
                try {
                    if(stock_arr[i].equals("0"))
                    {
                        e4.setHintTextColor(Color.RED);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Error","No value in Stock");
                }
            }
            if(size_arr[i].equals("XXL"))
            {
                e5.setEnabled(true);
                try {
                    if(stock_arr[i].equals("0"))
                    {
                        e5.setHintTextColor(Color.RED);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Error","No value in Stock");
                }
            }
            if(size_arr[i].equals("XXXL"))
            {
                e6.setEnabled(true);
                try {
                    if(stock_arr[i].equals("0"))
                    {
                        e6.setHintTextColor(Color.RED);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Error","No value in Stock");
                }
            }

        }
    }

    private void hitsubmit(String pid,int s, int m, int l, int xl, int xxl, int xxxl, String category_id) throws AuthFailureError {
        String url = "http://49.249.232.210:6262/webCreateSOMobileApp?&CustomerNo="+session_id.toString()+"&ItemNo="+pid.toString()+"&ItemSize=0,"+s+","+m+","+l+","+xl+","+xxl+","+xxxl+"&Remark=&DocumentNo=&BillToCustomer=&SellToCustomer=&UserID="+session_username+"&StoreName="+store+"&LocationCode=&ItemCategoryCode="+category_id;
        url = url.replace(" ", "%20");
        System.out.println("url request "+url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                response = response.replaceAll("\\\\","");
                System.out.println("Response on submit button LOADPID "+ response);

                if(!response.contains("Success\":\"False"))
                {
                    reset_editText();
                    finishActivity(0);
                }
                else
                {
                    System.out.println("Not in IF LOADPID");
                }
                int f = response.indexOf(",\"Message\":");
                String res = response.substring(f+12, response.length()-4);
                System.out.println("Res "+res);
                Toast.makeText(LoadPID.this, res, Toast.LENGTH_SHORT).show();
                e1.setText("");
                e2.setText("");
                e3.setText("");
                e4.setText("");
                e5.setText("");
                e6.setText("");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoadPID.this, error.toString(), Toast.LENGTH_SHORT).show();
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

                System.out.println(params.toString()+"Payload for LOADPID");

                return params;
            }

        };
        Volley.newRequestQueue(this).add(request);
        Log.i("LOADPID", "Request body: " + new String(request.getBodyContentType() +"--~--"+ new String(request.getHeaders().toString()))+"--~--"+ new String(request.getBody()));

    }

    private String say(String pid, final LoadPID.VolleyCallback callback) throws AuthFailureError {

        final String[] res = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // String url = "http://49.249.232.210:6262/webitemmasterapp?catagory_code="+category_code.trim();
        String url = "http://49.249.232.210:6262/webItemMasterAppWithStockItemWise?ItemNo="+pid.trim();
        System.out.println("Printing connection URL "+url+" .");

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("Response from Fetch API itemviewdetail "+response);
                        //  response = response.replaceAll("\\\\","");

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
    public int maxConsecutiveOnes(int[] nums) {

        int maxvalue=0, count=0;
        for(int i:nums)
            maxvalue = Math.max(maxvalue, count= i!=0 ? count+1: 0);


        return maxvalue;
    }
}