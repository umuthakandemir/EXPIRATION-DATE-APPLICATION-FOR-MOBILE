package com.demirgroup.skttakip.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.ActivityCheckProductBinding;
import com.demirgroup.skttakip.model.ProductInfoClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class checkProduct extends AppCompatActivity {

    private ActivityCheckProductBinding binding;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firestore = FirebaseFirestore.getInstance();
        mediaPlayer  = MediaPlayer.create(checkProduct.this,R.raw.ringtones);
       binding.barcodeTxt.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
           }

           @Override
           public void afterTextChanged(Editable s) {
               if (s.length()==13)
                   checkProductfun(s.toString());
           }
       });
    }
    public void scanBarcode(View view){
        Intent scanBarcodeIntent =new Intent(checkProduct.this, BarcodeScanAct.class);
        startActivityForResult(scanBarcodeIntent,205);
    }
    private void checkProductfun(String barcodeNumber) {
        ProgressDialog dialog = new ProgressDialog(checkProduct.this);
        dialog.setTitle("Ürün Sorgulanıyor...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        firestore.collection("productInfo/")
                .whereEqualTo("barcodeNumber",barcodeNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for (DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                                String productName = (String) snapshot.get("productName");
                                String productNote = (String) snapshot.get("productNote");
                                String addDate = (String) snapshot.get("addDate");
                                String sktDate = (String) snapshot.get("sktDate");
                                String barcodeNumber = (String) snapshot.get("barcodeNumber");
                                String productImage = (String) snapshot.get("productImage");
                                // Mevcut tarihi alma ve stringe dönüştürme
                                LocalDate currentDate = LocalDate.now();
                                long daysBetween = 0;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    String currentDateAsString = currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                                    LocalDate dateToSubtract = LocalDate.parse(sktDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                                    daysBetween = ChronoUnit.DAYS.between(currentDate, dateToSubtract);
                                }
                                binding.sktday.setText(daysBetween+" Gün kaldı.");
                                binding.ProductName.setText(productName);
                                binding.productnote.setText(productNote);
                                binding.SKTDate.setText(sktDate);
                                Picasso.get().load(productImage).into(binding.productImage);
                                binding.SKTDate.setEnabled(false);
                                binding.ProductName.setEnabled(false);
                                binding.productnote.setEnabled(false);
                                mediaPlayer.start();
                                dialog.hide();
                            }
                        }else{
                            dialog.hide();
                            Toast.makeText(checkProduct.this, "Ürün bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.hide();
                        Toast.makeText(checkProduct.this, "Ürün getirilemedi.: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    MediaPlayer mediaPlayer;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 205 && resultCode == RESULT_OK){
            binding.barcodeTxt.setText(data.getStringExtra("barcodeData"));
        }
    }
}