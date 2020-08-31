package com.example.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddelivery.Database.Shopper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainRegister extends AppCompatActivity {

    EditText emailId, password, confirmPassword,usernameTxt;
    Button signUp;
    RadioGroup rg_category;
    RadioButton radioButton;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.emailId);
        password = findViewById(R.id.txtPassword);
        usernameTxt= findViewById(R.id.usernameTxt);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUp = findViewById(R.id.btnRegister);
        rg_category= findViewById(R.id.rg_category);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                final String pwd = password.getText().toString();
                final String username = usernameTxt.getText().toString();
                String cpwd = confirmPassword.getText().toString();
                if (email.isEmpty()){
                    emailId.setError("Please enter email address!");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter password!");
                    password.requestFocus();
                }
                else if(!pwd.equals(cpwd)){
                    Toast.makeText(MainRegister.this,"The password are not match!",Toast.LENGTH_SHORT).show();
                    confirmPassword.getText().clear();
                    confirmPassword.requestFocus();
                }
                else if(pwd.isEmpty()&& email.isEmpty()){
                    Toast.makeText(MainRegister.this,"Field are empty!",Toast.LENGTH_SHORT).show();
                }
                else if (!(pwd.isEmpty()&& email.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(MainRegister.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseDatabase rootNode;
                                DatabaseReference UserRef,ShopperRef;
                                int radioID;
                                String type;

                                FirebaseUser userrr = mFirebaseAuth.getCurrentUser();
                                String userId = userrr.getUid();

                                radioID= rg_category.getCheckedRadioButtonId();
                                radioButton= findViewById(radioID);
                                type= radioButton.getText().toString();

                                if(type.equals("Customer")){
                                    User user = new User (email, pwd, userId, username);
                                    rootNode = FirebaseDatabase.getInstance();
                                    UserRef = rootNode.getReference("User");
                                    UserRef.child(userId).setValue(user);

                                    startActivity(new Intent(MainRegister.this,MainActivity.class));
                                    finish();
                                }
                                else{
                                    Shopper shopper = new Shopper (email, pwd, userId, username);
                                    rootNode = FirebaseDatabase.getInstance();
                                    ShopperRef = rootNode.getReference("Shopper");
                                    ShopperRef.child(userId).setValue(shopper);

                                    Intent intent = new Intent(MainRegister.this, MainAdmin.class);
                                    intent.putExtra("companyName",username);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            else{
                                Toast.makeText(MainRegister.this,task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainRegister.this,"Error Occured!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goBack(View v)
    {
        startActivity(new Intent(this,MainActivity.class));
    }
}