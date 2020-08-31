package com.example.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddelivery.Database.Shopper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText emailId, password;
    Button login;
    ProgressBar progessBar;

    String companyName;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference UserRef,ShopperRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.txtLogin);
        password = findViewById(R.id.txtPassword);
        login = findViewById(R.id.btnLogin);
        progessBar= findViewById(R.id.progessBar);

        UserRef = FirebaseDatabase.getInstance().getReference("User");
        ShopperRef = FirebaseDatabase.getInstance().getReference("Shopper");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null){
                    Toast.makeText(MainActivity.this,"You are logged in!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,MainUserActivity.class));
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //progess bar occur
                progessBar.setVisibility(View.VISIBLE);

                final String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()){
                    emailId.setError("Please enter email address!");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter password!");
                    password.requestFocus();
                }
                else if(pwd.isEmpty()&& email.isEmpty()){
                    Toast.makeText(MainActivity.this,"Field are empty!",Toast.LENGTH_SHORT).show();
                }
                else if (!(pwd.isEmpty()&& email.isEmpty())){
                   mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(!task.isSuccessful()){
                               //progess bar disappear
                               progessBar.setVisibility(View.GONE);
                               Toast.makeText(MainActivity.this,"Sign In unsuccessful. Please try again.",Toast.LENGTH_SHORT).show();
                           }
                           else{
                               final String userID= mFirebaseAuth.getCurrentUser().getUid();

                               UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            //customer login
                                            if(snapshot.hasChild(userID)){
                                                //progess bar disappear
                                                progessBar.setVisibility(View.GONE);
                                                startActivity(new Intent(MainActivity.this,MainUserActivity.class));
                                                finish();
                                            }
                                            //admin/ shopper login
                                            else{
                                                //get company name
                                                ShopperRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists()) {

                                                            for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                                                                Shopper shopperResult = resultSnapshot.getValue(Shopper.class);

                                                                if(shopperResult.getEmail().equals(email)){
                                                                    //progess bar disappear
                                                                    progessBar.setVisibility(View.GONE);
                                                                    companyName= shopperResult.getCompanyName();

                                                                    Intent intent = new Intent(MainActivity.this, MainAdmin.class);
                                                                    intent.putExtra("companyName",companyName);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        //progess bar disappear
                                                        progessBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       //progess bar disappear
                                       progessBar.setVisibility(View.GONE);
                                   }
                               });
                           }
                       }
                   });
                }
                else{
                    //progess bar disappear
                    progessBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Error Occured!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerPage(View v)
    {
        startActivity(new Intent(this,MainRegister.class));
    }


}
