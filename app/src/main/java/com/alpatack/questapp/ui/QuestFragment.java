package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class QuestFragment extends Fragment {
    private View root;
    private LinearLayout list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_quest, c, false);
        list = root.findViewById(R.id.llQuests);
        root.findViewById(R.id.btnAdd).setOnClickListener(v -> showAdd());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    private void bind() {
        AppState s = AppState.get(requireContext());
        ((TextView) root.findViewById(R.id.tvStreak)).setText(String.valueOf(s.player.getDailyStreak()));
        list.removeAllViews();
        ArrayList<Quest> quests = s.manager.getQuestList();
        root.findViewById(R.id.tvEmpty).setVisibility(quests.isEmpty() ? View.VISIBLE : View.GONE);
        for (Quest q : quests) {
            View row = getLayoutInflater().inflate(R.layout.item_quest, list, false);
            ((ImageView) row.findViewById(R.id.ivIcon)).setImageResource(Ui.catIcon(q.getCategory()));
            ((TextView) row.findViewById(R.id.tvCat)).setText(q.getCategory().toUpperCase());
            TextView title = row.findViewById(R.id.tvTitle);
            title.setText(q.getTitle());
            ((TextView) row.findViewById(R.id.tvXp)).setText("+" + q.calculateExp() + "xp");
            if (q.isCompleted()) {
                row.setAlpha(0.55f);
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            row.setOnClickListener(v -> showDetail(q));
            row.setOnLongClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete quest?")
                        .setPositiveButton("Delete", (d, w) -> {
                            s.manager.deleteQuest(s.manager.getQuestList().indexOf(q));
                            s.save();
                            bind();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
            list.addView(row);
        }
    }

    private void showDetail(Quest q) {
        AppState s = AppState.get(requireContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_quest_detail, null);
        Dialog d = Ui.dialog(requireContext(), v);
        ((ImageView) v.findViewById(R.id.ivIcon)).setImageResource(Ui.catIcon(q.getCategory()));
        ((TextView) v.findViewById(R.id.tvCat)).setText(q.getCategory().toUpperCase());
        ((TextView) v.findViewById(R.id.tvTitle)).setText(q.getTitle());
        ((TextView) v.findViewById(R.id.tvReward)).setText("+" + q.calculateExp() + "xp");
        ((TextView) v.findViewById(R.id.tvDesc)).setText(q.getDescription());
        TextView status = v.findViewById(R.id.tvStatus);
        int total = q.getSteps().size();
        Runnable updateStatus = () -> {
            if (total == 0) status.setText(q.isCompleted() ? "Done" : "Pending");
            else status.setText(q.getDoneCount() + "/" + total);
        };
        updateStatus.run();

        LinearLayout steps = v.findViewById(R.id.llSteps);
        for (int i = 0; i < total; i++) {
            final int idx = i;
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(q.getSteps().get(i));
            cb.setTextSize(12);
            cb.setTextColor(getResources().getColor(R.color.purple_dark));
            cb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));
            cb.setChecked(q.isStepDone(i));
            cb.setEnabled(!q.isCompleted());
            cb.setOnCheckedChangeListener((b, checked) -> {
                q.setStepDone(idx, checked);
                updateStatus.run();
                s.save();
            });
            steps.addView(cb);
        }

        TextView complete = v.findViewById(R.id.btnComplete);
        if (q.isCompleted()) {
            complete.setText("Completed");
            complete.setAlpha(0.5f);
        } else {
            complete.setOnClickListener(x -> {
                int exp = q.calculateExp();
                int before = s.player.getLevel();
                s.manager.completeQuest(s.manager.getQuestList().indexOf(q));
                s.save();
                String msg = "+" + exp + " XP";
                if (s.player.getLevel() > before) msg += "  Level up!";
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                d.dismiss();
                bind();
            });
        }
        v.findViewById(R.id.btnClose).setOnClickListener(x -> d.dismiss());
        d.show();
    }

    private void showAdd() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_quest, null);
        Dialog d = Ui.dialog(requireContext(), v);
        Spinner sp = v.findViewById(R.id.spCategory);
        String[] cats = {"PHYSICAL", "MENTAL", "SOCIAL", "SPIRITUAL", "DISCIPLINE"};
        sp.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cats));
        EditText title = v.findViewById(R.id.etTitle);
        EditText xp = v.findViewById(R.id.etXp);
        EditText stepsBox = v.findViewById(R.id.etSteps);

        v.findViewById(R.id.btnCancel).setOnClickListener(x -> d.dismiss());
        v.findViewById(R.id.btnOk).setOnClickListener(x -> {
            String t = title.getText().toString().trim();
            if (t.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a quest title", Toast.LENGTH_SHORT).show();
                return;
            }
            int exp = 10;
            try {
                exp = Integer.parseInt(xp.getText().toString().trim());
            } catch (NumberFormatException ignored) { }
            ArrayList<String> steps = new ArrayList<>();
            for (String line : stepsBox.getText().toString().split("\n")) {
                if (!line.trim().isEmpty()) steps.add(line.trim());
            }
            AppState s = AppState.get(requireContext());
            CustomQuest q = new CustomQuest(t, "Custom quest", ((String) sp.getSelectedItem()).toLowerCase(), "custom", exp);
            q.setSteps(steps);
            s.manager.addQuest(q);
            s.save();
            d.dismiss();
            bind();
        });
        d.show();
    }
}
