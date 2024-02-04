package com.demirgroup.skttakip.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;

import com.demirgroup.skttakip.databinding.ProductrecyclerviewrowBinding;
import com.demirgroup.skttakip.model.ProductInfoClass;
import com.demirgroup.skttakip.view.updateProductAct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    ArrayList<ProductInfoClass> productInfoClassArrayList;
    private Context mContext;
    private FirebaseFirestore firestore;

    public ProductAdapter(ArrayList<ProductInfoClass> productInfoClassArrayList,Context mContext) {
        this.productInfoClassArrayList = productInfoClassArrayList;
        this.mContext = mContext;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductrecyclerviewrowBinding productrecyclerviewrowBinding = ProductrecyclerviewrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ProductHolder(productrecyclerviewrowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        holder.productrecyclerviewrowBinding.productName.setText(productInfoClassArrayList.get(position).productname);
        holder.productrecyclerviewrowBinding.sktDate.setText("S.K.T.: "+productInfoClassArrayList.get(position).sktDate);
        holder.productrecyclerviewrowBinding.remainingSktDate.setText("son kullanma tarihi bitmesine: "+productInfoClassArrayList.get(position).remainingdate+" gün kaldı..");
        Picasso.get().load(productInfoClassArrayList.get(position).productImage).into(holder.productrecyclerviewrowBinding.productImage);
        holder.productrecyclerviewrowBinding.BarcodeTextview.setText("Barkod Numarası: " +productInfoClassArrayList.get(position).barcodeNumber);
        holder.productrecyclerviewrowBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductfun(productInfoClassArrayList.get(position).productId,position);
            }
        });
        holder.productrecyclerviewrowBinding.updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), updateProductAct.class);
                intent.putExtra("documentId",productInfoClassArrayList.get(position).productId);
                intent.putExtra("productName",productInfoClassArrayList.get(position).productname);
                intent.putExtra("sktDate",productInfoClassArrayList.get(position).sktDate);
                intent.putExtra("productNote",productInfoClassArrayList.get(position).productNote);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    private void deleteProductfun(String ProductId,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage("Bu ürünğ silmek istediğinize emin misiniz_?")
                .setTitle("Ürün Silme Sihirbazı")
                .setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firestore.collection("productInfo")
                                .document(ProductId)
                                .delete()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Ürün silinemdi.: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mContext, "Ürün silindi", Toast.LENGTH_SHORT).show();
                                        productInfoClassArrayList.remove(position);
                                        notifyItemChanged(position);
                                    }
                                });
                    }
                }).setNegativeButton("silme", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "Ürün silinmedi.", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return productInfoClassArrayList.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder{

        ProductrecyclerviewrowBinding productrecyclerviewrowBinding;
        public ProductHolder(ProductrecyclerviewrowBinding productrecyclerviewrowBinding) {
            super(productrecyclerviewrowBinding.getRoot());
            this.productrecyclerviewrowBinding = productrecyclerviewrowBinding;
        }
    }
}
