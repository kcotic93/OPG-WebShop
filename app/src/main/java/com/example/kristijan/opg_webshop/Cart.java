package com.example.kristijan.opg_webshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.ViewHolders.CartAdapter;
import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends BaseActivity {

    public static TextView total_price;

    RecyclerView recycler_cart;
    RecyclerView.LayoutManager layoutManager;


    FButton btn_submit_order;

    List<Order> carts= new ArrayList<>();
    CartAdapter adapter;

    ViewSwitcher viewSwitcher;
    View myFirstView,mySecondView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle(R.string.actionBarCart);

        carts = new Database(this).getCarts();

        viewSwitcher =   (ViewSwitcher)findViewById(R.id.viewSwitcher1);
        myFirstView= findViewById(R.id.switch_one);
        mySecondView = findViewById(R.id.switch_two);

        if (carts.size()==0)
        {
            viewSwitcher.showPrevious();
        }



        recycler_cart=(RecyclerView) findViewById(R.id.RecyclerCart);
        recycler_cart.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(layoutManager);

        carts = new Database(this).getCarts();
        adapter = new CartAdapter(this,carts,this);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

        });

        total_price = (TextView)findViewById(R.id.total);
        btn_submit_order = (FButton)findViewById(R.id.btn_place_order);

        loadCart();

        btn_submit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent checkout = new Intent(Cart.this,Checkout.class);
                startActivity(checkout);
                finish();
            }
        });

    }

    private void checkEmpty() {
        if(adapter.getItemCount()==0)
        {
            viewSwitcher.showPrevious();
        }
    }

    private void loadCart() {

        recycler_cart.setAdapter(adapter);
        int total=0;
        for(Order order:carts)
        {
            total +=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("hr","HR");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total_price.setText(fmt.format(total));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        carts = new Database(this).getCarts();
        getMenuInflater().inflate(R.menu.menu, menu);
        if (carts.size() > 0) {
            BadgeCounter.update(this,
                    menu.findItem(R.id.notification),
                    R.drawable.ic_shopping_cart_white_24dp,
                    BadgeCounter.BadgeColor.BLUE,
                    carts.size());
        } else {
            BadgeCounter.hide(menu.findItem(R.id.notification));
        }
        return true;
    }
}
