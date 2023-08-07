package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this, tb.getText().toString().trim(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
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
}