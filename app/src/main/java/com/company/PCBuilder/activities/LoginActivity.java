package com.company.PCBuilder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.company.PCBuilder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import static com.company.PCBuilder.activities.LoadScreenActivity.makePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    static final String EMAIL = "email", ACCOUNT_TYPE = "account_type";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);

        emailEditText = findViewById(R.id.email_text);
        passwordEditText = findViewById(R.id.password_text);

        emailEditText.setText("sam@cs440.com");
        passwordEditText.setText("strongpassword69");

        Button loginButton, registerButton;

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(loginListener);
        registerButton.setOnClickListener(registerListener);
    }


    private View.OnClickListener loginListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty()) {
                emailEditText.setError("Email can't be empty");
                emailEditText.requestFocus();
            } else if (password.isEmpty()){
                passwordEditText.setError("Password can't be empty");
                passwordEditText.requestFocus();
            } else{
                login(email, password);
            }
        }
    };

    private View.OnClickListener registerListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty()) {
                emailEditText.setError("Email can't be empty");
                emailEditText.requestFocus();
            } else if (password.isEmpty()){
                passwordEditText.setError("Password can't be empty");
                passwordEditText.requestFocus();
            } else{
                register(email, password);
            }
        }
    };

    private void login(String email, String password){
        final String emailFinal = email;

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Error logging in, Try Again", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String emailHolder = "<" + emailFinal + ">";
                            Toast.makeText(LoginActivity.this, "Logged in as " + emailHolder, Toast.LENGTH_SHORT).show();

                            final DocumentReference userDocument = db.collection("Users").document(emailFinal);

                            userDocument.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String buildName = documentSnapshot.getString("build_name");
                                            List<String> componentIds = (List) documentSnapshot.get("components");
                                            String accountType = documentSnapshot.getString(ACCOUNT_TYPE);

                                            if (buildName == null) {
                                                userDocument.set(makePair("build_name", emailFinal + " Build"), SetOptions.merge());
                                            }
                                            if (componentIds == null){
                                                userDocument.set(makePair("components", new ArrayList<String>()), SetOptions.merge());
                                            }
                                            if (accountType == null){
                                                accountType = "user";
                                                userDocument.set(makePair(ACCOUNT_TYPE, accountType), SetOptions.merge());
                                            }

                                            Intent i = new Intent(LoginActivity.this, LoadScreenActivity.class);
                                            i.putExtra(EMAIL, emailFinal);
                                            i.putExtra(ACCOUNT_TYPE, accountType);
                                            startActivity(i);
                                        }
                                    });
                        }
                    }
                });
    }

    private void register(String email, final String password){
        final String emailFinal = email;
        final String passwordFinal = password;

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Couldn't sign up, try again",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account made", Toast.LENGTH_SHORT).show();
                    login(emailFinal, passwordFinal);
                }
            }
        });
    }
}
