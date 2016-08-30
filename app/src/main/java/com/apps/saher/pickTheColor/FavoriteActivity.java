package com.apps.saher.pickTheColor;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

public class FavoriteActivity extends Activity {

    SwipeLayout swipeLayout;
    ArrayList<Colors> colors;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    DataBaseManager dataBaseManager;
    View inflater;

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        SpannableString title = new SpannableString("Favourites");
        title.setSpan(new TypefaceSpan("fonts/avenirmedium.otf"), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar = getActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));

        dataBaseManager = new DataBaseManager(this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        colors = new ArrayList<>();
//        initializeAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        colors = dataBaseManager.viewdata();
        Log.e("the list size "," from Activity ="+colors.size());
        SwipeAdapter adapter = new SwipeAdapter(this,colors);
        recyclerView.setAdapter(adapter);
    }

    private void initializeAdapter() {

        colors = dataBaseManager.viewdata();
        Log.e("the list size "," from Activity ="+colors.size());

        SwipeAdapter adapter = new SwipeAdapter(this,colors);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }
}
