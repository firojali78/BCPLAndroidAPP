package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    EditText e1,e2;
    Button b1;
    ToggleButton tb;
    TextView tv1;

    //String session_id = "C1";
    //String session_username = "Akash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStoragePermissionGranted();
        setContentView(R.layout.activity_login);
        e1 = findViewById(R.id.username);
        e2 = findViewById(R.id.password);
        b1 = findViewById(R.id.btn_login);
        tv1 = findViewById(R.id.tv1);
        tb = findViewById(R.id.login_toggle);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        SharedPreferences sharedPrefs = getSharedPreferences("com.example.test", MODE_PRIVATE);
        tb.setChecked(sharedPrefs.getBoolean("login_type", true));
      //  download_Images();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    say(e1.getText().toString().trim(),e2.getText().toString().trim(),tb.getText().toString().trim());
                } catch (AuthFailureError e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void say(String username, String password, String login_type) throws AuthFailureError {


        StringRequest request=new StringRequest(Request.Method.GET, "http://49.249.232.210:6262/webvalidatewebuserdp?UserID="+username+"&PWS="+password+"&LoginType="+login_type, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String session_username = "";
                String customer_code = "";
                System.out.println("Response from Login "+response);

                tv1.setText(response);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    if(obj.getString("Success").equals("False"))
                    {
                        Toast.makeText(Login.this, obj.getString("Message").toString(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        session_username = obj.getString("UserName");
                        customer_code = obj.getString("CustomerCode");
                        if(login_type.toLowerCase().equals("distributor"))
                        {
                            Intent i=new Intent(Login.this,
                                    Catagory.class);
                            i.putExtra("session_id",username);
                            i.putExtra("session_username",session_username);
                            i.putExtra("customer_code", customer_code);
                            i.putExtra("store_name","");
                            startActivity(i);
                        }
                        else
                        {
                            Intent i=new Intent(Login.this,
                                    UserPage.class);
                            i.putExtra("session_id",username);
                            i.putExtra("session_username",session_username);
                            i.putExtra("customer_code", customer_code);
                            //Intent is used to switch from one activity to another.
                            startActivity(i);
                        }

                    }
                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Server unavailable", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();

             Log.d("Login", "Error: " + error
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
                Map<String,String>params=new HashMap<>();
                params.put("UserID",username);
                params.put("PWS",password);
                params.put("LoginType", login_type);
               // System.out.println(params.toString());

                return params;
            }

        };
        Volley.newRequestQueue(this).add(request);
        Log.i("Login", "Request body: " + new String(request.getBodyContentType() +"--~--"+ new String(request.getHeaders().toString()))+"--~--"+ new String(request.getBody()));

    }




    @Override
    protected void onDestroy() {
        if (tb.isChecked())
        {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", MODE_PRIVATE).edit();
            editor.putBoolean("login_type", true);
            editor.commit();
        }
        else
        {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", MODE_PRIVATE).edit();
            editor.putBoolean("login_type", false);
            editor.commit();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (tb.isChecked())
        {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", MODE_PRIVATE).edit();
            editor.putBoolean("login_type", true);
            editor.commit();
        }
        else
        {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", MODE_PRIVATE).edit();
            editor.putBoolean("login_type", false);
            editor.commit();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (tb.isChecked())
        {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", MODE_PRIVATE).edit();
            editor.putBoolean("login_type", true);
            editor.commit();
        }
        else
        {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", MODE_PRIVATE).edit();
            editor.putBoolean("login_type", false);
            editor.commit();
        }
        super.onStop();
    }

    public boolean isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.v("LOG", "Permission granted");
                return true;
            } else {
                Log.v("LOG", "Permission revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else{
            Log.v("LOG", "Permission is granted");
            return true;
        }
    }

    public void download_Images()
    {
        ArrayList <String> imgUrl = new ArrayList<>();
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSCS-16010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16021.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16022.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16023.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16024.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16025.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16026.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16027.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16028.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16029.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16030.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16031.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16032.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16033.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16034.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16035.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16036.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16037.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16038.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16039.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16040.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16041.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16042.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16043.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16044.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/CAPRI_BSCS/BSCS-16045.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/BSL/BSL-19000.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/BSL/BSL-20000.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11014.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11015.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11016.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11022.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11032.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11033.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11050.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11051.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11052.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11053.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11053.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11055.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11056.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11057.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11058.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11059.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11060.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11061.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11062.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11063.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11064.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11065.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11066.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11067.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11068.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11069.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11070.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11071.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11072.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11073.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11074.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11075.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-11076.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12021.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12022.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12023.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12024.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12025.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12026.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12027.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12028.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12029.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12030.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12031.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12032.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12033.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12034.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12035.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12036.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12037.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-12038.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-13011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-14014.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15014.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15015.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15016.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/PYJAMA_SET_BSCS/BSLS-15018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21015.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21016.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21021.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21022.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21023.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21024.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21025.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21026.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21027.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21028.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21029.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/Fashion_BSLS/BSLS-21030.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10014.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10015.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10016.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-10019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-1029.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-1030.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-1031.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-1032.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-1033.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9014.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9015.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9016.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9021.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9022.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9023.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9024.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9025.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9026.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9027.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9028.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9029.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9030.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9031.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9032.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9033.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9034.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9035.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/NIGHTY_BSN/BSN-9036.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18010.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18011.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18012.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18013.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18014.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18015.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18016.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18017.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18018.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/COLLAR_NS_BSNS/BSNS-18021.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17001.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17002.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17003.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17004.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17005.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17006.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17007.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17008.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17009.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17019.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17020.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17021.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17022.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17023.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17024.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17025.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17026.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17027.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17028.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17029.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17030.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17031.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17032.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17033.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17034.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17035.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17036.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17037.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17038.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17501.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17502.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17503.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17504.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17505.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17506.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17507.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17508.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17509.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17510.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17511.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17512.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17513.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17514.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17515.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17516.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17517.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17518.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17519.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17520.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17521.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17522.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17523.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17524.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17525.jpg");
        imgUrl.add("https://www.bodycarecreations.com/MobileApp/SHORTS_BSSS/BSSS-17526.jpg");


        DownloadImagesTask downloadImagesTask = new DownloadImagesTask(Login.this);
        downloadImagesTask.execute(imgUrl);

    }


}