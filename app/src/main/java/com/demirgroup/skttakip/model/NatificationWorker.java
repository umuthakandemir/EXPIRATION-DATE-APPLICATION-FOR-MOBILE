package com.demirgroup.skttakip.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.demirgroup.skttakip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class NatificationWorker extends Worker {
    FirebaseFirestore firestore;
    NotificationManager manager;
    ArrayList<CustomNotificationViewInfo> arrayList;
    Context mContext;
    CustomNotificationViewInfo customNotificationViewInfo;
    public NatificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
        arrayList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        chechdata();
        System.out.println("worker basladı..");
        return Result.success();
    }

    String productName;
    String date;
    String productId,barcodeNumber,productImage,productNamee="",productDatee="";
    int sayac =0;

    private void chechdata() {
        firestore.collection("productInfo").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                            date = (String) snapshot.get("sktDate");
                            String productNote= (String) snapshot.get("productNote");
                            productName = (String) snapshot.get("productName");
                            productId = snapshot.getId();
                             barcodeNumber = (String) snapshot.get("barcodeNumber");
                             productImage = (String) snapshot.get("productImage");
                            if (dateFormatFun(date) <= 3 && dateFormatFun(date) >0){
                                productNamee+="Ürün Adı: "+productName+"\nS.K.T.: "+date+"\n";
                                customNotificationViewInfo = new CustomNotificationViewInfo(productImage,productName,date,barcodeNumber,productNote);
                                arrayList.add(customNotificationViewInfo);
                                sayac++;
                            }
                            if (dateFormatFun(date) <0) {
                                deleteProduct();
                            }
                        }
                        if (sayac>0)
                            sendNotification();
                    }
                });
    }

    private void deleteProduct() {
        firestore.collection("productInfo")
                .document(productId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        HashMap<String, Object> productInfoMap = new HashMap<>();
                        productInfoMap.put("productId",productId);
                        productInfoMap.put("productName",productName);
                        productInfoMap.put("productImage",productImage);
                        productInfoMap.put("sktDate",date);
                        productInfoMap.put("barcodeNumber",barcodeNumber);
                        firestore.collection("deletedProducts")
                                .document(productId)
                                .set(productInfoMap)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Hata:",e.getLocalizedMessage());
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Başarı:","silinenler klasorube yuklendı.");
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Hata:",e.getLocalizedMessage());
                    }
                });
    }

    private void sendNotification() {
        notificationClass.sendNotification(mContext,productNamee);

    }

    private long dateFormatFun(String sktDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDate = LocalDate.now();
        long daysBetween = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String currentDateAsString = currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            LocalDate dateToSubtract = LocalDate.parse(sktDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            daysBetween = ChronoUnit.DAYS.between(currentDate, dateToSubtract);
            System.out.println("SKT tarihi çevrildi"+daysBetween);
        }
        return daysBetween;
    }
}
