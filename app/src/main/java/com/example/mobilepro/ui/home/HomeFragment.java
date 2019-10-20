package com.example.mobilepro.ui.home;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobilepro.R;
import com.example.mobilepro.RecAdapter;
import com.example.mobilepro.item;
import com.example.mobilepro.tflite.Classifier;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements RecAdapter.OnRecmendationListener {

    private EditText userInput;
    private Button search;
    private HomeViewModel homeViewModel;
    private String searchToken;
    private Button camera;
    private Button album;
    private Context context;
    private String result;
    private RecAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<item> reclist;
    private ArrayList<String> recids;
    private item recitem;
    private String recitemid;
    private SensorManager sm;
    private float acelVal;
    private float acelLast;
    private float shake;
    private TextView zuoshang;
    private String searchfield;
    private String city;
    private FusedLocationProviderClient client;
    private double longitude;
    private double latitude;
    private Classifier classifier;
    private Uri imageURI;
    private String imageResult;

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
        zuoshang = (TextView) getView().findViewById(R.id.textView);
        context = getActivity();
        db = FirebaseFirestore.getInstance();
        searchfield = "city";

        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        if(ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            city = "Melbourne";
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
                        if(addresses!=null)
                            city = addresses.get(0).getLocality();
                        if(city==null)
                            city = "Melbourne";
                        Log.d("loc", city);
                    }
                    else
                    {
                        city="Melbourne";
                    }
                    zuoshang.setText(city);

                    db.collection("123")
                            .whereArrayContains(searchfield, city)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        reclist = new ArrayList<>();
                                        recids = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = document.getData();
                                            double price, latitude, longitude;
                                            try {
                                                price = Double.valueOf(data.get("price").toString());
                                                latitude = Double.valueOf(data.get("latitude").toString());
                                                longitude = Double.valueOf(data.get("longitude").toString());
                                            } catch (Exception x) {
                                                price = 0;
                                                latitude = 0;
                                                longitude = 0;
                                            }
                                            recitem = new item((String) data.get("name"), (String) data.get("address"), (String) data.get("image"), (String) data.get("shopName"), price, (String) data.get("phone"), (String) data.get("description"), (String) data.get("time"), (String) data.get("city"), null, null,latitude ,longitude);
                                            reclist.add(recitem);
                                            recids.add(document.getId());
                                        }
                                        initRecyleview(reclist);
                                    }
                                }
                            });
                }
            });
        }


        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchToken = userInput.getText().toString();
                Intent intent = new Intent(getActivity(), searchresult.class);
                intent.putExtra("token", searchToken);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude",latitude);
                startActivity(intent);
            }
        });

        album.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                openFilechooser();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageURI = data.getData();
            try {
                recreateClassifier();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
                Bitmap newBitmap = getCompressBitmap(bitmap);
                List<Classifier.Recognition> results = classifier.recognizeImage(newBitmap);
                result = results.get(0).getTitle();
                imageResult = result;
                Log.d("result", result);
                searchToken = imageResult.toLowerCase();
                Intent intent = new Intent(getActivity(), searchresult.class);
                intent.putExtra("token", searchToken);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude",latitude);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // compress the old bitmap to new one
    private Bitmap getCompressBitmap(Bitmap bitmap) {
        Bitmap newBitmap;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth=((float) classifier.getImageSizeX())/width;
        float scaleHeight=((float)classifier.getImageSizeY())/height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        newBitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return newBitmap;
    }

    // create classifier
    private void recreateClassifier() {
        if (classifier != null) {
            classifier.close();
            classifier = null;
        }

        try {
            //LOGGER.d(
            //        "Creating classifier (model=%s, device=%s, numThreads=%d)", model, device, numThreads);
            classifier = Classifier.create(this);
        } catch (IOException e) {
            //LOGGER.e(e, "Failed to create classifier.");
        }
    }


    private final SensorEventListener sensorListener = new SensorEventListener() {

        ArrayList<item> newItemList;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;
            if (shake > 12) {
                Random random = new Random();
                int abc = random.nextInt(reclist.size());
                for(int i=0;i<4;i++)
                {
                    if(abc>=reclist.size())
                        abc=0;
                    newItemList.add(reclist.get(abc));
                    abc++;
                }
                initRecyleview(newItemList);
                Log.d("shake", "shaked");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    public void initRecyleview(ArrayList itemlist) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        RecyclerView recRecyclerView = getView().findViewById(R.id.recommendation);
        recRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecAdapter(itemlist, context, this);
        recRecyclerView.setAdapter(adapter);
    }
    @Override
    public void OnRecmendationClick(int position) {
        Log.d("click", "onclick: clicked.");
        Intent intent = new Intent(getActivity(), SomeActivity.class);
        intent.putExtra("id",recids.get(position));
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude",latitude);
        startActivity(intent);
        recitemid  = recids.get(position);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void openFilechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
}
