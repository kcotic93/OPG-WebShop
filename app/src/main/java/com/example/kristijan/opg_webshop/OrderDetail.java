package com.example.kristijan.opg_webshop;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Model.OrderRequest;
import com.example.kristijan.opg_webshop.ViewHolders.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetail extends BaseActivity {

    TextView user_name,user_phone,state,city,postal_code,street_num,delivery_method,payment_method,comment,total,status;

    String OrderId="";

    FirebaseDatabase database;
    DatabaseReference order_request;

    RecyclerView recycler_Order_detail;
    RecyclerView.LayoutManager layoutManager;

    OrderDetailAdapter adapter;

    OrderRequest currentOrder;

    LinearLayout address_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setTitle(R.string.actionBarOrderDetail);

        database = FirebaseDatabase.getInstance();
        order_request = database.getReference("orderRequest");

        recycler_Order_detail=(RecyclerView) findViewById(R.id.recycle_order_detail);
        recycler_Order_detail.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(this);

        address_layout=(LinearLayout)findViewById(R.id.layout_address);

        user_name=(TextView)findViewById(R.id.name_enter);
        user_phone=(TextView)findViewById(R.id.phone_enter);
        state=(TextView)findViewById(R.id.state_enter);
        city=(TextView)findViewById(R.id.city_enter);
        postal_code=(TextView)findViewById(R.id.PostaCode_enter);
        street_num=(TextView)findViewById(R.id.Street_and_house_num_enter);
        delivery_method=(TextView)findViewById(R.id.delivery_method);
        payment_method=(TextView)findViewById(R.id.txt_payment_methods);
        comment=(TextView)findViewById(R.id.txt_Comment);
        total=(TextView)findViewById(R.id.total);
        status=(TextView)findViewById(R.id.txt_status);

        recycler_Order_detail.setLayoutManager(layoutManager);

        if (getIntent() !=null)
        {
            OrderId = getIntent().getStringExtra("OrderId");
        }

        loadOrder(OrderId);
    }

    private void loadOrder(String order_id) {
        order_request.child(order_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentOrder=dataSnapshot.getValue(OrderRequest.class);

                if (currentOrder.getUser().getState() == null )
                {
                   address_layout.setVisibility(View.GONE);
                }
                else
                {
                    state.setText(currentOrder.getUser().getState());
                    city.setText(currentOrder.getUser().getCity());
                    postal_code.setText(currentOrder.getUser().getPostalCode());
                    street_num.setText(currentOrder.getUser().getStreetHouseNum());
                }

                loadCart(currentOrder.getProducts());

                Locale locale = new Locale("hr","HR");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                user_name.setText(currentOrder.getUser().getName() +" "+ currentOrder.getUser().getSurname());
                delivery_method.setText(currentOrder.getDelivery_option());
                payment_method.setText(currentOrder.getPaymentMethom());
                comment.setText(currentOrder.getComment());
                total.setText(fmt.format(Integer.parseInt(currentOrder.getTotal())));

                if(currentOrder.getStatus().equals("0"))
                {
                    status.setText(R.string.order_sent);
                    status.setTextColor(Color.parseColor("#999999"));
                }
                else if (currentOrder.getStatus().equals("1"))
                {
                    status.setText(R.string.order_recieved);
                    status.setTextColor(Color.parseColor("#ff9900"));
                }
                else if (currentOrder.getStatus().equals("2"))
                {
                    status.setText(R.string.order_shipped);
                    status.setTextColor(Color.parseColor("#63ff00"));
                }
                else
                {
                    status.setText(R.string.order_canceled_small);
                    status.setTextColor(Color.parseColor("#ff0000"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCart(List<Order> order) {
        adapter = new OrderDetailAdapter(this,order,this);
        recycler_Order_detail.setAdapter(adapter);
    }
}
