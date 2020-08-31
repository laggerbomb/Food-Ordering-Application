package com.example.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.fooddelivery.Database.FoodList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainUserActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> companyNameList;

    private DatabaseReference FoodRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        listView= (ListView)findViewById(R.id.listView);

        companyNameList= new ArrayList<String>();

        FoodRef = FirebaseDatabase.getInstance().getReference("FoodList");

        FoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {

                        companyNameList.add(resultSnapshot.getKey());
                    }
                    listView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }


    public void listView(){

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,R.layout.color_list_view,companyNameList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String companyName= (String) listView.getItemAtPosition(position);

                Intent intent = new Intent(MainUserActivity.this, MainUserOrder.class);
                intent.putExtra("companyName",companyName);
                startActivity(intent);
            }
        });
    }

    //menu
    public void menuOnClick(View v){

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_design, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.cart:

                        Intent intent =new Intent(MainUserActivity.this,UserCart.class);
                        startActivity(intent);

                        return true;

                    case R.id.logout:

                        AlertDialog.Builder alertBuild= new AlertDialog.Builder(MainUserActivity.this);
                        alertBuild.setMessage("Do you want to exit this application?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();
                                        finish();
                                        startActivity(new Intent(MainUserActivity.this,MainActivity.class));
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