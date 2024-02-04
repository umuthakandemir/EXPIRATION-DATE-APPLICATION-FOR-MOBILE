package com.demirgroup.skttakip.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.AddproductBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class addProduct extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ActivityResultLauncher<String> permissionLauncher;
    private AddproductBinding binding;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddproductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        resultLauncher();
        progressDialoginit();
    }
    private void progressDialoginit() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void AddProductImage(View view){

        //camera permission
        if (ContextCompat.checkSelfPermission(addProduct.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(addProduct.this,Manifest.permission.CAMERA)){
                Snackbar.make(view,"save to need product image!!",Snackbar.LENGTH_INDEFINITE).setAction("give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                }).show();
            }else {
                //request permission
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }
        }else {
            //Give Image
            takePicture();

        }
        //storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(addProduct.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(addProduct.this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"save to need product image!!",Snackbar.LENGTH_INDEFINITE).setAction("give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else {
                //Give Image
            }
        }else {
            if (ContextCompat.checkSelfPermission(addProduct.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(addProduct.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"save to need product image!!",Snackbar.LENGTH_INDEFINITE).setAction("give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else {
                //Give Image
            }
        }
    }
    public void resultLauncher(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){

                }else
                    Toast.makeText(addProduct.this, "Need to product ımage ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void takePicture(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageData = (Bitmap) data.getExtras().get("data");
                System.out.println(imageData);
                if (imageData != null) {
                    // ImageView'a resmi set et
                    binding.productImage.setImageBitmap(imageData);
                }
            }
        } else if (requestCode==200 && resultCode == RESULT_OK) {
            binding.barcodeTextView1.setText(data.getStringExtra("barcodeData"));
        }
    }
    Bitmap imageData;
    public void SaveProduct(View view){
        save();
    }
    String formatSktDate;
    public void save() {
        UUID imageId = UUID.randomUUID();
        if (imageData != null){
            if (binding.barcodeTextView1.getText().toString().length()<=0){
                Toast.makeText(addProduct.this, "Lütfen barkod no gırın..", Toast.LENGTH_SHORT).show();
            }else{
                if (binding.SKTDate.getText().toString().contains(".")){
                    progressDialog.setTitle("Ürün yükleniyor.");
                    progressDialog.show();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            SktDate = LocalDate.parse(binding.SKTDate.getText().toString(),formatter);
                            formatSktDate = SktDate.format(formatter);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageData.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            String imagePath = "productImage/"+imageId+".jpg";
                            storageReference.child(imagePath).putBytes(data)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            StorageReference ImageReferance = firebaseStorage.getReference(imagePath);
                                            ImageReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    saveFirebase(uri.toString());
                                                }
                                            });
                                        }
                                    });
                        }catch(Exception e){
                            Toast.makeText(addProduct.this, "hata oluştu.: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else
                    Toast.makeText(addProduct.this, "Lütfen desteklenen bir tarih biçimi girin. Örnek: 01.01.2024", Toast.LENGTH_SHORT).show();

            }
        }else
            Toast.makeText(addProduct.this, "Lütfen ürünün fotoğrafını girin..", Toast.LENGTH_SHORT).show();
    }

    LocalDate SktDate;
    private void saveFirebase(String imageByte) {
        UUID propductId = UUID.randomUUID();
        HashMap<String, Object> productInfoMap = new HashMap<>();
        productInfoMap.put("productId",propductId.toString());
        productInfoMap.put("productName",binding.ProductName.getText().toString());
        productInfoMap.put("productImage",imageByte);
        productInfoMap.put("sktDate",formatSktDate.toString());
        productInfoMap.put("barcodeNumber",binding.barcodeTextView1.getText().toString());
        productInfoMap.put("productNote",binding.productnote.getText().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            productInfoMap.put("addDate",LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        firestore.collection("productInfo/").document(String.valueOf(propductId)).set(productInfoMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addProduct.this, "Beklenmedik bir hata oluştu..", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(addProduct.this, "Ürün başarıyla eklendi..", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        binding.SKTDate.setText("");
                        binding.productImage.setImageResource(R.drawable.addphoto);
                        binding.ProductName.setText("");
                        binding.productnote.setText("");
                        binding.barcodeTextView1.setText("");
                    }
                });
    }
    public void ScanBarcode(View view){
        Intent barcodeActIntent = new Intent(addProduct.this, BarcodeScanAct.class);
        startActivityForResult(barcodeActIntent,200);

    }
}