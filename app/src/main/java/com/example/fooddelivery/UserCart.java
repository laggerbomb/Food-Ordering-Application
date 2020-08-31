package com.example.fooddelivery;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserCart extends AppCompatActivity {

    private AdapterCart adapter;
    private RecyclerView recyclerviewcart;
    ArrayList<String> name, quantity, price;
    float total = 0;

    DatabaseReference CartRef,UserRef;

    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

                //disable button
        final Button btn = findViewById(R.id.btnPlaceOrder);
        btn.setEnabled(false);

        name = new ArrayList<>();
        quantity = new ArrayList<>();
        price = new ArrayList<>();

        CartRef = FirebaseDatabase.getInstance().getReference().child("Cart");
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");

        recyclerviewcart= (RecyclerView) findViewById(R.id.recyclerviewcart);
        recyclerviewcart.setLayoutManager(new LinearLayoutManager(this));

        //retrieve data
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userr = mFirebaseAuth.getCurrentUser();
        ID = userr.getUid();

        //retrieve data from database
        CartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recyclerviewcart.setAdapter(null);
                for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                    Cart post = resultSnapshot.getValue(Cart.class);
                    if (post.getUserID().equals(ID)) {
                        name.add(post.getFoodName());
                        quantity.add(post.getQuantity());
                        price.add(post.getTotalPrice());
                        total += Float.parseFloat(post.getTotalPrice());
                        btn.setEnabled(true);
                    }
                }
                TextView totalPrice = (TextView)findViewById(R.id.lblTotalPrice);
                totalPrice.setText(String.format("%.2f", total));
                initRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        aggRecyclerView();
    }

    public void initRecycleView(){
        adapter = new AdapterCart(this,name, quantity, price);
        recyclerviewcart.setAdapter(adapter);
    }

    //delete food
    public void aggRecyclerView(){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                Toast.makeText(UserCart.this,"swiped",Toast.LENGTH_SHORT).show();

                //Alert dialog to confirm delete items
                androidx.appcompat.app.AlertDialog.Builder alertBuild= new AlertDialog.Builder(UserCart.this);
                alertBuild.setMessage("Do you want to delete swiped items from cart?")
                        .setCancelable(false)
                        .setTitle("Delete Items?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //delete item from recycler view
                                final int position = viewHolder.getAdapterPosition();
                                final String food = name.get(position);
                                name.remove(position);
                                quantity.remove(position);
                                price.remove(position);
                                adapter.notifyDataSetChanged();

                                //Delete items from cart
                                CartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                                            Cart post = resultSnapshot.getValue(Cart.class);
                                            if (post.getUserID().equals(ID) && post.getFoodName().equals(food)) {
                                                CartRef.child(post.getPushID()).removeValue();
                                                total -= Float.parseFloat(post.getTotalPrice());
                                                Toast.makeText(UserCart.this,"Item deleted",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        TextView totalPrice = (TextView)findViewById(R.id.lblTotalPrice);
                                        totalPrice.setText(String.format("%.2f", total));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertBuild.create();
                alertBuild.setTitle("Delete Items?");
                alertBuild.show();
            }
        });
        helper.attachToRecyclerView(recyclerviewcart);
    }

    public void goBack(View v){
        Intent intent = new Intent(UserCart.this,MainUserActivity.class);
        startActivity(intent);
    }

    public void placeOrder(){
        Intent intent = new Intent(UserCart.this,LocationPage.class);
        startActivity(intent);
    }
}