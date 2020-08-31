package com.example.fooddelivery;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainAdmin extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    private Bundle bundle;

    private int totalOrder;
    private String companyName;
    DatabaseReference OrderRef,ShopperRef;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        //get intent data
        companyName= getIntent().getStringExtra("companyName");

        //create bundle
        bundle = new Bundle();
        bundle.putString("companyName", companyName);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction t = manager.beginTransaction();
        MyFrag1 m1 = new MyFrag1();
        m1.setArguments(bundle);
        t.add(R.id.frame1,m1);
        t.commit();

        OrderRef = FirebaseDatabase.getInstance().getReference().child("Order");
        ShopperRef = FirebaseDatabase.getInstance().getReference().child("Shopper");

        getCurrentOrderCount();
        notification();
    }

    private void getCurrentOrderCount(){

        OrderRef.child(companyName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    totalOrder= (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void notification(){

        OrderRef.child(companyName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    int eventTotalOrder= (int)dataSnapshot.getChildrenCount();

                    if(eventTotalOrder > totalOrder){

                        eventTotalOrder++;

                        //Notification
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainAdmin.this, default_notification_channel_id )
                                .setSmallIcon(R.drawable. ic_launcher_foreground )
                                .setContentTitle( "New Order Placed" )
                                .setContentText( "New orders have been placed by our valuable customer!" )
                                .setSmallIcon(R.mipmap.icon);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;

                        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                            int importance = NotificationManager. IMPORTANCE_HIGH ;
                            NotificationChannel notificationChannel = new
                                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
                            notificationChannel.enableLights( true ) ;
                            notificationChannel.setLightColor(Color. RED ) ;
                            notificationChannel.enableVibration( true ) ;
                            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
                            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
                            assert mNotificationManager != null;
                            mNotificationManager.createNotificationChannel(notificationChannel) ;
                        }
                        assert mNotificationManager != null;
                        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw  databaseError.toException();
            }
        });
    }

    public void ChangeFragment(View view){

        View line1= (View) findViewById(R.id.line1);
        View line2= (View) findViewById(R.id.line2);



        // androidx.fragment.app.Fragment fragment;
        if (view==findViewById(R.id.addrToLatLngTxt))
        {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.INVISIBLE);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction t = manager.beginTransaction();
            MyFrag1 m1 = new MyFrag1();
            m1.setArguments(bundle);
            t.replace(R.id.frame1,m1);
            t.commit();
        }

        if (view==findViewById(R.id.latlngtoaddrTxt))
        {
            line1.setVisibility(View.INVISIBLE);
            line2.setVisibility(View.VISIBLE);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction t = manager.beginTransaction();
            MyFrag2 m1 = new MyFrag2();
            m1.setArguments(bundle);
            t.replace(R.id.frame1,m1);
            t.commit();
        }
    }

    public void logout(View v){
        AlertDialog.Builder alertBuild= new AlertDialog.Builder(MainAdmin.this);
        alertBuild.setMessage("Do you want to exit this application?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(MainAdmin.this,MainActivity.class));
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
    }

}
