package com.project.tenvinc.bluetoothreminder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import static android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    public static String TAG = "SettingsActivity";
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sUpdateBeaconManagerListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            BeaconApplication.getInstance().updateBeaconManagerSettings(preference, newValue);
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sOnOffBeaconScanningListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            BeaconApplication.getInstance().onOffBeaconScan(preference, newValue);
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sUpdateNotifSettingsListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            BeaconApplication.getInstance().updateNotifSettings(preference, newValue);
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void triggerListenerUpdate(Preference preference) {
        if (preference instanceof SwitchPreference) {
            preference.getOnPreferenceChangeListener().onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        } else if (preference instanceof RingtonePreference) {
            Log.e(TAG, "This should not be happening");
        } else {
            preference.getOnPreferenceChangeListener().onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || ScanPreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows preferences for settings related to beacon scanning.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ScanPreferenceFragment extends PreferenceFragment {
        public static final String PREF_KEY_BG_PERIOD = "pref_bg_period";
        public static final String PREF_KEY_BG_DELAY = "pref_bg_delay";
        public static final String PREF_KEY_FG_PERIOD = "pref_fg_period";
        public static final String PREF_KEY_FG_DELAY = "pref_fg_delay";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_scan);

            bindScanSettingsToListeners(findPreference(PREF_KEY_BG_PERIOD));
            bindScanSettingsToListeners(findPreference(PREF_KEY_BG_DELAY));
            bindScanSettingsToListeners(findPreference(PREF_KEY_FG_PERIOD));
            bindScanSettingsToListeners(findPreference(PREF_KEY_FG_DELAY));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void bindScanSettingsToListeners(Preference preference) {
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                    sUpdateBeaconManagerListener.onPreferenceChange(preference, newValue);
                    return true;
                }
            });
            triggerListenerUpdate(preference);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {

        public static final String PREF_KEY_SYSTEM_NOTIF = "pref_system_notif";
        public static final String PREF_KEY_NOTIF_FREQUENCY = "pref_notif_frequency";
        public static final String PREF_KEY_SWITCH_NOTIF = "notifications_beacon_oor";
        public static final String PREF_KEY_SWITCH_VIBRATION = "notifications_vibrate";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            Preference intentPref = findPreference(PREF_KEY_SYSTEM_NOTIF);
            Intent intent = new Intent(ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("android.provider.extra.APP_PACKAGE", BeaconApplication.getInstance().getPackageName());
            intentPref.setIntent(intent);

            Preference notifPref = findPreference(PREF_KEY_NOTIF_FREQUENCY);
            notifPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                    sUpdateNotifSettingsListener.onPreferenceChange(preference, newValue);
                    return true;
                }
            });
            triggerListenerUpdate(notifPref);

            Preference vibratePref = findPreference(PREF_KEY_SWITCH_VIBRATION);
            vibratePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sUpdateNotifSettingsListener.onPreferenceChange(preference, newValue);
                    return true;
                }
            });
            triggerListenerUpdate(vibratePref);

            findPreference(PREF_KEY_SWITCH_NOTIF).setOnPreferenceChangeListener(sUpdateNotifSettingsListener);

            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        public static final String PREF_KEY_SWITCH_BG = "pref_switch_bg";
        public static final String PREF_KEY_SWITCH_FG = "pref_switch_fg";
        public static final String PREF_KEY_SWITCH_SCAN = "pref_switch_scan";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            final SwitchPreference bgPref = (SwitchPreference) findPreference(PREF_KEY_SWITCH_BG);
            final SwitchPreference fgPref = (SwitchPreference) findPreference(PREF_KEY_SWITCH_FG);

            findPreference(PREF_KEY_SWITCH_SCAN).setOnPreferenceChangeListener(sOnOffBeaconScanningListener);

            bgPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        fgPref.setChecked(!((Boolean) newValue));
                        sUpdateBeaconManagerListener.onPreferenceChange(preference, newValue);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            fgPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        bgPref.setChecked(!((Boolean) newValue));
                        sUpdateBeaconManagerListener.onPreferenceChange(preference, newValue);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
