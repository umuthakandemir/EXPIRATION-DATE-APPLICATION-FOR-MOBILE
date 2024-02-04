package com.demirgroup.skttakip.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.ActivityUpdateProductBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.HashMap;

public class updateProductAct extends AppCompatActivity {

    private ActivityUpdateProductBinding binding;
    FirebaseFirestore firestore;
    String documnetId;
    ProgressDialog progressDialog;
    Intent getDataIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);;
        getDataIntent =getIntent();
        getData();
        documnetId = getDataIntent.getStringExtra("documentId");
        binding.updateproductbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documnetId!=null){
                    if (binding.ProductName.getText().toString().length()>0){
                        if (binding.SKTDate.getText().toString().contains(".")){
                            setData();
                        }else
                            Toast.makeText(updateProductAct.this, "Lütfen desteklenen bir tarih biçimi girin. Örnek: 01.01.2024", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(updateProductAct.this, "Lütfen Ürün adını doldurun", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.dismiss();
        finish();
    }

    private void getData() {
        String productName=getDataIntent.getStringExtra("productName"),sktDate=getDataIntent.getStringExtra("sktDate"),productNote=getDataIntent.getStringExtra("productNote");
        binding.ProductName.setText(productName);
        binding.SKTDate.setText(sktDate);
        binding.productnote.setText(productNote);
    }

    private void diaologShow(String title){
        progressDialog.setTitle(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    private void setData() {
        diaologShow("Ürün güncelleniyor...");
        progressDialog.show();
        HashMap<String,Object> updateInfoProductMap=new HashMap<>();
        updateInfoProductMap.put("productName",binding.ProductName.getText().toString());
        updateInfoProductMap.put("sktDate",binding.SKTDate.getText().toString());
        if (binding.productnote.getText().length()>0)
            updateInfoProductMap.put("productNote",binding.productnote.getText().toString());
        firestore.collection("productInfo")
                .document(documnetId)
                .update(updateInfoProductMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.hide();
                        binding.productnote.setText(binding.productnote.getText().toString()+"\nGüncelleme Tarihi: "+LocalDate.now().toString());
                        Toast.makeText(updateProductAct.this, "Ürün güncellendi..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hide();
                        Toast.makeText(updateProductAct.this, "Bir Hata Oluştu..", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}