package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        EditText user = findViewById(R.id.etUsername);
        EditText mail = findViewById(R.id.etEmail);
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

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSignup).setOnClickListener(v -> {
            String u = user.getText().toString().trim();
            String e = mail.getText().toString().trim();
            String p = pass.getText().toString();
            if (u.isEmpty()) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            } else if (p.length() < 8) {
            Toast.makeText(this, "Password needs at least 8 characters", Toast.LENGTH_SHORT).show();
            } else if (!AppState.get(this).register(u, e, p)) {
                Toast.makeText(this, "That username is already taken", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(this, WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
