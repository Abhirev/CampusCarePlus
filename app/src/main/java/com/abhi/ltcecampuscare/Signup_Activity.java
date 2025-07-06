package com.abhi.ltcecampuscare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class Signup_Activity extends AppCompatActivity {

    private EditText name,email,password;
    private RadioGroup roleGrp;
    private RadioButton selectedRole;
    Button submit;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        roleGrp = findViewById(R.id.radio_group);
        submit = findViewById(R.id.submit);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameEdit = name.getText().toString().trim();
                String emailEdit = email.getText().toString().trim();
                String passEdit = password.getText().toString().trim();

                if(nameEdit.isEmpty() || emailEdit.isEmpty() || passEdit.isEmpty() ){
                    Toast.makeText(Signup_Activity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedId = roleGrp.getCheckedRadioButtonId();
                selectedRole = findViewById(selectedId);
                String role = selectedRole.getText().toString().trim();



                auth.createUserWithEmailAndPassword(emailEdit, passEdit)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();

                                // Store name and role in Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", nameEdit);
                                userMap.put("email", emailEdit);
                                userMap.put("role", role);

                                db.collection("Users").document(user.getUid())
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(Signup_Activity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Signup_Activity.this, Login_Activity.class));
                                            finish();
                                        });
                            } else {
                                Toast.makeText(Signup_Activity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}