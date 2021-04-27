package com.example.webandroid;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String JSON_URL ="https://myandroidback.wl.r.appspot.com/guardian/search?finderid=" ;
    private static final String BING_URL="https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=";
    //private static String JSON_URL="http://starlord.hackerearth.com/studio";
//    RecyclerView recyclerView;
//    List<News> news;
//    private static String JSON_URL="http://10.0.2.2:9000/guardian";
//    Adapter adapter;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    List<News> news;
    RecyclerView recyclerView;
    Adapter adapter;
    Handler handler;
    Toolbar toolbar;
    private ArrayAdapter<String> newsAdapter;
    SearchView.SearchAutoComplete searchAutoComplete;
    AutoSuggestAdapter  autoSuggestAdapter;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    recyclerView=findViewById(R.id.newsList);
      // View v=LayoutInflater.from(MainActivity.this).inflate(R.layout.world_fragment,null);
        //LinearLayout linearLayout=(LinearLayout) v.findViewById(R.id.newsList);
        news = new ArrayList<>();

        toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NewsApp");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //finish();
                return true;
            }
        });
            bottomNavigationView=findViewById(R.id.bottomNav);
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();
        }
        //handleIntent();
        navigationBar();


    }

//    private void handleIntent(Intent intent) {
//        if(Intent.ACTION_SEARCH.equals(intent.getAction()))
//        {
//            String query=intent.getStringExtra(SearchManager.QUERY);
//        }
//
//    }

    private void navigationBar()
    {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment=null;
                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        fragment=new HomeFragment();
                        break;
                    case R.id.trending:
                        fragment=new TrendingFragment();
                        break;
                    case R.id.headlines:
                        fragment=new HeadlineFragment();
                        break;
                    case R.id.bookmark:
                        fragment=new BookmarkFragment();
                        break;
                }
              getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        SearchManager searchManager=(SearchManager) getSystemService(SEARCH_SERVICE);
        final SearchView searchView=(SearchView) menu.findItem(R.id.action_search).getActionView();
        final SearchView searchView=(SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
       final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        //searchAutoComplete.setDropDownAnchor(R.id.action_search);
        //ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        //searchAutoComplete.setAdapter(newsAdapter);
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
                //searchAutoComplete.setThreshold(2);
                searchAutoComplete.setAdapter(autoSuggestAdapter);
           searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                  searchAutoComplete.setText(autoSuggestAdapter.getObject(position));


               }

//                @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
//                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
//                searchAutoComplete.setText("" + queryString);
//                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
//            }
        });
       searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }


        });

    //searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(MainActivity.this,SearchableActivity.class)));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>3){
                    Intent intent=new Intent(MainActivity.this,SearchableActivity.class);
                    intent.putExtra("myquery",query);
                    startActivity(intent);
                    //LoadJSON(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                makeApiCall(newText);
                return true;
            }
        });
        searchView.setIconifiedByDefault(false);
        //searchView.setOnQueryTextListener(qu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }


    
    //
    private void makeApiCall(String text) {
       final String subscriptionKey ="96fe9716160f4b08bbc2c6ac7dc67bb7" ;
         String host = "https://api.cognitive.microsoft.com";
        String path = "/bing/v7.0/Suggestions";
        String mkt = "en-US";
        final String query = text;



//
//
//
//

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonArrayRequest=new JsonObjectRequest(Request.Method.GET, BING_URL+query, null, new Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("Bing res:",BING_URL+query);
                String suggest="";
                String suggestzero="";
                String displayT;
                List<String> stringList = new ArrayList<>();
                //String stringList[];
                Log.i("jsonobj response", String.valueOf(response));
                try {
                    JSONArray suggestG=response.getJSONArray("suggestionGroups");
                    JSONObject getz=suggestG.getJSONObject(0);
                    JSONArray suggestS=getz.getJSONArray("searchSuggestions");
                    for(int i=0;i<5;i++)
                    {
                        JSONObject dsPart=suggestS.getJSONObject(i);
                        stringList.add(dsPart.getString("displayText")) ;
                        //Log.i("Stringlist", String.valueOf(stringList));
                    }
                    Log.i("olist", String.valueOf(stringList));

                    newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,stringList);

                    //newsAdapter.addAll(stringList);
                    //searchAutoComplete.setAdapter(newsAdapter);

                    //newsAdapter.addAll(stringList);
                    autoSuggestAdapter.setData(stringList);
                    autoSuggestAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                }



        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag","onErrorResponse:"+error.getMessage());
            }
        })
        {

            @Override
            public Map<String, String> getHeaders() {
                // Posting parameters to login url
                HashMap<String, String> params = new HashMap<String, String>();
                //Map<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key",subscriptionKey);
                return params;
            }

        };


        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);
    }




    private void LoadJSON(final String query) {
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, JSON_URL+query, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
        Log.i("whole string",JSON_URL+query);
                for (int i = 0; i < 10; i++) {
                    try {
                        //progressBar.setVisibility(View.GONE);
                        //recyclerView.setVisibility(View.VISIBLE);
                        JSONObject newsObject = response.getJSONObject(i);
                        News news_obj = new News();
                        String pr=newsObject.getString("title");
                        Log.i("Title",pr);
                        news_obj.setN_title(newsObject.getString("title").toString());
                        news_obj.setN_image(newsObject.getString("image"));
                        news_obj.setN_section(newsObject.getString("sectionName").toString());
                        news_obj.setN_desc(newsObject.getString("description").toString());
                        news.add(news_obj);
                        Log.i("news obj", String.valueOf(news_obj));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                adapter=new Adapter(MainActivity.this,news);
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

