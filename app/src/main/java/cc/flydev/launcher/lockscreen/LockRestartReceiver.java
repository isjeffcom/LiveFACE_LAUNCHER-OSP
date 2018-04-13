package cc.flydev.launcher.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import cc.flydev.launcher.debug.TimesDebugger;
import cc.flydev.launcher.settings.SettingsProvider;

public class LockRestartReceiver extends BroadcastReceiver {

	private Handler mHandler = new Handler();

	@Override
	public void onReceive(final Context context, Intent intent) {
		TimesDebugger.addTimes(context, "LockRestart");
		if(intent.getAction() == "cc.flydev.launcher.lockscreen.pause") {
			if(SettingsProvider.getBoolean(context, "locked", false)) {
				Intent i = new Intent(context, LockScreenActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						   Intent.FLAG_ACTIVITY_SINGLE_TOP |
						   Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}
		}
	}

}
