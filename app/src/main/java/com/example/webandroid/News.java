package com.example.webandroid;

import org.json.JSONObject;

public class News {
    private String n_title;
    private  String n_image;
    private String n_desc;
    private String n_section;
    private String n_date;

    public News(){}
    public  News(String n_title,String n_image,String n_desc,String n_section,String n_date)
    {
        this.n_title=n_title;
        this.n_desc=n_desc;
        this.n_image=n_image;
        this.n_section=n_section;
        this.n_date=n_date;
    }

    public String getN_title() {
        return n_title;
    }

    public void setN_title(String n_title) {
        this.n_title = n_title;
    }

    public String getN_image() {
        return n_image;
    }

    public void setN_image(String n_image) {
        this.n_image = n_image;
    }


    public String getN_desc() {
        return n_desc;
    }

    public void setN_desc(String n_desc) {
        this.n_desc = n_desc;
    }

    public String getN_section() {
        return n_section;
    }

    public void setN_section(String n_section) {
        this.n_section = n_section;
    }

    public String getN_date() {
        return n_date;
    }

    public void setN_date(String n_date) {
        this.n_date = n_date;
    }

}
