package cc.flydev.launcher.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import cc.flydev.face.R;
import cc.flydev.launcher.LauncherAppState;
import cc.flydev.launcher.LauncherApplication;

public class TestingActivity extends AbstractBarPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new TestingFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        // TODO: Implement this method
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO: Implement this method
        super.onResume();
    }

    class TestingFragment extends PreferenceFragment implements OnPreferenceChangeListener {

        private CheckBoxPreference mUpMenu;

        private final String upMenuOpen = "settings_testing_upmenu_open";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_testing);
            mUpMenu = (CheckBoxPreference) findPreference(upMenuOpen);

            boolean checked = SettingsProvider.getBoolean(getActivity(), upMenuOpen, false);
            mUpMenu.setChecked(checked);
            mUpMenu.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean ret = false, needsRestart = false;
            if (preference == mUpMenu) {
                SettingsProvider.putBoolean(getActivity(), upMenuOpen, (Boolean) newValue);
                ret = true;
                needsRestart = true;
            } else {
                ret = false;
            }

            if (needsRestart) {
                if (SettingsProvider.getBoolean(getActivity(), "settings_testing_upmenu_open", true)) {
                    boolean isRunning = LauncherApplication.getInstance().isServiceRunning("cc.flydev.launcher.services.UpMenuControlService");
                    if (isRunning) {
                        stopService(LauncherAppState.getInstance().getUpMenuService());
                    }
                    startService(LauncherAppState.getInstance().getUpMenuService());
                } else {
                    stopService(LauncherAppState.getInstance().getUpMenuService());
                }
            }
//			Toast.makeText(this, R.string.message_needs_restart, 1000).show();

            return ret;
        }

    }
}
