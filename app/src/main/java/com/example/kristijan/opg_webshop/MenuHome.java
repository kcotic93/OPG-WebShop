package com.example.kristijan.opg_webshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kristijan.opg_webshop.Common.ItemClickListener;
import com.example.kristijan.opg_webshop.Common.ToolbarElevationOffsetListener;
import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.Category;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Model.Token;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.UserAdministration.Profile;
import com.example.kristijan.opg_webshop.ViewHolders.CategoryHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class MenuHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    String mState;

    TextView set_email;

    AppBarLayout mAppBarLayout;

    List<Order> carts= new ArrayList<>();

    GoogleSignInClient mGoogleSignInClient;

    MaterialSearchBar materialSearchBar;

    FirebaseDatabase database;
    DatabaseReference category;
    DatabaseReference token_del;
    FirebaseRecyclerAdapter<Category,CategoryHolder> adapter;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    SwipeRefreshLayout refresh_category;

    ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Opg WebShop");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        database=FirebaseDatabase.getInstance();
        category= database.getReference("category");
        token_del= database.getReference("Tokens");

        //Inicijalizacija recycler view-a
        recycler_menu=(RecyclerView) findViewById(R.id.category_recycler);
        recycler_menu.setHasFixedSize(true);
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MenuHome.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log.e("Token",mToken);
                updateToken(mToken);
            }
        });



        mAppBarLayout=(AppBarLayout) findViewById(R.id.appbar);

        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        refresh_category = (SwipeRefreshLayout) findViewById(R.id.refresh_category);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        set_email = (TextView) headerView.findViewById(R.id.set_user_email);

        set_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        materialSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(MenuHome.this, Search.class);
                startActivity(search);

            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new ToolbarElevationOffsetListener(this, toolbar));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {

                    mState = "SHOW_MENU";
                    invalidateOptionsMenu();
                }
                else
                {
                    mState = "HIDE_MENU";
                    invalidateOptionsMenu();

                }
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mdialog = new ProgressDialog(MenuHome.this);
        mdialog.setMessage(getResources().getString(R.string.wait));

        loadCategory();

        refresh_category.setOnRefreshListener(this);
    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data);
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
        } else {
            BadgeCounter.hide(menu.findItem(R.id.notification));
        }

        if (mState == "HIDE_MENU")
        {

            menu.findItem(R.id.search).setVisible(false);
        }
        if (mState == "SHOW_MENU")
        {

            menu.findItem(R.id.search).setVisible(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.search) {
            Intent search = new Intent(MenuHome.this, Search.class);
            startActivity(search);
        }
        else if(id == R.id.notification){
            Intent cart = new Intent(MenuHome.this, Cart.class);
            startActivity(cart);

        }

        return super.onOptionsItemSelected(item);
    }


    private void loadCategory() {

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
        // recycler adapter Library za rad sa firebase
        adapter = new FirebaseRecyclerAdapter<Category, CategoryHolder>(Category.class,R.layout.category_item,CategoryHolder.class,category) {
            @Override
            protected void populateViewHolder(CategoryHolder viewHolder, Category model, int position) {

                //stavljanje podataka iz baze u polja za prikaz na ekranu
                viewHolder.cat_text.setText(model.getName());
                String imgUrl = model.getImage();
                Glide.with(getBaseContext()).load(imgUrl).thumbnail(0.5f).into(viewHolder.cat_img);

                final Category clickItem=model;

                //klikanje na pojedinu kategoriju i pokretanje nove aktivnosti
                viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent product_intent = new Intent(MenuHome.this,ProductList.class);
                        product_intent.putExtra("categoryId",adapter.getRef(position).getKey());
                        startActivity(product_intent);
                    }
                });
            }
            @Override
            protected void onDataChanged() {

                if (mdialog != null && mdialog.isShowing()) {
                    mdialog.dismiss();
                }
            }

        };

        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent Profie = new Intent(MenuHome.this, Profile.class);
            startActivity(Profie);
        } else if (id == R.id.nav_address) {
            Intent Address = new Intent(MenuHome.this, com.example.kristijan.opg_webshop.UserAdministration.Address.class);
            startActivity(Address);

        } else if (id == R.id.nav_log_out) {
            SignOut();

        }else if (id == R.id.nav_discount) {
            Intent Discount = new Intent(MenuHome.this, Discount.class);
            startActivity(Discount);
        }
        else if (id == R.id.nav_orders) {
            Intent Orders = new Intent(MenuHome.this, MyOrders.class);
            startActivity(Orders);
        }
        else if (id == R.id.nav_about) {
            Intent about = new Intent(MenuHome.this, AboutUs.class);
            startActivity(about);
        }
        else if (id == R.id.nav_news) {
            Intent news = new Intent(MenuHome.this, News.class);
            startActivity(news);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void SignOut()
    {
        token_del.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        Toast.makeText(this, R.string.singout, Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                    }
                });
        Intent main = new Intent(MenuHome.this, MainActivity.class);
        startActivity(main);
        finish();
        return;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onResume(){
        super.onResume();

        invalidateOptionsMenu();

    }

    @Override
    public void onRefresh() {
        loadCategory();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh_category.setRefreshing(false);
            }
        }, 2000);
    }

}
