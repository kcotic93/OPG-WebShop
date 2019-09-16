package com.example.kristijan.opg_webshop.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.R;

public class ProductHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
    public TextView product_name;
    public TextView product_price;
    public TextView product_discount;
    public ImageView product_image;
    private ItemClickListener itemClickListener;

    public ProductHolder(@NonNull View itemView) {
        super(itemView);
        product_name=(TextView) itemView.findViewById(R.id.txt_product_name);
        product_price=(TextView) itemView.findViewById(R.id.txt_product_price);
        product_discount=(TextView) itemView.findViewById(R.id.txt_product_discount);
        product_image=(ImageView) itemView.findViewById(R.id.img_product);
        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);

    }

}


