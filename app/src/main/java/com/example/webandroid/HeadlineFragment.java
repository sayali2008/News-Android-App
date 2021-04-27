package com.example.webandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class HeadlineFragment extends Fragment {

    TabLayout tabLayout;
    AppBarLayout appBarLayout;
    ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View headline_fragment= inflater.inflate(R.layout.headline_fragment,container,false);
        tabLayout = headline_fragment.findViewById(R.id.headtabLayout_id);
        //appBarLayout=headline_fragment.findViewById(R.id.headappbarId);
        viewPager=headline_fragment.findViewById(R.id.headviewPager_id);
        AdapterHeadline adapter=new AdapterHeadline(getFragmentManager());
        adapter.AddFragment(new WorldFragment(),"World");
        adapter.AddFragment(new BusinessFragment(),"Business");
        adapter.AddFragment(new PoliticsFragment(),"Politics");
        adapter.AddFragment(new SportsFragment(),"Sports");
        adapter.AddFragment(new TechnologyFragment(),"Technology");
        adapter.AddFragment(new ScienceFragment(),"Science");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);




        return headline_fragment;
        //return inflater.inflate(R.layout.headline_fragment,container,false);
    }
}
