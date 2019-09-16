package com.example.kristijan.opg_webshop.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.R;

public class NewsHolder extends RecyclerView.ViewHolder {

    public TextView news_heading;
    public TextView news_content;
    public TextView news_date;

    public NewsHolder(@NonNull View itemView) {
        super(itemView);
        news_heading=(TextView) itemView.findViewById(R.id.news_heading);
        news_content=(TextView) itemView.findViewById(R.id.news_content);
        news_date=(TextView) itemView.findViewById(R.id.news_date);

    }
}
