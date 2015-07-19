package cc.flydev.launcher.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.MenuItem;
import android.widget.Toast;
import cc.flydev.face.R;

public class EffectsActivity extends AbstractBarPreferenceActivity implements OnPreferenceChangeListener
{

    private CheckBoxPreference mAutoRotate;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Add them all
        addPreferencesFromResource(R.xml.settings_effects);
        setContentView(R.layout.listview_noline);
        // Find
        mAutoRotate = (CheckBoxPreference) findPreference(SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE);
        
        // Initialize
        boolean checked = SettingsProvider.getBoolean(this, 
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
            SettingsProvider.putBoolean(this, 
                SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE,
                (Boolean) newValue);
            ret = true;
        } else {
            ret = false;
        }
        
        if (needsRestart) {
            Toast.makeText(this, R.string.message_needs_restart, 1000);
        }
        
        return ret;
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
	
	
	
}
