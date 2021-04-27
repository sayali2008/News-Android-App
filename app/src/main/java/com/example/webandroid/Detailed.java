package com.example.webandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
//import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

public class Detailed extends AppCompatActivity {
    TextView newsTitle,newsDesc,newsSection,newsDate;
    ImageView coverImage;
    MenuItem menuItem;
    Context context;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_news);


        newsTitle=findViewById(R.id.newsTitle);
        newsDesc=findViewById(R.id.newsDesc);
        newsSection=findViewById(R.id.newsSection);
        newsDate=findViewById(R.id.newsDate);
        coverImage=findViewById(R.id.coverImage);

        toolbar=findViewById(R.id.myToolBar);


        Intent intent=getIntent();
        String n_title=intent.getStringExtra("n_title");
        String n_desc=intent.getStringExtra("n_desc");
        String n_section=intent.getStringExtra("n_section");
        String n_date=intent.getStringExtra("n_date");
        String n_image=intent.getStringExtra("n_image");

        newsTitle.setText(n_title);
        newsDesc.setText(HtmlCompat.fromHtml(n_desc, HtmlCompat.FROM_HTML_MODE_LEGACY));
        newsSection.setText(n_section);
        try {
            newsDate.setText(format_date(n_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // newsDesc.setN_desc(intent.getStringExtra("n_image");

            Glide.with(this).load(n_image).into(coverImage);


        //Glide.with(mContext).load(news.get(position).getN_image()).into(holder.coverImage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(n_title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    private String format_date(String n_date) throws ParseException {
        String nw=n_date.substring(0,10);
        SimpleDateFormat f1=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2=new SimpleDateFormat("dd MMM yyyy");
        Date date=f1.parse(n_date);
        return f2.format(date);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.topmenu,menu);

        return true;
        //return super.onCreateOptionsMenu(menu);
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
