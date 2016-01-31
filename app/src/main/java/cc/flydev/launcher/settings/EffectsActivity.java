package cc.flydev.launcher.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.widget.Toast;

import cc.flydev.face.R;

public class EffectsActivity extends AbstractBarPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new EffectsFragment())
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

    class EffectsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

        private CheckBoxPreference mAutoRotate;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_effects);
            mAutoRotate = (CheckBoxPreference) findPreference(SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE);

            // Initialize
            boolean checked = SettingsProvider.getBoolean(getActivity(),
                    SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE,
                    false);
            mAutoRotate.setChecked(checked);
            mAutoRotate.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean needsRestart = false;
            boolean ret = false;
            if (preference == mAutoRotate) {
                SettingsProvider.putBoolean(getActivity(),
                        SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE,
                        (Boolean) newValue);
                ret = true;
            } else {
                ret = false;
            }

            if (needsRestart) {
                Toast.makeText(getActivity(), R.string.message_needs_restart, 1000);
            }

            return ret;
        }
    }

}
