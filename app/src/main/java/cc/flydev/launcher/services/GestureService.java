package cc.flydev.launcher.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import cc.flydev.launcher.UpMenu;

public class GestureService extends Service{
	public static GestureDetector detector;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {	
		Log.i("Tag", "GService-->onCreate");
		super.onCreate();
		detector = new GestureDetector(getApplicationContext(), new MyOnGetureListener());
	}
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		Log.i("Tag", "GService-->onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.i("Tag", "GService--->onDestroy");
		super.onDestroy();
	}
	
	class MyOnGetureListener implements OnGestureListener{

		/*
		 * 按下屏幕时调用
		 */
		@Override
		public boolean onDown(MotionEvent e) {
			Log.i("Tag", "onDown");
			return false;
		}
		
		/*
		 * 按下屏幕，并没有松开时调用
		 */
		@Override
		public void onShowPress(MotionEvent e) {
			Log.i("Tag", "onShowPress");
		}

		/*
		 * 按屏幕松开手时调用
		 */
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.i("Tag", "onSingleTagUp");
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.i("Tag", "onScroll");
			return false;
		}
				
		/*
		 * 用户长按时
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			Log.i("Tag", "onLongPress");
		}
		
		/*
		 * 屏幕左上角的坐标是（0,0）
		 * 
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1.getY() - e2.getY() > 100 & e1.getY() > 950.0){
				Log.i("Tag", "onFling--->SlideUp");
				Intent intent = new Intent(getApplicationContext(), UpMenu.class);
				startActivity(intent);
			}
			return false;
		}
		
	}
}
