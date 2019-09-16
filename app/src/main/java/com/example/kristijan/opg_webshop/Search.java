package com.example.kristijan.opg_webshop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.Model.Product;
import com.example.kristijan.opg_webshop.ViewHolders.ProductHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.List;


public class Search extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference product;

    List<String> Suggestions = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    RecyclerView recycler_product;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Product,ProductHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recycler_product=(RecyclerView) findViewById(R.id.SearchRecycler);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_product.setLayoutManager(mLayoutManager);
        recycler_product.setItemAnimator(new DefaultItemAnimator());

        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);

        materialSearchBar.setHint("Enter your search");
        database=FirebaseDatabase.getInstance();
        product= database.getReference("product");

        ShowSuggestions();

        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {

                String sugest_clicked=Suggestions.get(position);
                startSearch(sugest_clicked);
                materialSearchBar.hideSuggestionsList();
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

               if (Suggestions.size()!=0)
               {
                   Suggestions.remove(position);
                   materialSearchBar.updateLastSuggestions(Suggestions);
                   materialSearchBar.showSuggestionsList();
               }
               else
               {
                   Toast.makeText(Search.this, R.string.no_suggestions, Toast.LENGTH_SHORT).show();
               }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final List<String>suggest=new ArrayList<>();
                for (String search:Suggestions)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        suggest.add(search);
                        materialSearchBar.setLastSuggestions(suggest);
                        materialSearchBar.showSuggestionsList();
                    }
                    else
                    {
                        materialSearchBar.hideSuggestionsList();
                        materialSearchBar.updateLastSuggestions(suggest);
                        materialSearchBar.showSuggestionsList();
                    }

                }

                materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                    @Override
                    public void OnItemClickListener(int position, View v) {

                        String sugest_clicked=suggest.get(position);
                        startSearch(sugest_clicked);
                        materialSearchBar.hideSuggestionsList();
                    }

                    @Override
                    public void OnItemDeleteListener(int position, View v) {
                        if (suggest.size()!=0)
                        {
                            suggest.remove(position);
                            materialSearchBar.updateLastSuggestions(suggest);
                            materialSearchBar.showSuggestionsList();
                        }
                        else
                        {
                            Toast.makeText(Search.this, R.string.no_suggestions, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                String s = enabled ? "enabled" : "disabled";


                if(s.equals("disabled"))
                {
                    recycler_product.setAdapter(adapter);
                    materialSearchBar.hideSuggestionsList();
                }


            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        materialSearchBar.showSuggestionsList();
                        break;
                    case MaterialSearchBar.BUTTON_BACK:
                        materialSearchBar.disableSearch();
                        materialSearchBar.hideSuggestionsList();

                        break;
                }
            }
        });
    }

    private void startSearch(CharSequence text) {

        adapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(Product.class,R.layout.product_item,ProductHolder.class,product.orderByChild("name").equalTo(text.toString())) {


            @Override
            protected void populateViewHolder(final ProductHolder viewHolder, Product prod, final int position) {

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
                viewHolder.product_price.setText((String.valueOf(discounted_price)+" kn"));
                viewHolder.product_discount.setText(" - "+(String.valueOf(prod.getDiscount())+"%"));
                String imgUrl = prod.getImage();
                Glide.with(getBaseContext()).load(imgUrl).thumbnail(0.5f).into(viewHolder.product_image);

                //klikanje na pojedinu kategoriju i pokretanje nove aktivnosti
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent Product_detail = new Intent(Search.this,ProductDetail.class);

                        Product_detail.putExtra("productId",adapter.getRef(position).getKey());
                        startActivity(Product_detail);
                    }
                });
            }
        };


        adapter.notifyDataSetChanged();

        recycler_product.setAdapter(adapter);
    }

    private void ShowSuggestions() {
        product.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Product prod= snapshot.getValue(Product.class);
                    Suggestions.add(prod.getName());
                }

                materialSearchBar.setLastSuggestions(Suggestions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
