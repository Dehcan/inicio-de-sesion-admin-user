package cl.example.toolbarlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Button botonsesion = (Button) findViewById(R.id.botoniniciarsesion);
        EditText texto_usuario = (EditText) findViewById(R.id.txtuser);
        EditText texto_contra = (EditText) findViewById(R.id.txtpassword);

        botonsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = texto_usuario.getText().toString();
                String pass = texto_contra.getText().toString();
                if (user.isEmpty() || pass.isEmpty()){
                Toast.makeText(Login.this, "Por Favor Rellenar Los Campos",Toast.LENGTH_LONG).show();
                }else {
                iniciarsesion(user,pass);

                }
                texto_usuario.setText("");
                texto_contra.setText("");

        };

        });
    }

    public void iniciarsesion(String usercred, String userpass){

        mAuth.signInWithEmailAndPassword(usercred,userpass).addOnCompleteListener(task->{

                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid();
                    db.collection("user").document(userId).get().addOnCompleteListener(documentTask -> {
                        if (documentTask.isSuccessful()){
                            DocumentSnapshot document = documentTask.getResult();
                            if (document.exists()){
                                String rol = document.getString("Rol");
                                if ("Admin".equals(rol)){
                                    startActivity(new Intent(Login.this, MenuAdmin.class));
                                }else {
                                    startActivity(new Intent(Login.this, Menu.class));
                                }
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(Login.this,"Error al iniciar Sesion", Toast.LENGTH_LONG).show();
                }
        });
    }

    /*
    //Método para registrar al usuario y almacenar datos adicionales
    private void registerUser(String email, String password, String nombre, String fechaNacimiento,
                          String domicilio, String rut, String numeroTelefono, String rol) {
    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Obtén el UID del usuario registrado
                    String userId = mAuth.getCurrentUser().getUid();

                    // Crea un mapa para almacenar los datos de usuario
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("nombre", nombre);
                    userData.put("FechadeNacimiento", fechaNacimiento);
                    userData.put("domicilio", domicilio);
                    userData.put("rut", rut);
                    userData.put("numeroTelefono", numeroTelefono);
                    userData.put("rol", rol); // Ej: "admin" o "user"

                    // Almacena el documento en la colección "users" con el UID como ID del documento
                    db.collection("users").document(userId).set(userData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Usuario registrado y datos guardados correctamente",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Error al guardar los datos del usuario",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error en el registro: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
}
     */

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(Login.this, Menu.class));
            finish();
        }
    }
    public boolean conexion(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()){

            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnectedOrConnecting() ||
                    wifi != null && wifi.isConnectedOrConnecting())){

                return true;
            }else {

                return false;
            }
        }else {
            return false;
        }
    }
}