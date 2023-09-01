package com.example.diana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClasificacionActivity extends AppCompatActivity {

    private List<Usuario> usuarioList;
    private RecyclerView recyclerView;
    private AdapterRecyclerViewClasificacion adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText editTextPuntuar;
    private Button botonPuntuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificacion);
        Log.d("intent", "Nombre del toreno: " + getIntent().getExtras().getString("nombreTorneo"));
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        usuarioList = new ArrayList<>();
        adapter = new AdapterRecyclerViewClasificacion(usuarioList);
        recyclerView = findViewById(R.id.clasificacionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        botonPuntuar = findViewById(R.id.botonPuntuar);
        obtenerJugadoresTorneo();
        botonPuntuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deshabilitarPuntuar();
            }
        });

    }


    //@Override
   public boolean onCreateOptionsMenu(Menu menu) {
       //obtenerJugadoresTorneo();
        //if(comprobarCurrentUserEsAdministrador(usuarioList, currentUser.getEmail())){
            getMenuInflater().inflate(R.menu.menu_puntuar, menu);
            return true;
        //}else{
        //    return false;
       // }
      }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.puntuar){
            habilitarPuntuar();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void obtenerJugadoresTorneo() {
        String nombreTorneo = getIntent().getExtras().getString("nombreTorneo");
        db.collection("torneos").document(nombreTorneo)
                .collection("jugadores").orderBy("puntuacion", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Oncomplete", "Ha entrado en el for de obtenerJugadoresTorneo");
                                Usuario usuario = document.toObject(Usuario.class);
                                usuarioList.add(usuario);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("fallo jugadores torneo", e.getMessage());
                    }
                });
    }

    public Usuario getUsuarioPrincipal(String emailUsuario) {
        final Usuario[] usuarioPrincipal = new Usuario[1];
        db.collection("usuarios").document(emailUsuario)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        usuarioPrincipal[0] = documentSnapshot.toObject(Usuario.class);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });
        return usuarioPrincipal[0];
    }

    public boolean comprobarCurrentUserEsAdministrador(List<Usuario> userList, String currentUserEmail){
        String aliasFirstUserList = userList.get(0).getAlias();
        String aliasCurrentUser = getUsuarioPrincipal(currentUserEmail).getAlias();
        if(aliasFirstUserList.equals(aliasCurrentUser))
            return true;
        else
            return false;
    }

    public void habilitarPuntuar(){
        botonPuntuar.setEnabled(true);
        adapter.setAllEditTextEnabled(true);

    }
    public void deshabilitarPuntuar(){
        botonPuntuar.setEnabled(false);
        adapter.setAllEditTextEnabled(false);
        Map<String, Integer> map = new HashMap<>();
        map = obtenerPuntuacion(recyclerView, adapter);
        List<String> mapKeys = new ArrayList<>(map.keySet());

        for(int i=0; i<map.size();i++){

            Log.d("Valor editText", "El valor del editText es " + String.valueOf(map.get(mapKeys.get(i))));
        }

    }

    public Map<String, Integer> obtenerPuntuacion(RecyclerView recyclerView, AdapterRecyclerViewClasificacion adapter){
        Map<String, Integer> map = new HashMap<>();
        EditText editText=null;
        TextView textView=null;
        try{
            for(int i = 0; i<adapter.getItemCount(); i++){
                AdapterRecyclerViewClasificacion.ClasificacionViewHolder holder = (AdapterRecyclerViewClasificacion.ClasificacionViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                editText = holder.getPuntuarEditText();
                textView = holder.getNombreJugadorTextView();
                String puntuacionString =editText.getText().toString();
                Integer puntuacionInteger;
                if(puntuacionString.equals("") || Integer.parseInt(puntuacionString)<0)
                    puntuacionInteger = new Integer(0);
                else
                    puntuacionInteger = Integer.parseInt(puntuacionString);

                String nombre = textView.getText().toString();
                map.put(nombre, puntuacionInteger);
                editText.setText("");
            }
        }catch (NumberFormatException nfe){
            Toast.makeText(this, "No se han introducido valores numericos", Toast.LENGTH_SHORT);

        }finally {
            editText.setText("");
        }

        return map;

    }


}