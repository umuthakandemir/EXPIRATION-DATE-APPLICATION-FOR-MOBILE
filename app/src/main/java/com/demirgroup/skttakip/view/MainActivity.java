package com.demirgroup.skttakip.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.ActivityMainBinding;
import com.demirgroup.skttakip.databinding.ActivityProductsBinding;
import com.demirgroup.skttakip.model.NatificationWorker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> permissionlauncher;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.POST_NOTIFICATIONS)){
                Snackbar.make(view,"isin gerekli ",Snackbar.LENGTH_INDEFINITE).setAction("give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionlauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                }).show();
            }else {
                //request permission
                permissionlauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }else {
            //Give Image
            workerNotification();
        }
    }

    public void myNotes(View view){
        Intent addNoteIntent=new Intent(MainActivity.this,addNoteActivity.class);
        startActivity(addNoteIntent);
    }
    private void registerLauncher() {
        permissionlauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    workerNotification();
                }else
                    Toast.makeText(MainActivity.this, "izin gerekli..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void workerNotification(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        WorkRequest workRequest = new PeriodicWorkRequest.Builder(NatificationWorker.class,1,TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this).enqueue(workRequest);

    }

    public void checkProduct(View view){
        Intent checkProductIntent=new Intent(MainActivity.this, checkProduct.class);
        startActivity(checkProductIntent);
    }
    public void myNoteFun(View view){
        Intent intent = new Intent(MainActivity.this,addNoteActivity.class);
        startActivity(intent);
    }
    public void Products(View view) {
        Intent productIntent = new Intent(MainActivity.this, ProductsActivity.class);
        startActivity(productIntent);
    }

    public void AddProduct(View view){
        Intent intent =new Intent(MainActivity.this, addProduct.class);
        startActivity(intent);
    }
}