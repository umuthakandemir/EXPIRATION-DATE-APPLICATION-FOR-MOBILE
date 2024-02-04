package com.demirgroup.skttakip.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demirgroup.skttakip.Adapters.adNoteAdapter;
import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.databinding.ActivityAddNoteBinding;
import com.demirgroup.skttakip.model.addNoteInfoClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class addNoteActivity extends AppCompatActivity {

    private ActivityAddNoteBinding binding;
    private FirebaseFirestore firestore;
    private adNoteAdapter adNoteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firestore = FirebaseFirestore.getInstance();
        addNoteInfoClassArrayList = new ArrayList<>();
        binding.addNoteRecyclerViewID.setLayoutManager(new LinearLayoutManager(this));
        adNoteAdapter = new adNoteAdapter(addNoteInfoClassArrayList,binding.getRoot().getContext());
        binding.addNoteRecyclerViewID.setAdapter(adNoteAdapter);
        getData();
    }
    ArrayList<addNoteInfoClass> addNoteInfoClassArrayList;
    private void getData() {
        firestore.collection("myNotes/")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addNoteActivity.this, "Notlar getirilemedi.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                            String note = (String) snapshot.get("Note");
                            String name = (String) "Ekleyen Kişi: "+snapshot.get("PersonName");
                            String noteId = snapshot.getId();
                            addNoteInfoClass addNoteInfoClass = new addNoteInfoClass(note,name,noteId);
                            addNoteInfoClassArrayList.add(addNoteInfoClass);
                            adNoteAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void addNoteFun(View view){
        Intent addNoteFunIntent = new Intent(addNoteActivity.this,addNoteDetails.class);
        startActivity(addNoteFunIntent);
    }
}