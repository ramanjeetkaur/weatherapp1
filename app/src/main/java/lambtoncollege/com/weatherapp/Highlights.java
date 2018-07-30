package lambtoncollege.com.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//this is highlights page it creates the list and show here in this page
public class Highlights extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private NewsModel model;
    private ArrayList<NewsModel> list = new ArrayList<>();
    MyAdapter mAdapter;
    String apiKey = "3c8cf96c12ed46f9b1e590339b534b6c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlights);
        getSupportActionBar().setTitle("Highlights");
        mRecyclerview = findViewById(R.id.Reclist);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MyAdapter(this);
        mRecyclerview.setAdapter(mAdapter);//setting the adapter to the list
        mRecyclerview.setHasFixedSize(false);
        mRecyclerview.setLayoutManager(linearLayoutManager);
//        createData();

        makeJsonObjReq();
    }


    //this is creating dummy data to show in that list

    void createData() {
        NewsModel model = new NewsModel();
        model.setTitle("U.S. child migrant policy");
        model.setDescription("Prime Minister Justin Trudeau calls U.S. child migrant policy 'wrong'");
        list.add(model);
        NewsModel model1 = new NewsModel();
        model1.setTitle("Man accused in brazen attack.");
        model1.setDescription("Police charge man accused in brazen attack on woman caught on video");
        list.add(model1);
        NewsModel model2 = new NewsModel();
        model2.setTitle("Canada's cannabis law");
        model2.setDescription("Canada's cannabis law makes headlines worldwide");
        list.add(model2);
        NewsModel model3 = new NewsModel();
        model3.setTitle("Migrant children");
        model3.setDescription("Separating migrant children from parents risks long-term effects, doctors warn");
        list.add(model3);
        NewsModel model4 = new NewsModel();
        model4.setTitle("Funky hats, high fashion at Royal Ascot horse.");
        model4.setDescription("The Duke and Duchess of Sussex attend their first Royal Ascot horse race as a married couple.");
        list.add(model4);
    }


    private void makeJsonObjReq() {

        String url = "https://newsapi.org/v2/top-headlines?country=ca&category=health&apiKey=3c8cf96c12ed46f9b1e590339b534b6c";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, // get request to fetch data
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {// here we get the response after request
                        Log.d("Response", response.toString());

                        try {
                            JSONArray articles = response.getJSONArray("articles");
                            list.clear();
                            for (int i=0;i<articles.length();i++){
                                JSONObject obj = articles.getJSONObject(i);
                                Log.d("check desc",obj.getString("description"));
                                if (!obj.getString("description").equals("null")){

                                    NewsModel model = new NewsModel();
                                    model.setTitle(obj.getString("title"));
                                    model.setImage(obj.getString("urlToImage"));
                                    model.setDescription(obj.getString("description"));
                                    list.add(model);
                                }else {
                                    Log.d(" null desc"," not null");

                                }


                            }

                            mAdapter.setData((ArrayList<NewsModel>) list);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override // if any error comes its caught here
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Log.d("error", error.toString());

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

}