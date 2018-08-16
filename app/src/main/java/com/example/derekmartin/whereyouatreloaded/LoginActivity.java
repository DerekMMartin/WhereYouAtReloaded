package com.example.derekmartin.whereyouatreloaded;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog o;
    private EditText email;
    private EditText pass;
    private String TAG = "LOGINACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.Email);
        pass = findViewById(R.id.Password);
        SetupProgressBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Toast.makeText(LoginActivity.this, ("Welcome back "+currentUser.getEmail()), Toast.LENGTH_SHORT).show();
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name",currentUser.getEmail());
        startActivity(intent);
    }

    private boolean validateTextFields(){
        boolean valid = true;
        if(!email.getText().toString().contains("@")){
            email.setError("Not a valid email.");
            valid = false;
        }
        if(pass.getText().toString().length()<6){
            pass.setError("Password needs to be at least six characters long.");
            valid = false;
        }
        return valid;
    }

    private void SetupProgressBar(){
        o = new ProgressDialog(this);
        o.setMessage("TEMP");
        o.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        o.setIndeterminate(true);
        o.setProgress(0);
    }

    public void SignUpClick(final View view) {
        if(!validateTextFields())
            return;
        o.setMessage("Signing you up");
        o.show();

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Sign up successful.",
                                    Toast.LENGTH_SHORT).show();
                            o.dismiss();
                            SignInClick(view);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Sign up failed.",
                                    Toast.LENGTH_SHORT).show();
                            o.dismiss();
                        }
                    }
                });
    }

    public void SignInClick(View view) {
        if(!validateTextFields())
            return;
        o.setMessage("Logging in");
        o.show();

        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Sign in successful.",
                                    Toast.LENGTH_SHORT).show();
                            o.dismiss();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();
                            o.dismiss();
                        }
                    }
                });
    }
}
