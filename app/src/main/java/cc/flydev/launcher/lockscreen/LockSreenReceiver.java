package cc.flydev.launcher.lockscreen;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cc.flydev.launcher.settings.SettingsProvider;

/***
 * lock screen BroadcastReceiver
 * 
 * @author WeiGuoWang
 * 
 */

public class LockSreenReceiver extends BroadcastReceiver {

	private KeyguardManager keyguardManager;
	private KeyguardManager.KeyguardLock keyguardLock;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("if 外面");
		if (SettingsProvider.getBoolean(context, "settings_lock_open", true) && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			System.out.println("if 里面");
			Intent lockIntent = new Intent(context, LockScreenActivity.class);
			lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			keyguardLock = keyguardManager.newKeyguardLock("");
			keyguardLock.disableKeyguard();

			context.startActivity(lockIntent);

		}

	}

}
