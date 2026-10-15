/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.alarmclock;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.deskclock.R;

/**
 * Lets the user pick the background (on/off, dark/light) and its opacity for a minimal clock widget.
 */
public class MinimalClockConfigurationActivity extends Activity {

    private int mAppWidgetId = INVALID_APPWIDGET_ID;
    private final MinimalClockWidgetProvider.Config mConfig =
            new MinimalClockWidgetProvider.Config();

    private ImageView mPreviewBg;
    private TextView mOpacityLabel;
    private SeekBar mOpacity;
    private RadioGroup mStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        final Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            mAppWidgetId = intent.getExtras().getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setContentView(R.layout.minimal_clock_widget_configuration);

        final MinimalClockWidgetProvider.Config saved =
                MinimalClockWidgetProvider.loadConfig(this, mAppWidgetId);
        mConfig.showBackground = saved.showBackground;
        mConfig.light = saved.light;
        mConfig.opacityPercent = saved.opacityPercent;

        mPreviewBg = findViewById(R.id.minimal_clock_bg);
        mOpacityLabel = findViewById(R.id.minimal_clock_opacity_label);
        mOpacity = findViewById(R.id.minimal_clock_opacity);
        mStyle = findViewById(R.id.minimal_clock_style_group);
        final CompoundButton bgSwitch = findViewById(R.id.minimal_clock_bg_switch);

        bgSwitch.setChecked(mConfig.showBackground);
        mStyle.check(mConfig.light ? R.id.minimal_clock_style_light : R.id.minimal_clock_style_dark);
        mOpacity.setProgress(mConfig.opacityPercent);

        bgSwitch.setOnCheckedChangeListener((b, checked) -> {
            mConfig.showBackground = checked;
            refresh();
        });
        mStyle.setOnCheckedChangeListener((g, id) -> {
            mConfig.light = id == R.id.minimal_clock_style_light;
            refresh();
        });
        mOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                mConfig.opacityPercent = progress;
                refresh();
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {}

            @Override
            public void onStopTrackingTouch(SeekBar bar) {}
        });
        findViewById(R.id.minimal_clock_done).setOnClickListener(v -> onDone());

        refresh();
    }

    private void refresh() {
        mPreviewBg.setVisibility(mConfig.showBackground ? ImageView.VISIBLE : ImageView.GONE);
        mPreviewBg.setColorFilter(MinimalClockWidgetProvider.backgroundColor(mConfig.light));
        mPreviewBg.setImageAlpha(MinimalClockWidgetProvider.alpha255(mConfig.opacityPercent));
        mOpacityLabel.setText(getString(R.string.minimal_clock_opacity, mConfig.opacityPercent));

        final boolean enabled = mConfig.showBackground;
        mOpacity.setEnabled(enabled);
        mOpacityLabel.setEnabled(enabled);
        for (int i = 0; i < mStyle.getChildCount(); i++) {
            mStyle.getChildAt(i).setEnabled(enabled);
        }
    }

    private void onDone() {
        MinimalClockWidgetProvider.saveConfig(this, mAppWidgetId, mConfig);
        MinimalClockWidgetProvider.updateAppWidget(this, AppWidgetManager.getInstance(this),
                mAppWidgetId);

        final Intent result = new Intent();
        result.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }
}
