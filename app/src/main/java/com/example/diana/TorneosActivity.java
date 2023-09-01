package com.example.diana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TorneosActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Button button;

    private FirebaseUser currentUser;

    private String emailUsuario;

    private List<String> torneosList;
    private RecyclerView recyclerView;
    private AdepterRecyclerViewTorneos adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torneos);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        emailUsuario = currentUser.getEmail();
        db = FirebaseFirestore.getInstance();
        torneosList = new ArrayList<>();
        adapter = new AdepterRecyclerViewTorneos(torneosList);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        obtenerTorenosPorEmail(emailUsuario);
        button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarRegistroTorneoActivity(view);
            }
        });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarClasificacionActivity(view);
            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser != null){
            reload();
            getUsuarios(emailUsuario);
            Log.d("Current User On Start EmptyActivity", "Usuario on empty"+ emailUsuario);
        } else{
            Log.d("Current User On Start EmptyActivity","null");
        }
    }
    // [END on_start_check_user]

    public void reload(){}


    public void getUsuarios(String email){
        DocumentReference docRef = db.collection("usuarios").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                Log.d("Current User On Start EmptyActivity", "El usuario se llama "+ usuario.getNombre()+ " " + usuario.getEmail());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Current User On Start EmptyActivity", "No existe el usuario o el documento");
            }
        });

    }

    public void cambiarRegistroTorneoActivity(View view){
        Intent intent = new Intent(this, RegistroTorneoActivity.class);
        startActivity(intent);
    }

    public void obtenerTorenosPorEmail(String emailUsuario){
        db.collection("usuarios").document(emailUsuario)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       obtenerTorneosPorAlias(documentSnapshot.get("alias").toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });
    }

     public void obtenerTorneosPorAlias(String alias){
        db.collection("torneos").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document: task.getResult()){
                        torneosList.clear();
                        db.collection("torneos").document(document.getId())
                                .collection("jugadores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if(task2.isSuccessful()){
                                    for(QueryDocumentSnapshot document2: task2.getResult()){
                                        if(document2.get("alias").equals(alias)){
                                            torneosList.add(document.getId());
                                            Log.d("torneos", document.getId() + " Añadido correctamente" );
                                        }
                                    }

                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                }else{
                    Log.d("torneos", "La peticion ha fallado" );
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("torneos", e.getMessage() );
            }
        });
     }

     public void cambiarClasificacionActivity(View view){
         AdepterRecyclerViewTorneos.TorneosViewHolder viewHolder = new AdepterRecyclerViewTorneos.TorneosViewHolder(view);
         Intent intent = new Intent(this, ClasificacionActivity.class);
         String nombreTorneo = viewHolder.getNombreTorneoTextView().getText().toString();
         intent.putExtra("nombreTorneo", nombreTorneo);
         startActivity(intent);
     }

}