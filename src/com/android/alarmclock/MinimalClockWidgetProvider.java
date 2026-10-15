/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.alarmclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import com.android.deskclock.DeskClock;
import com.android.deskclock.R;
import com.android.deskclock.Utils;

/**
 * Minimal widget showing only the time in white text, with an optional rounded background whose
 * style (dark/light) and opacity are configurable per widget.
 */
public class MinimalClockWidgetProvider extends AppWidgetProvider {

    private static final String PREFS_NAME = "minimal_clock_widget";
    private static final String KEY_SHOW_BG = "show_bg_";
    private static final String KEY_LIGHT = "light_";
    private static final String KEY_OPACITY = "opacity_";

    static final int DEFAULT_OPACITY_PERCENT = 60;

    private static final int DARK_BG = Color.BLACK;
    // White text is always used, so the "light" background is a mid-tone grey that keeps it legible.
    private static final int LIGHT_BG = Color.rgb(0x9E, 0x9E, 0x9E);

    static final class Config {
        boolean showBackground = true;
        boolean light = false;
        int opacityPercent = DEFAULT_OPACITY_PERCENT;
    }

    static Config loadConfig(Context context, int widgetId) {
        final SharedPreferences prefs = prefs(context);
        final Config c = new Config();
        c.showBackground = prefs.getBoolean(KEY_SHOW_BG + widgetId, true);
        c.light = prefs.getBoolean(KEY_LIGHT + widgetId, false);
        c.opacityPercent = prefs.getInt(KEY_OPACITY + widgetId, DEFAULT_OPACITY_PERCENT);
        return c;
    }

    static void saveConfig(Context context, int widgetId, Config c) {
        prefs(context).edit()
                .putBoolean(KEY_SHOW_BG + widgetId, c.showBackground)
                .putBoolean(KEY_LIGHT + widgetId, c.light)
                .putInt(KEY_OPACITY + widgetId, c.opacityPercent)
                .apply();
    }

    static int backgroundColor(boolean light) {
        return light ? LIGHT_BG : DARK_BG;
    }

    static int alpha255(int percent) {
        return Math.round(Math.max(0, Math.min(100, percent)) * 255 / 100f);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager wm, int[] widgetIds) {
        super.onUpdate(context, wm, widgetIds);
        for (int widgetId : widgetIds) {
            updateAppWidget(context, wm, widgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
        final SharedPreferences.Editor editor = prefs(context).edit();
        for (int id : widgetIds) {
            editor.remove(KEY_SHOW_BG + id).remove(KEY_LIGHT + id).remove(KEY_OPACITY + id);
        }
        editor.apply();
    }

    static void updateAppWidget(Context context, AppWidgetManager wm, int widgetId) {
        final Config c = loadConfig(context, widgetId);
        final RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.minimal_clock_widget);

        rv.setViewVisibility(R.id.minimal_clock_bg, c.showBackground ? View.VISIBLE : View.GONE);
        rv.setInt(R.id.minimal_clock_bg, "setColorFilter", backgroundColor(c.light));
        rv.setInt(R.id.minimal_clock_bg, "setImageAlpha", alpha255(c.opacityPercent));

        // Tapping on the widget opens the app (if not on the lock screen).
        if (Utils.isWidgetClickable(wm, widgetId)) {
            final Intent openApp = new Intent(context, DeskClock.class);
            final PendingIntent pi = PendingIntent.getActivity(context, 0, openApp,
                    PendingIntent.FLAG_IMMUTABLE);
            rv.setOnClickPendingIntent(R.id.minimal_clock_root, pi);
        }

        wm.updateAppWidget(widgetId, rv);
    }
}
