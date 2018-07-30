package lambtoncollege.com.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static lambtoncollege.com.weatherapp.ProfileActivity.PROFILE_PREFF;

//this is the main class  where user enters the postal code which first checked that it is valid or not then sends
//the request to get the current weather. it also implements the navigation drawer which shows to navigate to profile page and payment page

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String Auth_key = "5927335b06c4d2d356d4adeba3889c03";  //Auth key for the api request
    TextView tempText,mintemp,maxtemp,descEd;
    EditText postalCodeEd;
    ProgressBar progressBar;
    SharedPreferences preferences;
    String req = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Perfect Weather");
        tempText = (TextView) findViewById(R.id.tempText);
        mintemp = (TextView) findViewById(R.id.max);
        maxtemp = (TextView) findViewById(R.id.min);
        descEd = (TextView) findViewById(R.id.desc);
        postalCodeEd = (EditText) findViewById(R.id.postalcode);
        preferences = getSharedPreferences(PROFILE_PREFF,MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final Button search =  (Button) findViewById(R.id.button);
        postalCodeEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()==6){
                    search.setVisibility(View.VISIBLE);

                }else {
                    search.setVisibility(View.GONE);
                }

                if (charSequence.length() == 3){
                    req = charSequence+"";
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String postalCode = postalCodeEd.getText().toString();
                char[] digs = postalCode.toCharArray();
               if (checkPostalCode(postalCode)){
                   progressBar.setVisibility(View.VISIBLE);
                   String upToNCharacters = postalCode.substring(0, Math.min(postalCode.length(), 3));
                   makeJsonObjReq(upToNCharacters);
                   Log.d("check",upToNCharacters);
               }else {
                   postalCodeEd.setError("Wrong value");
                   progressBar.setVisibility(View.GONE);
               }


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        TextView navUsername = hView.findViewById(R.id.username);
        ImageView imageView = hView.findViewById(R.id.imageView);
        navUsername.setText(preferences.getString("firstName","")+" "+preferences.getString("lastName",""));
        if (preferences.getString("profilePic","").equals("")){


        }else {
            imageView.setImageBitmap(decodeBase64(preferences.getString("profilePic","")));
        }

    }

    boolean flag =false;

    // this method checks the postal code entered that it is valid or not.

    boolean checkPostalCode(String string){

        char[] letters = string.toCharArray(); //string is converted into char array like Sachin becomes [S,a,c,h,i,n]

        for (int i = 0;i<letters.length;i++){ //loop through the length of array letters[]
            Log.d("Letters",letters[i]+"...");

            if (i%2==0){    // checking odd places and mathching them for alphabets below
                String regexStr = "[a-zA-Z]";
                if (String.valueOf(letters[i]).matches(regexStr)){
                    Log.d("Test match","Mathched..."+letters[i]);
                    flag = true;

                }else {
                    Log.d("Test match","Not Mathched"+letters[i]);
                    flag = false;
                    break;
                }
                Log.d("Even","..."+letters[i]+"....."+i);
            }else {// checking even places and mathching them for numbers below
                String regex = "\\d+";

            if (String.valueOf(letters[i]).matches(regex)){
                Log.d("Test match","Mathched..."+letters[i]);
                flag = true;

            }else {
                Log.d("Test match","Not Mathched"+letters[i]);
                flag = false;
                break;
            }
            }

        }


        return flag; //result is shown by this boolean flag if true code valid if not invalid

    }


    //this method decodes the base64 string into Bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) { //profile click in navigation drawer

            startActivity(new Intent(MainActivity.this,ProfileActivity.class)); // going to ProfileActivity
        } else if (id == R.id.payment) {
            startActivity(new Intent(MainActivity.this,PaymentActivity.class));// going to PaymentActivity

        } else if (id == R.id.premium) {
            startActivity(new Intent(MainActivity.this,PremiumFeatures.class));// going to PremiumFeatures

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    // this method requests the api with postal code and returns the response.
    private void makeJsonObjReq(final String postalcode) {

        String url = "http://api.openweathermap.org/data/2.5/weather?zip="+postalcode+",ca";// url on which request is done


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, // get request to fetch data
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {// here we get the response after request
                        Log.d("Response", response.toString());

                        try {
                            JSONObject main = response.getJSONObject("main");
// parsing the json
                            double tempr = main.getInt("temp");
                            String temp  = String.valueOf(tempr - 273);
                            double temp_min = main.getInt("temp_min");
                            String min  = String.valueOf(temp_min - 273);
                            double temp_max = main.getInt("temp_max");
                            String max  = String.valueOf(temp_max - 273);

                            tempText.setText(temp+" \u00b0C");
                            mintemp.setText("Min "+min+" \u00b0C"); // setting the temprature values
                            maxtemp.setText("Max "+max+" \u00b0C");// setting the temprature values

                            progressBar.setVisibility(View.GONE);

                            JSONArray weather = response.getJSONArray("weather");
                            for (int i = 0;i < weather.length();i++){
                                JSONObject object = weather.getJSONObject(i);
                                descEd.setText(object.getString("main"));  //
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override // if any error comes its caught here
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Log.d("error", error.toString());
                postalCodeEd.setError("Invalid Postal Code");
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                headers.put("x-api-key", Auth_key);// we have to pass header to authenticate the request
                return headers;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }
}
