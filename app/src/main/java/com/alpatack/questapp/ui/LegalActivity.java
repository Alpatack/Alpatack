package com.alpatack.questapp.ui;

import com.alpatack.questapp.R;
import com.alpatack.questapp.data.AppState;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Shows either the Terms & Conditions or the Privacy Policy.
public class LegalActivity extends AppCompatActivity {
    public static final String PRIVACY = "privacy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        boolean privacy = getIntent().getBooleanExtra(PRIVACY, false);
        Ui.header(this, privacy ? "Privacy Policy" : "Terms & Conditions", getColor(R.color.purple));

        if (privacy) {
            TextView updated = findViewById(R.id.tvUpdated);
            updated.setText(Html.fromHtml("Last Updated: <b>November 2026</b>", Html.FROM_HTML_MODE_LEGACY));
            updated.setVisibility(TextView.VISIBLE);
        }
        ((TextView) findViewById(R.id.tvBody)).setText(
                Html.fromHtml(getString(privacy ? R.string.privacy_body : R.string.terms_body), Html.FROM_HTML_MODE_LEGACY));
    }
}
