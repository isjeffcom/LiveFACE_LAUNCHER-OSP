package cc.flydev.launcher;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cc.flydev.launcher.debug.TimesDebugger;
import cc.flydev.launcher.lockscreen.LockScreenActivity;
import cc.flydev.launcher.settings.SettingsProvider;

public class SwitchLockActivity extends Activity {
	private Intent mLauncherIntent, mLockIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TimesDebugger.addTimes(this, "HomeClick");
		super.onCreate(savedInstanceState);
		mLauncherIntent = new Intent(this, Launcher.class);
		mLockIntent = new Intent(this, LockScreenActivity.class);
		
		
		if(SettingsProvider.getBoolean(this, "locked", false)) {
			startActivity(mLockIntent);
		} else {
			startActivity(mLauncherIntent);
		}	
		finish();
	}	
}
