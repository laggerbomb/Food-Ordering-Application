package com.example.fooddelivery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationPage extends FragmentActivity implements OnMapReadyCallback {

    //Google Map
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng userLatLng;
    private GoogleMap map;
    private int entry;

    //Notification
    int PERMISSION_ID = 44, result, status = 0;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    DatabaseReference OrderRef,UserRef,CartRef;
    String userID, username;
    Boolean found;
    ArrayList<String> companyNameList;

    EditText addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_page);

        addresses = findViewById(R.id.address);

        companyNameList= new ArrayList<String>();

        //Firebase child reference
        UserRef = FirebaseDatabase.getInstance().getReference("User");
        OrderRef = FirebaseDatabase.getInstance().getReference("Order");
        CartRef = FirebaseDatabase.getInstance().getReference().child("Cart");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnPlaceOrder = findViewById(R.id.buttonCheckOut);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(LocationPage.this, default_notification_channel_id )
                        .setSmallIcon(R.drawable. ic_launcher_foreground )
                        .setContentTitle( "Order Placed" )
                        .setContentText( "Your food will arrive soon." )
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
                mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build());

                //retrieve data
                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser userr = mFirebaseAuth.getCurrentUser();
                userID = userr.getUid();

                //get the username (key)
                UserRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username= dataSnapshot.getValue(User.class).getUsername();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

                //get the company name
                CartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                            Cart post = resultSnapshot.getValue(Cart.class);
                            if (post.getUserID().equals(userID)) {

                                String companyName= post.getCompanyName();
                                found=false;

                                for(int i=0; i<companyNameList.size();i++){
                                    if(companyNameList.get(i) == companyName){
                                        found=true;
                                        break;
                                    }
                                }

                                if(!found){
                                    companyNameList.add(companyName);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });

                //Save data to database
                OrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //loop for each company
                        for(int i=0; i<companyNameList.size();i++){

                            String companyNameNow= companyNameList.get(i);

                            if(dataSnapshot.exists()) {

                                if(dataSnapshot.child(companyNameNow).exists()){
                                    for (DataSnapshot resultSnapshot : dataSnapshot.child(companyNameNow).getChildren()) {
                                        Order ord = resultSnapshot.getValue(Order.class);

                                        //if same ID
                                        if (ord.getUID().equals(userID)) {
                                            OrderRef.child(companyNameNow).child(ord.getUsername()).child("address").setValue(addresses.getText().toString());
                                            result = 1;
                                        }
                                    }
                                    //not same ID but got other user in database
                                    if (result == 0) {
                                        Order o = new Order(userID, addresses.getText().toString(), username);
                                        OrderRef.child(companyNameNow).child(username).setValue(o);
                                    }
                                }
                                //new company data
                                else{
                                    Order orders = new Order(userID,addresses.getText().toString(),username);
                                    OrderRef.child(companyNameNow).child(username).setValue(orders);
                                }
                            }
                            //new data
                            else{
                                Order orders = new Order(userID,addresses.getText().toString(),username);
                                OrderRef.child(companyNameNow).child(username).setValue(orders);
                            }
                        }

                        //Next Activity
                        Intent intent = new Intent(LocationPage.this, MainUserActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        entry=0;

        //get self location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //only enable for first time
                if(entry==0){

                    //store user Lat Lng
                    userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));

                    getAddress(location.getLatitude(), location.getLongitude());

                    entry++;
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        //asking for map permission with user/ location permission
        askLocationPermission();
    }

    private void askLocationPermission() {

        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(LocationPage.this,"This app needs access to your location " +
                        "to access google map",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    //geocode lat lng to address
    public void getAddress(double latitude2, double longitude2){
        Geocoder geocoder;
        double latitude = latitude2;
        double longitude = longitude2;
        List<Address> addressesList;

        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addressesList = geocoder.getFromLocation(latitude, longitude,1);
            String address = addressesList.get(0).getAddressLine(0);

            addresses.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void locate(View v){
        entry=0;
    }
}