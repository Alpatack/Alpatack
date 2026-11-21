package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.ImageView;

import java.io.File;

public class Ui {
    public static int dp(Context c, float v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    // Rounded popup dialog around a content view (the content has its own background).
    public static Dialog dialog(Context c, View content) {
        Dialog d = new Dialog(c);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(content);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout((int) (c.getResources().getDisplayMetrics().widthPixels * 0.88f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return d;
    }

    public static CharSequence boldLabel(String label, String rest) {
        SpannableString s = new SpannableString(label + rest);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static int catIcon(String cat) {
        switch (cat.toLowerCase()) {
            case "mental": return R.drawable.cat_mental;
            case "social": return R.drawable.cat_social;
            case "spiritual": return R.drawable.cat_spiritual;
            case "discipline": return R.drawable.cat_discipline;
            default: return R.drawable.cat_physical;
        }
    }

    // Shows the saved profile photo, or the Alpatack logo, in a circle.
    public static void loadAvatar(Context c, ImageView iv) {
        File f = AppState.get(c).avatarFile(c);
        iv.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View v, Outline o) {
                o.setOval(0, 0, v.getWidth(), v.getHeight());
            }
        });
        iv.setClipToOutline(true);
        Bitmap b = f.exists() ? BitmapFactory.decodeFile(f.getPath()) : null;
        if (b != null) {
            iv.setPadding(0, 0, 0, 0);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageBitmap(b);
        } else {
            int p = dp(c, 18);
            iv.setPadding(p, p, p, p);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setImageResource(R.drawable.logo_purple);
        }
    }
}
