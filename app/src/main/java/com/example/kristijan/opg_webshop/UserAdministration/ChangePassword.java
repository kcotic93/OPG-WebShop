package com.example.kristijan.opg_webshop.UserAdministration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kristijan.opg_webshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import info.hoang8f.widget.FButton;

public class ChangePassword extends AppCompatActivity {

    EditText old_password,new_password;
    FButton change;
    FirebaseUser user;
    ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        old_password = (EditText) findViewById(R.id.txt_old_password);
        new_password = (EditText) findViewById(R.id.txt_new_password);

        change = (FButton) findViewById(R.id.btn_changePassw);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mdialog = new ProgressDialog(ChangePassword.this);
        mdialog.setMessage(getResources().getString(R.string.wait));

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(old_password.getText().toString().equals(""))
                {
                    Toast.makeText(ChangePassword.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(new_password.getText().toString().equals(""))
                {
                    Toast.makeText(ChangePassword.this, R.string.enter_new_password, Toast.LENGTH_SHORT).show();
                }

                final String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email, old_password.getText().toString());
                mdialog.show();
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(new_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        mdialog.dismiss();
                                        Toast.makeText(ChangePassword.this, R.string.wrong_password_change, Toast.LENGTH_SHORT).show();
                                    } else {
                                        mdialog.dismiss();
                                        Toast.makeText(ChangePassword.this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            mdialog.dismiss();
                            Toast.makeText(ChangePassword.this,R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
