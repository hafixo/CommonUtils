package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.gianlu.commonutils.Dialogs.ActivityWithDialog;
import com.gianlu.commonutils.LogsActivity;
import com.gianlu.commonutils.R;

import java.util.List;

public abstract class BasePreferenceActivity extends ActivityWithDialog {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_preference);
        setTitle(R.string.preferences);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.basePreference, MainFragment.get(), MainFragment.TAG)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    protected abstract List<MaterialAboutItem> getPreferencesItems();

    public static class MainFragment extends MaterialAboutFragment {
        private static final String TAG = MainFragment.class.getName();

        @NonNull
        public static MainFragment get() {
            return new MainFragment();
        }

        @Override
        protected int getTheme() {
            return R.style.MaterialAbout_Default;
        }

        @Override
        protected MaterialAboutList getMaterialAboutList(final Context context) {
            MaterialAboutCard developer = new MaterialAboutCard.Builder()
                    .title(R.string.about_app)
                    .addItem(new MaterialAboutTitleItem(R.string.developer, R.string.email, R.drawable.outline_info_24))
                    .build();

            MaterialAboutCard.Builder preferencesBuilder = null;
            List<MaterialAboutItem> preferencesItems = ((BasePreferenceActivity) context).getPreferencesItems();
            if (!preferencesItems.isEmpty()) {
                preferencesBuilder = new MaterialAboutCard.Builder()
                        .title(R.string.preferences);

                for (MaterialAboutItem item : preferencesItems)
                    preferencesBuilder.addItem(item);
            }

            MaterialAboutCard logs = new MaterialAboutCard.Builder()
                    .title(R.string.logs)
                    .addItem(new MaterialAboutActionItem.Builder()
                            .icon(R.drawable.baseline_announcement_24)
                            .text(R.string.logs)
                            .setOnClickAction(new MaterialAboutItemOnClickAction() {
                                @Override
                                public void onClick() {
                                    startActivity(new Intent(context, LogsActivity.class));
                                }
                            }).build())
                    .addItem(new MaterialAboutActionItem.Builder()
                            .icon(R.drawable.baseline_delete_24)
                            .text(R.string.deleteAllLogs)
                            .setOnClickAction(new MaterialAboutItemOnClickAction() {
                                @Override
                                public void onClick() {
                                    // TODO
                                }
                            }).build())
                    .build();

            // TODO: Donate
            // TODO: Third-part projects

            MaterialAboutList.Builder listBuilder = new MaterialAboutList.Builder();
            listBuilder.addCard(developer);
            if (preferencesBuilder != null) listBuilder.addCard(preferencesBuilder.build());
            listBuilder.addCard(logs);
            return listBuilder.build();
        }
    }
}