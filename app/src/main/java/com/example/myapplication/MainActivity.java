package com.example.myapplication;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText emailTxt;
    private EditText passwordTxt;
    private TextView textCreateCompte;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailTxt = findViewById(R.id.idRegisterEmail);
        passwordTxt = findViewById(R.id.idRegisterPassword);
        textCreateCompte = findViewById(R.id.idtextCreateCompte);
        btnLogin = (Button) findViewById(R.id.idLoginBtnLogin);

        auth = FirebaseAuth.getInstance();

        textCreateCompte.setOnClickListener(e->{
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(e-> {
            String txtEmail = emailTxt.getText().toString();
            String txtPassword = passwordTxt.getText().toString();

            if(TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtEmail)){
                Toast.makeText(MainActivity.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
            }else {
                login(txtEmail, txtPassword);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email ,password).addOnSuccessListener(MainActivity.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(MainActivity.this, "sign In user successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
            }

        });
    }


}