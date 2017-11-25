package com.gianlu.commonutils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

public abstract class AnalyticsApplication extends Application implements Thread.UncaughtExceptionHandler {
    private FirebaseAnalytics tracker;

    public static void sendAnalytics(Context context, String event, @Nullable Bundle bundle) {
        AnalyticsApplication app = get(context);
        if (app != null) app.sendAnalytics(event, bundle);
    }

    public static void sendAnalytics(Context context, String event) {
        sendAnalytics(context, event, null);
    }

    private static String getAppName(Context context) {
        int id = context.getResources().getIdentifier("app_name", "string", context.getPackageName());
        if (id == 0) return context.getString(com.gianlu.commonutils.R.string.unknown);
        else return context.getString(id);
    }

    @Nullable
    public static AnalyticsApplication get(Context context) {
        if (context == null) return null;
        Context app = context.getApplicationContext();
        if (app instanceof AnalyticsApplication) return (AnalyticsApplication) app;
        else return null;
    }

    @Override
    public final void uncaughtException(Thread thread, Throwable throwable) {
        if (CommonUtils.isDebug()) {
            throwable.printStackTrace();
        } else {
            Crashlytics.logException(throwable);
            UncaughtExceptionActivity.startActivity(this, getAppName(this), throwable);
        }
    }

    public final void sendAnalytics(String event, @Nullable Bundle bundle) {
        if (tracker != null && event != null && !isDebug() && !Prefs.getBoolean(this, Prefs.Keys.TRACKING_DISABLE, false))
            tracker.logEvent(event, bundle);
    }

    @SuppressWarnings("SameReturnValue")
    protected abstract boolean isDebug();

    @Override
    public void onCreate() {
        super.onCreate();

        CommonUtils.setDebug(isDebug());
        Logging.init(this);
        Thread.setDefaultUncaughtExceptionHandler(this);

        tracker = FirebaseAnalytics.getInstance(this);
        tracker.setAnalyticsCollectionEnabled(!isDebug() && !Prefs.getBoolean(this, Prefs.Keys.TRACKING_DISABLE, false));
    }
}