package com.example.kristijan.opg_webshop.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.R;

public class CategoryHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {

    public TextView cat_text;
    public ImageView cat_img;
    private ItemClickListener itemClickListener;

    public CategoryHolder(@NonNull View itemView) {
        super(itemView);
        cat_text=(TextView) itemView.findViewById(R.id.image_name);
        cat_img=(ImageView) itemView.findViewById(R.id.image_model);
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
