package com.abhi.ltcecampuscare;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_Activity extends AppCompatActivity {
    private EditText emailEdit;
    private EditText passEdit;
    private Button login;
    private TextView signup;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ConstraintLayout mainLayout = findViewById(R.id.main);


             mainLayout.setBackgroundColor(Color.WHITE);
        }



        emailEdit = findViewById(R.id.email);
        passEdit = findViewById(R.id.password);
        login = findViewById(R.id.submit);
        signup = findViewById(R.id.signupRedirect);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Signup_Activity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString().trim();
                String pass = passEdit.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(Login_Activity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                String uid = auth.getCurrentUser().getUid();

                                db.collection("Users").document(uid).get()
                                        .addOnSuccessListener(document -> {
                                            if(document.exists()){
                                                String role = document.getString("role");
                                                Toast.makeText(Login_Activity.this, role, Toast.LENGTH_SHORT).show();
                                                if("ADMIN".equals(role)){
                                                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                                                    intent.putExtra("role", "ADMIN"); // ✅ pass role here too
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                                                    intent.putExtra("role", "USER");
                                                    startActivity(intent);
                                                }
                                                finish();
                                            } else {
                                                Toast.makeText(Login_Activity.this, "No role found!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Toast.makeText(Login_Activity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}