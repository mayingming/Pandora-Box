package com.example.mobilepro.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilepro.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SomeActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private TextView itemName;
    private TextView itemshopName;
    private TextView itemPrice;
    private TextView itemAddress;
    private TextView itemPhone;
    private TextView itemdDescription;
    private TextView itemReviews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_main);

    }
}
