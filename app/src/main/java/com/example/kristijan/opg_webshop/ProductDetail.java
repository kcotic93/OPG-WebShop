package com.example.kristijan.opg_webshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import me.himanshusoni.quantityview.QuantityView;

public class ProductDetail extends BaseActivity {

    LinearLayout layoutDiscount;

    TextView ProductDiscountDescription,ProductPrice,ProductDiscount,ProductMesUnit,ProductDescription,unavailable;
    ImageView ProductImage,DiscountImage;

    CollapsingToolbarLayout collapsingToolbarLayout;

    FirebaseDatabase database;
    DatabaseReference product;

    FButton btn_cart,btn_comment;

    QuantityView quantityViewDefault;

    int discounted_price=0;

    String product_id="";

    Product currentProd;

    Map<String, Object> updateQuantity = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getSupportActionBar().setTitle("");

        layoutDiscount=(LinearLayout)findViewById(R.id.layoutDiscount) ;

        database=FirebaseDatabase.getInstance();
        product= database.getReference("product");
        quantityViewDefault = (QuantityView) findViewById(R.id.quantityView_custom);
        btn_cart=(FButton)findViewById(R.id.add_to_cart) ;
        btn_comment=(FButton)findViewById(R.id.btn_comment) ;

        //dohvaćanje intenta iz ModelActivity
        if (getIntent() !=null)
        {
            product_id = getIntent().getStringExtra("productId");
        }
        if(!product_id.isEmpty() && product_id != null)
        {
            load_product_details(product_id);

        }

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        product_id,
                        currentProd.getName(),
                        String.valueOf(quantityViewDefault.getQuantity()),
                        String.valueOf(discounted_price),
                        String.valueOf(currentProd.getDiscount()),
                        String.valueOf(currentProd.getImage())
                ));
                invalidateOptionsMenu();
                Toast.makeText(ProductDetail.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();

                updateQuantity.put("quantity", currentProd.getQuantity()-quantityViewDefault.getQuantity());
                product.child(product_id).updateChildren(updateQuantity);
            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent comment = new Intent(ProductDetail.this,CommentActivity.class);
                comment.putExtra("productId",product_id);
                startActivity(comment);
            }
        });


        //inicijalizacija text viewa
        unavailable=(TextView) findViewById(R.id.txt_unavailable);
        ProductMesUnit=(TextView) findViewById(R.id.product_name_detail);
        ProductPrice=(TextView) findViewById(R.id.food_price_detail);
        ProductDiscount=(TextView) findViewById(R.id.food_discount_detail);
        ProductDiscountDescription=(TextView) findViewById(R.id.food_discount_description);
        ProductDescription=(TextView) findViewById(R.id.product_description_detail);

        ProductImage=(ImageView) findViewById(R.id.img_detail);
        DiscountImage=(ImageView) findViewById(R.id.DiscountImage);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ColapsedAppbar);

    }

    private void load_product_details(String product_id) {
        product.child(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentProd=dataSnapshot.getValue(Product.class);

                String imgUrl = currentProd.getImage();
                Glide.with(getBaseContext()).load(imgUrl).thumbnail(0.5f).into(ProductImage);

                int discoount = Math.round((currentProd.getPrice() / 100.0f) * currentProd.getDiscount());
                discounted_price=currentProd.getPrice()-discoount;

                if(currentProd.getQuantity()>0 && currentProd.getQuantity()<20)
                {
                    quantityViewDefault.setMaxQuantity(currentProd.getQuantity());
                }

                else if(currentProd.getQuantity()==0)
                {
                    quantityViewDefault.setMaxQuantity(0);
                    btn_cart.setVisibility(View.GONE);
                    unavailable.setVisibility(View.VISIBLE);
                }

                if (discoount > 0)
                {
                    ProductDiscount.setVisibility(View.VISIBLE);
                    ProductDiscountDescription.setVisibility(View.VISIBLE);
                    DiscountImage.setVisibility(View.VISIBLE);
                }

                else
                {
                    layoutDiscount.setVisibility(View.GONE);
                    ProductDiscount.setVisibility(View.GONE);
                    ProductDiscountDescription.setVisibility(View.GONE);
                    DiscountImage.setVisibility(View.GONE);
                }

                ProductDiscount.setText(" - "+(String.valueOf(currentProd.getDiscount())+"%"));
                collapsingToolbarLayout.setTitle(currentProd.getName());
                ProductMesUnit.setText(currentProd.getMesUnit());
                ProductDescription.setText(currentProd.getDescription());

                Locale locale = new Locale("hr","HR");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                ProductPrice.setText(fmt.format(discounted_price));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

