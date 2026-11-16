package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppState state = AppState.get(this);
        if (state.loggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        EditText name = findViewById(R.id.etName);
        EditText pass = findViewById(R.id.etPassword);

        TextView showPass = findViewById(R.id.btnShowPass);
        showPass.setOnClickListener(v -> {
            boolean hidden = pass.getTransformationMethod() instanceof PasswordTransformationMethod;
            pass.setTransformationMethod(hidden
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            showPass.setText(hidden ? "HIDE" : "SHOW");
            pass.setSelection(pass.getText().length()); // keeps the cursor at the end
        });

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            if (!state.hasAccount()) {
                Toast.makeText(this, "No account yet. Please sign up first.", Toast.LENGTH_SHORT).show();
            } else if (state.login(name.getText().toString().trim(), pass.getText().toString())) {
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong name or password", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btnSignup).setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }
}
