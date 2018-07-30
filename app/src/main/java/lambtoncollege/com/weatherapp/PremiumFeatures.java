package lambtoncollege.com.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PremiumFeatures extends AppCompatActivity {

    ImageView imageView;
    TextView myLocation;
    LinearLayout detect;
    private static final int CONTENT_REQUEST=1337;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ProgressBar detectProgress;
    ProgressBar weeklogProgress;
    ProgressBar progressBar;
   static Location myLoc;
    TextView tempText,mintemp,maxtemp,descEd;
    String Auth_key = "5927335b06c4d2d356d4adeba3889c03";
    View tempContainter;
    TextView day1,day2,day3,day4,day5,day6,day7;
    TextView day1Temp,day2Temp,day3Temp,day4Temp,day5Temp,day6Temp,day7Temp;
    View weekLog;
    boolean isWeekLogVisible = false;
    ImageView weeklogIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_features);
        getSupportActionBar().setTitle("Premium Features");
        imageView = findViewById(R.id.image);
        myLocation = findViewById(R.id.myLocation);
        detect = findViewById(R.id.detect);
        detectProgress = findViewById(R.id.detectProgress);
        weeklogProgress = findViewById(R.id.weeklogProgress);
        weeklogIcon = findViewById(R.id.weeklogIcon);
        weekLog = findViewById(R.id.weekLog);
        tempText = (TextView) findViewById(R.id.tempText);
        mintemp = (TextView) findViewById(R.id.max);
        maxtemp = (TextView) findViewById(R.id.min);
        descEd = (TextView) findViewById(R.id.desc);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tempContainter = findViewById(R.id.tempContainter);
        day1 = findViewById(R.id.day1);
        day1Temp = findViewById(R.id.day1Temp);
        day2 = findViewById(R.id.day2);
        day2Temp = findViewById(R.id.day2Temp);
        day3 = findViewById(R.id.day3);
        day3Temp = findViewById(R.id.day3Temp);
        day4 = findViewById(R.id.day4);
        day4Temp = findViewById(R.id.day4Temp);
        day5 = findViewById(R.id.day5);
        day5Temp = findViewById(R.id.day5Temp);
        day6 = findViewById(R.id.day6);
        day6Temp = findViewById(R.id.day6Temp);
        day7 = findViewById(R.id.day7);
        day7Temp = findViewById(R.id.day7Temp);

        //when clicked on check week log
       View checkWeekLog = findViewById(R.id.checkWeekLog);
       checkWeekLog.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if (isWeekLogVisible){ //if week view is visible it hides the weekview
                   weekLog.setVisibility(View.GONE);
                   isWeekLogVisible = false;
                   weeklogIcon.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
               }else {//if not visible is show the week view
                   makeWeekLogJsonObjReq(myLoc);
                   isWeekLogVisible = true;
                   weeklogIcon.setImageResource(R.drawable.arrow_up);

               }
           }
       });



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        checkLocationPermission();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CONTENT_REQUEST);
                }
            }

        });

        //when click on the detect button it calls the method placesRequest(myLoc); which passed argument the current location
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myLoc !=  null){
                    placesRequest(myLoc);
                }
            }
        });
        //when clicked on highlights button it opens highlight page
        findViewById(R.id.highlights).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PremiumFeatures.this,Highlights.class));
            }
        });


    }

    // convert milliseconds into the day of the week string
    public static String dayStringFormat(long msecs) {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(new Date(msecs));

        int dow = cal.get(Calendar.DAY_OF_WEEK);

        switch (dow) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
        }

        return "Unknown";
    }


    @Override//again camera feature when camera window closes it returns the image
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (requestCode == CONTENT_REQUEST && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");//here
                    imageView.setImageBitmap(imageBitmap);//here set the image to imageview
                    if (myLoc !=  null){
                        makeJsonObjReq(myLoc);//and here when image is set. this line send request the api for current weather according to
                        //the location
                    }


                }
            }
        }
    }

    //this method is checking if user has location permissio or not and if not then open a dialog to take location perission
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


                ActivityCompat.requestPermissions(PremiumFeatures.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);



            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            }
            return false;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listener);

            return true;
        }
    }

    @Override//this results from the above method if location permission was not there already this open a dialog and ask for
    // permission and sets the permission
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, listener);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listener);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    //this is the custom class which takes the location using LocationListener to get the current location of the user
    public class MyLocationListener implements LocationListener {


        public void onLocationChanged(final Location loc) {
            Log.i("*****************", "Location changed");
            Log.d("Location",loc.getLatitude()+"...."+loc.getLongitude());

            myLoc = loc;//here you get you location



        }

        public void onProviderDisabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


    }


    //this is request method which is called when you clicked on detect method to the current postal code from googleapis
    // which further request the weather apis to get the current weather.
    public void placesRequest(final Location location) {

        detectProgress.setVisibility(View.VISIBLE);

        String setLocation = location.getLatitude()+","+location.getLongitude();


        final String loc_places_url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+setLocation+"&key=AIzaSyCeufwmVrXfixFfMKu1bhSj4b9kwVT2cmE";
        Log.d("Request Url",loc_places_url);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                loc_places_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response", response.toString());


                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0;i<results.length();i++){
                                JSONObject obj = results.getJSONObject(0);
                                String formatted_address = obj.getString("formatted_address");

                                String placename = formatted_address.split(",")[0];
                            //here we get the place name now we will request weathter api to give us the current weather according ot this place
                                myLocation.setText(formatted_address);
                                detectProgress.setVisibility(View.GONE);
                                makeJsonObjReq(location);//here calling the other method to get weather info by passing location name
                                Log.d("Place name",placename);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());

                Log.d("error", error.toString());
            }
        }) {



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");

                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }


