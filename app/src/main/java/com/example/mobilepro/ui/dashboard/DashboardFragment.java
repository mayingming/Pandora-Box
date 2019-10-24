package com.example.mobilepro.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.mobilepro.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DashboardFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button buttonChooseImage;
    private Button buttonPost;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    private ImageView uploadImageView;
    private EditText uploadName;
    private EditText uploadshopName;
    private EditText uploadPrice;
    private EditText uploadAddress;
    private EditText uploadPhone;
    private EditText uploadDescription;
    private double longitude;
    private double latitude;
    private String city;

    private FusedLocationProviderClient client;

    private String imageUrl;

    private Uri imageURI;
    private Map<String, Object> item;

    private FirebaseFirestore db;
    private LinearLayoutManager layoutManager;
    private Context context;


    private DashboardViewModel dashboardViewModel;
    private FirebaseAuth myAuthentication;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return root;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getActivity();
        layoutManager = new LinearLayoutManager(context);
        db = FirebaseFirestore.getInstance();

        myAuthentication = FirebaseAuth.getInstance();
        user = myAuthentication.getCurrentUser();
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(getActivity());


        buttonChooseImage = (Button) getView().findViewById(R.id.choosePic);
        buttonPost = (Button) getView().findViewById(R.id.post);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar2 = (ProgressBar) getView().findViewById(R.id.progressBar2);
        uploadImageView = (ImageView) getView().findViewById(R.id.uploadImageView);
        uploadName = (EditText) getView().findViewById(R.id.upload_name);
        uploadshopName = (EditText) getView().findViewById(R.id.upload_shopName);
        uploadPrice = (EditText) getView().findViewById(R.id.upload_price);
        uploadAddress = (EditText) getView().findViewById(R.id.upload_address);
        uploadPhone = (EditText) getView().findViewById(R.id.upload_phone);
        uploadDescription = (EditText) getView().findViewById(R.id.upload_discription);

        progressBar.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);

        if(ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        else
        {
            client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            addresses = null;
                        }
                        if(addresses!=null && !addresses.isEmpty())
                            city = addresses.get(0).getLocality();
                        if(city==null)
                            city = "Melbourne";
                        Log.d("loc", city);
                    }
                }
            });
        }

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilechooser();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                item = new HashMap<String, Object>();
                item.put("name",uploadName.getText().toString());
                item.put("shopName",uploadshopName.getText().toString());
                item.put("address",uploadAddress.getText().toString());
                item.put("phone",uploadPhone.getText().toString());
                item.put("description",uploadDescription.getText().toString());
                item.put("image",imageUrl);
                item.put("latitude", latitude);
                item.put("longitude", longitude);
                item.put("city", city);
                if(user!=null)
                    item.put("user", user.getEmail());
                String[] tags = uploadName.getText().toString().split(" ");
                List<String> tagList = new ArrayList<String>();
                int size = tags.length;
                for(int i=0;i<size;i++)
                    for(int j=i;j<size;j++)
                    {
                        String tagCombo = "";
                        for(int k=i;k<j;k++)
                            tagCombo = tagCombo+tags[k]+" ";
                        tagCombo+=tags[j];
                        tagList.add(tagCombo.toLowerCase());
                    }
//                for(String s:tags)
//                    tagList.add(s.toLowerCase());
                item.put("tags",tagList);
                double p;
                try {
                    p = Double.valueOf(uploadPrice.getText().toString());
                }
                catch (Exception x) {
                    p=0;
                }
                item.put("price",p);
                db.collection("123")
                        .add(item)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressBar.setVisibility(View.INVISIBLE);
                                promptMessage("Post Successful!");
                                uploadName.setText("");
                                uploadshopName.setText("");
                                uploadPrice.setText("");
                                uploadAddress.setText("");
                                uploadPhone.setText("");
                                uploadDescription.setText("");
                                uploadImageView.setImageResource(R.drawable.spacedog);
                            }
                        });
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION},1);
    }
    private void openFilechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void promptMessage(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = toast.getView();
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toast.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar2.setVisibility(View.VISIBLE);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null) {
            Random random = new Random();
            imageURI = data.getData();
            Glide.with(this).load(imageURI).into(uploadImageView);

            String id = "image/"+System.currentTimeMillis() + "" + random.nextInt(10000) +".JPEG";
            FirebaseStorage fs = FirebaseStorage.getInstance("gs://mobile-test-fea0b.appspot.com");
            final StorageReference storageReference = fs.getReference().child(id);

            UploadTask uploadTask = storageReference.putFile(imageURI);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        throw  task.getException();
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        imageUrl = downloadUri.toString();
                        progressBar2.setVisibility(View.INVISIBLE);
                        Log.d("123",imageUrl);
                    }
                }
            });
        }
    }
}