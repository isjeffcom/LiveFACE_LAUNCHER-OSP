package cc.flydev.launcher.services;


import java.util.List;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.flydev.face.R;
import cc.flydev.launcher.db.MusicDBhelper;
import cc.flydev.launcher.entity.Music;
import cc.flydev.launcher.settings.SettingsProvider;
import cc.flydev.launcher.utils.DataUtils;

public class UpMenuControlService extends Service {

	private WindowManager mWM;
	private WindowManager.LayoutParams mTouch, mBkg, mExpand;
	private View mView, mBack;

	private int maxHeight, screenHeight;
	private boolean measured = false;
	
	private int upY = 0, downY;
	

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
	

	@Override
	public IBinder onBind(Intent p1) {
		return null;
	}

	@Override
	public void onCreate() {
		
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		
		if(SettingsProvider.getBoolean(this, "settings_testing_upmenu_open", false)) {
			initLP();
			initView();
		} else {
			stopSelf();
		}

		super.onCreate();
	}

	private void initView() {
		
		LayoutInflater lf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = lf.inflate(R.layout.up_menu, null);
		
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		cr = getContentResolver();
		
		initView2();
		initEvent();
		loadMusic();
		
		mView.setOnTouchListener(mTouchListener);
		
		
		mBack = new View(this);
		//mBack.setBackgroundColor(0x77000000);
		mBack.setOnTouchListener(mBackListener);
		mWM.addView(mBack, mBkg);
		
		mWM.addView(mView, mTouch);

		DisplayMetrics dm = new DisplayMetrics();
		mWM.getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
		
		ViewTreeObserver vto = mView.getViewTreeObserver();
		vto.addOnPreDrawListener(new OnPreDrawListener() {

				@Override
				public boolean onPreDraw() {
					if(!measured) {
						maxHeight = mView.getHeight();
						if(maxHeight != 0) {
							measured = true;
							debug("" + maxHeight);
							mTouch.y = downY = 18 - maxHeight;
							updatePos();
						}
					}
					return true;
				}

			});
		
		
	}
	

	/**
	 * 初始化控件
	 */
	private void initView2() {
		musicNameTv = (TextView) mView.findViewById(R.id.musicName);
		playIv = (Button) mView.findViewById(R.id.play);
		nextIv = (Button) mView.findViewById(R.id.next);

		airmodIv = (ImageView) mView.findViewById(R.id.airmod);

		wifiIv = (ImageView) mView.findViewById(R.id.wifi);
		if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
			wifiIv.setImageResource(R.drawable.wifi_on);

		gpsIv = (ImageView) mView.findViewById(R.id.gps);
		dataIv = (ImageView) mView.findViewById(R.id.data);
		torchIv = (ImageView) mView.findViewById(R.id.torch);

		brightnessIv = (ImageView) mView.findViewById(R.id.brightness);
		brightSeekBar = (SeekBar) mView.findViewById(R.id.brightSeekbar);
		brightSeekBar.setMax(255);
		try {
			bright = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
			brightmode = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);
			if(brightmode == 0) {
				brightnessIv.setImageResource(R.drawable.brightness_normal);
			} else {
				brightnessIv.setImageResource(R.drawable.brightness_auto);
			}
		} catch(SettingNotFoundException e) {
			e.printStackTrace();
		}
		brightSeekBar.setProgress(bright);

