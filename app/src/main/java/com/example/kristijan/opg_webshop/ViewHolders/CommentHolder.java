package com.example.kristijan.opg_webshop.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.R;

public class CommentHolder extends RecyclerView.ViewHolder {

    public TextView comment_name;
    public TextView comment_content;
    public TextView comment_date;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);

        comment_name=(TextView) itemView.findViewById(R.id.comment_username);
        comment_content=(TextView) itemView.findViewById(R.id.comment_content);
        comment_date=(TextView) itemView.findViewById(R.id.comment_date);
    }
}
