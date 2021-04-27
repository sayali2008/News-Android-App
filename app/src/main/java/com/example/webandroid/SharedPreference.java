package com.example.webandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SharedPreference {
    public static final String PREFS_NAME="NEWS_APP";
    public static final String FAVORITES="News_Favorite";

    public SharedPreference(){
        super();
    }

    public void saveFavorites(Context context, ArrayList<JSONObject> favorites)
    {
        SharedPreferences sharedPreferences;
        //settings= (SharedPreference) context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        sharedPreferences= (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String jsonFavorites=gson.toJson(favorites);
        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();


    }
    public void addFavorites(Context context, JSONObject news) throws JSONException {
        Log.i("add news", String.valueOf(news));
        ArrayList<JSONObject> favorites=getFavorites(context);
        Log.i("add fav", String.valueOf(favorites));
        if(favorites==null)
        {
            favorites=new ArrayList<JSONObject>();

        }
        favorites.add(news);
        saveFavorites(context,favorites);
    }

    public void removeFavorites(Context context, JSONObject news) throws JSONException {
       Log.i("news", String.valueOf(news));

        ArrayList<JSONObject> favorites=getFavorites(context);

        Gson gson=new Gson();
        JSONObject favoriteItems;
        String json = gson.toJson(favorites);
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<JSONObject> arrayList = new ArrayList(jsonArray.length());
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject oldnewsObject =(jsonArray.getJSONObject(i));
            JSONObject offerObject=oldnewsObject.getJSONObject("nameValuePairs");
            offerObject.getString("n_title");
            arrayList.add(offerObject);

        }
        Log.i("arraylist", String.valueOf(arrayList));
//        if(favorites==null)
//        {
//            favorites=new ArrayList<JSONObject>();
//
//        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj= jsonArray.getJSONObject(i);
            JSONObject offerObject=obj.getJSONObject("nameValuePairs");
            Log.i("jsonong", String.valueOf(offerObject));
            if(offerObject.getString("n_title").equals(news.getString("n_title")))
            {
                // add this item in some collection i.e PublishedList, and later use this collection
                arrayList.remove(offerObject);
            }
        }

        //arrayList.remove(news);
        Log.i("remove ", String.valueOf(arrayList));
        //arrayList.clear();
        saveFavorites(context,arrayList);

    }

   public ArrayList<JSONObject> getFavorites(Context context) throws JSONException {
        SharedPreferences sharedPreferences;
       //settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        //settings= (SharedPreference) context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        sharedPreferences= (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context);
     //   ArrayList<JSONObject> favorites;
       ArrayList<JSONObject> favoriteItems;
        //if(sharedPreferences.contains(FAVORITES))
        //{
            Log.i("inside getfav","");
            String jsonFavorites=sharedPreferences.getString(FAVORITES,null);
            Gson gson=new Gson();
            favoriteItems = gson.fromJson(jsonFavorites,ArrayList.class);
            //favorites=Arrays.asList(favoriteItems);
            //favorites=new ArrayList<News>(favorites);

        //}
        //else
          //  return null;

        return (ArrayList<JSONObject>) favoriteItems;


    }


}