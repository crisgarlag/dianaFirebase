package com.example.diana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistroTorneoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private Spinner spinner;
    private List<String> usuariosListSpinner, usuariosListView;
    private ListView listView;

    private Button botonEliminarJugador, botonRegistrarTorneo;
    private EditText nombreTorneoEditText;
    private boolean isFirstSelection = true;
    private String emailUsuarioAdministrador;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retistro_torneo);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        emailUsuarioAdministrador = currentUser.getEmail();
        db = FirebaseFirestore.getInstance();
        usuariosListSpinner = new ArrayList<>();
        usuariosListView = new ArrayList<>();
        listView = findViewById(R.id.listView);
        spinner = findViewById(R.id.participantesSpinner);
        nombreTorneoEditText = findViewById(R.id.nombreTorneoEditTextText);
        botonRegistrarTorneo = findViewById(R.id.buttonRegistroTorneo);
        getUsuarioPrincipal(emailUsuarioAdministrador);
        getUsuarios();

       /* View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_listview, new ViewGroup(getApplicationContext()) {
            @Override
            protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

            }
        });
        botonEliminarJugador = view.findViewById(R.id.buttonListView);
        botonEliminarJugador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarJugador(view);
            }
        });*/

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                } else {
                    TextView text = view.findViewById(R.id.textViewSpinner);
                    String textoAlias = text.getText().toString();
                    if (usuariosListView.stream().filter(alias -> alias.equals(textoAlias))
                            .collect(Collectors.toList()).size() == 0) {
                        if (usuariosListView.size() < 6) {
                            usuariosListView.add(textoAlias);
                            cargaListView(usuariosListView);
                        } else {
                            hacerToast();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        botonRegistrarTorneo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearTorneo();
                actualizarUI();
            }
        });
    }

    public void getUsuarios() {

        db.collection("usuarios")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Usuarios", document.getId() + " " + document.get("alias"));
                                usuariosListSpinner.add(document.get("alias").toString());
                            }
                            cargaSpinner(usuariosListSpinner);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });
    }

    public void getUsuarioPrincipal(String emailUsuario) {

        db.collection("usuarios").document(emailUsuario)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Usuario usuarioPrincipal = documentSnapshot.toObject(Usuario.class);
                        usuariosListView.add(usuarioPrincipal.getAlias());
                        cargaListView(usuariosListView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });
    }

    public void cargaSpinner(List<String> usuariosListSpinner) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, R.id.textViewSpinner, usuariosListSpinner);
        spinner.setAdapter(adapter);

    }

    public void cargaListView(List<String> usuariosListView) {

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_listview, R.id.textViewListView, usuariosListView);
        AdapterListViewTorneo<String> adapter = new AdapterListViewTorneo<>(this, R.layout.item_listview, R.id.textViewListView, usuariosListView);

        listView.setAdapter(adapter);

    }

    public void hacerToast() {
        Toast.makeText(this, "No pueden jugar mas de 6 jugadores", Toast.LENGTH_SHORT).show();
    }

    public void eliminarJugadorListView(View view) {
        View parent = (View) view.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.textViewListView);
        String aliasJugador = textView.getText().toString();
        usuariosListView.removeIf(alias -> (alias.equals(aliasJugador)));
        cargaListView(usuariosListView);

    }

    private void crearTorneo() {
        String nombreTorneo = nombreTorneoEditText.getText().toString();
        if (nombreTorneo != null) {
            Map<String, Object> hashmap = new HashMap<String, Object>();
            hashmap.put("nombre", "Coleccion de jugadores");
            db.collection("torneos").document(nombreTorneo).set(hashmap);
            for (int i = 0; i < usuariosListView.size(); i++) {
                Usuario jugador = new Usuario(usuariosListView.get(i), 0);
                if (i == 0) {
                    jugador.setRol(Roles.ADMINISTRADOR);
                } else {
                    jugador.setRol(Roles.JUGADOR);
                }
                db.collection("torneos").document(nombreTorneo)
                        .collection("jugadores").document("jugador " + (i + 1))
                        .set(jugador).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Torneo", "Torneo " + nombreTorneo + " creado correctamente");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Torneo", "Torneo no creado");
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Debe elegir un nombre para el torneo", Toast.LENGTH_SHORT);
        }

    }

    public void actualizarUI() {
        usuariosListView.clear();
        cargaListView(usuariosListView);
        nombreTorneoEditText.setText("");
    }


}