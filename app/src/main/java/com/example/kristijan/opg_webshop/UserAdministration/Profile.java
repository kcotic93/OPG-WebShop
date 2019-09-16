package com.example.kristijan.opg_webshop.UserAdministration;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kristijan.opg_webshop.BaseActivity;
import com.example.kristijan.opg_webshop.Model.User;
import com.example.kristijan.opg_webshop.Network.CheckConnectivity;
import com.example.kristijan.opg_webshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class Profile extends BaseActivity {

    FirebaseDatabase database;
    DatabaseReference user_table;
    FirebaseUser Auth_user;

    EditText name, surname, phone;

    FButton update,change_password,delete_user;

    User user = new User();

    ProgressDialog mdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.actionBarProfil);

        name = (EditText) findViewById(R.id.txt_name);
        surname = (EditText) findViewById(R.id.txt_surname);
        phone = (EditText) findViewById(R.id.txt_phone);

        update = (FButton) findViewById(R.id.Btn_Update_Profile);
        change_password=(FButton)findViewById(R.id.btn_changePass);
        delete_user=(FButton)findViewById(R.id.btn_deleteAccount);


        database = FirebaseDatabase.getInstance();
        user_table = database.getReference("user");
        Auth_user = FirebaseAuth.getInstance().getCurrentUser();

        mdialog = new ProgressDialog(Profile.this);
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
        user_table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Provjera dali korisnik postoji u bazi
                if (dataSnapshot.child(Auth_user.getUid()).exists()) {
                    mdialog.dismiss();
                    user = dataSnapshot.child(Auth_user.getUid()).getValue(User.class);
                    name.setText(user.getName());
                    surname.setText(user.getSurname());
                    phone.setText(user.getPhone());

                }
                else{
                    mdialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Auth_user.getUid()).exists()) {
                            Map<String, Object> user_prof = new HashMap<>();
                            user_prof.put(Auth_user.getUid()+"/name",name.getText().toString() );
                            user_prof.put(Auth_user.getUid()+"/surname",surname.getText().toString() );
                            user_prof.put(Auth_user.getUid()+"/phone",phone.getText().toString() );
                            user_table.updateChildren(user_prof);
                        }
                        else
                        {
                            user = new User(name.getText().toString(), surname.getText().toString(), phone.getText().toString());
                            user_table.child(Auth_user.getUid()).setValue(user);
                            Toast.makeText(Profile.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent change_pas = new Intent(Profile.this, ChangePassword.class);
                startActivity(change_pas);
            }
        });

        delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
    }

    public void deleteUser()

    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle(R.string.delete_user);
        alertDialog.setMessage(R.string.delete_user_message);

        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                user_table.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                Auth_user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Profile.this, R.string.user_deleted, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(Profile.this, R.string.error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                FirebaseAuth.getInstance().signOut();
                Intent redirect_login = new Intent(Profile.this, Login.class);
                startActivity(redirect_login);
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
