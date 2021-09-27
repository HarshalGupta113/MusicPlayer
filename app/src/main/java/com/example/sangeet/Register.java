package com.example.sangeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button register;
    EditText name,email,password;
    TextView login;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseFirestore firestore;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=findViewById(R.id.Register);
        name=findViewById(R.id.Name);
        email=findViewById(R.id.Email);
        password=findViewById(R.id.Password);
        login=findViewById(R.id.Login);
        mAuth = FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        firestore=FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() !=null){
            startActivity(new Intent(Register.this,MainActivity.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString().trim();
                String Pass = password.getText().toString().trim();
                String Name = name.getText().toString().trim();

                if (TextUtils.isEmpty(Name)){
                    name.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(Email)){
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(Pass)){
                    password.setError("Password is required");
                    return;
                }
                if (Pass.length() < 6){
                    password.setError("Password Should Be more Than 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(Email,Pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Register.this,"Successfully Register",Toast.LENGTH_SHORT).show();
                                    userid=mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference =firestore.collection("user").document(userid);
                                    Map<String,Object> user=new HashMap<>();
                                    user.put("name",Name);
                                    user.put("email",Email);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG","User Profile is created for"+userid);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent=new Intent(Register.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(Register.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });
            }
        });
       login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(Register.this,Login.class));
              finish();
          }
      });
    }
}