package cc.flydev.launcher;

import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cc.flydev.face.R;
import cc.flydev.launcher.db.MusicDBhelper;
import cc.flydev.launcher.entity.Music;
import cc.flydev.launcher.services.PlayMusicService;
import cc.flydev.launcher.utils.DataUtils;

/*
 * Settings 设置类:
 * The Settings provider contains global system-level device preferences.
 * 
 * Settings.System 静态内部类
 * System settings, containing miscellaneous system preferences.
 * This table holds simple name/value pairs. 
 * There are convenience functions for accessing individual settings entries.
 * 
 * 静态方法,getString(ContentResolver resolver, String name):
 * Look up a name in the database.
 */

public class UpMenu extends Activity implements OnClickListener, OnTouchListener {

	private View floatView;
	private LinearLayout mParent;
	private TextView musicNameTv;
	private Button playIv;
	private Button nextIv;
	private ImageView airmodIv;
	private ImageView wifiIv;
	private ImageView dataIv;
	private ImageView gpsIv;
	private ImageView torchIv;
	private ImageView brightnessIv;
	private SeekBar brightSeekBar;

	private int musicIndex = 0;
	private List<Music> mList = DataUtils.getmList();
	private MyHandler handler = new MyHandler();
	private int bright;//记录当前屏幕的亮度
	private int brightmode;//记录当前屏幕的亮度的调节模式
	private WifiManager wifiManager;
	private ContentResolver cr;
	private Camera camera;

	private LinearLayout upMenuBkg;
	
	private WindowManager mWM;
	private WindowManager.LayoutParams mTouch;
	
	private int maxHeight;
	private boolean hasMeasured = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow():Window The current window, or null if the activity is not visual.
		getWindow().setGravity(Gravity.BOTTOM);
		
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater lf = getLayoutInflater();
		floatView = lf.inflate(R.layout.up_menu, null);
		
		mTouch = new WindowManager.LayoutParams();
		mTouch.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mTouch.gravity = Gravity.BOTTOM;
		//mTouch.format = PixelFormat.TRANSLUCENT;

		mTouch.height = 18;
		mTouch.width = WindowManager.LayoutParams.MATCH_PARENT;
		mTouch.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		
		
		
		setContentView(floatView);
		
		
	
		
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		cr = getContentResolver();
		initView();
		initEvent();
		//加载歌曲
		loadMusic();
		
		
		
		ViewTreeObserver vto = upMenuBkg.getViewTreeObserver();
		vto.addOnPreDrawListener(new OnPreDrawListener() {

				@Override
				public boolean onPreDraw() {
					if(hasMeasured == false) {
						maxHeight = upMenuBkg.getHeight();
						if(maxHeight != 0) {
							hasMeasured = true;
							Toast.makeText(UpMenu.this, "" + maxHeight, 500).show();
						}
					}
					return true;
				}

			});
		
	}

	/**
	 * 加载歌曲
	 */
	private void loadMusic() {
		new Thread(new Runnable() {
				@Override
				public void run() {
					MusicDBhelper dBhelper = new MusicDBhelper(getApplicationContext());
					SQLiteDatabase db = dBhelper.getWritableDatabase();
					Cursor cursor = db.query("music", null, null, null, null, null, null);

					//一开始表中没有数据,要从内容提供者中获取
					if(cursor == null || cursor.getCount() == 0) {
						ContentResolver resolver = getContentResolver();
						cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
						while(cursor.moveToNext()) {
							String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
							String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
							String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
							ContentValues values = new ContentValues();
							values.put("path", path);
							values.put("name", name);
							values.put("artist", artist);
							db.insert("music", null, values);
							Music music = new Music(1, name, path, artist);
							mList.add(music);
						}
					} else {
						while(cursor.moveToNext()) {
							String path = cursor.getString(cursor.getColumnIndex("path"));
							String name = cursor.getString(cursor.getColumnIndex("name"));
							String artist = cursor.getString(cursor.getColumnIndex("artist"));
							Music music = new Music(1, name, path, artist);
							mList.add(music);
						}
					}
					cursor.close();
					db.close();
					handler.sendEmptyMessage(1);
				}
			})
			.start();
	}

	//开启播放音乐服务
	private void play() {
		startService(new Intent(this, PlayMusicService.class));
	}

	/**
	 * 设置控件监听
	 */
	private void initEvent() {
		mParent.setOnClickListener(this);
		upMenuBkg.setOnClickListener(this);
		playIv.setOnClickListener(this);
		nextIv.setOnClickListener(this);
		airmodIv.setOnClickListener(this);
		wifiIv.setOnClickListener(this);
		gpsIv.setOnClickListener(this);
		dataIv.setOnClickListener(this);
		torchIv.setOnClickListener(this);
		brightnessIv.setOnClickListener(this);
		
		mParent.setOnTouchListener(this);
		
		brightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
											  boolean fromUser) {
					if(brightmode == 0) {
						seekBar.setProgress(progress);
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
					}	

				}
			});
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mParent = (LinearLayout) findViewById(R.id.up_menu_ll);
		musicNameTv = (TextView) findViewById(R.id.musicName);
		playIv = (Button) findViewById(R.id.play);
		nextIv = (Button) findViewById(R.id.next);

		airmodIv = (ImageView) findViewById(R.id.airmod);

		wifiIv = (ImageView) findViewById(R.id.wifi);
		if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
			wifiIv.setImageResource(R.drawable.wifi_on);

		gpsIv = (ImageView) findViewById(R.id.gps);
		dataIv = (ImageView) findViewById(R.id.data);
		torchIv = (ImageView) findViewById(R.id.torch);

		brightnessIv = (ImageView) findViewById(R.id.brightness);
		brightSeekBar = (SeekBar) findViewById(R.id.brightSeekbar);
		brightSeekBar.setMax(255);
		try {
			brightmode = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);
			if(brightmode == 0) {
				brightnessIv.setImageResource(R.drawable.brightness_normal);
			} else {
				brightnessIv.setImageResource(R.drawable.brightness_auto);
			}
		} catch(SettingNotFoundException e) {
			e.printStackTrace();
		}

		upMenuBkg = (LinearLayout) findViewById(R.id.up_menu_bkg);
		
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			Toast.makeText(this, "touched down", Toast.LENGTH_SHORT).show();
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			Toast.makeText(this, "touched up", Toast.LENGTH_SHORT).show();
		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			//Toast.makeText(this, "touched move", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	/**
	 * 点击事件实现的方法
	 */

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.play://播放暂停按钮
				if(PlayMusicService.playstate == PlayMusicService.PLAYING_STATE) {
					playIv.setBackgroundResource(R.drawable.play_select);
					PlayMusicService.pause();
				} else if(PlayMusicService.playstate == PlayMusicService.PAUSE_STATE) {
					playIv.setBackgroundResource(R.drawable.stop_select);
					PlayMusicService.goOn();
				} else if(PlayMusicService.playstate == PlayMusicService.INITIAL_STATE) {
					playIv.setBackgroundResource(R.drawable.stop_select);
					PlayMusicService.playMusic();
					handler.sendEmptyMessage(2);
				}
				break;
			case R.id.next://下一首歌
				PlayMusicService.playNext();
				handler.sendEmptyMessage(2);
				break;
			case R.id.airmod:
