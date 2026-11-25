package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_profile, c, false);
        root.findViewById(R.id.ivAvatar).setOnClickListener(v -> showAvatarOverlay());
        root.findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));
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
        ((TextView) root.findViewById(R.id.tvTotal)).setText(p.getTotalExp() + " XP");
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

    private void showAvatarOverlay() {
        Dialog d = new Dialog(requireContext());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout container = new FrameLayout(requireContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView preview = new ImageView(requireContext());
        int size = Ui.dp(requireContext(), 260);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        preview.setLayoutParams(params);

        Ui.loadAvatar(requireContext(), preview);
        container.addView(preview);

        d.setContentView(container);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B3000000")));
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        d.setCanceledOnTouchOutside(true);
        container.setOnClickListener(v -> d.dismiss());
        preview.setOnClickListener(v -> d.dismiss());
        d.show();
    }
}
