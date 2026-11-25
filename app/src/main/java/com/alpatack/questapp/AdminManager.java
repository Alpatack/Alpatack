package com.alpatack.questapp;

import android.content.Context;
import com.alpatack.questapp.data.AppState;

public class AdminManager {

    // Check if current account is an admin
    public static boolean isAdmin(Context context) {
        AppState s = AppState.get(context);
        return s.isAdmin || s.username.equalsIgnoreCase("admin");
    }

    // Grant or revoke admin status
    public static void setAdmin(Context context, boolean isAdmin) {
        AppState s = AppState.get(context);
        s.isAdmin = isAdmin;
        s.save();
    }

    // Modify EXP, Coins, etc. directly through AppState.get(context) (s.player and s.save())
    public static void updateStats(Context context, int newExp, int newCoins) {
        AppState s = AppState.get(context);
        if (s.player != null) {
            s.player.setCurrentExp(newExp);
            s.player.setCoin(newCoins);
            s.save();
        }
    }

    public static int getExp(Context context) {
        AppState s = AppState.get(context);
        return s.player != null ? s.player.getTotalExp() : 0;
    }

    public static int getCoins(Context context) {
        AppState s = AppState.get(context);
        return s.player != null ? s.player.getCoin() : 0;
    }
}