		//upMenuBkg = (RelativeLayout) mView.findViewById(R.id.up_menu_bkg);
	}
	

	/**
	 * 设置控件监听
	 */
	private void initEvent() {
		//upMenuBkg.setOnClickListener(mOnClickListener);
		playIv.setOnClickListener(mOnClickListener);
		nextIv.setOnClickListener(mOnClickListener);
		airmodIv.setOnClickListener(mOnClickListener);
		wifiIv.setOnClickListener(mOnClickListener);
		gpsIv.setOnClickListener(mOnClickListener);
		dataIv.setOnClickListener(mOnClickListener);
		torchIv.setOnClickListener(mOnClickListener);
		brightnessIv.setOnClickListener(mOnClickListener);
		//musicNameTv.setOnTouchListener(mTouchListener);

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
	
	private void play() {
		startService(new Intent(this, PlayMusicService.class));
	}

	private void initLP() {
		mTouch = new WindowManager.LayoutParams();
		mTouch.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mTouch.gravity = Gravity.BOTTOM;
		mTouch.format = PixelFormat.TRANSLUCENT;
		mTouch.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mTouch.width = WindowManager.LayoutParams.MATCH_PARENT;
		mTouch.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mTouch.x = 0;
		mTouch.y = 0;
		
		mBkg = new WindowManager.LayoutParams();
		mBkg.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mBkg.format = PixelFormat.TRANSLUCENT;
		mBkg.height = 0;//WindowManager.LayoutParams.MATCH_PARENT;
		mBkg.width = 0;
		mBkg.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mBkg.gravity = Gravity.TOP | Gravity.LEFT;
		mBkg.x = mBkg.y = 0;
		
		mExpand = new WindowManager.LayoutParams();
		mExpand.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		mExpand.format = PixelFormat.TRANSLUCENT;
		mExpand.height = WindowManager.LayoutParams.MATCH_PARENT;
		mExpand.width = WindowManager.LayoutParams.MATCH_PARENT;
		mExpand.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mExpand.gravity = Gravity.TOP | Gravity.LEFT;
		mExpand.x = mBkg.y = 0;
	}

	private void updatePos() {
		mWM.updateViewLayout(mView, mTouch);
	}
	
	private void moveUp() {
		int time = upY - mTouch.y;
		ValueAnimator va = ValueAnimator.ofInt(mTouch.y, upY);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					mTouch.y = (Integer) animation.getAnimatedValue();
					updatePos();
					mBack.setAlpha(getAlphaValue(mTouch.y));
				}

			});
		va.setDuration(time);
		va.setInterpolator(new OvershootInterpolator());
		va.start();
	}
	
	private void moveDown() {
		int time = mTouch.y - downY;
		ValueAnimator va = ValueAnimator.ofInt(mTouch.y, downY);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					mTouch.y = (Integer) animation.getAnimatedValue();
					updatePos();
					mBack.setAlpha(getAlphaValue(mTouch.y));
				}

			});
		va.setDuration(time);
		va.setInterpolator(new AccelerateDecelerateInterpolator());
		va.start();
		
		mWM.updateViewLayout(mBack, mBkg);
		debug("" + mBkg.height);
	}
	

	private OnTouchListener mTouchListener = new OnTouchListener() {
		
		int stY = 0, mY = 0;
		
		@Override
		public boolean onTouch(View view, MotionEvent me) {

			if(me.getAction() == MotionEvent.ACTION_DOWN) {
				stY = (int) me.getRawY();
				mY = mTouch.y;
				debug("" + stY + " " + mY);
				
				mWM.updateViewLayout(mBack, mExpand);
				
			} else if(me.getAction() == MotionEvent.ACTION_UP) {
				int temp = mTouch.y - downY;
				if(temp >= maxHeight / 2) {
					moveUp();
				} else {
					moveDown();
				}
			} else if(me.getAction() == MotionEvent.ACTION_MOVE) {
				mTouch.y = mY + stY - (int) me.getRawY();//screenHeight - (int) me.getRawY() - maxHeight + stY;
				//debug("" + mTouch.y + " " + stY + " " + (int) me.getRawY());
				if(mTouch.y >= upY)
					mTouch.y = upY;
				else if(mTouch.y <= downY)
					mTouch.y = downY;
				//stY = (int) me.getRawY();
				updatePos();
				mBack.setAlpha(getAlphaValue(mTouch.y));
			}

			return true;
		}

	};
	
	private OnTouchListener mBackListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent me) {
			if(mTouch.y >= 0 && (int) me.getRawY() < screenHeight - maxHeight) {
				moveDown();
				//mWM.updateViewLayout(mBack, mBkg);
				return true;
			}
			return false;
		}
		
	};
	
	private float getAlphaValue(int y) {
		return (y - downY) / (2 * maxHeight);
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {

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
			}
		}
		
		
	};
	
	private void debug(String s) {
		Log.d("aliveface", s);
		//Toast.makeText(UpMenuControlService.this, s, 300).show();
	}

	@Override
	public void onDestroy() {
		if(mView != null)
			mWM.removeView(mView);
		super.onDestroy();
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
					UpMenuControlService.this.play();				
					break;
				case 2:
					musicNameTv.setText(DataUtils.getMusic(PlayMusicService.musicIndex).getName());
					break;
				case 3:

					break;
			}
		}
	}

}
