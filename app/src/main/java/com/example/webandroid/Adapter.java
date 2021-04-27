package com.example.webandroid;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context mContext;
    LayoutInflater inflater;
    List<News> news;
    ProgressBar progressBar;
    //after
    Dialog myDialog;
    SharedPreference sharedPreference;
    ArrayList<JSONObject> favourites;
    Adapter adapter;

    public Adapter(Context mContext,List<News> news)
    {
        //this.inflater=LayoutInflater.from(ctx);
      this.mContext=mContext;
        this.news=news;
       sharedPreference=new SharedPreference();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //after


        ///before
        //View view=inflater.inflate(R.layout.custom_list_layout,parent,false);
        View view =LayoutInflater.from(mContext).inflate(R.layout.custom_list_layout,parent,false);
        final ViewHolder vHolder=new ViewHolder(view);


        myDialog=new Dialog(mContext);
        myDialog.setContentView(R.layout.dialog_news);
        myDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        vHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView dialog_Title=(TextView) myDialog.findViewById(R.id.dialog_Title);
                ImageView dialog_Image=(ImageView) myDialog.findViewById(R.id.dialog_Image);
                ImageView twitter_Image=(ImageView) myDialog.findViewById(R.id.twitter_Image);
                ImageView bkmk_Image=(ImageView) myDialog.findViewById(R.id.bkmkImage);
                dialog_Title.setText(news.get(vHolder.getAdapterPosition()).getN_title());

               Glide.with(mContext).load(news.get(vHolder.getAdapterPosition()).getN_image()).into(dialog_Image);
                Toast.makeText(mContext,"Text Click"+String.valueOf(vHolder.getAdapterPosition()),Toast.LENGTH_SHORT).show();




                twitter_Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(mContext,
//                                "The favorite list would appear on clicking this icon",
//                                Toast.LENGTH_LONG).show();

                        String url = "http://www.twitter.com/intent/tweet";
                        //?url=YOURURL&text=YOURTEXT
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });
               myDialog.show();
                return true;
            }
        });



            return  vHolder;
        //return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            //here we need to set the songs title//single object here
        holder.newsTitle.setText(news.get(position).getN_title());
        holder.newsSection.setText(news.get(position).getN_section());
        String ndate=news.get(position).getN_date();
        holder.newsDate.setText(change_time(ndate));
       // holder.newsDesc.setText(HtmlCompat.fromHtml(news.get(position).getN_desc(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        Glide.with(mContext).load(news.get(position).getN_image()).into(holder.coverImage);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,Detailed.class);
                intent.putExtra("n_title",news.get(position).getN_title());
                intent.putExtra("n_desc",news.get(position).getN_desc());
                intent.putExtra("n_image",news.get(position).getN_image());
                intent.putExtra("n_date",news.get(position).getN_date());
                intent.putExtra("n_section",news.get(position).getN_section());
                mContext.startActivity(intent);
            }
        });
         final JSONObject jObj = new JSONObject();
        try{
            jObj.put("n_title",news.get(position).getN_title());
            jObj.put("n_image",news.get(position).getN_image());
            jObj.put("n_date",news.get(position).getN_date());
            jObj.put("n_section",news.get(position).getN_section());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if(checkFavoriteItem(jObj))
            {
                holder.bkmkImage.setImageResource(R.drawable.bookmark_filled);
                holder.bkmkImage.setTag("red");
            }
            else
            {
                holder.bkmkImage.setImageResource(R.drawable.bookmark_notfilled);
                holder.bkmkImage.setTag("blank");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.bkmkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag=holder.bkmkImage.getTag().toString();
                if(tag.equalsIgnoreCase("blank"))
                {
                    try {

                        sharedPreference.addFavorites(mContext,jObj);
                        List sampleP = sharedPreference.getFavorites(mContext);
                        Log.i("bookmared example print", String.valueOf(sampleP));
                        Log.i("tududuu","i");
                        Toast.makeText(mContext,"was added to bookmarks",Toast.LENGTH_SHORT).show();
                        holder.bkmkImage.setTag("red");
                        holder.bkmkImage.setImageResource(R.drawable.bookmark_filled);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    try {
                        Log.i("before remove", String.valueOf(favourites));
                        sharedPreference.removeFavorites(mContext,jObj);
                        Log.i("favorites", String.valueOf(jObj));
                        holder.bkmkImage.setTag("blank");
                        holder.bkmkImage.setImageResource(R.drawable.bookmark_notfilled);
                        Toast.makeText(mContext, "was removed from bookmarks!", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {

                    }
                }
            }
        });

                 //progressBar.setVisibility(View.GONE);

    }

    String change_time(String dataDate) {
        String convTime = null;
        String suffix = "ago";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmtTime = TimeZone.getTimeZone("PST");
        dateFormat.setTimeZone(gmtTime);
        Date pasTime = null;
        Date nowTime = new Date();

        try { pasTime = dateFormat.parse(dataDate); }
        catch (ParseException | java.text.ParseException e) { e.printStackTrace(); }

        assert pasTime != null;
        long dateDiff = nowTime.getTime() - pasTime.getTime();
        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

        if (second < 60) { convTime = second+"s "+suffix; }
        else if (minute < 60) { convTime = minute+"m "+suffix; }
        else if (hour < 24) { convTime = hour+"h "+suffix; }
        else { convTime = day+"d "+suffix; }

        return convTime;
    }


    private String format_date(String ndate) {

        return ndate.substring(0,10);

    }

    private boolean checkFavoriteItem(JSONObject news) throws JSONException {
       boolean check=false;
    ArrayList<JSONObject> sample=sharedPreference.getFavorites(mContext);
        Gson gson = new Gson();
        String json = gson.toJson(sample);
        JSONArray jsonArray = new JSONArray(json);
      if(json!=null)
       {
            for (int i=0;i<jsonArray.length();i++){
                JSONObject oldnewsObject = jsonArray.getJSONObject(i);
                JSONObject offerObject=oldnewsObject.getJSONObject("nameValuePairs");

                //Log.i("prs", String.valueOf(news));
                //Log.i("pr", "pr");
                News news_obj = new News();
                String pr=offerObject.getString("n_title");
                if(pr.equals(news.getString("n_title")))
                {
                    check=true;
                    break;
                }
            }
        }
        return  check;
    }

    @Override
    public int getItemCount() {
        return 10;//no of items displayed in the news app
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout custom_list_layout;
        private LinearLayout detailed_custom_list_layout;
        TextView newsTitle,newsSection,newsDate,newsDesc;
        ImageView coverImage,bkmkImage;
        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            custom_list_layout=(LinearLayout) itemView.findViewById(R.id.custom_list_id);
            newsTitle=itemView.findViewById(R.id.newsTitle);
            coverImage=itemView.findViewById(R.id.coverImage);
            newsSection=itemView.findViewById(R.id.newsSection);
            cardView=itemView.findViewById(R.id.cardView);
            newsDate=itemView.findViewById(R.id.newsDate);
            bkmkImage=itemView.findViewById(R.id.bkmkImage);
            //newsDesc=itemView.findViewById(R.id.newsDesc);
        }
    }

    public void onResume() {
        //super.onResume();
        adapter.notifyDataSetChanged();
    }
}
