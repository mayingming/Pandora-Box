package com.example.mobilepro.ui.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.mobilepro.FriendAdapter;
import com.example.mobilepro.R;
import com.example.mobilepro.RecAdapter;
import com.example.mobilepro.item;
import com.example.mobilepro.tflite.Classifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements RecAdapter.OnRecmendationListener {

    private EditText userInput;
    private Button search;
    private HomeViewModel homeViewModel;
    private String searchToken;
    private Button camera;
    private Button album;
    private Context context;
    private final int OPEN_ALBUM_FLAG = 1023;
    private final int OPEN_CAMERA_FLAG = 1024;
    private String mSaveDir;
    private String mFileName;
    private String result;
    private RecAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<item> reclist;
    private ArrayList<String> recids;
    private item recitem;
    private String recitemid;

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
        camera = (Button) getView().findViewById(R.id.camera);
        album = (Button) getView().findViewById(R.id.gallery);
        context = getActivity();
        db = FirebaseFirestore.getInstance();
        db.collection("123")
                .whereArrayContains("city", "melbourne")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reclist = new ArrayList<>();
                            recids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                double price;
                                try {
                                    price = Double.valueOf(data.get("price").toString());
                                } catch (Exception x) {
                                    price = 0;
                                }
                                recitem = new item((String) data.get("name"), (String) data.get("address"), (String) data.get("image"), (String) data.get("shopName"), price, (String) data.get("phone"), (String) data.get("description"), (String) data.get("time"), (String) data.get("city"), null, null);
                                reclist.add(recitem);
                                recids.add(document.getId());
                            }
                            initRecyleview();
                        }
                    }
                });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchToken = userInput.getText().toString();
                Intent intent = new Intent(getActivity(), searchresult.class);
                intent.putExtra("token", searchToken);
                startActivity(intent);
            }
        });
    }
//        camera.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//
//                Intent intent;
//                mSaveDir = Environment.getExternalStorageDirectory() + "/wyk_dir/";
//                File dir = new File(mSaveDir);
//                if (!dir.exists()) {
//                    dir.mkdir();
//                }
//                mFileName = "WYK" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//                File file = new File(mSaveDir, mFileName);
//                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//                startActivityForResult(intent, OPEN_CAMERA_FLAG);
//
//
//            }
//        });


//        album.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent;
//                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(intent, OPEN_ALBUM_FLAG);
//            }
//        });
//

//
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        File file;
//        Bitmap bitmap;
//        OutputStream outputStream;
//        Classifier classifier = null;
//
//
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case OPEN_ALBUM_FLAG:
//                    Uri originUri = data.getData();
//                    String[] proj = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContext().getContentResolver().query(originUri, proj, null, null, null);
//                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    cursor.moveToFirst();
//                    String path = cursor.getString(columnIndex);
//                    bitmap = getCompressBitmap(path);
//                    List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
//                    result = results.get(0).getTitle();
//                    Log.d("camera",result);
//                    String saveDir = Environment.getExternalStorageDirectory() + "/wyk_dir/";
//                    File dir = new File(saveDir);
//                    if (!dir.exists()) {
//                        dir.mkdir();
//                    }
//
//                    String fileName = "tmp.jpg";
//                    file = new File(saveDir, fileName);
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                    try {
//                        file.createNewFile();
//                        outputStream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                        System.out.println("file size:" + file.length());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    break;
//                case OPEN_CAMERA_FLAG:
//                    file = new File(mSaveDir + mFileName);
//                    bitmap = getCompressBitmap(mSaveDir + mFileName);
//                    List<Classifier.Recognition> results2 = classifier.recognizeImage(bitmap);
//                    result = results2.get(0).getTitle();
//                    Log.d("camera",result);
//                    try {
//                        outputStream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                        System.out.println("file size:" + file.length());
//                        break;
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//
//    private Bitmap getCompressBitmap(String path) {
//        Bitmap bitmap;
//        Bitmap newBitmap;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Classifier classifier = new Classifier(this);
//        options.inJustDecodeBounds = true;
//        bitmap = BitmapFactory.decodeFile(path, options);
//
//        int width = options.outWidth;
//        int height = options.outHeight;
//
//        float scaleWidth=((float) classifier.getImageSizeX())/width;
//        float scaleHeight=((float)classifier.getImageSizeY())/height;
//
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleWidth,scaleHeight);
//
//        newBitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
//        return newBitmap;
//    }

    public void initRecyleview() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        RecyclerView recRecyclerView = getView().findViewById(R.id.recommendation);
        recRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecAdapter(reclist, context, this);
        recRecyclerView.setAdapter(adapter);
    }
    @Override
    public void OnRecmendationClick(int position) {
        Log.d("click", "onclick: clicked.");
        Intent intent = new Intent(getActivity(), SomeActivity.class);
        intent.putExtra("id",recids.get(position));
        startActivity(intent);
        recitemid  = recids.get(position);
    }
}
