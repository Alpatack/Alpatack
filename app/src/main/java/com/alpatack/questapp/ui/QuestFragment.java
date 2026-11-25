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
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class QuestFragment extends Fragment {
    private View root;
    private LinearLayout list;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private String shownDate = "";

    private final Runnable dayWatcher = new Runnable() {
        @Override
        public void run() {
            if (!AppState.get(requireContext()).today().equals(shownDate)) bind();
            handler.postDelayed(this, 60_000);
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_quest, c, false);
        list = root.findViewById(R.id.llQuests);
        root.findViewById(R.id.btnAdd).setOnClickListener(v -> showAdd());
        ((TextView) root.findViewById(R.id.tvWarning)).setText(Ui.boldLabel("WARNING: ",
                "Failure to complete the daily quest will result in an appropriate penalty."));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
        handler.postDelayed(dayWatcher, 60_000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(dayWatcher);
    }

    private void bind() {
        AppState s = AppState.get(requireContext());
        s.rollDaily();
        shownDate = s.today();
        ((TextView) root.findViewById(R.id.tvStreak)).setText(String.valueOf(s.player.getDailyStreak()));
        list.removeAllViews();
        ArrayList<Quest> quests = s.manager.getQuestList();
        root.findViewById(R.id.tvEmpty).setVisibility(quests.isEmpty() ? View.VISIBLE : View.GONE);
        quests.sort((a, b) -> Boolean.compare(a.isCompleted(), b.isCompleted()));
        for (Quest q : quests) {
            View row = getLayoutInflater().inflate(R.layout.item_quest, list, false);
            ((ImageView) row.findViewById(R.id.ivIcon)).setImageResource(Ui.catIcon(q.getCategory()));
            ((TextView) row.findViewById(R.id.tvCat)).setText(q.getCategory().toUpperCase());
            TextView title = row.findViewById(R.id.tvTitle);
            title.setText(q.getTitle());
            ((TextView) row.findViewById(R.id.tvXp)).setText("+" + q.calculateExp() + "xp");
            TextView done = row.findViewById(R.id.btnDone);
            if (q.isCompleted()) {
                row.setAlpha(0.55f);
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                done.setClickable(false);
            } else {
                done.setOnClickListener(v -> completeQuest(q));
            }
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

    // Used by the DONE! button on a card and by the Complete button in the detail popup.
    private void completeQuest(Quest q) {
        AppState s = AppState.get(requireContext());
        s.rollDaily();
        int exp = q.calculateExp();
        int before = s.player.getLevel();
        s.manager.completeQuest(s.manager.getQuestList().indexOf(q));
        s.save();
        String msg = "+" + exp + " XP";
        if (s.player.getLevel() > before) msg += "  Level up!";
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        bind();
    }


    private void showAdd() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_quest, null);
        Dialog d = Ui.dialog(requireContext(), v);
        Spinner sp = v.findViewById(R.id.spCategory);
        String[] cats = {"PHYSICAL", "MENTAL", "SOCIAL", "SPIRITUAL", "DISCIPLINE"};
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, cats);
        ad.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp.setAdapter(ad);
        EditText title = v.findViewById(R.id.etTitle);
        EditText xp = v.findViewById(R.id.etXp);

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
            if (exp <= 0) {
                Toast.makeText(requireContext(), "XP must be between 1 and 100", Toast.LENGTH_SHORT).show();
                return;
            }
            if (exp > 100) {
                Toast.makeText(requireContext(), "XP is capped at 100 per quest", Toast.LENGTH_SHORT).show();
                exp = 100;
            }
            AppState s = AppState.get(requireContext());
            CustomQuest q = new CustomQuest(t, "Custom quest", ((String) sp.getSelectedItem()).toLowerCase(), "custom", exp);
            s.manager.addQuest(q);
            s.save();
            d.dismiss();
            bind();
        });
        d.show();
    }
}
