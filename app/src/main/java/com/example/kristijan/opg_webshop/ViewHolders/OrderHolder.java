package com.example.kristijan.opg_webshop.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.R;

public class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView order_number;
    public TextView order_status;
    public TextView order_total_items;
    public TextView order_total_price;
    public TextView order_date;
    public ImageView order_detail_btn;
    private ItemClickListener itemClickListener;

    public OrderHolder(@NonNull View itemView) {
        super(itemView);
        order_date=(TextView) itemView.findViewById(R.id.txt_date);
        order_number=(TextView) itemView.findViewById(R.id.txt_order_number);
        order_status=(TextView) itemView.findViewById(R.id.txt_order_status);
        order_total_items=(TextView) itemView.findViewById(R.id.txt_total_items);
        order_total_price=(TextView) itemView.findViewById(R.id.txt_total_price);
        order_detail_btn=(ImageView) itemView.findViewById(R.id.btn_next);

        //itemView.setOnClickListener(this);
        order_detail_btn.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);

    }

}
