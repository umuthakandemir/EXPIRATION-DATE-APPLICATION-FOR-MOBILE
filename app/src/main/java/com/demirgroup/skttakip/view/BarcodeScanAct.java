package com.demirgroup.skttakip.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.demirgroup.skttakip.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeScanAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        new IntentIntegrator(BarcodeScanAct.this)
                .setOrientationLocked(false)
                .setTorchEnabled(true)
                .initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result.getContents() != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("barcodeData",result.getContents());
           setResult(RESULT_OK,resultIntent);
           finish();
        }else
            setResult(RESULT_CANCELED);
    }
}