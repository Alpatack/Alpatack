package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Ui.header(this, "Contact Details", getColor(R.color.white));
        AppState s = AppState.get(this);

        EditText phone = findViewById(R.id.etPhone);
        EditText mail = findViewById(R.id.etEmail);
        phone.setText(s.phone);
        mail.setText(s.email);

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String digits = phone.getText().toString().replaceAll("\\D", "");
            String e = mail.getText().toString().trim();
            if (!digits.isEmpty() && !digits.matches("09\\d{9}")) {
                Toast.makeText(this, "Enter a valid number (09XX-XXX-XXXX)", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                String number = digits.isEmpty() ? "" : digits.substring(0, 4) + "-" + digits.substring(4, 7) + "-" + digits.substring(7);
                s.updateContact(e, number);
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
