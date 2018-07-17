package com.squeeze.squeezeadmin.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.network.RequestScheme;
import com.squeeze.squeezeadmin.utils.DisplayUtils;
import com.squeeze.squeezeadmin.utils.NetworkUtils;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();


    private Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settingsToolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setTitle(R.string.setting_title);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment {

        private SharedPreferences mSharedPrefs;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences_settings);
            mSharedPrefs = getActivity().getSharedPreferences(getString(R.string.shared_prefs_file_key), MODE_PRIVATE);

        }

        @Override
        public void onResume() {
            super.onResume();
            Preference deletePrefs = findPreference("pref_delete_pictures");
            deletePrefs.setOnPreferenceClickListener((pref) -> {
                NetworkUtils.sendRemovePicsSignal(getActivity(), mSharedPrefs);
                return true;
            });

            Preference deleteLogPrefs = findPreference("pref_delete_logs");
            deleteLogPrefs.setOnPreferenceClickListener((pref) -> {
                NetworkUtils.sendRemoveLogsSignal(getActivity(), mSharedPrefs);
                return true;
            });

            Preference showJwtPrefs = findPreference("pref_show_jwt");
            try {
                final JWT jwt = new JWT(mSharedPrefs.getString(getString(R.string.shared_prefs_jwt_key),
                        getString(R.string.jwt)));
                showJwtPrefs.setTitle("JWT valid until " + DisplayUtils.parseDate(jwt.getExpiresAt()));
                showJwtPrefs.setOnPreferenceClickListener((pref) -> {
                    NetworkUtils.getJwt(getActivity(), mSharedPrefs, () -> {
                        JWT jwtUpdated = new JWT(mSharedPrefs.getString(getString(R.string.shared_prefs_jwt_key),
                                getString(R.string.jwt)));
                        pref.setTitle("JWT valid until " + DisplayUtils.parseDate(jwtUpdated.getExpiresAt()));
                    });

                    return true;
                });
            } catch (DecodeException e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(getContext(), "Unable to retrieve JWT", Toast.LENGTH_LONG).show();
            }

            Preference showIpPrefs = findPreference("pref_show_ip");
            showIpPrefs.setSummary(RequestScheme.AUTHORITY);
        }


        @Override
        public void onPause() {

            super.onPause();
        }

    }
}
