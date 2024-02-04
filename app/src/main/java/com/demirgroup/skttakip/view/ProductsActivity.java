package com.demirgroup.skttakip.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.Adapters.ProductAdapter;
import com.demirgroup.skttakip.databinding.ActivityProductsBinding;
import com.demirgroup.skttakip.model.ProductInfoClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProductInfoClass productInfoClass;
    private ActivityProductsBinding binding;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityProductsBinding.inflate(getLayoutInflater());
       View view = binding.getRoot();
       setContentView(view);
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        productInfoClassArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(productInfoClassArrayList,this);
        binding.productRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        getData();
        binding.productRecyclerview.setAdapter(productAdapter);
    }

    ArrayList<ProductInfoClass> productInfoClassArrayList;
    private void getData() {
        firestore.collection("productInfo").orderBy("sktDate", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                            String productName = (String) snapshot.get("productName");
                            String productNote = (String) snapshot.get("productNote");
                            String addDate = (String) snapshot.get("addDate");
                            String sktDate = (String) snapshot.get("sktDate");
                            String barcodeNumber = (String) snapshot.get("barcodeNumber");
                            String productImage = (String) snapshot.get("productImage");
                            String productId=snapshot.getId();
                            // Mevcut tarihi alma ve stringe dönüştürme
                            LocalDate currentDate = LocalDate.now();
                            long daysBetween = 0;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                String currentDateAsString = currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                                LocalDate dateToSubtract = LocalDate.parse(sktDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                               daysBetween = ChronoUnit.DAYS.between(currentDate, dateToSubtract);
                            }
                            if (daysBetween >=0){
                                productInfoClass = new ProductInfoClass(barcodeNumber,productImage,productName,productNote,sktDate,addDate,daysBetween,productId);
                                productInfoClassArrayList.add(productInfoClass);
                                productAdapter.notifyDataSetChanged();
                            }
                            //String productBarcode = (String) snapshot.get("productBarcode");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductsActivity.this, "Beklnemedik bir hata oluştu..", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}