package com.example.felixcity.mycab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class DriverLoginRegisterActivity extends AppCompatActivity {

    private Button driverLoginButton;
    private Button driverRegisterButton;
    private TextView driverregisterlink;
    private TextView driverstatus;
    private EditText EmailDriver;
    private EditText PasswordDriver;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth = FirebaseAuth.getInstance();

        driverLoginButton = (Button) findViewById(R.id.driver_login_btn);
        driverRegisterButton=(Button)findViewById(R.id.driver_register_btn);
        driverregisterlink = (TextView)findViewById(R.id.driver_register_link);
        driverstatus = (TextView)findViewById(R.id.driver_status);
        EmailDriver =(EditText)findViewById(R.id.email_driver);
        PasswordDriver =(EditText)findViewById(R.id.password_driver);
        loadingBar = new ProgressDialog(this);


        driverRegisterButton.setVisibility(View.INVISIBLE);
        driverRegisterButton.setEnabled(false);

        driverregisterlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverLoginButton.setVisibility(View.INVISIBLE);
                driverregisterlink.setVisibility(View.INVISIBLE);
                driverstatus.setText("Register Customer");

                driverRegisterButton.setVisibility(View.VISIBLE);
                driverRegisterButton.setEnabled(true);
            }
        });

        driverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                RegisterDriver(email,password);
            }
        });

        driverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                SignInDriver(email,password);
            }
        });

    }

    private void SignInDriver(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please write Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write Your Password", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Driver LoggedIn");
            loadingBar.setMessage("Please Wait While, We Logged you In");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Driver LoggedIn Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this,DriversMapsActivity.class);
                                startActivity(driverIntent);
                            } else {
                                Toast.makeText(DriverLoginRegisterActivity.this, "LoggingIn Unsuccessful.., please try again", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void RegisterDriver(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write Email", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write Your Password", Toast.LENGTH_SHORT).show();
        }
        else
        {

              loadingBar.setTitle("Driver Registration");
              loadingBar.setMessage("Please Wait While, We Register Your Data");
              loadingBar.show();

              mAuth.createUserWithEmailAndPassword(email,password)
                      .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful())
                              {
                                  Toast.makeText(DriverLoginRegisterActivity.this, "Driver Register Successfully...", Toast.LENGTH_SHORT).show();
                                  loadingBar.dismiss();

                                  Intent driverIntent = new Intent(DriverLoginRegisterActivity.this,DriversMapsActivity.class);
                                  startActivity(driverIntent);
                              }
                              else
                              {
                                  Toast.makeText(DriverLoginRegisterActivity.this, "Registration Unsuccessful.., please try again", Toast.LENGTH_SHORT).show();
                                  loadingBar.dismiss();
                              }
                          }
                      });
        }

    }
}
