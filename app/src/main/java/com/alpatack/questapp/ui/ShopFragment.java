package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
<<<<<<< HEAD
=======
import android.widget.ImageView;
>>>>>>> bae7d0b (Stat features enhanced)
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ShopFragment extends Fragment {
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle b) {
        root = inflater.inflate(R.layout.fragment_shop, c, false);
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
        ((TextView) root.findViewById(R.id.tvCoins)).setText(String.valueOf(s.player.getCoin()));
        LinearLayout grid = root.findViewById(R.id.llGrid);
        grid.removeAllViews();
        ArrayList<Reward> rewards = s.manager.getRewardList();
        LinearLayout row = null;
        int m = Ui.dp(requireContext(), 6);
        for (int i = 0; i < rewards.size(); i++) {
            final int idx = i;
            Reward r = rewards.get(i);
            if (i % 3 == 0) {
                row = new LinearLayout(requireContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                grid.addView(row, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            View card = getLayoutInflater().inflate(R.layout.item_reward, row, false);
            ((TextView) card.findViewById(R.id.tvName)).setText(r.getName());
<<<<<<< HEAD
=======
            ((ImageView) card.findViewById(R.id.ivIcon)).setImageResource(iconFor(r.getDescription()));
>>>>>>> bae7d0b (Stat features enhanced)
            TextView price = card.findViewById(R.id.tvPrice);
            price.setText(r.getCost() + " COINS\nREDEEM");
            price.setOnClickListener(v -> {
                if (!r.canRedeem(s.player)) {
                    Toast.makeText(requireContext(), "Not enough coins (need " + r.getCost() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(requireContext())
                        .setTitle("Redeem " + r.getName() + "?")
                        .setMessage("This costs " + r.getCost() + " coins.")
                        .setPositiveButton("Redeem", (d, w) -> {
                            s.manager.redeemReward(idx);
                            s.save();
                            Toast.makeText(requireContext(), "Enjoy your reward!", Toast.LENGTH_SHORT).show();
                            bind();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
            card.setOnLongClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Remove " + r.getName() + "?")
                        .setPositiveButton("Remove", (d, w) -> {
                            s.manager.getRewardList().remove(idx);
                            s.save();
                            bind();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
<<<<<<< HEAD
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, Ui.dp(requireContext(), 124), 1f);
=======
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, Ui.dp(requireContext(), 140), 1f);
>>>>>>> bae7d0b (Stat features enhanced)
            lp.setMargins(m, m, m, m);
            row.addView(card, lp);
        }
        // fill the last row so cards keep the same width
        if (row != null) {
            int missing = (3 - rewards.size() % 3) % 3;
            for (int k = 0; k < missing; k++) {
                View spacer = new View(requireContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 1, 1f);
                lp.setMargins(m, m, m, m);
                row.addView(spacer, lp);
            }
        }
    }

<<<<<<< HEAD
=======
    // The reward's category (kept in its description) decides which icon the card shows.
    private int iconFor(String category) {
        if (category == null) return R.drawable.ic_cat_gift;
        switch (category) {
            case "Food": return R.drawable.ic_cat_food;
            case "Travel": return R.drawable.ic_cat_travel;
            case "Clothing": return R.drawable.ic_cat_clothing;
            case "Shopping": return R.drawable.ic_cat_shopping;
            case "Tech": return R.drawable.ic_cat_tech;
            default: return R.drawable.ic_cat_gift;
        }
    }

>>>>>>> bae7d0b (Stat features enhanced)
    private void showAdd() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_wish, null);
        Dialog d = Ui.dialog(requireContext(), v);
        Spinner sp = v.findViewById(R.id.spCategory);
<<<<<<< HEAD
        sp.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Clothing", "Shopping", "Food", "Tech", "Travel", "Gift"}));
=======
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(), R.layout.spinner_item,
                new String[]{"Clothing", "Shopping", "Food", "Tech", "Travel", "Gift"});
        ad.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp.setAdapter(ad);
>>>>>>> bae7d0b (Stat features enhanced)
        EditText name = v.findViewById(R.id.etName);
        EditText price = v.findViewById(R.id.etPrice);
        v.findViewById(R.id.btnCancel).setOnClickListener(x -> d.dismiss());
        v.findViewById(R.id.btnOk).setOnClickListener(x -> {
            String n = name.getText().toString().trim();
            int cost;
            try {
                cost = Integer.parseInt(price.getText().toString().trim());
            } catch (NumberFormatException e) {
                cost = -1;
            }
            if (n.isEmpty() || cost <= 0) {
                Toast.makeText(requireContext(), "Enter a name and a price", Toast.LENGTH_SHORT).show();
                return;
            }
            AppState s = AppState.get(requireContext());
            s.manager.getRewardList().add(new Reward(n, (String) sp.getSelectedItem(), cost));
            s.save();
            d.dismiss();
            bind();
        });
        d.show();
    }
}
