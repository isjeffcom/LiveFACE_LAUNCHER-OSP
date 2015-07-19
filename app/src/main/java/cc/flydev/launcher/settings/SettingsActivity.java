package cc.flydev.launcher.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.MenuItem;
import cc.flydev.face.R;

public class SettingsActivity extends AbstractBarPreferenceActivity implements
		OnPreferenceClickListener {
	private Preference mRestart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		setContentView(R.layout.listview_noline);

//		 Initialize the preferences
		mRestart = findPreference(SettingsProvider.KEY_SETTINGS_RESTART);
		mRestart.setOnPreferenceClickListener(this);

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == mRestart) {
			// I am Launcher, so I will restart after killing
			System.exit(0);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
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
	
	
}
