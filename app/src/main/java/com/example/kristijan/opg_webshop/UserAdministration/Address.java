package com.example.kristijan.opg_webshop.UserAdministration;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kristijan.opg_webshop.BaseActivity;
import com.example.kristijan.opg_webshop.Model.User;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class Address extends BaseActivity {

    EditText state, city, postal_code,streetAndHouseNum;
    FButton updateAddress;
    User user_address = new User();
    ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getSupportActionBar().setTitle(R.string.actionBarAddress);
        state = (EditText) findViewById(R.id.txt_State);
        city = (EditText) findViewById(R.id.txt_City);
        postal_code = (EditText) findViewById(R.id.txt_Post);
        streetAndHouseNum = (EditText) findViewById(R.id.txt_Street);
        updateAddress = (FButton) findViewById(R.id.Btn_Update_Address);

        final FirebaseUser Auth_user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_adrs = database.getReference("user");
        //final DatabaseReference user = user_adrs.child(Auth_user.getUid());
        mdialog = new ProgressDialog(Address.this);
        mdialog.setMessage(getResources().getString(R.string.wait));
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

        user_adrs.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //FirebaseUser Auth_user = FirebaseAuth.getInstance().getCurrentUser();
                //Provjera dali korisnik postoji u bazi
                if (dataSnapshot.child(Auth_user.getUid()).exists()) {
                    mdialog.dismiss();
                    user_address = dataSnapshot.child(Auth_user.getUid()).getValue(User.class);
                    state.setText(user_address.getState());
                    city.setText(user_address.getCity());
                    postal_code.setText(user_address.getPostalCode());
                    streetAndHouseNum.setText(user_address.getStreetHouseNum());
                }
                else{
                    mdialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_adrs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //FirebaseUser Auth_user = FirebaseAuth.getInstance().getCurrentUser();
                        Toast.makeText(Address.this, R.string.update_address, Toast.LENGTH_SHORT).show();

                        Map<String, Object> user_addr_update = new HashMap<>();
                        user_addr_update.put(Auth_user.getUid()+"/state",state.getText().toString() );
                        user_addr_update.put(Auth_user.getUid()+"/city",city.getText().toString() );
                        user_addr_update.put(Auth_user.getUid()+"/postalCode",postal_code.getText().toString() );
                        user_addr_update.put(Auth_user.getUid()+"/streetHouseNum",streetAndHouseNum.getText().toString() );
                        user_adrs.updateChildren(user_addr_update);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });
    }
}
