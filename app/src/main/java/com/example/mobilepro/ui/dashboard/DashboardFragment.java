package com.example.mobilepro.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.mobilepro.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button buttonChooseImage;
    private Button buttonPost;
    private ProgressBar progressBar;
    private ImageView uploadImageView;
    private EditText uploadName;
    private EditText uploadshopName;
    private EditText uploadPrice;
    private EditText uploadAddress;
    private EditText uploadPhone;
    private EditText uploadDescription;

    private Uri imageURI;
    private Map<String, Object> item;

    private FirebaseFirestore db;
    private LinearLayoutManager layoutManager;
    private Context context;

    private DashboardViewModel dashboardViewModel;

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


        buttonChooseImage = (Button) getView().findViewById(R.id.choosePic);
        buttonPost = (Button) getView().findViewById(R.id.post);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        uploadImageView = (ImageView) getView().findViewById(R.id.uploadImageView);
        uploadName = (EditText) getView().findViewById(R.id.upload_name);
        uploadshopName = (EditText) getView().findViewById(R.id.upload_shopName);
        uploadPrice = (EditText) getView().findViewById(R.id.upload_price);
        uploadAddress = (EditText) getView().findViewById(R.id.upload_address);
        uploadPhone = (EditText) getView().findViewById(R.id.upload_phone);
        uploadDescription = (EditText) getView().findViewById(R.id.upload_discription);

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilechooser();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = new HashMap<String, Object>();
                item.put("name",uploadName.getText().toString());
                item.put("shopName",uploadshopName.getText().toString());
                item.put("address",uploadAddress.getText().toString());
                item.put("phone",uploadPhone.getText().toString());
                item.put("description",uploadDescription.getText().toString());
                item.put("price",Double.valueOf(uploadPrice.getText().toString()));
                db.collection("123")
                        .add(item)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String id = documentReference.getId();
                                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(id);
                                Log.d("uri",imageURI.getPath());
                                Uri path = Uri.fromFile((new File(imageURI.getPath())));
                                UploadTask uploadTask = storageReference.putFile(path);
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
                                            Log.d("123",downloadUri.toString());
                                        }
                                    }
                                });
                            }
                        });
            }
        });
    }
    private void openFilechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null) {
            imageURI = data.getData();
            Glide.with(this).load(imageURI).into(uploadImageView);
        }
    }
}