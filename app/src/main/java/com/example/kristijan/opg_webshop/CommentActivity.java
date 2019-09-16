package com.example.kristijan.opg_webshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.kristijan.opg_webshop.Model.Comment;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.ViewHolders.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference comment;

    Comment newComment;

    RecyclerView recycler_comment;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Comment,CommentHolder> adapter;

    String product_id="";
    String date;
    AlertDialog adialog;

    EditText com;

    ViewSwitcher viewSwitcher;
    View myFirstView,mySecondView;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        com=(EditText)findViewById(R.id.txt_comment);

        adialog = new ProgressDialog(CommentActivity.this);
        adialog.setMessage(getResources().getString(R.string.wait));

        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcherProduct);
        myFirstView= findViewById(R.id.switch_product_one);
        mySecondView = findViewById(R.id.switch_product_two);

        database=FirebaseDatabase.getInstance();
        comment= database.getReference("comment");

        recycler_comment=(RecyclerView) findViewById(R.id.comment_recycler);
        recycler_comment.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_comment.setLayoutManager(layoutManager);

        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(com, InputMethodManager.SHOW_FORCED);

        final Locale locale = new Locale("hr", "HR");
        Calendar calendar = Calendar.getInstance(locale);
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");

        date = mdformat.format(calendar.getTime());

        fab = (FloatingActionButton) findViewById(R.id.floating_comment);


        if (getIntent() !=null)
        {
            product_id = getIntent().getStringExtra("productId");
        }

        final Query queries=comment.orderByChild("productId").equalTo(product_id);
        queries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    viewSwitcher.showPrevious();
                }
                else{
                    if(viewSwitcher.getCurrentView() == mySecondView) {
                        viewSwitcher.showNext();//if the current view is the RatingBar, then show
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!product_id.isEmpty() && product_id != null)
        {
            loadComments(product_id);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(com.getText().toString().equals(""))
                {
                    Toast.makeText(CommentActivity.this, R.string.enter_comment, Toast.LENGTH_SHORT).show();
                    return;
                }

                newComment= new Comment(com.getText().toString(),date,product_id,FirebaseAuth.getInstance().getCurrentUser().getEmail());
                Toast.makeText(CommentActivity.this, R.string.comment_add, Toast.LENGTH_SHORT).show();
                comment.push().setValue(newComment);
                com.setText("");
                com.clearFocus();

                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void loadComments(String product_id) {

        adialog.show();
        if( new CheckConnectivity(this).isNetworkConnectionAvailable())
        {
            adialog.show();
            new CheckConnectivity.TestInternet(this).execute();
        }
        else
        {
            adialog.dismiss();
        }

        // recycler adapter Library za rad sa firebase
        adapter = new FirebaseRecyclerAdapter<Comment,CommentHolder>(Comment.class,R.layout.comment_item,CommentHolder.class,comment.orderByChild("productId").equalTo(product_id)) {

            @Override
            protected void populateViewHolder(final CommentHolder viewHolder, Comment com, final int position) {
                //stavljanje podataka iz baze u polja za prikaz na ekranu


                viewHolder.comment_name.setText(com.getUsername());
                viewHolder.comment_content.setText(com.getComment());
                viewHolder.comment_date.setText(com.getDate());
            }
            @Override
            protected void onDataChanged() {
                if (adialog != null && adialog.isShowing()) {
                    adialog.dismiss();
                }
            }
        };

        adapter.notifyDataSetChanged();
        recycler_comment.setAdapter(adapter);

    }
}
