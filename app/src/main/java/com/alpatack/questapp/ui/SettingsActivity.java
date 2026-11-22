package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        AppState s = AppState.get(this);
        Ui.header(this, "Settings", getColor(R.color.white));

        Ui.row(findViewById(R.id.rowManage), "Manage Account", v -> startActivity(new Intent(this, ManageAccountActivity.class)));
        Ui.row(findViewById(R.id.rowTerms), "Terms & Conditions", v -> openLegal(false));
        Ui.row(findViewById(R.id.rowPrivacy), "Privacy Policy", v -> openLegal(true));

        SwitchCompat push = findViewById(R.id.swPush);
        push.setChecked(s.pushEnabled());
        push.setOnCheckedChangeListener((b, on) -> s.setPushEnabled(on));

        String version = "1.0.0";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) { }
        ((TextView) findViewById(R.id.tvVersion)).setText("Version " + version);

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            s.logout();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void openLegal(boolean privacy) {
        Intent i = new Intent(this, LegalActivity.class);
        i.putExtra(LegalActivity.PRIVACY, privacy);
        startActivity(i);
    }
}
