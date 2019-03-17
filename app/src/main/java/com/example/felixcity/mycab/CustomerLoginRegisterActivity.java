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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginRegisterActivity extends AppCompatActivity {
    private Button customerLoginButton;
    private Button customerRegisterButton;
    private TextView customerregisterlink;
    private TextView customerstatus;
    private EditText EmailCustomer;
    private EditText PasswordCustomer;
    private ProgressDialog loadingBar;


    private FirebaseAuth mAuth;
    private DatabaseReference customerDatabaseRef;
    private String onlineCustomerID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);

        mAuth = FirebaseAuth.getInstance();

        customerLoginButton = (Button) findViewById(R.id.customer_login_btn);
        customerRegisterButton = (Button) findViewById(R.id.customer_register_btn);
        customerregisterlink = (TextView) findViewById(R.id.register_customer_link);
        customerstatus = (TextView) findViewById(R.id.customer_status);
        EmailCustomer = (EditText) findViewById(R.id.email_customer);
        PasswordCustomer = (EditText) findViewById(R.id.password_customer);
        loadingBar = new ProgressDialog(this);


        customerRegisterButton.setVisibility(View.INVISIBLE);
        customerRegisterButton.setEnabled(false);

        customerregisterlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerLoginButton.setVisibility(View.INVISIBLE);
                customerregisterlink.setVisibility(View.INVISIBLE);
                customerstatus.setText("Register Customer");

                customerRegisterButton.setVisibility(View.VISIBLE);
                customerRegisterButton.setEnabled(true);
            }
        });

        customerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                RegisterCustomer(email, password);
            }
        });

               customerLoginButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       String email = EmailCustomer.getText().toString();
                       String password = PasswordCustomer.getText().toString();

                       SignInCustomer(email,password);
                   }
               });
    }

    private void SignInCustomer(String email, String password)
    {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please write Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write Your Password", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Customer Logged In");
            loadingBar.setMessage("Please Wait While, We Confirm Your Data");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                Intent customerIntent = new Intent(CustomerLoginRegisterActivity.this,CustomersMapsActivity.class);
                                startActivity(customerIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Logged In Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "LoggedIn Unsuccessful.., please try again", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }

    }

    private void RegisterCustomer(String email, String password) {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write Email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write Your Password", Toast.LENGTH_SHORT).show();
        }
        else
            {

            loadingBar.setTitle("Customer Registration");
            loadingBar.setMessage("Please Wait While, We Register Your Data");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                onlineCustomerID = mAuth.getCurrentUser().getUid();
                                customerDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Customers").child(onlineCustomerID);

                                customerDatabaseRef.setValue(true);

                                Intent driverIntent = new Intent(CustomerLoginRegisterActivity.this,CustomersMapsActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Register Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Registration Unsuccessful.., please try again", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }

    }
}
