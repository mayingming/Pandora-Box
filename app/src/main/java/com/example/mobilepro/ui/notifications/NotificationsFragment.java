package com.example.mobilepro.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.mobilepro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URI;

import static android.app.Activity.RESULT_OK;


public class NotificationsFragment extends Fragment {

    private Button userbutton;
    private ImageView userphoto;
    private TextView userName;
    private Object fragment;
    private URI userImageURL;
    private Button logOut;


    private NotificationsViewModel notificationsViewModel;
    FirebaseAuth myAuthentication = FirebaseAuth.getInstance();
    FirebaseUser user = myAuthentication.getCurrentUser();

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
        logOut = (Button) getView().findViewById(R.id.logOutButton);

        userbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userbutton.setVisibility(View.INVISIBLE);
                logOut.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                userbutton.setVisibility(View.VISIBLE);
                logOut.setVisibility(View.INVISIBLE);
                userName.setText("");
                userphoto.setImageResource(R.drawable.userphoto);
            }
        });

        if (user != null){
            userName.setText(user.getEmail());
            Glide.with(this).load(user.getPhotoUrl()).into(userphoto);
            userbutton.setVisibility(View.INVISIBLE);
        }
        else {
            logOut.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK)
        {
            try{
                userName.setText(user.getEmail());
                Glide.with(this).load(user.getPhotoUrl()).into(userphoto);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}