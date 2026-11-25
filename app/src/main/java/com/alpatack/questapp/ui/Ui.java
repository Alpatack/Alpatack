package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;
import com.alpatack.questapp.model.*;

import android.app.Activity;
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
import android.net.Uri;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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

    // Back arrow + centered title used by every settings screen.
    public static void header(Activity a, String title, int color) {
        TextView t = a.findViewById(R.id.tvHeader);
        t.setText(title);
        t.setTextColor(color);
        ImageView back = a.findViewById(R.id.btnBack);
        back.setColorFilter(color);
        back.setOnClickListener(v -> a.finish());
    }

    // One tappable row (text + arrow) from item_setting_row.
    public static void row(View row, String text, View.OnClickListener l) {
        ((TextView) row.findViewById(R.id.tvRowText)).setText(text);
        row.setOnClickListener(l);
    }

    // Eye icon that shows / hides the text of a password field.
    public static void passToggle(EditText et, ImageView eye) {
        eye.setOnClickListener(v -> {
            boolean hidden = et.getTransformationMethod() instanceof PasswordTransformationMethod;
            et.setTransformationMethod(hidden ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            eye.setImageResource(hidden ? R.drawable.ic_eye : R.drawable.ic_eye_off);
            et.setSelection(et.getText().length());
        });
    }

    // Saves a picked gallery image as the profile photo. Returns false if it could not be read.
    public static boolean saveAvatar(Context c, Uri uri) {
        try (InputStream in = c.getContentResolver().openInputStream(uri)) {
            Bitmap b = BitmapFactory.decodeStream(in);
            if (b == null) return false;
            int max = 600;
            float scale = Math.min(1f, (float) max / Math.max(b.getWidth(), b.getHeight()));
            Bitmap small = Bitmap.createScaledBitmap(b, Math.round(b.getWidth() * scale), Math.round(b.getHeight() * scale), true);
            try (FileOutputStream out = new FileOutputStream(AppState.get(c).avatarFile(c))) {
                small.compress(Bitmap.CompressFormat.JPEG, 85, out);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

<<<<<<< HEAD
=======
    // Uses one of the built-in alpaca pictures as the profile photo.
    public static boolean savePreset(Context c, int drawableRes) {
        try {
            Bitmap b = BitmapFactory.decodeResource(c.getResources(), drawableRes);
            if (b == null) return false;
            try (FileOutputStream out = new FileOutputStream(AppState.get(c).avatarFile(c))) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

>>>>>>> bae7d0b (Stat features enhanced)
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
