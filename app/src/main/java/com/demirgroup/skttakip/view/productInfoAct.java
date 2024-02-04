package com.demirgroup.skttakip.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.ActivityProductInfoBinding;
import com.squareup.picasso.Picasso;

public class productInfoAct extends AppCompatActivity {

    private ActivityProductInfoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getData();
    }

    private void getData() {
        Intent intent=getIntent();
        String barcodeNumber = intent.getStringExtra("barcodeNumber"),productNote = intent.getStringExtra("productNote"),productName=intent.getStringExtra("productName");
        binding.ProductName.setText(productName);
        binding.productnote.setText(productNote);
        Picasso.get().load(intent.getStringExtra("productImage")).into(binding.productImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
        startActivity(intent);
    }
}