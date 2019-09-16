package com.example.kristijan.opg_webshop.UserAdministration;

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
import com.google.firebase.auth.FirebaseAuth;

import info.hoang8f.widget.FButton;

public class ResetPassword extends AppCompatActivity {

    EditText email_reset;
    FButton Reset_Btn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email_reset=(EditText) findViewById(R.id.txt_email_reset);
        Reset_Btn=(FButton) findViewById(R.id.Btn_Reset);

        auth = FirebaseAuth.getInstance();

        Reset_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_reset.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), R.string.registrated_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPassword.this, R.string.send_reset, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPassword.this, R.string.failed_send_reset, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

    }
}