//			try {
				/*
				 * 0:代表没有开启飞行模式，1代表开启了飞行模式
				 * 因为只读，不能写入，没能成功实现开启飞行模式和关闭
				 */
//				int airmod = Settings.System.getInt(cr, Settings.System.AIRPLANE_MODE_ON);
//				Log.i("Tag", "airmod:"+airmod);
//				Settings.System.putInt(cr,Settings.System.AIRPLANE_MODE_ON, 1);
//				int airmod2 = Settings.System.getInt(cr, Settings.System.AIRPLANE_MODE_ON);
//				Log.i("Tag", "change-airmod:"+airmod2);
//			} catch (SettingNotFoundException e1) {
//				e1.printStackTrace();
//			}
				break;
			case R.id.wifi:
				int wifiState = wifiManager.getWifiState();
				if(WifiManager.WIFI_STATE_DISABLED == wifiState) {
					wifiManager.setWifiEnabled(true);
					wifiIv.setImageResource(R.drawable.wifi_on);
				} else if(WifiManager.WIFI_STATE_ENABLED == wifiState) {
					wifiManager.setWifiEnabled(false);
					wifiIv.setImageResource(R.drawable.wifi);
				}
				break;
			case R.id.gps:
//			Settings.System.putString(cr, Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "gps");
				/*
				 * 获取Location定位服务,(network,gps)
				 * 因为只读，不能写入，没能成功实现开启GPS和关闭
				 */
//			String gps = Settings.Secure.getString(cr, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//			Log.i("Tag", "location:"+gps);			
				break;
			case R.id.torch:
				/*
				 * 打开闪光灯,关闭闪光灯(作为手电筒)
				 */
				//获取Camera对象
				if(camera == null) camera = Camera.open();
				Parameters parameters = camera.getParameters();
				String flashMode = parameters.getFlashMode();
				Log.i("Tag", flashMode);
				if(flashMode.equals(Parameters.FLASH_MODE_OFF)) {
					camera.startPreview();
					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
					camera.setParameters(parameters);
					torchIv.setImageResource(R.drawable.torch_on);
				} else if(flashMode.equals(Parameters.FLASH_MODE_TORCH)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(parameters);
					camera.release();
					camera = null;
					torchIv.setImageResource(R.drawable.torch);
				}
				break;
			case R.id.brightness:
				try {
					bright = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
					Log.i("Tag", "bright:" + String.valueOf(bright));
					//1:自动调节亮度   0：表示手动调节亮度
					brightmode = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);
					Log.i("Tag", "start-brightmode:" + String.valueOf(brightmode));
					if(brightmode == 0) {
						Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
						brightnessIv.setImageResource(R.drawable.brightness_auto);
					} else {
						Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
						brightnessIv.setImageResource(R.drawable.brightness_normal);
					}
					brightmode = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);
					Log.i("Tag", "change-brightmode:" + String.valueOf(brightmode));
				} catch(SettingNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case R.id.up_menu_ll:
				break;
			case R.id.up_menu_bkg:
				finish();
				break;
		}
	}

	/**
	 * handler
	 * @author boshao
	 */
	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case 1:
					UpMenu.this.play();				
					break;
				case 2:
					musicNameTv.setText(DataUtils.getMusic(PlayMusicService.musicIndex).getName());
					break;
				case 3:

					break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		Log.i("Tag", "Activity--->onDestroy");
		super.onDestroy();
	}
}
