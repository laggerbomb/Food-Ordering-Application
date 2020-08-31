package com.example.fooddelivery;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fooddelivery.Database.FoodList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFrag2 extends Fragment {

    View v;
    Button btnAddMenu;
    AdapterMenu adapter;
    RecyclerView recyclerview;
    ArrayList<String> title, price,imaegeUrlList,companyNameList;

    String companyName;

    DatabaseReference FoodRef;

    public MyFrag2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_my_frag2,container,false);

        recyclerview= (RecyclerView)v.findViewById(R.id.recyclerview);

        //get bundle data
        companyName = getArguments().getString("companyName");

        //Assign database child and class declaration
        FoodRef = FirebaseDatabase.getInstance().getReference("FoodList");

        //Array declaration
        title = new ArrayList<>();
        price= new ArrayList<>();
        imaegeUrlList= new ArrayList<>();
        companyNameList= new ArrayList<>();

        //Retrieve data and asssign to array list
        FoodRef.child(companyName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                    FoodList foodlist = resultSnapshot.getValue(FoodList.class);
                    title.add(foodlist.getFoodName());
                    price.add(foodlist.getFoodPrice());
                    imaegeUrlList.add(foodlist.getFoodUrl());
                    companyNameList.add(companyName);
                }
                initRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(v.getContext()));
        onclick();
        return v;
    }

    public void initRecycleView(){
        adapter = new AdapterMenu(v.getContext(),title, imaegeUrlList, price,companyNameList);
        recyclerview.setAdapter(adapter);
    }

    protected void onclick(){
        btnAddMenu = v.findViewById(R.id.btnAddMenu);
        btnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AdminAddMenu.class);
                intent.putExtra("type","add");
                intent.putExtra("companyName",companyName);
                startActivity(intent);
            }
        });
    }

}
