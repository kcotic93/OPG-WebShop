package com.example.kristijan.opg_webshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class Discount extends BaseActivity {

    FirebaseDatabase database;
    DatabaseReference product;

    RecyclerView recycler_product;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Product,ProductHolder> adapter;

    ViewSwitcher viewSwitcher;
    View myFirstView,mySecondView;

    ProgressDialog mdialog;

    Locale locale = new Locale("hr","HR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        getSupportActionBar().setTitle(R.string.actionBarDiscount);

        database=FirebaseDatabase.getInstance();
        product= database.getReference("product");

        recycler_product=(RecyclerView) findViewById(R.id.product_recycler);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_product.setLayoutManager(mLayoutManager);
        recycler_product.setItemAnimator(new DefaultItemAnimator());

        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcherProduct);
        myFirstView= findViewById(R.id.switch_product_one);
        mySecondView = findViewById(R.id.switch_product_two);

        mdialog = new ProgressDialog(Discount.this);
        mdialog.setMessage(getResources().getString(R.string.wait));


        final Query queries=product.orderByChild("discount").startAt(9.0);
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

            loadProduct();
    }

    private void loadProduct() {

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
        // recycler adapter Library za rad sa firebase
        adapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(Product.class,R.layout.product_item,ProductHolder.class,product.orderByChild("discount").startAt(9.0)) {
            @Override
            protected void populateViewHolder(final ProductHolder viewHolder, Product prod, final int position) {

                //stavljanje podataka iz baze u polja za prikaz na ekranu
                int discoount = Math.round((prod.getPrice() / 100.0f) * prod.getDiscount());
                int discounted_price=prod.getPrice()-discoount;

                viewHolder.product_name.setText(prod.getName());
                viewHolder.product_discount.setText("- "+(String.valueOf(prod.getDiscount())+"%"));
                String imgUrl = prod.getImage();
                Glide.with(getBaseContext()).load(imgUrl).thumbnail(0.5f).into(viewHolder.product_image);

                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                viewHolder.product_price.setText(fmt.format(discounted_price));

                //klikanje na pojedinu kategoriju i pokretanje nove aktivnosti
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent Product_detail = new Intent(Discount.this,ProductDetail.class);

                        Product_detail.putExtra("productId",adapter.getRef(position).getKey());
                        startActivity(Product_detail);
                    }
                });
            }

            @Override
            protected void onDataChanged() {

                if (mdialog != null && mdialog.isShowing()) {
                    mdialog.dismiss();
                }
            }
        };

        adapter.notifyDataSetChanged();
        recycler_product.setAdapter(adapter);

    }
}
