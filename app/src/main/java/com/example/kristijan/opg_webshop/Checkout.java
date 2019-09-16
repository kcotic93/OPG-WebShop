package com.example.kristijan.opg_webshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kristijan.opg_webshop.Common.Common;
import com.example.kristijan.opg_webshop.Database.Database;
import com.example.kristijan.opg_webshop.Model.Currency;
import com.example.kristijan.opg_webshop.Model.DataMessage;
import com.example.kristijan.opg_webshop.Model.MyResponse;
import com.example.kristijan.opg_webshop.Model.Order;
import com.example.kristijan.opg_webshop.Model.OrderRequest;
import com.example.kristijan.opg_webshop.Model.Token;
import com.example.kristijan.opg_webshop.Model.User;
import com.example.kristijan.opg_webshop.Remote.APIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;

import static com.paypal.android.sdk.payments.PayPalConfiguration.ENVIRONMENT_SANDBOX;

public class Checkout extends BaseActivity {

    private static final int PAYPAL_REQUEST_CODE=999;

    ArrayList<Currency> currency = new ArrayList<Currency>();

    APIService mService;


    int total,total_paypal,shipping;

    String orderNumber;

    String delivery_opt,payment_met,payment_status,date,converted;

    DatabaseReference order_save;

    User user= new User();
    User user_ship=new User();
    User user_pick_in_store;

    List<Order> carts= new ArrayList<>();
    CardView address_view;

    TextView total_price,state,city,postal_code,street_and_home_address,shippment;

    EditText comment,state_new,city_new,postal_code_new,street_house_num_new;

    FButton place_order;

    RadioButton radioHomeAddress,radioSelectAddress,radioPickInStore,radioPaypal,radioCashOnDelivery;

    PayPalConfiguration payPalConfiguration = new PayPalConfiguration().environment(ENVIRONMENT_SANDBOX).clientId("AYegTkx5X8CQCpAb2AhvVCHLFEADS0ixPwVGrcIV7QSeorpF9tC2M5jpLjWhkVpqQmtF0dQnSoB0TYPC");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setTitle(R.string.actionBarCheckout);

        mService=Common.getFCMService();

        Intent paypal=new Intent(this,PayPalService.class);
        paypal.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        startService(paypal);

        FirebaseDatabase databaseOrder = FirebaseDatabase.getInstance();
        order_save = databaseOrder.getReference("orderRequest");

        final Locale locale = new Locale("hr","HR");
        final NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        place_order=(FButton) findViewById(R.id.btn_place_order);
        address_view=(CardView)findViewById(R.id.card_address) ;
        total_price=(TextView)findViewById(R.id.total);
        state=(TextView)findViewById(R.id.state_enter);
        city=(TextView)findViewById(R.id.city_enter);
        postal_code=(TextView)findViewById(R.id.PostaCode_enter);
        street_and_home_address=(TextView)findViewById(R.id.Street_and_house_num_enter);
        shippment=(TextView)findViewById(R.id.aditional_cost);

        comment=(EditText) findViewById(R.id.txt_comment);

        radioHomeAddress= (RadioButton) findViewById(R.id.use_home_address);
        radioSelectAddress= (RadioButton) findViewById(R.id.use_this_address);
        radioPickInStore= (RadioButton) findViewById(R.id.pickup_in_store);
        radioPaypal= (RadioButton) findViewById(R.id.Paypal);
        radioCashOnDelivery= (RadioButton) findViewById(R.id.cash_on_delivery);

        final FirebaseUser Auth_user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_adrs = database.getReference("user");

        final ProgressDialog mdialog = new ProgressDialog(Checkout.this);
        mdialog.setMessage(getResources().getString(R.string.wait));
        mdialog.show();

