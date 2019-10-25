package com.example.mobilepro.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobilepro.R;
import com.example.mobilepro.item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class SomeActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private TextView itemName;
    private TextView itemshopName;
    private TextView itemPrice;
    private TextView itemAddress;
    private TextView itemPhone;
    private TextView itemDescription;
    private TextView itemReviews;
    private FirebaseFirestore db;
    private item item;
    private Context context;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_main);
        context = this;
        itemImageView = (ImageView) findViewById(R.id.imageView3);
        itemName = (TextView) findViewById(R.id.showItemName);
        itemshopName = (TextView) findViewById(R.id.showItemShopName);
        itemPrice = (TextView) findViewById(R.id.showItemPrice);
        itemAddress = (TextView) findViewById(R.id.showItemAddress);
        itemPhone = (TextView) findViewById(R.id.showItemPhone);
        itemDescription = (TextView) findViewById(R.id.showItemDescription);


        String itemid = getIntent().getExtras().getString("id");
        Log.d("item",itemid);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("123").document(itemid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        longitude = (double) document.getData().getOrDefault("longitude",0);
                        latitude = (double) document.getData().getOrDefault("latitude",0);
                        itemName.setText((String)document.getData().getOrDefault("name","null"));
                        itemshopName.setText((String)document.getData().getOrDefault("shopName","null"));
                        itemPrice.setText(document.getData().getOrDefault("price","null").toString());
                        itemPhone.setText((String)document.getData().getOrDefault("phone","null"));
                        itemAddress.setText((String)document.getData().getOrDefault("address","null"));
                        itemAddress.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                        itemDescription.setText((String)document.getData().getOrDefault("description","null"));
                        Glide.with(context)
                                .load((String)document.getData().getOrDefault("image","null"))
                                .into(itemImageView);
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });

        itemAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(longitude != 0 || latitude != 0){
                    Log.d("geo",longitude +"/////" +latitude);
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:"    + latitude +
                            ","       + longitude +
                            "?q="     + latitude +
                            ","       + longitude +
                            "("       + itemshopName.getText().toString() + ")"));
                    startActivity(i);
                }
            }
        });


    }
}
