package com.example.kristijan.opg_webshop.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder{

    public TextView txt_cart_name,txt_cart_price,txt_cart_unit_price,item_cart_count;
    public ImageView cart_image;
    public TextView total_price;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public OrderDetailViewHolder(View itemView) {
        super(itemView);
        txt_cart_name=(TextView)itemView.findViewById(R.id.cart_item_name);
        txt_cart_price=(TextView)itemView.findViewById(R.id.item_price_text);
        txt_cart_unit_price=(TextView)itemView.findViewById(R.id.cart_unit_price);
        item_cart_count=(TextView) itemView.findViewById(R.id.cart_quantity_count);
        cart_image=(ImageView) itemView.findViewById(R.id.item_image);


    }

}
