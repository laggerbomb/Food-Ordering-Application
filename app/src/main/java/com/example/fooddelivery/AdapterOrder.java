package com.example.fooddelivery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.holderview> {

    private LayoutInflater layoutInflater;
    private List<String> username;
    private List<String> deliveryAddress;
    private List<String>userIDList;
    private List<String>companyNameList;
    Context context;

    AdapterOrder(Context context, List<String> username, List<String> deliveryAddress, List<String>userIDList, List<String>companyNameList){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.username = username;
        this.deliveryAddress = deliveryAddress;
        this.userIDList= userIDList;
        this.companyNameList=companyNameList;
    }

    @NonNull
    @Override
    public AdapterOrder.holderview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.order_layout,parent,false);
        return new holderview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final holderview holder, int position) {
        final String uname = username.get(position);
        final String address = deliveryAddress.get(position);
        final String userID= userIDList.get(position);
        final String companyName= companyNameList.get(position);
        holder.lblUsername.setText(uname);
        holder.lblDeliveryAddress.setText(address);

        holder.orderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameCust = uname;

                Intent displayOrderIntent = new Intent(context,OrderDetailsAdmin.class);
                displayOrderIntent.putExtra("username", usernameCust);
                displayOrderIntent.putExtra("address",address);
                displayOrderIntent.putExtra("userID", userID);
                displayOrderIntent.putExtra("companyName", companyName);

                context.startActivity(displayOrderIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class holderview extends RecyclerView.ViewHolder{
        TextView lblUsername, lblDeliveryAddress;
        ConstraintLayout orderList;
        public holderview(@NonNull View itemView) {
            super(itemView);
            lblUsername = itemView.findViewById(R.id.username);
            lblDeliveryAddress = itemView.findViewById(R.id.deliveryAddress);
            orderList = itemView.findViewById(R.id.orderList);
        }
    }
}
