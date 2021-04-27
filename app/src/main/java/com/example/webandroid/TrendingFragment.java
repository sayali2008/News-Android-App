package com.example.webandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TrendingFragment extends Fragment {
    private LineChart lineChart;
    private String JSON_URL="https://myandroidback.wl.r.appspot.com/guardian/trend/";
    Context mContext;
    LineChart chart;
    View trending_fragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Log.i("In trending","fvj");
        trending_fragment= inflater.inflate(R.layout.trending_fragment,container,false);
        lineChart=trending_fragment.findViewById(R.id.linechart);
//        lineChart.setOnChartGestureListener((OnChartGestureListener) this);
//        lineChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        EditText editText;
        final String addurl = "coronavirus";
        display("coronavirus");
         final EditText query=trending_fragment.findViewById(R.id.selectquery);
         query.setImeOptions(EditorInfo.IME_ACTION_SEND);
        query.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String term = query.getText().toString();

                    display(term);
                    return true;
                }
                return false;
            }




        });







        return trending_fragment;
        //return inflater.inflate(R.layout.trending_fragment,container,false);
    }
    private void display(final String term) {

        RequestQueue queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest jsonArrayRequest=new JsonObjectRequest(Method.GET, JSON_URL + term, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //JSONObject newsObject = response.getJSONObject(i);
                    //JSONArray offerObject=newsObject.getJSONArray("default");
                    //Log.i("default", String.valueOf(offerObject));
                    Log.i("inside","cfyh");
                    Log.i("resp", String.valueOf(response));
                    List<Entry> entries = new ArrayList<>();
                    JSONObject obj=response.getJSONObject("default");
                    JSONArray results=obj.getJSONArray("timelineData");

                    for(int i=0;i<results.length();i++){
                        JSONObject index = results.getJSONObject(i);
                        String value=index.getString("value");
                        value=value.substring(1);
                        value=value.substring(0,value.length()-1);
                        int val= Integer.parseInt(value);
                        entries.add(new Entry(i, val));

                    }
                    String label="Trending Chart for "+term;
                    LineDataSet dataset = new LineDataSet(entries, label);
                    dataset.setColor(Color.parseColor("#BB86FC"));
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataset);
                    LineData data = new LineData(dataSets);
                    chart = (LineChart) trending_fragment.findViewById(R.id.linechart);

                    dataset.setDrawIcons(false);
                    dataset.setColor(Color.parseColor("#8418f0"));
                    dataset.setCircleColor(Color.parseColor("#8418f0"));
                    dataset.setLineWidth(1f);
                    dataset.setCircleRadius(3f);
                    dataset.setDrawCircleHole(false);
                    //set1.setColor(Color.parseColor("#6200EE"));

                    Legend legend=chart.getLegend();
                    legend.setTextSize(20f);
                    dataset.setValueTextSize(9f);
                    dataset.setFormLineWidth(1f);
                    dataset.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    dataset.setFormSize(15.f);
                    dataset.setValueTextColor(Color.parseColor("#8418f0"));
                    chart.getAxisLeft().setDrawGridLines(false);
                    chart.getXAxis().setDrawGridLines(false);
                    chart.getAxisRight().setDrawGridLines(false);
                    dataset.setDrawHighlightIndicators(false);
                    YAxis leftAxis = chart.getAxisLeft();
                    leftAxis.setDrawAxisLine(false);
                    chart.setData(data);
                    chart.invalidate(); // refresh


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        },new Response.ErrorListener() {
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

