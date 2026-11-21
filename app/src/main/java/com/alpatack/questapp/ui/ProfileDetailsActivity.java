package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileDetailsActivity extends AppCompatActivity {
    private static final SimpleDateFormat DF = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

    private ImageView avatar;

    // Lets the user pick a profile photo from the gallery.
    private final ActivityResultLauncher<String> picker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) savePhoto(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        Ui.header(this, "Profile Details", getColor(R.color.white));
        AppState s = AppState.get(this);

        avatar = findViewById(R.id.ivAvatar);
        Ui.loadAvatar(this, avatar);
        avatar.setOnClickListener(v -> picker.launch("image/*"));
        findViewById(R.id.btnCamera).setOnClickListener(v -> picker.launch("image/*"));

        EditText full = findViewById(R.id.etFull);
        EditText user = findViewById(R.id.etUser);
        EditText addr = findViewById(R.id.etAddr);
        TextView birth = findViewById(R.id.etBirth);
        full.setText(s.fullName);
        user.setText(s.username);
        addr.setText(s.address);
        birth.setText(s.birthday);
        birth.setOnClickListener(v -> pickBirthday(birth));

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String u = user.getText().toString().trim().replace("@", "");
            if (u.isEmpty()) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
            } else if (s.usernameTaken(u)) {
                Toast.makeText(this, "That username is already taken", Toast.LENGTH_SHORT).show();
            } else {
                s.updateProfile(full.getText().toString().trim(), u,
                        birth.getText().toString().trim(), addr.getText().toString().trim());
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void pickBirthday(TextView birth) {
        Calendar c = Calendar.getInstance();
        c.set(2000, Calendar.JANUARY, 1);   // where the picker opens if nothing is set yet
        try {
            Date d = DF.parse(birth.getText().toString().trim());
            if (d != null) c.setTime(d);
        } catch (Exception ignored) { }
        DatePickerDialog dp = new DatePickerDialog(this, (view, y, m, day) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(y, m, day);
            birth.setText(DF.format(picked.getTime()));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMaxDate(System.currentTimeMillis());
        dp.show();
    }

    private void savePhoto(Uri uri) {
        if (Ui.saveAvatar(this, uri)) Ui.loadAvatar(this, avatar);
        else Toast.makeText(this, "Could not use that photo", Toast.LENGTH_SHORT).show();
    }
}
