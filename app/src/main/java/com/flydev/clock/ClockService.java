package com.flydev.clock;

import java.util.Timer;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class ClockService extends Service {
	AppWidgetManager manager;
	private RemoteViews views;
	private Timer timer;
	TimeChangeReceiver receiver;
	int width;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		WindowManager wm = (WindowManager) getBaseContext().getSystemService(
				Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		System.out.println(width);
		manager = AppWidgetManager.getInstance(this);
		views = MyWidgetProvider.updateTime(this, width);

		if (views != null) {
			manager.updateAppWidget(new ComponentName(this,
					MyWidgetProvider.class), views);
		}
		receiver = new TimeChangeReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, intentFilter);
	}

	public class TimeChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("时间变化");
			views = MyWidgetProvider.updateTime(ClockService.this, width);

			if (views != null) {
				manager.updateAppWidget(new ComponentName(ClockService.this,
						MyWidgetProvider.class), views);
			}
		}
	}


	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);

	}

}
