package com.example.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.fooddelivery.Database.Shopper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailsAdmin extends AppCompatActivity {

    private TextView custNameLbl,custAddrsLbl,totalLbl;
    private TableLayout orderTable;
    private TableRow tableRow;
    private TextView foodNameLbl, quantityLbl;

    private ArrayList<String> foodNameList;
    private ArrayList<Integer> quantityList;

    private String customerName,userID,companyName;

    FirebaseAuth mFirebaseAuth;
    DatabaseReference CartRef,UserRef,ShopperRef;

    double total=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_admin);

        custNameLbl = findViewById(R.id.custNameLbl);
        custAddrsLbl= findViewById(R.id.custAddrsLbl);
        totalLbl= findViewById(R.id.totalLbl);
        orderTable= (TableLayout) findViewById(R.id.orderTable);

        foodNameList= new ArrayList<String>();
        quantityList= new ArrayList<Integer>();

        CartRef = FirebaseDatabase.getInstance().getReference().child("Cart");
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");
        ShopperRef = FirebaseDatabase.getInstance().getReference("Shopper");

        //get intent data
        if(!getIntent().getStringExtra("username").equals("")){
            customerName = getIntent().getStringExtra("username");
            String custAddrs= getIntent().getStringExtra("address");
            userID= getIntent().getStringExtra("userID");
            companyName= getIntent().getStringExtra("companyName");

            custNameLbl.setText(customerName);
            custAddrsLbl.setText(custAddrs);
        }

        getSetArrayListOrder();
    }

    private void getSetArrayListOrder(){

        CartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                        Cart cart = resultSnapshot.getValue(Cart.class);

                        if(cart.getCompanyName().equals(companyName)){
                            if (cart.getUserID().equals(userID)) {

                                String foodName= cart.getFoodName();
                                int quantity= Integer.parseInt(cart.getQuantity());

                                foodNameList.add(foodName);
                                quantityList.add(quantity);

                                //Calc total
                                total += Double.parseDouble(cart.getTotalPrice());
                            }
                        }
                    }
                    displayTable();
                    totalLbl.setText(String.format("%.2f",total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    private void displayTable(){

        TableRow.LayoutParams lp;
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        for (int i = 0; i < foodNameList.size(); i++) {
            tableRow = new TableRow(this);

            foodNameLbl = new TextView(this);
            foodNameLbl.setText(foodNameList.get(i));
            foodNameLbl.setGravity(Gravity.CENTER);
            foodNameLbl.setTextSize(20);
            foodNameLbl.setLayoutParams(lp);
            quantityLbl= new TextView(this);
            quantityLbl.setText(Integer.toString(quantityList.get(i)));
            quantityLbl.setGravity(Gravity.CENTER);
            quantityLbl.setTextSize(20);
            quantityLbl.setLayoutParams(lp);

            //put item as column to table
            tableRow.addView(foodNameLbl);
            tableRow.addView(quantityLbl);

            //display row
            orderTable.addView(tableRow);
        }
    }
}