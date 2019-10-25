package com.example.mobilepro.ui.notifications;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mobilepro.R;
import com.example.mobilepro.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView userImg;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri selectedImgUri;

    private EditText userEmail, userPassword, confirmPassword, userName;
    private ProgressBar progressBar;
    private Button regBtn;

    private FirebaseAuth myAuthentication;

    private TextView toSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        confirmPassword = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        progressBar = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);

        progressBar.setVisibility(View.INVISIBLE);

        myAuthentication = FirebaseAuth.getInstance();
        toSignIn = findViewById(R.id.toSignIn);
        toSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String confirmPassword = RegisterActivity.this.confirmPassword.getText().toString();
                final String name = userName.getText().toString();

                if( email.isEmpty() || name.isEmpty() || password.isEmpty()|| !password.equals(confirmPassword)){
                    //something goes wrong, message prompt
                    promptMessage("Please Verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    CreateUserAccount(email, name, password);
                }
            }
        });


        userImg = findViewById(R.id.regUserPhoto);

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("onclick","is");
//                if (Build.VERSION.SDK_INT>=22){

                    checkAndRequestForPermission();
//                }
//                else
                {
                    openGallery();
                }
            }
        });
    }
    private void CreateUserAccount(String email, final String name, String password) {
        myAuthentication.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        //user account create successfully
                        promptMessage("Account created");
                        //After create account, we need to update user profile and name
                        updateUserInfo(name, selectedImgUri, myAuthentication.getCurrentUser());
                    }
                    else{
                        promptMessage("Account creation failed");
                        regBtn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility((View.INVISIBLE));
                    }
                }
            });
    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photo");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded successfully,we can get our image url\
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // uri contain user image url

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //user info update successfully
                                            promptMessage("Register Complete");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });

    }

    private void updateUI() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
        finish();
    }

    private void promptMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        //TODO: OPEN gallery intent and wait for user to pick an image
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(galleryIntent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        PReqCode);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==RESULT_OK && requestCode == REQUESCODE && data != null){
            selectedImgUri = data.getData();
            userImg.setImageURI(selectedImgUri);
        }
    }
}
