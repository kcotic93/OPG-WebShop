package com.example.kristijan.opg_webshop.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.OrderDetail;
import com.example.kristijan.opg_webshop.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {

    private final Context mContext;
    private List<Order> listData = new ArrayList<>();
    private OrderDetail orderDetail;

    public OrderDetailAdapter(Context mContext,List<Order> listData, OrderDetail orderDetail) {
        this.listData = listData;
        this.orderDetail = orderDetail;
        this.mContext=mContext;

    }



    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(orderDetail);
        View itemView = inflater.inflate(R.layout.order_detail_item,viewGroup,false);

        return new OrderDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderDetailViewHolder orderDetailViewHolder, final int position) {

        orderDetailViewHolder.item_cart_count.setText(listData.get(position).getQuantity());
        Locale locale = new Locale("hr","HR");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price =(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        orderDetailViewHolder.txt_cart_price.setText(fmt.format(price));
        orderDetailViewHolder.txt_cart_name.setText(listData.get(position).getProductName());
        orderDetailViewHolder.txt_cart_unit_price.setText(fmt.format(Integer.parseInt(listData.get(position).getPrice())));
        String imgUrl = listData.get(position).getImage();
        Glide.with(orderDetail.getBaseContext()).load(imgUrl).thumbnail(0.5f).into(orderDetailViewHolder.cart_image);


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public Order getItem(int position)
    {
        return listData.get(position);
    }


}
