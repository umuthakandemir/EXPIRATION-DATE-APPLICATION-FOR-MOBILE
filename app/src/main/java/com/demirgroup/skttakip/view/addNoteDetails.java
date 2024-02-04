package com.demirgroup.skttakip.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.ActivityAddNoteDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class addNoteDetails extends AppCompatActivity {
    private ActivityAddNoteDetailsBinding binding;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteDetailsBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);
        firestore = FirebaseFirestore.getInstance();
    }
    public void SaveNoteFun(View view){
        ProgressDialog progressDialog = new ProgressDialog(addNoteDetails.this);
        progressDialog.setTitle("Not ekleniyor..");
        progressDialog.show();
        if (binding.addPersonNAmeTxt.getText().toString().equals("") && binding.productnote.getText().toString().equals(""))
            Toast.makeText(addNoteDetails.this, "Lütfen Adınızı veya Notunuzu girin.", Toast.LENGTH_SHORT).show();
        else {
            UUID noteId = UUID.randomUUID();
            String personName = binding.addPersonNAmeTxt.getText().toString();
            String Note = binding.productnote.getText().toString();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("PersonName",personName.toUpperCase());
            hashMap.put("Note",Note);
            hashMap.put("NoteId",noteId.toString());
            hashMap.put("Date", FieldValue.serverTimestamp());
            firestore.collection("myNotes")
                    .document(noteId.toString())
                    .set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(addNoteDetails.this, "Not eklendi.", Toast.LENGTH_SHORT).show();
                            binding.productnote.setText("");
                            binding.addPersonNAmeTxt.setText("");
                            progressDialog.hide();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addNoteDetails.this, "Hata oluştu."+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}