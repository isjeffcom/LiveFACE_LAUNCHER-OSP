package cc.flydev.launcher.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import cc.flydev.face.R;

public class AboutActivity extends AbstractBarPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new AboutFragment())
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

    class AboutFragment extends PreferenceFragment {
        private final String KEY_VERSION = "about_version";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about);

            String versionName;
            try {
                versionName = getPackageManager().getPackageInfo("cc.flydev.face", 0).versionName;
            } catch (Exception e) {
                versionName = "0.1";
            }
            findPreference(KEY_VERSION).setSummary(versionName);

        }
    }

}