//this the method which takes the address from above method and returns the weather
    private void makeJsonObjReq(Location loc) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+"";
        setComponentVisibility(false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            JSONObject main = response.getJSONObject("main");

                            double tempr = main.getInt("temp");
                            String temp  = String.valueOf(tempr - 273);
                            double temp_min = main.getInt("temp_min");
                            String min  = String.valueOf(temp_min - 273);
                            double temp_max = main.getInt("temp_max");
                            String max  = String.valueOf(temp_max - 273);

                            tempText.setText(temp+" \u00b0C");
                            mintemp.setText("Min "+min+" \u00b0C");
                            maxtemp.setText("Max "+max+" \u00b0C");

                            setComponentVisibility(true);

                            Log.d("values",temp);
                            Log.d("values",min);
                            Log.d("values",max);

                            JSONArray weather = response.getJSONArray("weather");
                            for (int i = 0;i < weather.length();i++){
                                JSONObject object = weather.getJSONObject(i);
                                descEd.setText(object.getString("main"));
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Log.d("error", error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                headers.put("x-api-key", Auth_key);
                return headers;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }



    //this is the method which request the weather api and returns the weeks weather
    private void makeWeekLogJsonObjReq(Location loc) {
        weeklogProgress.setVisibility(View.VISIBLE);

        String url = "http://api.openweathermap.org/data/2.5/forecast?lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+"";
        setComponentVisibility(false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());

                        try {
                            JSONArray list = response.getJSONArray("list");
                            for (int i = 0; i < list.length(); i++) {


                            JSONObject obj = list.getJSONObject(i);
                            Long day = obj.getLong("dt");
                           String dayString = dayStringFormat(day*1000);


//setting the weeks weather by getting response above and setting below
                           Log.d("dayString",dayString);
                            JSONObject main = obj.getJSONObject("main");
                            double tempr = main.getInt("temp");
                            String temp = String.valueOf(tempr - 273);
                            day1Temp.setText(temp);
                            double temp_min = main.getInt("temp_min");
                            String min = String.valueOf(temp_min - 273);
                            double temp_max = main.getInt("temp_max");
                            String max = String.valueOf(temp_max - 273);

                            tempText.setText(temp + " \u00b0C");
                            mintemp.setText("Min " + min + " \u00b0C");
                            maxtemp.setText("Max " + max + " \u00b0C");
                                weeklogProgress.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                if (i==0){
                                    day1.setText("Today");
                                    day1Temp.setText(temp + " \u00b0C");
                                    Log.d("Check 0  Date",dayString+day);

                                }else if (i==1){
                                    day2.setText(dayString);
                                    day2Temp.setText(temp + " \u00b0C");
                                    Log.d("Check Date",dayString+day);
                                }else if (i==9){
                                    day3.setText(dayString);
                                    day3Temp.setText(temp + " \u00b0C");
                                    Log.d("Check Date",dayString+day);
                                }else if (i==17){
                                    day4.setText(dayString);
                                    day4Temp.setText(temp + " \u00b0C");
                                    Log.d("Check Date",dayString+day);
                                }else if (i==25){
                                    day5.setText(dayString);
                                    day5Temp.setText(temp + " \u00b0C");
                                    Log.d("Check Date",dayString+day);
                                }else if (i==33){
                                    day6.setText(dayString);
                                    day6Temp.setText(temp + " \u00b0C");
                                    Log.d("Check Date",dayString+day);
                                }
                            Log.d("values", temp);
                            Log.d("values", min);
                            Log.d("values", max);
                                weekLog.setVisibility(View.VISIBLE);
                        }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Log.d("error", error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                headers.put("x-api-key", Auth_key);
                return headers;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

//this is just helper method to show and hide components
    public void setComponentVisibility(boolean flag){
        if (flag){
            tempContainter.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else {
            tempContainter.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }




    }

}
