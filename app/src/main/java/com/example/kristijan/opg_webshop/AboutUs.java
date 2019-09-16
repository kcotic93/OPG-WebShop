package com.example.kristijan.opg_webshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.About;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AboutUs extends AppCompatActivity implements OnMapReadyCallback {

    List<Order> carts= new ArrayList<>();
    About about = new About();

    MapView mapView;
    ProgressDialog mdialog;

    LatLng position = new LatLng(43.157406, 17.459833);

    String markerText = "OPG Web shop";

    TextView activities,goals,iban,email,facebook,comapny_address,company_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activities=(TextView)findViewById(R.id.activities_description_detail);
        goals=(TextView)findViewById(R.id.Goals_description_detail);
        comapny_address=(TextView)findViewById(R.id.txt_address);
        iban=(TextView)findViewById(R.id.txt_Iban);
        email=(TextView)findViewById(R.id.txt_email);
        facebook=(TextView)findViewById(R.id.txt_facebook);
        company_phone=(TextView)findViewById(R.id.txt_phone);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("aboutUs");

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.up_bar_layout);

        mdialog = new ProgressDialog(AboutUs.this);
        mdialog.setMessage(getResources().getString(R.string.wait));

        mapView = (MapView) findViewById(R.id.main_activity_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();

        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();

        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.actionBarAboutUs));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        mdialog.show();
        if( new CheckConnectivity(this).isNetworkConnectionAvailable())
        {
            mdialog.show();
            new CheckConnectivity.TestInternet(this).execute();
        }
        else
        {
            mdialog.dismiss();
        }

        user_table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("01").exists()) {
                    mdialog.dismiss();
                    about = dataSnapshot.child("01").getValue(About.class);
                    activities.setText(about.getActivities());
                    goals.setText(about.getGoals());
                    iban.setText(about.getIban());
                    comapny_address.setText(about.getAddress());
                    email.setText(about.getEmail());
                    company_phone.setText(about.getPhone());
                    facebook.setText(about.getFacebook());
                }
                else
                    {

                    mdialog.dismiss();

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuItem = menu.findItem(R.id.search);
        carts = new Database(this).getCarts();
        getMenuInflater().inflate(R.menu.menu,menu);

        if (carts.size() > 0) {
            BadgeCounter.update(this,
                    menu.findItem(R.id.notification),
                    R.drawable.ic_shopping_cart_white_24dp,
                    BadgeCounter.BadgeColor.BLUE,
                    carts.size());
        }
        else {
            BadgeCounter.hide(menu.findItem(R.id.notification));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            Intent search = new Intent(AboutUs.this, Search.class);
            startActivity(search);
        }
        else if(id == R.id.notification){
            Intent cart = new Intent(AboutUs.this, Cart.class);
            startActivity(cart);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("DEBUG", "onMapReady");

        //add marker
        Marker marker = googleMap.addMarker(new MarkerOptions().position(position).title(markerText));

        //zoom to position with level 16
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
        googleMap.animateCamera(cameraUpdate);
        googleMap.setPadding(0,0,0,50);
        UiSettings uiSettings=googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

