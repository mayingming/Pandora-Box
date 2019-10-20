package com.example.mobilepro.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobilepro.FriendAdapter;
import com.example.mobilepro.item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.mobilepro.R;

public class HomeFragment extends Fragment implements FriendAdapter.OnItemListener{

    private LinearLayoutManager layoutManager;
    private Context context;
    private String token;
    private EditText userInput;
    private Button search;
    private FirebaseFirestore db;
    private FriendAdapter adapter;
    private List<item> msgList;


    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getActivity();
        layoutManager = new LinearLayoutManager(context);
        db = FirebaseFirestore.getInstance();

        userInput = (EditText) getView().findViewById(R.id.userInput);
        search = (Button) getView().findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                token = userInput.getText().toString();
                db.collection("123")
                        .whereArrayContains("tags",token.toLowerCase())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    msgList = new ArrayList<>();
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String,Object> data = document.getData();
                                        item item = new item((String)data.get("name"),(String)data.get("address"),(String)data.get("shopName"),(String)data.get("image"),Double.valueOf(data.get("price").toString()));
                                        msgList.add(item);
                                    }
                                    initRecyleview();
                                }
                            }
                        });
            }
        });
    }

    public void initRecyleview(){
        RecyclerView msgRecyclerView = getView().findViewById(R.id.list);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new FriendAdapter(msgList,context,this);
        msgRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        Log.d("click", "onclick: clicked.");
        Intent intent = new Intent(getActivity(), SomeActivity.class);
        startActivity(intent);


    }
}