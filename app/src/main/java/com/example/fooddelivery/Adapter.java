package com.example.fooddelivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<String> title;
    private List<String> price;
    private List<String> images;
    private List<String> companyList;
    Context context;

    Adapter(Context context, List<String> data, List<String> images, List<String> data3,List<String> companyList){
        this.layoutInflater = LayoutInflater.from(context);
        title= data;
        price = data3;
        this.images = images;
        this.companyList= companyList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.custom_view,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, final int i) {

        final String newTitle = title.get(i);
        viewholder.textTitle.setText(newTitle);

        final String newPrice = price.get(i);
        viewholder.textPrice.setText(newPrice);

        final String url= images.get(i);
        //load picture in avatar
        Picasso.get().load(url).into(viewholder.imageFood);

        final String companyName = companyList.get(i);

        viewholder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserOrderQty.class);
                intent.putExtra("data1", newTitle);
                intent.putExtra("data3", newPrice);
                intent.putExtra("myImage", url);
                intent.putExtra("companyName", companyName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle, textPrice;
        ImageView imageFood;
        LinearLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPrice = itemView.findViewById(R.id.textPrice);
            imageFood = itemView.findViewById(R.id.imageFood);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