        user_adrs.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Provjera dali korisnik postoji u bazi
                if (dataSnapshot.child(Auth_user.getUid()).exists()) {
                    mdialog.dismiss();
                    user= dataSnapshot.child(Auth_user.getUid()).getValue(User.class);

                }
                else{
                    mdialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        carts = new Database(this).getCarts();
        for(Order order:carts)
        {
            total +=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        total_paypal=total;
        final int total_reset=total+30;
        shipping=0;
        getServerData();
        total_price.setText(fmt.format(total));

        Calendar calendar=Calendar.getInstance(locale);
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        date = mdformat.format(calendar.getTime());


        radioHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    user_ship=user;
                    if (user.getCity() !=null || !TextUtils.isEmpty(user.getCity()))
                    {
                        state.setText(user.getState());
                        city.setText(user.getCity());
                        postal_code.setText(user.getPostalCode());
                        street_and_home_address.setText(user.getStreetHouseNum());
                        delivery_opt=getResources().getString(R.string.deliver_to_addr);
                        address_view.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(Checkout.this, R.string.enter_home_address, Toast.LENGTH_SHORT).show();
                    }
                    if(total<500)
                    {
                        shipping=30;
                        total_paypal=total_reset;
                        total=total_reset;
                        total_price.setText(fmt.format(total_paypal));
                        shippment.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        radioSelectAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                    if(total<500)
                    {
                        shipping=30;
                        total_paypal=total_reset;
                        total=total_reset;
                        total_price.setText(fmt.format(total_reset));
                        shippment.setVisibility(View.VISIBLE);
                    }

                    delivery_opt= getResources().getString(R.string.deliver_to_address);
                    address_view.setVisibility(View.VISIBLE);
                    showNewAddressDialog();
                }
            }
        });

        radioPickInStore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    shipping=0;
                    shippment.setVisibility(View.GONE);
                    total_price.setText(fmt.format(total));

                    user_pick_in_store=new User(user.getName(),user.getSurname(),user.getPhone());
                    user_ship=user_pick_in_store;
                    delivery_opt=getResources().getString(R.string.pick_up_in_store);
                    address_view.setVisibility(View.GONE);
                }

            }
        });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((!radioHomeAddress.isChecked() && !radioSelectAddress.isChecked() &&
                        !radioPickInStore.isChecked()) ||( !radioPaypal.isChecked() && !radioCashOnDelivery.isChecked()))
                {
                    Toast.makeText(Checkout.this, R.string.select_options, Toast.LENGTH_SHORT).show();
                }
                else if(user_ship.getName()==null)
                {
                    Toast.makeText(Checkout.this, R.string.enter_user_info, Toast.LENGTH_SHORT).show();
                }
                else
                    {

                    if(radioCashOnDelivery.isChecked())
                    {
                        payment_met= radioCashOnDelivery.getText().toString();
                        payment_status= getResources().getString(R.string.pending);

                        orderNumber=String.valueOf(System.currentTimeMillis());
                        OrderRequest order_request= new OrderRequest(comment.getText().toString(),String.valueOf(total),"0",payment_met,payment_status,carts,user_ship,delivery_opt,FirebaseAuth.getInstance().getCurrentUser().getEmail(),date,String.valueOf(shipping),FirebaseAuth.getInstance().getCurrentUser().getUid());
                        order_save.child(orderNumber).setValue(order_request);

                        new Database(getBaseContext()).cleanCart();

                        sendNotificationOrder(orderNumber);

                        Intent redirect = new Intent(Checkout.this,MenuHome.class);
                        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(redirect);
                        finish();
                    }
                    else if(radioPaypal.isChecked())
                    {
                        PayPalPayment payPalPayment=new PayPalPayment(new BigDecimal(converted),"EUR","OPG order",PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent= new Intent(getApplicationContext(),PaymentActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                        startActivityForResult(intent,PAYPAL_REQUEST_CODE);

                    }
                }
            }
        });
    }

    private void sendNotificationOrder(final String orderNumber) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapShot.getValue(Token.class);
                    //Notification notification = new Notification("OPG Webshop", "You have new order" + orderNumber);
                    //Sender content = new Sender(serverToken.getToken(), notification);

                    Map<String,String> datasend=new HashMap<>();

                    datasend.put("title","OPG WebShop");
                    datasend.put("message",getResources().getString(R.string.new_order)+" "+orderNumber);
                    DataMessage dataMessage=new DataMessage(serverToken.getToken(),datasend);

                    mService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {

                            if(response.code()==200)
                            {
                                if(response.body().success==1)
                                {
                                    Toast.makeText(Checkout.this, R.string.order_placed, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(Checkout.this, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
       if(requestCode==PAYPAL_REQUEST_CODE)
       {
           if(resultCode==RESULT_OK)
           {
               PaymentConfirmation paymentConfirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

               if(paymentConfirmation!= null){

                   try
                   {
                       String paymentDetail=paymentConfirmation.toJSONObject().toString(4);
                       JSONObject jsonObject=new JSONObject(paymentDetail);

                       OrderRequest order_request= new OrderRequest(comment.getText().toString(),
                               String.valueOf(total),
                               "0",
                               payment_met,
                               jsonObject.getJSONObject("response").getString("state"),
                               carts,
                               user_ship,
                               delivery_opt,
                               FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                               date,FirebaseAuth.getInstance().getCurrentUser().getUid());

                       order_save.child(String.valueOf(System.currentTimeMillis())).setValue(order_request);

                       new Database(getBaseContext()).cleanCart();

                       sendNotificationOrder(orderNumber);

                       Toast.makeText(Checkout.this, R.string.order_placed, Toast.LENGTH_SHORT).show();
                       Intent redirect = new Intent(Checkout.this,MenuHome.class);
                       startActivity(redirect);
                       finish();
                   }
                   catch (JSONException e){
                       e.printStackTrace();
                   }
               }
           }
           else if(resultCode==Activity.RESULT_CANCELED)
           {
               Toast.makeText(Checkout.this, R.string.cancel_order, Toast.LENGTH_SHORT).show();
           }
           else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID)
           {
               Toast.makeText(Checkout.this, R.string.invalid_payment, Toast.LENGTH_SHORT).show();
           }
       }
    }

    private void showNewAddressDialog() {

        final AlertDialog.Builder AdressBuilder= new AlertDialog.Builder(Checkout.this);

        AdressBuilder.setTitle(R.string.new_address_heading);
        AdressBuilder.setMessage(R.string.new_address_message);
        LayoutInflater inflater= this.getLayoutInflater();
        final View add_address= inflater.inflate(R.layout.add_new_address,null);
        state_new=add_address.findViewById(R.id.txt_state);
        city_new=add_address.findViewById(R.id.txt_city);
        postal_code_new=add_address.findViewById(R.id.txt_postal_code);
        street_house_num_new=add_address.findViewById(R.id.txt_street_hose_num);

        AdressBuilder.setView(add_address);

        AdressBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                state.setText(state_new.getText().toString());
                city.setText(city_new.getText().toString());
                postal_code.setText(postal_code_new.getText().toString());
                street_and_home_address.setText(street_house_num_new.getText().toString());

                if (state_new.getText().toString() =="" || !TextUtils.isEmpty(state_new.getText().toString()))

                {
                    user_ship.setName(user.getName());
                    user_ship.setSurname(user.getSurname());
                    user_ship.setPhone(user.getPhone());
                    user_ship.setState(state_new.getText().toString());
                    user_ship.setCity(city_new.getText().toString());
                    user_ship.setPostalCode(postal_code_new.getText().toString());
                    user_ship.setStreetHouseNum(street_house_num_new.getText().toString());
                }
                else{

                    Toast.makeText(Checkout.this, R.string.fill_all_info, Toast.LENGTH_SHORT).show();
                    showNewAddressDialog();
                }
            }
        });

        AdressBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                radioSelectAddress.setChecked(false);
                address_view.setVisibility(View.GONE);
                dialog.dismiss();

            }
        });

        AdressBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                radioSelectAddress.setChecked(false);
                address_view.setVisibility(View.GONE);

            }
        });

        AdressBuilder.show();
    }

    private void getServerData(){

        String urlGetServerData = "http://hnbex.eu/api/v1/rates/daily";
        System.out.print(urlGetServerData);

        StringRequest request = new StringRequest(urlGetServerData, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {

                parseJsonData(string);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(Checkout.this);
        rQueue.add(request);
    }

    void parseJsonData(String jsonString) {

        try {
            Gson gson = new Gson();
            JSONArray jarray = new JSONArray(jsonString);
            double total_return;
            String formatTotal = "";

            for (int p = 0; p < jarray.length(); p++) {

                JSONObject jsonObject = jarray.getJSONObject(p);
                Currency cur = gson.fromJson(String.valueOf(jsonObject), Currency.class);//This method deserializes the Json read from the specified parse tree into an object of the specified type.
                currency.add(cur);
                if (cur.getCurrency_code().equals("EUR")) {
                    total_return = total_paypal / Double.parseDouble(cur.getMedian_rate());
                    String format = String.format("%.2f", total_return);
                    formatTotal = format.replace(",", ".");
                    converted=String.valueOf(formatTotal);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
