package com.demirgroup.skttakip.Adapters;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demirgroup.skttakip.databinding.CustomrecyclerviewrowBinding;
import com.demirgroup.skttakip.model.CustomNotificationViewInfo;
import com.demirgroup.skttakip.view.productInfoAct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class customNotificationViewAdapter extends RecyclerView.Adapter<customNotificationViewAdapter.customNotificationViewholder> {
    ArrayList<CustomNotificationViewInfo> arrayList;
    @NonNull
    @Override
    public customNotificationViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomrecyclerviewrowBinding customrecyclerviewrowBinding = CustomrecyclerviewrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new customNotificationViewholder(customrecyclerviewrowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull customNotificationViewholder holder, int position) {
        holder.customrecyclerviewrowBinding.bildirimContemtyetxt.setText(arrayList.get(position).productName);
        Picasso.get().load(arrayList.get(position).productImage).into(holder.customrecyclerviewrowBinding.productImageviewNotification);
        holder.customrecyclerviewrowBinding.notificationButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), productInfoAct.class);
                intent.putExtra("productImage",arrayList.get(position).productImage);
                intent.putExtra("barcodeNumber",arrayList.get(position).barcodeNumber);
                intent.putExtra("productName",arrayList.get(position).productName);
                intent.putExtra("productNote",arrayList.get(position).productNote);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class customNotificationViewholder extends RecyclerView.ViewHolder{
        CustomrecyclerviewrowBinding customrecyclerviewrowBinding;
        public customNotificationViewholder(CustomrecyclerviewrowBinding customrecyclerviewrowBinding) {
            super(customrecyclerviewrowBinding.getRoot());
            this.customrecyclerviewrowBinding = customrecyclerviewrowBinding;
        }
    }
}
