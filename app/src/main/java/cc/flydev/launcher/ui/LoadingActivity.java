package cc.flydev.launcher.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cc.flydev.launcher.settings.SettingsProvider;

public class LoadingActivity extends Activity {

	private final String IS_FIRST_LAUNCH = "is_first_launch";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(SettingsProvider.getBoolean(this, IS_FIRST_LAUNCH, true)) {
			SettingsProvider.putString(this, SettingsProvider.KEY_INTERFACE_ICONPACK, "cc.flydev.face");
			SettingsProvider.putBoolean(this, IS_FIRST_LAUNCH, false);
		}
		Intent i = new Intent(this, cc.flydev.launcher.Launcher.class);
		startActivity(i);
		finish();
	}
	
	
}
