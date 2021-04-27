package com.example.webandroid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarkFragment extends Fragment {
    SharedPreference sharedPreference = new SharedPreference();
    ArrayList<News> favorites;
    //List<News> news;

    TextView nbmk;
    LinearLayout favoriteList;
    BookmarkAdapter bkmkadapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View world_fragment= inflater.inflate(R.layout.recycler_frag,container,false);
        recyclerView = world_fragment.findViewById(R.id.newsList);
        progressBar=world_fragment.findViewById(R.id.loading_sign);
        nbmk=world_fragment.findViewById(R.id.nbmk);
        favorites = new ArrayList<News>();
        if(favorites==null)
        {
            progressBar.setVisibility(View.GONE);
            nbmk.setVisibility(View.VISIBLE);
        }
        else
        {
            nbmk.setVisibility(View.GONE);
        }
        //news=new ArrayList<>();
        try {
            ArrayList<JSONObject> sample=sharedPreference.getFavorites(getContext());
           // ArrayList<JSONObject> resample=sharedPreference.removeFavorites(getContext(),sample);
            Log.i("sad", String.valueOf(sample));

            Gson gson = new Gson();
            String json = gson.toJson(sample);
            Log.i("json converted",json);

            JSONArray jsonArray = new JSONArray(json);
            if(jsonArray.length()==0)
            {

                    progressBar.setVisibility(View.GONE);
                    nbmk.setVisibility(View.VISIBLE);

            }
            else
            {
                nbmk.setVisibility(View.GONE);
            }
            for(int i=0;i<jsonArray.length();i++)
            {
                progressBar.setVisibility(View.GONE);
                //recyclerView.setVisibility(View.VISIBLE);
                JSONObject oldnewsObject = jsonArray.getJSONObject(i);
               JSONObject offerObject=oldnewsObject.getJSONObject("nameValuePairs");
               // String pr =offerObject.getString("n_title");
                //Log.i("pr", "pr");
                News news_obj = new News();
                // String pr=newsObject.getString("title");
                //Log.i("Title",pr);
                //Log.i("newsObject", String.valueOf(newsObject));
                news_obj.setN_title(offerObject.getString("n_title").toString());
                news_obj.setN_image(offerObject.getString("n_image"));
                news_obj.setN_section(offerObject.getString("n_section").toString());
                String sedate=offerObject.getString("n_date").toString();
                news_obj.setN_date(format_date(sedate));
                favorites.add(news_obj);


            }


            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),2));
            bkmkadapter=new BookmarkAdapter(getActivity().getApplicationContext(),favorites);
            recyclerView.setAdapter(bkmkadapter);


        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        return world_fragment;
    }

    private String format_date(String sedate) throws ParseException {
            String nw=sedate.substring(0,10);
            SimpleDateFormat f1=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat f2=new SimpleDateFormat("dd MMM");
            Date date=f1.parse(sedate);
            return f2.format(date);
    }
}