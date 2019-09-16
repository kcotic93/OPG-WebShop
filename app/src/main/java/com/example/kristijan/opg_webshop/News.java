package com.example.kristijan.opg_webshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.Model.Category;
import com.example.kristijan.opg_webshop.Model.NewsModel;
import com.example.kristijan.opg_webshop.Model.Product;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.ViewHolders.CategoryHolder;
import com.example.kristijan.opg_webshop.ViewHolders.NewsHolder;
import com.example.kristijan.opg_webshop.ViewHolders.ProductHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class News extends BaseActivity {

    FirebaseDatabase database;
    DatabaseReference news;

    RecyclerView recycler_news;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<NewsModel,NewsHolder> adapter;

    ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().setTitle(R.string.news);

        database=FirebaseDatabase.getInstance();
        news= database.getReference("news");

        recycler_news = (RecyclerView)findViewById(R.id.recycler_news) ;
        recycler_news.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recycler_news.setLayoutManager(layoutManager);

        mdialog = new ProgressDialog(News.this);
        mdialog.setMessage(getResources().getString(R.string.wait));

        loadNews();
    }

    private void loadNews() {

        mdialog.show();
        if( new CheckConnectivity(this).isNetworkConnectionAvailable())
        {
            mdialog.show();
            new CheckConnectivity.TestInternet(this).execute();
        }
        else
        {
            mdialog.dismiss();
        }

        adapter = new FirebaseRecyclerAdapter<NewsModel, NewsHolder>(NewsModel.class,R.layout.news_item,NewsHolder.class,news.orderByChild("date").limitToFirst(10)) {
            @Override
            protected void populateViewHolder(NewsHolder viewHolder, NewsModel model, int position) {

                //stavljanje podataka iz baze u polja za prikaz na ekranu
                viewHolder.news_heading.setText(model.getHeading());
                viewHolder.news_content.setText(model.getContent());
                viewHolder.news_date.setText(model.getDate());
            }

            @Override
            protected void onDataChanged() {
                if (mdialog != null && mdialog.isShowing()) {

                    mdialog.dismiss();
                }
            }
        };
        adapter.notifyDataSetChanged();
        recycler_news.setAdapter(adapter);
    }
}
