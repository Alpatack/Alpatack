package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    private final int[] on = {R.drawable.nav_home_on, R.drawable.nav_quest_on, R.drawable.nav_trophy_on,
            R.drawable.nav_shop_on, R.drawable.nav_user_on};
    private final int[] off = {R.drawable.nav_home_off, R.drawable.nav_quest_off, R.drawable.nav_trophy_off,
            R.drawable.nav_shop_off, R.drawable.nav_user_off};
    private final int[] ids = {R.id.nav0, R.id.nav1, R.id.nav2, R.id.nav3, R.id.nav4};
    private final ImageView[] tabs = new ImageView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            tabs[i] = findViewById(ids[i]);
            tabs[i].setOnClickListener(v -> show(idx));
        }
        show(0);
    }

    private void show(int i) {
        Fragment f;
        switch (i) {
            case 1: f = new QuestFragment(); break;
            case 2: f = new AchievementsFragment(); break;
            case 3: f = new ShopFragment(); break;
            case 4: f = new ProfileFragment(); break;
            default: f = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
        for (int k = 0; k < 5; k++) tabs[k].setImageResource(k == i ? on[k] : off[k]);
    }
}
