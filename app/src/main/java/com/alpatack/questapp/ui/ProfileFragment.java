package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileFragment extends Fragment {
    private View root;

    // Lets the user pick a profile photo from the gallery.
    private final ActivityResultLauncher<String> picker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) savePhoto(uri);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_profile, c, false);
        root.findViewById(R.id.ivAvatar).setOnClickListener(v -> picker.launch("image/*"));
        root.findViewById(R.id.btnEdit).setOnClickListener(v -> showEdit());
        root.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            AppState.get(requireContext()).logout();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    private void bind() {
        AppState s = AppState.get(requireContext());
        Player p = s.player;
        Ui.loadAvatar(requireContext(), root.findViewById(R.id.ivAvatar));
        ((TextView) root.findViewById(R.id.tvName)).setText(s.fullName.isEmpty() ? s.username : s.fullName);
        ((TextView) root.findViewById(R.id.tvUser)).setText("@" + s.username);
        ((TextView) root.findViewById(R.id.tvBirthday)).setText(Ui.boldLabel("Birthday\n", s.birthday.isEmpty() ? "Not set" : s.birthday));
        ((TextView) root.findViewById(R.id.tvAddress)).setText(Ui.boldLabel("Address\n", s.address.isEmpty() ? "Not set" : s.address));
        ((TextView) root.findViewById(R.id.tvJoined)).setText(Ui.boldLabel("Joined\n", s.joined));
        ((TextView) root.findViewById(R.id.tvLvl)).setText(String.format("LVL %02d", p.getLevel()));
        ((TextView) root.findViewById(R.id.tvTotal)).setText(s.totalXp() + " XP");
        ProgressBar bar = root.findViewById(R.id.bar);
        bar.setMax(p.getMaxExp());
        bar.setProgress(p.getCurrentExp());
        ((TextView) root.findViewById(R.id.tvBarLeft)).setText(p.getCurrentExp() + "/" + p.getMaxExp());
        ((TextView) root.findViewById(R.id.tvNext)).setText(String.format("Next Level: LVL %02d", p.getLevel() + 1));

        // badges light up when the matching achievement is unlocked
        setBadge(R.id.b1, s, "cat:mental");
        setBadge(R.id.b2, s, "cat:social");
        setBadge(R.id.b3, s, "streak");
    }

    private void setBadge(int id, AppState s, String type) {
        boolean got = false;
        for (Achievement a : s.manager.getAchievementList()) {
            if (a.getType().equals(type) && a.isUnlocked()) got = true;
        }
        root.findViewById(id).setAlpha(got ? 1f : 0.3f);
    }

    private void showEdit() {
        AppState s = AppState.get(requireContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        Dialog d = Ui.dialog(requireContext(), v);
        EditText full = v.findViewById(R.id.etFull);
        EditText user = v.findViewById(R.id.etUser);
        EditText birth = v.findViewById(R.id.etBirth);
        EditText addr = v.findViewById(R.id.etAddr);
        full.setText(s.fullName);
        user.setText(s.username);
        birth.setText(s.birthday);
        addr.setText(s.address);
        v.findViewById(R.id.btnCancel).setOnClickListener(x -> d.dismiss());
        v.findViewById(R.id.btnOk).setOnClickListener(x -> {
            s.fullName = full.getText().toString().trim();
            String u = user.getText().toString().trim().replace("@", "");
            if (!u.isEmpty()) s.username = u;
            s.birthday = birth.getText().toString().trim();
            s.address = addr.getText().toString().trim();
            s.player.setName(s.fullName.isEmpty() ? s.username : s.fullName);
            s.save();
            d.dismiss();
            bind();
        });
        d.show();
    }

    private void savePhoto(Uri uri) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap b = BitmapFactory.decodeStream(in);
            if (b == null) return;
            int max = 600;
            float scale = Math.min(1f, (float) max / Math.max(b.getWidth(), b.getHeight()));
            Bitmap small = Bitmap.createScaledBitmap(b, Math.round(b.getWidth() * scale), Math.round(b.getHeight() * scale), true);
            try (FileOutputStream out = new FileOutputStream(AppState.get(requireContext()).avatarFile(requireContext()))) {
                small.compress(Bitmap.CompressFormat.JPEG, 85, out);
            }
            bind();
        } catch (Exception ignored) { }
    }
}
