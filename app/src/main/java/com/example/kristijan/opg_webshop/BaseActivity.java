package com.example.kristijan.opg_webshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.github.juanlabrador.badgecounter.BadgeCounter;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    List<Order> carts= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if( new CheckConnectivity(this).isNetworkConnectionAvailable())
        {
            new CheckConnectivity.TestInternet(this).execute();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        carts = new Database(this).getCarts();
        getMenuInflater().inflate(R.menu.menu,menu);
        if (carts.size() > 0) {
            BadgeCounter.update(this,
                    menu.findItem(R.id.notification),
                    R.drawable.ic_shopping_cart_white_24dp,
                    BadgeCounter.BadgeColor.BLUE,
                    carts.size());
        } else {
            BadgeCounter.hide(menu.findItem(R.id.notification));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            Intent search = new Intent(BaseActivity.this, Search.class);
            startActivity(search);
            return true;
        }
        else if(id == R.id.notification){
            Intent cart = new Intent(BaseActivity.this, Cart.class);
            startActivity(cart);

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();

        invalidateOptionsMenu();

    }

    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
    }
}
