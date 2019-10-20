package com.example.mobilepro.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilepro.FriendAdapter;
import com.example.mobilepro.R;
import com.example.mobilepro.item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class searchresult extends AppCompatActivity implements FriendAdapter.OnItemListener{


    private LinearLayoutManager layoutManager;
    private FirebaseFirestore db;
    private FriendAdapter adapter;
    private List<item> msgList;
    private List<String> ids;
    String itemid;
    private item item;
    private String token;


    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        layoutManager = new LinearLayoutManager(this);
        db = FirebaseFirestore.getInstance();
        token = getIntent().getExtras().getString("token");

        db.collection("123")
                .whereArrayContains("tags",token.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            msgList = new ArrayList<>();
                            ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                double price;
                                try {
                                    price = Double.valueOf(data.get("price").toString());
                                } catch (Exception x) {
                                    price = 0;
                                }
                                item = new item((String) data.get("name"), (String) data.get("address"), (String) data.get("image"), (String) data.get("shopName"), price, (String) data.get("phone"), (String) data.get("description"), (String) data.get("time"), (String) data.get("city"),null, null);
                                msgList.add(item);
                                ids.add(document.getId());
                            }
                            initRecyleview();
                        }
                    }
                });
    }

    public void initRecyleview(){
        RecyclerView msgRecyclerView = findViewById(R.id.list);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new FriendAdapter(msgList,this,this);
        msgRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onItemClick(int position) {
        Log.d("click", "onclick: clicked.");
        Intent intent = new Intent(this, SomeActivity.class);
        intent.putExtra("id",ids.get(position));
        startActivity(intent);
        itemid  = ids.get(position);
    }
}



