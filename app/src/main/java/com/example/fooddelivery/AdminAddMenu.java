package com.example.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddelivery.Database.FoodList;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AdminAddMenu extends AppCompatActivity {

    final private int PICK_IMAGE_REQUEST=1;

    Button btnAdd;
    EditText txtFoodName, txtFoodPrice;
    ImageView imageFoodBtn;
    TextView headerLbl;

    private Uri mImageUri;
    private String itemNameChange, type,companyName;
    private String id, foodName,foodPrice;

    DatabaseReference FoodRef;
    private StorageReference storageRef;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_menu);

        btnAdd = findViewById(R.id.btnAdd);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodPrice = findViewById(R.id.txtFoodPrice);
        imageFoodBtn= findViewById(R.id.imageFoodBtn);
        headerLbl= findViewById(R.id.headerLbl);

        //Assign Child
        FoodRef = FirebaseDatabase.getInstance().getReference("FoodList");
        storageRef= FirebaseStorage.getInstance().getReference("foodImage");

        //get intent value
        if(getIntent().getStringExtra("itemName") != ""){
            itemNameChange = getIntent().getStringExtra("itemName");
        }
        type= getIntent().getStringExtra("type");
        companyName= getIntent().getStringExtra("companyName");

        //change xml view
        setup();

        choosePicture();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(AdminAddMenu.this, "Please don't spam this"+
                            "update button. Fill ALL information/picture then update", Toast.LENGTH_LONG).show();
                }else {
                    uploadImage();
                }

                if(!TextUtils.isEmpty(txtFoodName.getText().toString()) && !TextUtils.isEmpty(txtFoodPrice.getText().toString())){
                    foodName = txtFoodName.getText().toString();
                    foodPrice = String.format("%.2f",Double.parseDouble(txtFoodPrice.getText().toString()));

                    saveToDatabase();
                }
                else{
                    Toast.makeText(AdminAddMenu.this, "Please fill in all details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setup(){

        if(type.equals("add")){
            headerLbl.setText("ADD NEW FOOD");
            btnAdd.setText("Add");

        }
        else {
            headerLbl.setText("UPDATE FOOD");
            btnAdd.setText("Update");

            FoodRef.child(companyName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //find id from item name
                    for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                        FoodList foodlistResult = resultSnapshot.getValue(FoodList.class);

                        String nameFoodDatabase=  foodlistResult.getFoodName();
                        if(nameFoodDatabase.equals(itemNameChange)){

                            id= foodlistResult.getId();

                            String foodPrice= foodlistResult.getFoodPrice();

                            //set text
                            txtFoodName.setText(itemNameChange);
                            txtFoodPrice.setText(foodPrice);

                            //load picture in avatar
                            if(snapshot.child(id).hasChild("foodUrl")){
                                String url= foodlistResult.getFoodUrl();
                                Picasso.get().load(url).into(imageFoodBtn);
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
    }

    private void saveToDatabase(){
        if(type.equals("add")){

            id= FoodRef.child(companyName).push().getKey();

            FoodList foodlist = new FoodList(foodName, foodPrice,id);

            //Save Food into Database
            FoodRef.child(companyName).child(id).setValue(foodlist);

            Toast.makeText(AdminAddMenu.this, "New Food Saved in Database", Toast.LENGTH_SHORT).show();
        }
        else {
            //Save Food into Database
            FoodRef.child(companyName).child(id).child("foodName").setValue(foodName);
            FoodRef.child(companyName).child(id).child("foodPrice").setValue(foodPrice)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete()){
                        Toast.makeText(AdminAddMenu.this, "All Food Data has been updated to Database",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        txtFoodName.setText("");
        txtFoodPrice.setText("");
    }

    public void choosePicture(){
        imageFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open file chooser
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if(mImageUri != null){
            final StorageReference fileReference= storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask=fileReference.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        //upload info (url) to database
                        FoodRef.child(companyName).child(id).child("foodUrl").setValue(downloadUri.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isComplete())
                                {
                                    Toast.makeText(AdminAddMenu.this, "Picture upload successful",
                                        Toast.LENGTH_SHORT).show();

                                    imageFoodBtn.setImageDrawable(getResources().getDrawable(R.mipmap.add_profile_pic));
                                }
                            }
                        });

                    } else {
                        Toast.makeText(AdminAddMenu.this, "Error upload image",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                imageFoodBtn.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(AdminAddMenu.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void back(View v){
        Intent intent = new Intent(AdminAddMenu.this, MainAdmin.class);
        intent.putExtra("companyName",companyName);
        startActivity(intent);
    }
}