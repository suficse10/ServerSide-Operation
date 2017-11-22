package bdjobs.lict.volleyexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bdjobs.lict.volleyexample.helper.ServerAddress;

public class MainActivity extends AppCompatActivity {

    EditText eTusername, eTemail, eTphone;
    Button submitBTN;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTusername = findViewById(R.id.editTextUsername);
        eTemail = findViewById(R.id.editTextEmail);
        eTphone = findViewById(R.id.editTextPhoneNo);
        submitBTN = findViewById(R.id.buttonSubmit);
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        //RequestQueue queue = Volley.newRequestQueue(this);
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        if (isNetworkAvailable()) {

            submitBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitData();
                }
            });

            getdata();

        } else {
            Toast.makeText(MainActivity.this, "Connect Your Internet Please", Toast.LENGTH_LONG).show();
        }
    }

    public void getdata() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ServerAddress.SNQ_GETDATA,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //txtDisplay.setText("Response = "+response.toString());
                        int length = response.length();
                        for (int i = 0; i < length; i++) {
                            try {
                                String name = response.getJSONObject(i).getString("name");
                                String email = response.getJSONObject(i).getString("email");
                                String phone = response.getJSONObject(i).getString("phone");
                                arrayList.add(name + "\n" + email + "\n" + phone);
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getCause().toString());
            }
        });
        queue.add(jsonArrayRequest);
    }

    public void submitData() {

        String user_str = eTusername.getText().toString().trim();
        //Log.i("UserName:", user_str);
        String email_str = eTemail.getText().toString().trim();
        //Log.i("Email:", email_str);
        String phone_str = eTphone.getText().toString().trim();
        //Log.i("PhoneNo:", phone_str);

        postMessage(user_str, email_str, phone_str);
    }

    private void postMessage(final String name, final String email, final String phone) {

        // Creating RequestQueue.
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAddress.SNQ_POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        requestQueue.getCache().clear();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Response Err", error.toString());
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
                Log.i("Params", params.toString());
                return params;
            }
        };

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
        Log.i("URL", stringRequest.toString());

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

