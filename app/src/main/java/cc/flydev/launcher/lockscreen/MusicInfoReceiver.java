package cc.flydev.launcher.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MusicInfoReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		((LockScreenActivity) context).setMusicName(intent.getStringExtra("track"));
	}
	
}
