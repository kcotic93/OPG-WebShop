package com.example.kristijan.opg_webshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewSwitcher;

import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.Model.OrderRequest;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.ViewHolders.OrderHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyOrders extends BaseActivity {

    FirebaseDatabase database;
    DatabaseReference order_request;

    RecyclerView order_recycler;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<OrderRequest, OrderHolder> adapter;

    ProgressDialog mdialog;

    ViewSwitcher viewSwitcher;
    View myFirstView,mySecondView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        getSupportActionBar().setTitle(R.string.actionBarOrders);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcherProduct);
        myFirstView = findViewById(R.id.switch_product_one);
        mySecondView = findViewById(R.id.switch_product_two);

        database = FirebaseDatabase.getInstance();
        order_request = database.getReference("orderRequest");

        order_recycler = (RecyclerView)findViewById(R.id.orders_recycler) ;
        order_recycler.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        order_recycler.setLayoutManager(layoutManager);

        mdialog = new ProgressDialog(MyOrders.this);
        mdialog.setMessage(getResources().getString(R.string.wait));

        final Query queries = order_request.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    viewSwitcher.showPrevious();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            loadOrders(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }


    private void loadOrders(String email) {

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
        adapter = new FirebaseRecyclerAdapter<OrderRequest, OrderHolder>(OrderRequest.class, R.layout.order_item, OrderHolder.class, order_request.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            @Override
            protected void populateViewHolder(final OrderHolder viewHolder, OrderRequest ord, final int position) {
                //stavljanje podataka iz baze u polja za prikaz na ekranu
                viewHolder.order_date.setText(ord.getDate());
                viewHolder.order_number.setText(adapter.getRef(position).getKey());
                viewHolder.order_status.setText(ord.getStatus());
                viewHolder.order_total_items.setText(String.valueOf(ord.getProducts().size()));
                viewHolder.order_total_price.setText(ord.getTotal()+" HRK");

                if(ord.getStatus().equals("0"))
                {
                    viewHolder.order_status.setText(R.string.order_sent_small);
                    viewHolder.order_status.setTextColor(Color.parseColor("#999999"));
                }
                else if (ord.getStatus().equals("1"))
                {
                    viewHolder.order_status.setText(R.string.order_recieved_small);
                    viewHolder.order_status.setTextColor(Color.parseColor("#ff9900"));
                }
                else if (ord.getStatus().equals("2"))
                {
                    viewHolder.order_status.setText(R.string.order_shipped_small);
                    viewHolder.order_status.setTextColor(Color.parseColor("#00FF00"));
                }

                else
                {
                    viewHolder.order_status.setText(R.string.order_canceled_small);
                    viewHolder.order_status.setTextColor(Color.parseColor("#ff0000"));
                }

                final OrderRequest clickItem = ord;

                //klikanje na pojedinu kategoriju i pokretanje nove aktivnosti
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent order_detail = new Intent(MyOrders.this,OrderDetail.class);
                        order_detail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(order_detail);
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

        order_recycler.setAdapter(adapter);

    }
}
