package com.example.fooddelivery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserOrderQty extends AppCompatActivity {

    private ImageView mainImageView;
    private TextView title, foodPrice, quantity;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference CartRef;

    private String data1, data3, ID,itemID;
    private String myImage;
    private int fqty = 0;
    private int result=0;
    private int status = 0;

    private float totalPrice;

    private String fqtyString, totalPriceString, pushID,companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_qty);

        final Button btnGoCart = (Button)findViewById(R.id.buttonCheckOut);
        btnGoCart.setEnabled(false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userr = mFirebaseAuth.getCurrentUser();
        ID = userr.getUid();

        getData();

        mainImageView = (ImageView)findViewById(R.id.mainImageView);
        title = (TextView)findViewById(R.id.titleFood);
        foodPrice = (TextView)findViewById(R.id.foodPrice);

        CartRef = FirebaseDatabase.getInstance().getReference().child("Cart");
        CartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()){
                    Cart post = resultSnapshot.getValue(Cart.class);
                    if (post.getFoodName().equals(data1) && post.getUserID().equals(ID)) {
                        quantity = findViewById(R.id.foodQty);
                        quantity.setText(post.getQuantity());
                        fqty = Integer.parseInt(post.getQuantity());
                        status = 1;
                        btnGoCart.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        setData();
    }

    private void getData(){
        if(getIntent().hasExtra("myImage")&&getIntent().hasExtra("data1")
                &&getIntent().hasExtra("data3") &&getIntent().hasExtra("companyName")){
            data1 = getIntent().getStringExtra("data1");
            data3 = getIntent().getStringExtra("data3");
            myImage = getIntent().getStringExtra("myImage");
            companyName= getIntent().getStringExtra("companyName");
        }else{
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(){
        Picasso.get().load(myImage).into(mainImageView);
        title.setText(data1);
        foodPrice.setText(data3);
        TextView foodqty = (TextView)findViewById(R.id.foodQty);
        int quantity = Integer.parseInt(foodqty.getText().toString());
        fqty = quantity;
    }

    public void addQuantity(View v){
        Button btnGoCart = (Button)findViewById(R.id.buttonCheckOut);
        if(status == 1){
            btnGoCart.setText("Update Cart");
        }
        TextView foodQty = (TextView)findViewById(R.id.foodQty);
        int quantity = Integer.parseInt(foodQty.getText().toString());
        float price = Float.parseFloat(foodPrice.getText().toString());
        quantity += 1;
        btnGoCart.setEnabled(true);
        fqty = quantity;
        foodQty.setText(String.valueOf(fqty));
    }

    public void minusQuantity(View v){
        Button btnGoCart = (Button)findViewById(R.id.buttonCheckOut);
        if(status == 1){
            btnGoCart.setText("Update Cart");
        }
        TextView foodqty = (TextView)findViewById(R.id.foodQty);
        int quantity = Integer.parseInt(foodqty.getText().toString());
        if(quantity-1 <=0)
        {
            quantity = 0;
            btnGoCart.setText("Delete from Cart");
        }
        else
        {
            quantity -= 1;
            btnGoCart.setEnabled(true);
        }
        fqty = quantity;
        foodqty.setText(String.valueOf(fqty));
    }

    public void goCart(View v){
        totalPrice = Float.parseFloat(data3)*Float.parseFloat(String.valueOf(fqty));

        //change to string to store in firebase
        fqtyString= Integer.toString(fqty);
        totalPriceString= String.format("%.2f", totalPrice);

        CartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {

                        Cart post = resultSnapshot.getValue(Cart.class);

                        //Update cart data
                        if (post.getUserID().equals(ID) && post.getFoodName().equals(data1)) {

                            //update data (quantity is exist)
                            if(fqty > 0 ){
                                itemID = post.getPushID();
                                if(itemID != null){
                                    CartRef.child(itemID).child("quantity").setValue(String.valueOf(fqty));
                                    CartRef.child(itemID).child("totalPrice").setValue(totalPriceString);
                                    result=1;
                                }
                                else{
                                    pushID = CartRef.push().getKey();
                                    Cart cart = new Cart(data1, ID, fqtyString, totalPriceString, pushID,companyName);
                                    CartRef.child(pushID).setValue(cart);
                                    result=1;
                                }
                            }
                            //remove data from database (quantity=0)
                            else {
                                itemID = post.getPushID();
                                Toast.makeText(UserOrderQty.this,post.getFoodName() + " deleted in cart",Toast.LENGTH_SHORT).show();
                                CartRef.child(itemID).removeValue();
                                result=1;
                            }
                        }
                    }

                    //not update (no same item)
                    if(result==0){
                        //Add new item using same ID
                        pushID = CartRef.push().getKey();
                        Cart cart = new Cart(data1, ID, fqtyString, totalPriceString, pushID,companyName);
                        CartRef.child(pushID).setValue(cart);
                    }
                }

                //new first item
                else{
                    pushID = CartRef.push().getKey();
                    //Add new item using same ID
                    Cart cart = new Cart(data1, ID, fqtyString, totalPriceString, pushID,companyName);
                    CartRef.child(pushID).setValue(cart);
                }

                Intent intent = new Intent(UserOrderQty.this,UserCart.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserOrderQty.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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

                        Intent intent =new Intent(UserOrderQty.this,UserCart.class);
                        startActivity(intent);

                        return true;

                    case R.id.logout:

                        AlertDialog.Builder alertBuild= new AlertDialog.Builder(UserOrderQty.this);
                        alertBuild.setMessage("Do you want to log out now?")
                                .setCancelable(false)
                                .setTitle("Log Out?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();
                                        finish();
                                        startActivity(new Intent(UserOrderQty.this,MainActivity.class));
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

    public void goMenu(View v){
        Intent intent = new Intent(UserOrderQty.this, MainUserOrder.class);
        intent.putExtra("companyName",companyName);
        startActivity(intent);
    }
}

