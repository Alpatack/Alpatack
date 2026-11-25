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
import android.widget.Toast;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AchievementsFragment extends Fragment {
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_ach, c, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    private void bind() {
        AppState s = AppState.get(requireContext());
        s.rollDaily();
        s.manager.checkAchievements();
        s.save();
        ((TextView) root.findViewById(R.id.tvCoins)).setText(String.format("%,d", s.player.getCoin()));

        // ---- daily check-in ----
        int streak = s.player.getDailyStreak();
        boolean today = s.checkedInToday();
        int dailyDone = s.player.getDailyQuestsCompleted();
        boolean canCheckIn = dailyDone >= 3;
        int done = streak == 0 ? 0 : (today ? (streak - 1) % 7 + 1 : streak % 7);
        ((TextView) root.findViewById(R.id.tvCheckText)).setText("You've checked in for " + streak + " consecutive days");
        LinearLayout days = root.findViewById(R.id.llDays);
        days.removeAllViews();
        for (int i = 0; i < 7; i++) {
            View tile = getLayoutInflater().inflate(R.layout.item_day, days, false);
            boolean isDone = i < done;
            boolean isToday = today ? i == done - 1 : i == done;
            ((TextView) tile.findViewById(R.id.tvDay)).setText(isToday ? "Today" : "Day " + (i + 1));
            tile.findViewById(R.id.box).setBackgroundResource(isDone ? R.drawable.bg_tile_done : R.drawable.bg_tile_todo);
            ((ImageView) tile.findViewById(R.id.ivMark)).setImageResource(isDone ? R.drawable.ic_check_white : R.drawable.ic_star_light);
            ((TextView) tile.findViewById(R.id.tvReward)).setText("+" + AppState.CHECKIN_REWARDS[i]);
            days.addView(tile);
        }
        View btn = root.findViewById(R.id.btnCheckIn);
        TextView tvCheckBtn = root.findViewById(R.id.tvCheckBtn);
        if (today) {
            tvCheckBtn.setText("CHECKED IN TODAY");
            root.findViewById(R.id.ivCheck).setVisibility(View.VISIBLE);
            btn.setAlpha(0.85f);
            btn.setClickable(false);
        } else if (canCheckIn) {
            tvCheckBtn.setText("CHECK IN TODAY");
            root.findViewById(R.id.ivCheck).setVisibility(View.GONE);
            btn.setAlpha(1f);
            btn.setClickable(true);
            btn.setOnClickListener(v -> {
                int reward = s.checkIn();
                if (reward > 0) Toast.makeText(requireContext(), "+" + reward + " coins!", Toast.LENGTH_SHORT).show();
                bind();
            });
        } else {
            tvCheckBtn.setText("Finish at least 3 quest to check in");
            root.findViewById(R.id.ivCheck).setVisibility(View.GONE);
            btn.setAlpha(0.5f);
            btn.setClickable(false);
            btn.setOnClickListener(null);
        }

        // ---- milestones + badges ----
        int total = s.manager.getAchievementList().size();
        int unlocked = 0;
        for (Achievement a : s.manager.getAchievementList()) if (a.isUnlocked()) unlocked++;
        ((TextView) root.findViewById(R.id.tvMilestones)).setText(unlocked + " of " + total);
        ProgressBar mb = root.findViewById(R.id.barMile);
        mb.setMax(Math.max(total, 1));
        mb.setProgress(unlocked);

        LinearLayout badges = root.findViewById(R.id.llBadges);
        badges.removeAllViews();
        for (Achievement a : s.manager.getAchievementList()) {
            View row = getLayoutInflater().inflate(R.layout.item_badge, badges, false);
            ((ImageView) row.findViewById(R.id.ivIcon)).setImageResource(iconFor(a));
            ((TextView) row.findViewById(R.id.tvTitle)).setText(a.getTitle());
            ((TextView) row.findViewById(R.id.tvSub)).setText(subFor(a));
            TextView action = row.findViewById(R.id.tvAction);
            setBadgeLit(row, a.isUnlocked() || a.isClaimed());
            if (a.isClaimed()) {
                action.setText("Complete");
                action.setBackgroundResource(R.drawable.bg_btn_outline);
                action.setTextColor(getResources().getColor(R.color.purple));
                action.setClickable(false);
            } else if (a.isUnlocked()) {
                action.setText("Claim +" + a.getCoinReward() + " coins");
                action.setOnClickListener(v -> {
                    a.claim(s.player);
                    s.save();
                    bind();
                });
            } else {
                int prog = Math.min(a.getProgress(s.player), a.getRequiredValue());
                action.setText("Locked " + prog + "/" + a.getRequiredValue());
                action.setBackgroundResource(R.drawable.bg_btn_light);
                action.setTextColor(getResources().getColor(R.color.purple));
                action.setClickable(false);
            }
            badges.addView(row);
        }
    }

    // Locked honors are greyed out and dimmed; unlocked ones show full color.
    private void setBadgeLit(View row, boolean lit) {
        ImageView icon = row.findViewById(R.id.ivIcon);
        View title = row.findViewById(R.id.tvTitle);
        View sub = row.findViewById(R.id.tvSub);
        if (lit) {
            icon.clearColorFilter();
            icon.setAlpha(1f);
            title.setAlpha(1f);
            sub.setAlpha(1f);
        } else {
            ColorMatrix grey = new ColorMatrix();
            grey.setSaturation(0f);
            icon.setColorFilter(new ColorMatrixColorFilter(grey));
            icon.setAlpha(0.4f);
            title.setAlpha(0.45f);
            sub.setAlpha(0.45f);
        }
    }

    private int iconFor(Achievement a) {
        String t = a.getType();
        if (t.equals("streak")) return R.drawable.ic_flame_outline;
        if (t.equals("quests")) return R.drawable.ic_ribbon;
        if (t.equals("cat:mental")) return R.drawable.ic_brain;
        if (t.equals("cat:social")) return R.drawable.cat_social;
        return R.drawable.nav_trophy_off;
    }

    private String subFor(Achievement a) {
        String t = a.getType();
        if (t.equals("streak")) return "Streak";
        if (t.startsWith("cat:")) return t.substring(4, 5).toUpperCase() + t.substring(5);
        return "Milestone";
    }
}
