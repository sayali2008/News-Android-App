package com.example.webandroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {


    private Context context;
    private SharedPreference sharedPreference;
    ArrayList<News> favorites;
    List<News> news;
    View view;
    public BookmarkAdapter(Context context, ArrayList<News> favorites){
        this.context=context;
        this.favorites=favorites;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_fragment,parent,false);
        return  new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            //all set methods
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        //Log.i("json bookfrag",json);
        final News news_obj = new News();
        if(favorites==null)
        {
            holder.nbmk.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.nbmk.setVisibility(View.GONE);
        }



        holder.newsTitle.setText(favorites.get(position).getN_title());
        holder.newsSection.setText(favorites.get(position).getN_section());
        holder.newsDate.setText(favorites.get(position).getN_date());
        // holder.newsDesc.setText(HtmlCompat.fromHtml(news.get(position).getN_desc(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        Glide.with(context).load(favorites.get(position).getN_image()).into(holder.coverImage);



        holder.bkmkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    final JSONObject jObj = new JSONObject();
//                    try{
//                        jObj.put("n_title",favorites.get(position).getN_title());
//                        jObj.put("n_image",favorites.get(position).getN_image());
//                        jObj.put("n_date",favorites.get(position).getN_date());
//                        jObj.put("n_section",favorites.get(position).getN_section());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("jsononj", String.valueOf(jObj));
//                    sharedPreference.removeFavorites(context,jObj);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout bookmark_fragment;
        TextView newsTitle,newsSection,newsDate,nbmk;
        ImageView coverImage;
        CardView cardView;
        ImageView bkmkImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmark_fragment=(LinearLayout) itemView.findViewById(R.id.bookmark_mainpage);
            newsTitle=itemView.findViewById(R.id.newsTitle);
            coverImage=itemView.findViewById(R.id.coverImage);
            newsSection=itemView.findViewById(R.id.newsSection);
            cardView=itemView.findViewById(R.id.cardView);
            newsDate=itemView.findViewById(R.id.newsDate);
            bkmkImage=itemView.findViewById(R.id.bkmkImage);
            nbmk=itemView.findViewById(R.id.nbmk);

            //remove after adding onclick
        }
    }
}
