package com.example.mobilepro.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.mobilepro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static android.app.Activity.RESULT_OK;


public class NotificationsFragment extends Fragment {

    private Button userbutton;
    private ImageView userphoto;
    private boolean isLogIn = false;
    private TextView userName;
    private Object fragment;
    private URI userImageURL;


    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragment = this;
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        userbutton = (Button) getView().findViewById(R.id.userbutton);
        userphoto = (ImageView) getView().findViewById(R.id.userimage);
        userName = (TextView) getView().findViewById(R.id.userName);

        userbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivityForResult(intent, 3);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("trans",""+123);
        if (requestCode == 3 && resultCode == RESULT_OK)
        {
            try{
                //Log.d("trans",data.getExtras().getString("userName"));
                FirebaseAuth myAuthentication = FirebaseAuth.getInstance();
                FirebaseUser user = myAuthentication.getCurrentUser();
                userName.setText(user.getEmail());
                Glide.with(this).load(user.getPhotoUrl()).into(userphoto);
                isLogIn = data.getExtras().getBoolean("isLogIn");
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}