package com.example.webandroid;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchableActivity extends AppCompatActivity {

    private static final String JSON_URL ="https://myandroidback.wl.r.appspot.com/guardian/search?finderid=" ;

    List<News> news;
    RecyclerView recyclerView;
    Adapter adapter;
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sports_fragment);
        toolbar=findViewById(R.id.myToolBar);
        progressBar=findViewById(R.id.loader);
        Intent intent=getIntent();
        String myquery=intent.getStringExtra("myquery");
        Log.i("In searchable activity",myquery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search results for "+ myquery);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoadJSON(myquery);
        recyclerView= findViewById(R.id.newsList);


        news = new ArrayList<>();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_menu,menu);
//        SearchManager searchManager=(SearchManager) getSystemService(SEARCH_SERVICE);
//        final SearchView searchView=(SearchView) menu.findItem(R.id.action_search).getActionView();
//        MenuItem menuItem=menu.findItem(R.id.action_search);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setQueryHint("Search Latest News...");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if(query.length()>2){
//                    LoadJSON(query);
//                }
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        searchView.setIconifiedByDefault(false);
//        //return super.onCreateOptionsMenu(menu);
//        return true;
//    }


    private void LoadJSON(final String query) {
        //recyclerView.setVisibility(View.GONE);
        RequestQueue queue= Volley.newRequestQueue(SearchableActivity.this);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, JSON_URL+query, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("whole string",JSON_URL+query);
                for (int i = 0; i < 10; i++) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        //recyclerView.setVisibility(View.VISIBLE);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchableActivity.this));
                adapter=new Adapter(SearchableActivity.this,news);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

}
