package com.demirgroup.skttakip.Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demirgroup.skttakip.databinding.AddnoterecyclerviewrolBinding;
import com.demirgroup.skttakip.model.addNoteInfoClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class adNoteAdapter extends RecyclerView.Adapter<adNoteAdapter.addNoteHolder> {
    ArrayList<addNoteInfoClass> addNoteInfoClassArrayList;
    private FirebaseFirestore firestore;
    public Context mContext;

    public adNoteAdapter(ArrayList<addNoteInfoClass> addNoteInfoClassArrayList,Context mContext) {
        this.addNoteInfoClassArrayList = addNoteInfoClassArrayList;
        this.mContext = mContext;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public addNoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddnoterecyclerviewrolBinding addnoterecyclerviewrolBinding = AddnoterecyclerviewrolBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new addNoteHolder(addnoterecyclerviewrolBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull addNoteHolder holder, int position) {
        holder.addnoterecyclerviewrolBinding.note.setText(addNoteInfoClassArrayList.get(position).note);
        holder.addnoterecyclerviewrolBinding.personname.setText(addNoteInfoClassArrayList.get(position).personName);
        holder.addnoterecyclerviewrolBinding.note.setEnabled(false);
        holder.addnoterecyclerviewrolBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("selam");
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(mContext)
                        .setMessage("Notu silmek ister misin?")
                        .setTitle("Silme işlemi")
                        .setPositiveButton("sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialog progressDialog = new ProgressDialog(mContext);
                                progressDialog.setTitle("Siliniyor...");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                firestore.collection("myNotes/")
                                        .document(addNoteInfoClassArrayList.get(position).noteId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(mContext, "Not silindi.", Toast.LENGTH_SHORT).show();
                                                addNoteInfoClassArrayList.remove(position);
                                                notifyItemChanged(position);
                                                progressDialog.hide();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(mContext, "Hata oluştu Not silinemedi. "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("silme", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "silinmiyor.", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertdialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addNoteInfoClassArrayList.size();
    }

    public class addNoteHolder extends RecyclerView.ViewHolder{
        AddnoterecyclerviewrolBinding addnoterecyclerviewrolBinding;
        public addNoteHolder(AddnoterecyclerviewrolBinding addnoterecyclerviewrolBinding) {
            super(addnoterecyclerviewrolBinding.getRoot());
            this.addnoterecyclerviewrolBinding = addnoterecyclerviewrolBinding;
            addnoterecyclerviewrolBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    System.out.println("listenner dinlemede..");
                    return true;
                }
            });
        }
    }
}
