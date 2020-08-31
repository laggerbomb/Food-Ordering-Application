package com.example.fooddelivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Database.FoodList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<String> title;
    private List<String> price;
    private List<String> images;
    private List<String> companyNameList;
    Context context;

    DatabaseReference FoodRef;
    String id, foodUrl;
    boolean error;

    AdapterMenu(Context context, List<String> data, List<String> images, List<String> data3, List<String>companyNameList){
        this.layoutInflater = LayoutInflater.from(context);
        this.title= data;
        this.price = data3;
        this.images = images;
        this.context = context;
        this.companyNameList= companyNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.menu_list_design,viewGroup,false);
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

        final String companyName= companyNameList.get(i);

        viewholder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Update Data
                Intent updateIntent = new Intent(context,AdminAddMenu.class);
                updateIntent.putExtra("itemName", newTitle);
                updateIntent.putExtra("type","update");
                updateIntent.putExtra("companyName",companyName);

                context.startActivity(updateIntent);
            }
        });

        viewholder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodRef = FirebaseDatabase.getInstance().getReference("FoodList");

                FoodRef.child(companyName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //find id from item name
                        for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                            FoodList foodlistResult = resultSnapshot.getValue(FoodList.class);

                            String nameFoodDatabase=  foodlistResult.getFoodName();
                            if(nameFoodDatabase.equals(newTitle)){

                                id= foodlistResult.getId();

                                //delete data in realtime database
                                FoodRef.child(companyName).child(id).removeValue();

                                //Delete Picture from Firebase Storage (if have picture)
                                if(snapshot.child(id).hasChild("foodUrl")){
                                    foodUrl= foodlistResult.getFoodUrl();

                                    StorageReference storageRef= FirebaseStorage.getInstance().getReferenceFromUrl(foodUrl);

                                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            error=false;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            error=true;
                                        }
                                    });
                                }

                                if(!error){
                                    title.remove(i);
                                    price.remove(i);
                                    images.remove(i);
                                    notifyDataSetChanged();
                                    Toast.makeText(context,"Delete Success",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context,"Error Delete",Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });
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
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPrice = itemView.findViewById(R.id.textPrice);
            imageFood = itemView.findViewById(R.id.imageFood);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
