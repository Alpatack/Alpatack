package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ManageAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        Ui.header(this, "Manage Account", getColor(R.color.white));
        Ui.row(findViewById(R.id.rowProfile), "Profile Details", v -> startActivity(new Intent(this, ProfileDetailsActivity.class)));
        Ui.row(findViewById(R.id.rowContact), "Contact Details", v -> startActivity(new Intent(this, ContactDetailsActivity.class)));
        Ui.row(findViewById(R.id.rowPassword), "Change Password", v -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());
    }

    private void confirmDelete() {
        View v = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
        Dialog d = Ui.dialog(this, v);
        v.findViewById(R.id.btnCancel).setOnClickListener(x -> d.dismiss());
        v.findViewById(R.id.btnConfirm).setOnClickListener(x -> {
            AppState.get(this).deleteAccount();
            d.dismiss();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        d.show();
    }
}
