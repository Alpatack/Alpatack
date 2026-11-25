package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Ui.header(this, "Change Password", getColor(R.color.white));

        EditText current = findViewById(R.id.etCurrent);
        EditText next = findViewById(R.id.etNew);
        EditText confirm = findViewById(R.id.etConfirm);
        Ui.passToggle(current, (ImageView) findViewById(R.id.eyeCurrent));
        Ui.passToggle(next, (ImageView) findViewById(R.id.eyeNew));
        Ui.passToggle(confirm, (ImageView) findViewById(R.id.eyeConfirm));

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String cur = current.getText().toString();
            String n = next.getText().toString();
            if (cur.isEmpty()) {
                Toast.makeText(this, "Enter your current password", Toast.LENGTH_SHORT).show();
            } else if (n.length() < 8) {
                Toast.makeText(this, "Password needs at least 8 characters", Toast.LENGTH_SHORT).show();
            } else if (!n.equals(confirm.getText().toString())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (n.equals(cur)) {
                Toast.makeText(this, "New password must be different", Toast.LENGTH_SHORT).show();
            } else if (!AppState.get(this).changePassword(cur, n)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
