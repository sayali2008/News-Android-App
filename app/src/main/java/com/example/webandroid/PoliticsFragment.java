package com.example.webandroid;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PoliticsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<News> news;
    private static String JSON_URL="https://myandroidback.wl.r.appspot.com/guardian/section/politics";
    private Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View world_fragment= inflater.inflate(R.layout.world_fragment,container,false);
        swipeRefreshLayout=world_fragment.findViewById(R.id.swipeRefresh);
        recyclerView = world_fragment.findViewById(R.id.newsList);
        progressBar=world_fragment.findViewById(R.id.loading_sign);
        news = new ArrayList<>();
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
                //getLocation();

            }
        });
        extractNews();
        return world_fragment;
        // v=inflater.inflate(R.layout.world_fragment,container,false);
        //return v;
    }

    private void extractNews() {
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
                        JSONObject newsObject = response.getJSONObject(i);
                        News news_obj = new News();
                        // String pr=newsObject.getString("title");
                        //Log.i("Title",pr);
                        news_obj.setN_title(newsObject.getString("title").toString());
                        news_obj.setN_image(newsObject.getString("image"));
                        news_obj.setN_section(newsObject.getString("sectionName").toString());
                        news_obj.setN_desc(newsObject.getString("description").toString());
                        news_obj.setN_date(newsObject.getString("date").toString());
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
    }
}
