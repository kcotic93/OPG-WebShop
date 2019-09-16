package com.example.kristijan.opg_webshop.ViewHolders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Cart;
import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Model.Product;
import com.example.kristijan.opg_webshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.kristijan.opg_webshop.Cart.total_price;


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{


    private final Context mContext;
    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    private Locale locale = new Locale("hr","HR");
    private NumberFormat fmt;

    private FirebaseDatabase database;
    private DatabaseReference product;

    private Product currentProd;

    private String productId;

    private Map<String, Object> updateQuantity = new HashMap<>();

    public CartAdapter(Context mContext,List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
        this.mContext=mContext;

    }



    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_item,viewGroup,false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder cartViewHolder, final int position) {


        fmt = NumberFormat.getCurrencyInstance(locale);
        int price =(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));

        cartViewHolder.item_cart_count.setText(listData.get(position).getQuantity());
        cartViewHolder.txt_cart_price.setText(fmt.format(price));
        cartViewHolder.txt_cart_name.setText(listData.get(position).getProductName());
        cartViewHolder.txt_cart_unit_price.setText(fmt.format(Integer.parseInt(listData.get(position).getPrice())));

        String imgUrl = listData.get(position).getImage();
        Glide.with(cart.getBaseContext()).load(imgUrl).thumbnail(0.5f).into(cartViewHolder.cart_image);

        cartViewHolder.img_del_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database=FirebaseDatabase.getInstance();
                product= database.getReference("product");

                 productId = getItem(cartViewHolder.getAdapterPosition()).getProductId();

                product.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        currentProd=dataSnapshot.getValue(Product.class);

                        String quantity=getItem(cartViewHolder.getAdapterPosition()).getQuantity();

                        updateQuantity.put("quantity", currentProd.getQuantity()+Integer.parseInt(quantity.toString()));
                        product.child(productId).updateChildren(updateQuantity);

                        Order delete_item=getItem(cartViewHolder.getAdapterPosition());
                        int deleteIndex=cartViewHolder.getAdapterPosition();

                        removeItem(deleteIndex);

                        new Database(mContext).deleteFromCart(delete_item.getID());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                List<Order> carts = new Database(cart.getBaseContext()).getCarts();
                int total=0;
                for(Order order:carts)
                {
                    total +=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
                }
                Locale locale = new Locale("hr","HR");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                total_price.setText(fmt.format(total));
            }
        });



    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public Order getItem(int position)
    {
        return listData.get(position);
    }

    public void removeItem(int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }



}
