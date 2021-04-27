package com.example.webandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private TextView cityName,stateName,temperature_id,weatherType,loadingsign;
    private ImageView weather_Image;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private List<News> news;
    private Toolbar toolbar;
    //private String city;
    private static String JSON_URL="https://myandroidback.wl.r.appspot.com/guardian";
    private Adapter adapter;


    class Weather extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();

                //to retrieve data from url

                InputStream is=connection.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                int data=isr.read();
                String context="";
                char ch;
                while(data!=-1)
                {
                    ch=(char) data;
                    context=context+ch;
                    data=isr.read();
                }
                return context;

            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View home_fragment= inflater.inflate(R.layout.home_fragment,container,false);
        cityName=home_fragment.findViewById(R.id.cityName);
        stateName=home_fragment.findViewById(R.id.stateName);
        weatherType=home_fragment.findViewById(R.id.weatherType);
        temperature_id=home_fragment.findViewById(R.id.temperature_id);
        weather_Image=home_fragment.findViewById(R.id.weather_Image);
        loadingsign=home_fragment.findViewById(R.id.loadingtext);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            getLocation();
        }
        else
        {
            //ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        swipeRefreshLayout=home_fragment.findViewById(R.id.swipeRefresh);
        progressBar=home_fragment.findViewById(R.id.loader);
        recyclerView = home_fragment.findViewById(R.id.newsList);
        toolbar=home_fragment.findViewById(R.id.main_toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        news = new ArrayList<>();
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                extractNews();
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                extractNews();
                getLocation();

            }
        });

        extractNews();



        return home_fragment;
    }//on create ends




    private void getLocation() {
        final LocationRequest  locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getContext()).removeLocationUpdates(this);
                if(locationResult!=null && locationResult.getLocations().size()>0){
                    int latestLocationIndex=locationResult.getLocations().size()-1;
                   // double latitude=locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    //double longitude=locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        double latitude=34.0522;
                        double longitude=-118.2437;

//                    cityName.setText(String.valueOf(latitude));
//                    stateName.setText(String.valueOf(longitude));

                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses  = geocoder.getFromLocation(latitude,longitude, 1);
//                        String ans="Lat:"+addresses.get(0).getLatitude()+"\n"+"Lon:"+ addresses.get(0).getLongitude()+"\n"+"Country:"+
//                                addresses.get(0).getCountryName();
                        Address obj=addresses.get(0);
                        String city=obj.getAddressLine(0);
                        city=obj.getLocality();
                        String  state=obj.getAddressLine(0);
                        state=obj.getAdminArea();
                        cityName.setText(city);
                        stateName.setText(state);

                        String content;
                        Weather weather=new Weather();
                        try {
//            content=weather.execute("http://openweathermap.org/data/2.5/weather?q="+
//                    city+"&appid=d6f7b94a400ec6335fcbceb7784862c7").get();
                            content=weather.execute("http://openweathermap.org/data/2.5/weather?q="+
                                    city+"&appid=439d4b804bc8187953eb36d2a8c26a02").get();
                            Log.i("contentData",content);
                            Log.i("City",city);

                            JSONObject jsonObject=new JSONObject(content);
                            String weatherData=jsonObject.getString("weather");
                            String mainTemperature=jsonObject.getString("main");
                            Log.i("maintemp",mainTemperature);

                            //weather array mai hai so


                            JSONArray jsonArray=new JSONArray(weatherData);
                            String main="";
                            String description="";
                            String temperature="";


                            for(int i=0;i< jsonArray.length();i++)
                            {
                                JSONObject weatherPart=jsonArray.getJSONObject(i);
                                main=weatherPart.getString("main");
                                description=weatherPart.getString("description");
                            }
                            JSONObject mainPart=new JSONObject(mainTemperature);
                            temperature=String.valueOf(Math.round(mainPart.getDouble("temp")));

                            temperature_id.setText(temperature+"°C");
                            Log.i("main weather type",main);
                            weatherType.setText(main);
                            if(main.equals("Clear")) {
                                Glide.with(getContext().getApplicationContext()).
                                        load("https://csci571.com/hw/hw9/images/android/clear_weather.jpg").
                                        into(weather_Image);
                            }
                            else if(main.equals("Clouds"))
                            {
                                Glide.with(getContext().getApplicationContext()).
                                        load("https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg").
                                        into(weather_Image);
                            }
                            else if(main.equals("Snow"))
                            {
                                Glide.with(getContext().getApplicationContext()).
                                        load("https://csci571.com/hw/hw9/images/android/snowy_weather.jpeg").
                                        into(weather_Image);
                            }
                            else if(main.equals("Rain") || main.equals("Drizzle"))
                            {
                                Glide.with(getContext().getApplicationContext()).
                                        load("https://csci571.com/hw/hw9/images/android/rainy_weather.jpg").
                                        into(weather_Image);
                            }
                            else if(main.equals("Thunderstorm"))
                            {
                                Glide.with(getContext().getApplicationContext()).
                                        load("https://csci571.com/hw/hw9/images/android/thunder_weather.jpg").
                                        into(weather_Image);
                            }
                            else
                            {
                                Glide.with(getContext().getApplicationContext()).
                                        load("https://csci571.com/hw/hw9/images/android/sunny_weather.jpg").
                                        into(weather_Image);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, Looper.getMainLooper());
    }//getlocation ends



    private void extractNews() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        RequestQueue queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < 10; i++) {
                    try {
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadingsign.setVisibility(View.GONE);
                        JSONObject newsObject = response.getJSONObject(i);
                        News news_obj = new News();
                        // String pr=newsObject.getString("title");
                        //Log.i("Title",pr);

                        news_obj.setN_title(newsObject.getString("title").toString());
                        news_obj.setN_image(newsObject.getString("image"));
                        news_obj.setN_section(newsObject.getString("sectionName").toString());
                        news_obj.setN_date(newsObject.getString("date").toString());
                        news_obj.setN_desc(newsObject.getString("description").toString());
                        news.add(news_obj);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                adapter=new Adapter(getActivity().getApplicationContext(),news);
                recyclerView.setAdapter(adapter);



                // adapter.notifyDataSetChanged();
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag","onErrorResponse:"+error.getMessage());
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);
    }//extractnews ends


}
