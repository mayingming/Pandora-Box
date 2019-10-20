package com.example.mobilepro.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobilepro.R;

public class HomeFragment extends Fragment {

    private EditText userInput;
    private Button search;
    private HomeViewModel homeViewModel;
    private String searchToken;

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
        userInput = (EditText) getView().findViewById(R.id.userInput);
        search = (Button) getView().findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                searchToken = userInput.getText().toString();
                Intent intent = new Intent(getActivity(), searchresult.class);
                intent.putExtra("token",searchToken);
                startActivity(intent);
            }
        });
    }
}
