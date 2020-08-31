package com.example.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.fooddelivery.Database.FoodList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainUserOrder extends AppCompatActivity {

    Adapter adapter;
    RecyclerView recyclerview;

    private String companyName;
    ArrayList<String> titleList, priceList,imageUrlList,companyNameList;

    DatabaseReference FoodRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_order);

        companyName= getIntent().getStringExtra("companyName");

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        //Assign database child and class declaration
        FoodRef = FirebaseDatabase.getInstance().getReference("FoodList");

        //Array declaration
        titleList = new ArrayList<>();
        priceList= new ArrayList<>();
        imageUrlList= new ArrayList<>();
        companyNameList= new ArrayList<>();

        //Retrieve data and asssign to array list
        FoodRef.child(companyName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                    FoodList foodlist = resultSnapshot.getValue(FoodList.class);
                    titleList.add(foodlist.getFoodName());
                    priceList.add(foodlist.getFoodPrice());
                    imageUrlList.add(foodlist.getFoodUrl());
                    companyNameList.add(companyName);
                }
                initRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public void initRecycleView(){
        adapter = new Adapter(getBaseContext(),titleList,imageUrlList, priceList,companyNameList);
        recyclerview.setAdapter(adapter);
    }

    public void menuOnClick(View v){

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_design, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.cart:

                        Intent intent =new Intent(MainUserOrder.this,UserCart.class);
                        startActivity(intent);

                        return true;

                    case R.id.logout:

                        AlertDialog.Builder alertBuild= new AlertDialog.Builder(MainUserOrder.this);
                        alertBuild.setMessage("Do you want to exit this application?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();
                                        finish();
                                        startActivity(new Intent(MainUserOrder.this,MainActivity.class));
                                    }
                                })

                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                        alertBuild.create();
                        alertBuild.setTitle("Alert! Warning!");
                        alertBuild.show();

                        return true;

                    default: return false; }
            }
        });
    }
}
