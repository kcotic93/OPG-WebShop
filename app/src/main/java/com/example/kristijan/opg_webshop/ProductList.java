package com.example.kristijan.opg_webshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.Model.Product;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.ViewHolders.ProductHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductList extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    FirebaseDatabase database;
    DatabaseReference product;

    RecyclerView recycler_product;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Product,ProductHolder> adapter;

    String categoryId="";
    AlertDialog adialog;

    SwipeRefreshLayout refresh_product;

    ViewSwitcher viewSwitcher;
    View myFirstView,mySecondView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        getSupportActionBar().setTitle(R.string.actionBarProducts);

        adialog = new ProgressDialog(ProductList.this);
        adialog.setMessage(getResources().getString(R.string.wait));

        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcherProduct);
        myFirstView= findViewById(R.id.switch_product_one);
        mySecondView = findViewById(R.id.switch_product_two);

        refresh_product = (SwipeRefreshLayout) findViewById(R.id.refresh_product);

        database=FirebaseDatabase.getInstance();
        product= database.getReference("product");

        recycler_product=(RecyclerView) findViewById(R.id.product_recycler);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_product.setLayoutManager(mLayoutManager);
        recycler_product.setItemAnimator(new DefaultItemAnimator());

        if (getIntent() !=null)
        {
            categoryId = getIntent().getStringExtra("categoryId");
        }

        final Query queries=product.orderByChild("categoryId").equalTo(categoryId);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(!dataSnapshot.exists())
               {
                    viewSwitcher.showPrevious();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!categoryId.isEmpty() && categoryId != null)
        {
            loadProduct(categoryId);
        }

        refresh_product.setOnRefreshListener(this);
    }


    private void loadProduct(String categoryId) {
        adialog.show();
        if( new CheckConnectivity(this).isNetworkConnectionAvailable())
        {
            adialog.show();
            new CheckConnectivity.TestInternet(this).execute();
        }
        else
        {
            adialog.dismiss();
        }

        // recycler adapter Library za rad sa firebase
        adapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(Product.class,R.layout.product_item,ProductHolder.class,product.orderByChild("categoryId").equalTo(categoryId)) {

            @Override
            protected void populateViewHolder(final ProductHolder viewHolder, Product prod, final int position) {
                //stavljanje podataka iz baze u polja za prikaz na ekranu

                int discoount = Math.round((prod.getPrice() / 100.0f) * prod.getDiscount());
                if (discoount > 0)
                {
                    viewHolder.product_discount.setVisibility(View.VISIBLE);
                }
                else
                {
                    viewHolder.product_discount.setVisibility(View.INVISIBLE);
                }
                int discounted_price=prod.getPrice()-discoount;
                viewHolder.product_name.setText(prod.getName());
                viewHolder.product_discount.setText(" - "+(String.valueOf(prod.getDiscount())+"%"));
                String imgUrl = prod.getImage();
                Glide.with(getBaseContext()).load(imgUrl).thumbnail(0.5f).into(viewHolder.product_image);

                Locale locale = new Locale("hr","HR");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                viewHolder.product_price.setText(fmt.format(discounted_price));

                final Product clickItem=prod;

                //klikanje na pojedinu kategoriju i pokretanje nove aktivnosti
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent Product_detail = new Intent(ProductList.this,ProductDetail.class);

                        Product_detail.putExtra("productId",adapter.getRef(position).getKey());
                        startActivity(Product_detail);
                    }
                });
            }
            @Override
            protected void onDataChanged() {
                if (adialog != null && adialog.isShowing()) {
                    adialog.dismiss();
                }
            }
        };

        adapter.notifyDataSetChanged();
        recycler_product.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        loadProduct(categoryId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh_product.setRefreshing(false);
            }
        }, 2000);
    }
}