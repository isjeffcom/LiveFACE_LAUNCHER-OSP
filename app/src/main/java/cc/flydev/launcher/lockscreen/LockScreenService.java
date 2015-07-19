package cc.flydev.launcher.lockscreen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import cc.flydev.launcher.settings.SettingsProvider;
/***
 * lock screen Service
 * 
 * @author WeiGuoWang
 * 
 */

public class LockScreenService extends Service {

	private Intent startIntent = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if(!SettingsProvider.getBoolean(this, "settings_lock_open", true)) {
			stopSelf();
		}
		startIntent = new Intent(LockScreenService.this, LockScreenActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		LockScreenService.this.registerReceiver(MyLockScreenReceiver, filter);
		System.out.println("onCreate()................");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy()...........");
		// 注销广播
		unregisterReceiver(MyLockScreenReceiver);
		// 再次启动服务
		startService(new Intent(LockScreenService.this, LockScreenService.class));
	}

	// 取消系统默认锁屏界面的关键对象
	private KeyguardManager keyguardManager;
	private KeyguardManager.KeyguardLock keyguardLock;

	private BroadcastReceiver MyLockScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("receiver--action=" + intent.getAction());
			/**
			 * ACTION_SCREEN_OFF表示按下电源键，屏幕黑屏 ACTION_SCREEN_ON 屏幕黑屏情况下，按下电源键
			 */
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
				// 取消默认的锁屏
				keyguardLock = keyguardManager.newKeyguardLock("");
				keyguardLock.disableKeyguard();
				startActivity(startIntent);
			}
		}
	};

}
