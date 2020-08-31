package com.example.fooddelivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFrag1 extends Fragment {

    View v;

    AdapterOrder adapter;
    ArrayList<String> username, deliveryAddress,userIDList,companyNameList;

    String companyName;

    DatabaseReference OrderRef;

    public MyFrag1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_my_frag1,container,false);

        //get bundle data
        companyName = getArguments().getString("companyName");

        username = new ArrayList<>();
        deliveryAddress = new ArrayList<>();
        userIDList= new ArrayList<>();
        companyNameList= new ArrayList<>();

        OrderRef = FirebaseDatabase.getInstance().getReference().child("Order");

        OrderRef.child(companyName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                        //Retrieve Order
                        Order order = resultSnapshot.getValue(Order.class);
                        username.add(order.getUsername());
                        deliveryAddress.add(order.getAddress());
                        userIDList.add(order.getUID());
                        companyNameList.add(companyName);
                    }
                    initRecycleView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        return v;
    }


    public void initRecycleView(){
        RecyclerView recyclerview = (RecyclerView)v.findViewById(R.id.recyclerViewOrder);
        recyclerview.setLayoutManager(new LinearLayoutManager(v.getContext()));
        adapter = new AdapterOrder(v.getContext(),username, deliveryAddress,userIDList,companyNameList);
        recyclerview.setAdapter(adapter);
    }
}
