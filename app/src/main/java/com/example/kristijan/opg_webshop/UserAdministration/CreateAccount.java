package com.example.kristijan.opg_webshop.UserAdministration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kristijan.opg_webshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import info.hoang8f.widget.FButton;

public class CreateAccount extends AppCompatActivity {
    EditText email_reg,password_reg;
    FButton Create_account;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        email_reg=(EditText) findViewById(R.id.input_email_reg);
        password_reg=(EditText) findViewById(R.id.input_password_reg);
        Create_account=(FButton) findViewById(R.id.Btn_Register);

        auth = FirebaseAuth.getInstance();

        Create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_reg.getText().toString().trim();
                String password = password_reg.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.password_short, Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(CreateAccount.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(CreateAccount.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateAccount.this, R.string.success, Toast.LENGTH_SHORT).show();
                                    sendVerificationEmail();
                                }
                            }
                        });

            }
        });


    }

    private void sendVerificationEmail()
    {
        auth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(this,new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(CreateAccount.this, R.string.email_verification_send, Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(CreateAccount.this, Login.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(CreateAccount.this, R.string.error_verification_email, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
