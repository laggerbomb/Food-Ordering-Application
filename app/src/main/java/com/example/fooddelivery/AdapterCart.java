package com.example.fooddelivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.holder> {

    private LayoutInflater layoutInflater;
    private List<String> nameT;
    private List<String> priceT;
    private List<String> quantityT;
    Context context;

    AdapterCart(Context context, List<String> data, List<String> data2, List<String> data3){
        this.layoutInflater = LayoutInflater.from(context);
        nameT= data;
        quantityT = data2;
        priceT = data3;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterCart.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cart_layout,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        final String newName = nameT.get(position);
        holder.name.setText(newName);

        final String newQuantity = quantityT.get(position);
        holder.quantity.setText(newQuantity);

        final String newPrice = priceT.get(position);
        holder.price.setText(newPrice);

    }

    @Override
    public int getItemCount() {
        return nameT.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView name, quantity, price;
        ConstraintLayout cartLayout;
        public holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.total);
            cartLayout = itemView.findViewById(R.id.cartList);
        }
    }
}
