package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class HomeFragment extends Fragment {
    private static final String[] QUOTES = {
            "\"Small steps every day add up to big change.\"",
            "\"Discipline is choosing what you want most over what you want now.\"",
            "\"You don't have to be perfect, just consistent.\"",
            "\"Level up one quest at a time.\"",
            "\"Progress, not perfection.\""
    };
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_home, c, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppState s = AppState.get(requireContext());
        Player p = s.player;
        Ui.loadAvatar(requireContext(), root.findViewById(R.id.ivAvatar));
        ((TextView) root.findViewById(R.id.tvName)).setText(s.fullName.isEmpty() ? s.username : s.fullName);
        ((TextView) root.findViewById(R.id.tvUser)).setText("@" + s.username);
        ((TextView) root.findViewById(R.id.tvLvl)).setText(String.format("LVL %02d", p.getLevel()));
        ((TextView) root.findViewById(R.id.tvTotal)).setText(p.getTotalExp() + " XP");
        ProgressBar bar = root.findViewById(R.id.bar);
        bar.setMax(p.getMaxExp());
        bar.setProgress(p.getCurrentExp());
        ((TextView) root.findViewById(R.id.tvBarLeft)).setText(p.getCurrentExp() + "/" + p.getMaxExp());
        ((TextView) root.findViewById(R.id.tvNext)).setText(String.format("Next Level: LVL %02d", p.getLevel() + 1));


        String[] labels = {"PHYSICAL: ", "MENTAL: ", "SOCIAL: ", "SPIRITUAL: ", "DISCIPLINE: "};
        String[] cats = {"physical", "mental", "social", "spiritual", "discipline"};
        LinearLayout list = root.findViewById(R.id.llStatus);
        list.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View row = getLayoutInflater().inflate(R.layout.item_status, list, false);
            ((ImageView) row.findViewById(R.id.ivIcon)).setImageResource(Ui.catIcon(cats[i]));
            String status = "Level " + p.getCategoryLevel(i);
            ((TextView) row.findViewById(R.id.tvText)).setText(Ui.boldLabel(labels[i], status));
            ProgressBar barCat = row.findViewById(R.id.barCat);
            barCat.setMax(p.getCategoryMaxExp(i));
            barCat.setProgress(p.getCategoryCurrentExp(i));
            list.addView(row);
        }
        ((TextView) root.findViewById(R.id.tvQuote)).setText(QUOTES[new Random().nextInt(QUOTES.length)]);
    }
}
