package com.example.myapplication;

import android.net.Uri;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity" ;
    private Button btnRegister;
    private EditText emailTxt, passwordTxt, confirmPasswordTxt,fnameTxt, lnameTxt, phoneTxt;
    private TextView errorTxt;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private String userID;
    private String randomKey;

    private ImageView imageProfile;
    private Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailTxt = findViewById(R.id.idRegisterEmail);
        passwordTxt = findViewById(R.id.idRegisterPassword);
        confirmPasswordTxt = findViewById(R.id.idRegisterConfirmPassword);
        fnameTxt = findViewById(R.id.idRegisterfname);
        lnameTxt = findViewById(R.id.idRegisterlname);
        phoneTxt = findViewById(R.id.idRegisterPhone);
        errorTxt = findViewById(R.id.idTextErrorView);
        btnRegister = findViewById(R.id.idBtnRegister);
        imageProfile = findViewById(R.id.idImageProfile);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // save image
        imageProfile.setOnClickListener(e->{
            choosePicture();
        });
        //Register info
        btnRegister.setOnClickListener(e->{
            errorTxt.setVisibility(View.GONE);

            String txtEmail = emailTxt.getText().toString();
            String txtPassword = passwordTxt.getText().toString();
            String txtconfirmPassword = confirmPasswordTxt.getText().toString();
            String txtfname = fnameTxt.getText().toString();
            String txtlname = lnameTxt.getText().toString();
            String txtphone = phoneTxt.getText().toString();

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)
                    || TextUtils.isEmpty(txtconfirmPassword) || TextUtils.isEmpty(txtfname)
                    || TextUtils.isEmpty(txtlname) || TextUtils.isEmpty(txtphone) || TextUtils.isEmpty(randomKey)){
                Toast.makeText(RegisterActivity.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
                errorTxt.setText("Empty credentials!");
                errorTxt.setVisibility(View.VISIBLE);
            } else if (txtPassword.length() < 6){
                Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
            } else if (!txtconfirmPassword.equals(txtPassword)){
                Toast.makeText(RegisterActivity.this, "Password and confirm password not matched", Toast.LENGTH_SHORT).show();

            }else {
                registerUser(txtEmail , txtPassword, txtfname, txtlname, txtphone);
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("images/**");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    private void uploadPicture() {
        randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);
        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegisterActivity.this, "Image uploaded!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RegisterActivity.this, "Failed To Upload!", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data){
        super.onActivityResult(reqCode, resCode, data);
        if(reqCode==1 && resCode==RESULT_OK &&  data!=null && data.getData()!=null){
            imageUri=data.getData();
            imageProfile.setImageURI(imageUri);
            uploadPicture();
        }
    }


    private void registerUser(String email, String password,String fname,String lname,String phone) {

        auth.createUserWithEmailAndPassword(email ,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Registering user successfully", Toast.LENGTH_SHORT).show();
                    userID = auth.getCurrentUser().getUid();
                    DocumentReference docref = fstore.collection("freelancers").document(userID);

                    // add data with empty
                    HashMap<String,String> user = new HashMap<>();
                    user.put("firstname", fname);
                    user.put("lastname", lname);
                    user.put("phone", phone);
                    user.put("email", email);
                    user.put("profile", String.valueOf(imageUri));
                    docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG,"user profile created with Id"+userID);
                        }
                    });

                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }else {
                    Toast.makeText(RegisterActivity.this, "Registering user failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}