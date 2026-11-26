package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.app.DatePickerDialog;
<<<<<<< HEAD
=======
<<<<<<< HEAD
import android.net.Uri;
import android.os.Bundle;
=======
>>>>>>> origin/main
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
=======
>>>>>>> bae7d0b (Stat features enhanced)
>>>>>>> origin/main
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
<<<<<<< HEAD
        avatar.setOnClickListener(v -> chooseProfile());
        findViewById(R.id.btnCamera).setOnClickListener(v -> chooseProfile());
=======
<<<<<<< HEAD
        avatar.setOnClickListener(v -> picker.launch("image/*"));
        findViewById(R.id.btnCamera).setOnClickListener(v -> picker.launch("image/*"));
=======
        avatar.setOnClickListener(v -> chooseProfile());
        findViewById(R.id.btnCamera).setOnClickListener(v -> chooseProfile());
>>>>>>> bae7d0b (Stat features enhanced)
>>>>>>> origin/main

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

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
>>>>>>> origin/main
    // "Choose Profile" popup: gallery, or one of the built-in alpacas, then Done.
    private void chooseProfile() {
        View v = getLayoutInflater().inflate(R.layout.dialog_choose_profile, null);
        Dialog d = Ui.dialog(this, v);
        int[] ids = {R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5};
        int[] pics = {R.drawable.avatar_alpaca_1, R.drawable.avatar_alpaca_2, R.drawable.avatar_alpaca_3,
                R.drawable.avatar_alpaca_4, R.drawable.avatar_alpaca_5};
        int[] picked = {-1};
        for (int i = 0; i < ids.length; i++) {
            final int idx = i;
            v.findViewById(ids[i]).setOnClickListener(x -> {
                picked[0] = idx;
                for (int k = 0; k < ids.length; k++) {
                    v.findViewById(ids[k]).setBackgroundResource(k == idx ? R.drawable.bg_avatar_selected : R.drawable.bg_avatar_idle);
                }
            });
        }
        v.findViewById(R.id.pGallery).setOnClickListener(x -> {
            d.dismiss();
            picker.launch("image/*");
        });
        v.findViewById(R.id.btnDone).setOnClickListener(x -> {
            if (picked[0] >= 0 && Ui.savePreset(this, pics[picked[0]])) Ui.loadAvatar(this, avatar);
            d.dismiss();
        });
        d.show();
    }

<<<<<<< HEAD
=======
>>>>>>> bae7d0b (Stat features enhanced)
>>>>>>> origin/main
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
