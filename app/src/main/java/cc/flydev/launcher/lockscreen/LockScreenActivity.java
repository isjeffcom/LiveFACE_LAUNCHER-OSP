package cc.flydev.launcher.lockscreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import cc.flydev.face.R;
import cc.flydev.launcher.LauncherAppState;
import cc.flydev.launcher.debug.TimesDebugger;
import cc.flydev.launcher.settings.LockPassSettingActivity;
import cc.flydev.launcher.settings.SettingsProvider;



/***
 * lock screen Activity
 * 
 * @author WeiGuoWang
 * 
 */

public class LockScreenActivity extends Activity implements OnTouchListener, OnGestureListener {
	private LinearLayout music_layout; // 音乐控制
	private TextView unlock_hint; // 向上滑动的提示
	private RelativeLayout notie_layout; // 通知layout
	private RelativeLayout black_bar;//黑条
	private TextView cell_num; // 电话数量
	private TextView sms_num; // 短信数量
	private TextView hours; // 时间
	private TextView week; // 周
	private TextView years; // 年月日
	private ImageView wallpaper; //壁纸
	private TextView music_name;//音乐名称
	private ImageView nomusic_icon;

	private Handler UnlockHintHandler = new Handler();
	private Handler mCheckLockHandler = new Handler();

	//音乐控制
	private ImageButton skipleft, skipright, pause;
	private AudioManager am;

	private Intent intentService;
	private Context mContext;
	public static final int LOCKED_SUCCESS = 1;
	public static final int VIEW_INVALIDATE = 2;
	private GestureDetector mGestureDetector;
	private static final int UPDATA_TIME = 3; // 更新时间
	private static final int UPDATA_WEEK = 4;// 星期
	private static final int UPDATA_YEARS = 5; // 年月
	private Thread timeThread = null;
	private Thread weekThread = null;
	private Thread yearsThread = null;

	MusicInfoReceiver mMusicInfoReceiver = null;

	//滑动效果
	private boolean mFinish = false;//是否要结束
	private LinearLayout layoutRoot = null;//视图根节点
	private Scroller mScroller = null;//滑动效果
	private View mParent = null;//父视图作为滑动对象

	private LinearLayout mLockPass = null;//锁屏密码
	private DisplayMetrics dm = new DisplayMetrics();//用于获得屏幕宽高
	private int lockPassHeight;//锁屏密码高度
	private boolean lockPassOpen;//是否有锁屏密码
	private TextView lockPassNum, lockForget;
	private ImageView lockPass1,
	lockPass2,
	lockPass3,
	lockPass4,
	lockPass5,
	lockPass6,
	lockPass7,
	lockPass8,
	lockPass9;

	private boolean mOnPhone = false;//是否处在通话状态
	private boolean mNeedRestart = true;//屏幕是否关闭
	private boolean mLocking = false;



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		TimesDebugger.addTimes(this, "LockScreen");

		if (!SettingsProvider.getBoolean(this, "settings_lock_open", true))
			finish();

		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//添加变量完成滑动操作
		/////begin

		mScroller = new Scroller(this);

		//添加可以响应scroller的LinearLayout
		layoutRoot = new LinearLayout(this) {
			@Override
			public void computeScroll() {
				if (mScroller.computeScrollOffset()) {
					scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
					postInvalidate();
					if (mScroller.isFinished() && mFinish) {
						SettingsProvider.putBoolean(LockScreenActivity.this, "locked", false);
						mLocking = false;
						LockScreenActivity.this.finish();
					}
				}
			}
		};

		//填充layout到layoutRoot
		LayoutInflater inflater = getLayoutInflater();
		inflater.inflate(R.layout.activity_lockscreen, layoutRoot, true);

		mParent = layoutRoot;
		/////end

		setContentView(layoutRoot);

		SettingsProvider.putBoolean(this, "locked", true);
		mLocking = true;

		lockPassOpen = SettingsProvider.getBoolean(this, "settings_lock_pass_open", false);

		mLockPass = (LinearLayout) findViewById(R.id.lock_screen_pass);

		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		if (!lockPassOpen) {
			mLockPass.setVisibility(View.GONE);
		} else {

			mLockPass.setVisibility(View.VISIBLE);
			
			ViewTreeObserver vto = layoutRoot.getViewTreeObserver();
			vto.addOnPreDrawListener(new OnPreDrawListener() {
					boolean measured = false;
					@Override
					public boolean onPreDraw() {
						if (!measured) {
							if ((lockPassHeight = mLockPass.getHeight()) != 0) {
								FrameLayout.LayoutParams lp =
									new FrameLayout.LayoutParams(
									FrameLayout.LayoutParams.MATCH_PARENT,
									lockPassHeight + dm.heightPixels);

								layoutRoot.setLayoutParams(lp);
								measured = true;
							}
						}
						return true;
					}

				});

		}



		initViews();
		initData();
		startService();
		
		RelativeLayout.LayoutParams wallLp =
			new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			dm.heightPixels);
		wallpaper.setLayoutParams(wallLp);

		mGestureDetector = new GestureDetector((OnGestureListener) this);
		RelativeLayout viewSnsLayout = (RelativeLayout) findViewById(R.id.viewlockscreen);
		viewSnsLayout.setOnTouchListener(this);
		//viewSnsLayout.setLongClickable(true);

		UpdateTime();

		TelephonyManager mTeleManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTeleManager.listen(new HideLockOnPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);


		
	}

	/**
	 * 更新相关时间的操作
	 * 
	 * **/

	private void UpdateTime() {
		// 更新时间线程
		timeThread = new Thread() {

			@Override
			public void run() {
				Message message = handler.obtainMessage(UPDATA_TIME, getTime());
				handler.sendMessage(message);
				handler.postDelayed(this, 3000);

			}
		};
		timeThread.start();
		// 更新日期线程
		yearsThread = new Thread() {
			public void run() {
				Message message = handler.obtainMessage(UPDATA_YEARS, getYear());
				handler.sendMessage(message);
				handler.postDelayed(this, 10000);

			};
		};
		yearsThread.start();
		// 更新星期
		weekThread = new Thread() {
			public void run() {
				Message message = handler.obtainMessage(UPDATA_WEEK, getWeek());
				handler.sendMessage(message);
				handler.postDelayed(this, 10000);
			};
		};
		weekThread.start();

	}

	/**
	 * 初始化视图，并添加相应事件
	 */

	private void initViews() {
		mContext = LockScreenActivity.this;
		music_layout = (LinearLayout) findViewById(R.id.screen_music_layout);
		unlock_hint = (TextView) findViewById(R.id.screen_unlock_hint);
		unlock_hint.setTypeface(LauncherAppState.getInstance().getIuniTypeface());
		notie_layout = (RelativeLayout) findViewById(R.id.screen_notie_layout);
		cell_num = (TextView) findViewById(R.id.screen_cell_num);
		cell_num.setTypeface(LauncherAppState.getInstance().getAvanGardeTwoBQTypeface());
		sms_num = (TextView) findViewById(R.id.screen_sms_num);
		sms_num.setTypeface(LauncherAppState.getInstance().getAvanGardeTwoBQTypeface());
		hours = (TextView) findViewById(R.id.screen_hours);
		hours.setTypeface(LauncherAppState.getInstance().getAvanGardeTwoBQTypeface());
		week = (TextView) findViewById(R.id.screen_week);
		week.setTypeface(LauncherAppState.getInstance().getIuniTypeface());
		years = (TextView) findViewById(R.id.screen_years);
		years.setTypeface(LauncherAppState.getInstance().getAvanGardeTwoBQTypeface());
		wallpaper = (ImageView) findViewById(R.id.screen_wallpaper);
		black_bar = (RelativeLayout) findViewById(R.id.screen_bottom_layout);
		music_name = (TextView) findViewById(R.id.screen_music_name);
		music_name.setTypeface(LauncherAppState.getInstance().getAvanGardeTwoBQTypeface());
		nomusic_icon = (ImageView) findViewById(R.id.screen_nomusic);

		lockForget = (TextView) findViewById(R.id.lock_forget);
		lockForget.setTypeface(LauncherAppState.getInstance().getIuniTypeface());
		lockPassNum = (TextView) findViewById(R.id.lock_pass_num);
		lockPassNum.setTypeface(LauncherAppState.getInstance().getAvanGardeTwoBQTypeface());
		lockPass1 = (ImageView) findViewById(R.id.lock_pass_1);
		lockPass2 = (ImageView) findViewById(R.id.lock_pass_2);
		lockPass3 = (ImageView) findViewById(R.id.lock_pass_3);
		lockPass4 = (ImageView) findViewById(R.id.lock_pass_4);
		lockPass5 = (ImageView) findViewById(R.id.lock_pass_5);
		lockPass6 = (ImageView) findViewById(R.id.lock_pass_6);
		lockPass7 = (ImageView) findViewById(R.id.lock_pass_7);
		lockPass8 = (ImageView) findViewById(R.id.lock_pass_8);
		lockPass9 = (ImageView) findViewById(R.id.lock_pass_9);

		lockPass1.setOnClickListener(LockPassListener);
		lockPass2.setOnClickListener(LockPassListener);
		lockPass3.setOnClickListener(LockPassListener);
		lockPass4.setOnClickListener(LockPassListener);
		lockPass5.setOnClickListener(LockPassListener);
		lockPass6.setOnClickListener(LockPassListener);
		lockPass7.setOnClickListener(LockPassListener);
		lockPass8.setOnClickListener(LockPassListener);
		lockPass9.setOnClickListener(LockPassListener);

		lockForget.setOnClickListener(ForgetListener);

		SharedPreferences msp = getSharedPreferences("LockScreen", Context.MODE_PRIVATE);

		// 隐藏 与显示操作
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (am.isMusicActive()) {
			music_layout.setVisibility(View.VISIBLE);
			nomusic_icon.setVisibility(View.GONE);
		} else {
			music_layout.setVisibility(View.GONE);
			nomusic_icon.setVisibility(View.VISIBLE);
		}

		notie_layout.setVisibility(View.GONE);
		unlock_hint.setText(msp.getString("lock_hint", "我是自定义文字"));
		TextPaint unlockHintPaint = unlock_hint.getPaint();
		unlockHintPaint.setFakeBoldText(true);
		unlock_hint.setVisibility(View.VISIBLE);
		unlock_hint.setAlpha(1.0f);
		UnlockHintHandler.postDelayed(fadeOutUnlockHint, 5000);

		//音乐控制
		final AudioManager.OnAudioFocusChangeListener afcl = new AudioManager.OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int p1) {

			}
		};

		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged"); 
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");
		iF.addAction("com.htc.music.metachanged"); 
		iF.addAction("fm.last.android.metachanged");
		iF.addAction("com.sec.android.app.music.metachanged"); 
		iF.addAction("com.nullsoft.winamp.metachanged"); 
		iF.addAction("com.amazon.mp3.metachanged"); 
		iF.addAction("com.miui.player.metachanged"); 
		iF.addAction("com.real.IMP.metachanged"); 
		iF.addAction("com.sonyericsson.music.metachanged");
		iF.addAction("com.rdio.android.metachanged");
		iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
		iF.addAction("com.andrew.apollo.metachanged");
		iF.addAction("com.kugou.android.music.metachanged"); 
		iF.addAction("com.ting.mp3.playinfo_changed"); 

		registerReceiver(mMusicInfoReceiver = new MusicInfoReceiver(), iF);

		skipleft = (ImageButton) findViewById(R.id.screen_skip_left);
		skipright = (ImageButton) findViewById(R.id.screen_skip_right);
		pause = (ImageButton) findViewById(R.id.screen_pause);
		skipleft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent("com.android.music.musicservicecommand");
					i.putExtra("command", "previous");
					sendBroadcast(i);
				}
			});

		skipright.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent("com.android.music.musicservicecommand");
					i.putExtra("command", "next");
					sendBroadcast(i);
				}
			});

		pause.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (am.isMusicActive()) {
						Intent i = new Intent("com.android.music.musicservicecommand");
						i.putExtra("command", "togglepause");
						sendBroadcast(i);
						am.requestAudioFocus(afcl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
						pause.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
					} else {
						Intent i = new Intent("com.android.music.musicservicecommand");
						i.putExtra("command", "togglepause");
						sendBroadcast(i);
						am.abandonAudioFocus(afcl);
						pause.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
					}
				}
			});

		//壁纸设置
		if (msp.getBoolean("isDrawable", true)) {
			int wallpaperId = msp.getInt("wallId", 0);
			if (wallpaperId == 0) {
				wallpaperId = R.drawable.wallpaper_1;
				msp.edit().putInt("wallId", wallpaperId).commit();
			}
			new WallpaperAsyncTask(wallpaper).execute(wallpaperId);
			msp.edit().putBoolean("isDrawable", true).commit();
		} else {
			String path = msp.getString("wallPath", "none");

			if (path.equals("none")) {
				int wallpaperId = msp.getInt("wallId", 0);
				if (wallpaperId == 0) {
					wallpaperId = R.drawable.wallpaper_1;
					msp.edit().putInt("wallId", wallpaperId).commit();
				}
				new WallpaperAsyncTask(wallpaper).execute(wallpaperId);
				msp.edit().putBoolean("isDrawable", true).commit();
			} else {
				new WallpaperAsyncTask2(wallpaper).execute(path);
			}
		}

	}
	
	class WallpaperAsyncTask2 extends AsyncTask<String,Void,Bitmap>{
		private ImageView iv;
		public WallpaperAsyncTask2(ImageView iv) {
			this.iv = iv;
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			String path = params[0];
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = calculateInSampleSize(opts,dm.widthPixels,dm.heightPixels);
			return BitmapFactory.decodeFile(path, opts);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
		}
		
	}
	
	
	
	class WallpaperAsyncTask extends AsyncTask<Integer,Void,Bitmap>{
		private ImageView iv;
		public WallpaperAsyncTask(ImageView iv) {
			this.iv = iv;
		}
		@Override
		protected Bitmap doInBackground(Integer... params) {
			int resid = params[0];
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(), resid , opts);
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = calculateInSampleSize(opts,dm.widthPixels,dm.heightPixels);
			return BitmapFactory.decodeResource(getResources(), resid , opts);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
		}
		
	}
	
	public static int calculateInSampleSize(
            Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 2;

    if (height > reqHeight || width > reqWidth) {
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }
    return inSampleSize;
}

	//设置音乐名称信息
	public void setMusicName(String name) {
		music_name.setText(name);
	}

	/**
	 * 
	 * 屏蔽掉Home键
	 * 
	 */
	@Override
	public void onAttachedToWindow() {
		//getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	/**
	 * 
	 * 屏蔽掉返回键
	 * 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private OnClickListener LockPassListener = new OnClickListener() {

		int pass = 0, digits = 0;
		String t;

		@Override
		public void onClick(View v) {
			if (digits == 0) {
				lockPassNum.setText("");
			}
			t = "";
			for (int i = 1; i <= digits; ++i) {
				t += " •";
			}
			lockPassNum.setText(t);
			switch (v.getId()) {
				case R.id.lock_pass_1 :
					lockPassNum.setText(t + " 1");
					pass *= 10;
					pass += 1;
					digits++;
					break;
				case R.id.lock_pass_2 :
					lockPassNum.setText(t + " 2");
					pass *= 10;
					pass += 2;
					digits++;
					break;
				case R.id.lock_pass_3 :
					lockPassNum.setText(t + " 3");
					pass *= 10;
					pass += 3;
					digits++;
					break;
				case R.id.lock_pass_4 :
					lockPassNum.setText(t + " 4");
					pass *= 10;
					pass += 4;
					digits++;
					break;
				case R.id.lock_pass_5 :
					lockPassNum.setText(t + " 5");
					pass *= 10;
					pass += 5;
					digits++;
					break;
				case R.id.lock_pass_6 :
					lockPassNum.setText(t + " 6");
					pass *= 10;
					pass += 6;
					digits++;
					break;
				case R.id.lock_pass_7 :
					lockPassNum.setText(t + " 7");
					pass *= 10;
					pass += 7;
					digits++;
					break;
				case R.id.lock_pass_8 :
					lockPassNum.setText(t + " 8");
					pass *= 10;
					pass += 8;
					digits++;
					break;
				case R.id.lock_pass_9 :
					lockPassNum.setText(t + " 9");
					pass *= 10;
					pass += 9;
					digits++;
					break;
			}
			if (digits >= 4) {
				if (pass * 10007 == SettingsProvider.getInt(LockScreenActivity.this, "lockp", -1)) {
					moveOut();
				} else {
					lockPassNum.setText(R.string.lock_pass_wrong);
				}
				digits = pass = 0;
			}
		}

	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LOCKED_SUCCESS:
					LockScreenActivity.this.finish();
					break;
				case VIEW_INVALIDATE:
					break;
				case UPDATA_TIME:
					hours.setText((String) msg.obj);
					break;
				case UPDATA_WEEK:
					week.setText((String) msg.obj);
					break;
				case UPDATA_YEARS:
					years.setText((String) msg.obj);
					break;
				default:
					break;
			}
		};
	};

	// 加载例如显示时间等的操作

	private void initData() {
		hours.setText(getTime());
		week.setText(getWeek());
		years.setText(getYear());
	}

	/**
	 * 启动服务
	 */
	private void startService() {
		intentService = new Intent(mContext, LockScreenService.class);
		startService(intentService);
	}

	/**
	 * @return 屏幕宽度
	 */
	private int getScreenWidth() {
		int width = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		return width;
	}

	private int verticalMinDistance = 20;
	private int horMinDistance = 300;
	private int minVelocity = 200;

	//显示解锁提示
	private void showUnlockHint() {
		Animation fadeIn =
			AnimationUtils.loadAnimation(this, R.anim.unlock_hint_fade_in);
		unlock_hint.startAnimation(fadeIn);
	}

	//关闭解锁提示
	private void hideUnlockHint() {
		Animation fadeOut =
			AnimationUtils.loadAnimation(this, R.anim.unlock_hint_fade_out);
		unlock_hint.startAnimation(fadeOut);
	}

	//放大壁纸
	private void showLargeBar() {
		ValueAnimator va = ValueAnimator.ofFloat(wallpaper.getScaleX(), 1.05f);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					wallpaper.setScaleX((Float) animator.getAnimatedValue());
					wallpaper.setScaleY((Float) animator.getAnimatedValue());
				}

			});
		va.setDuration(1000);
		va.setInterpolator(new AccelerateDecelerateInterpolator());
		va.start();
	}

	//缩小壁纸
	private void showSmallBar() {
		ValueAnimator va = ValueAnimator.ofFloat(wallpaper.getScaleX(), 1.0f);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					wallpaper.setScaleX((Float) animator.getAnimatedValue());
					wallpaper.setScaleY((Float) animator.getAnimatedValue());
				}

			});
		va.setDuration(1000);
		va.setInterpolator(new AccelerateDecelerateInterpolator());
		va.start();
	}

	Runnable fadeInUnlockHint = new Runnable() {

		@Override
		public void run() {
			showUnlockHint();
			UnlockHintHandler.postDelayed(fadeOutUnlockHint, 7000);
		}

	};

	Runnable fadeOutUnlockHint = new Runnable() {

		@Override
		public void run() {
			hideUnlockHint();
			UnlockHintHandler.postDelayed(fadeInUnlockHint, 2000);
		}

	};

	private int ty, temp;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//showUnlockHint();
			showLargeBar();
			ty = (int) event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//hideUnlockHint();
			showSmallBar();
			if (lockPassOpen) {
				if (mParent.getScrollY() < lockPassHeight / 2)
					moveOrig();
				else
					movePass();
			} else {
				if (mParent.getScrollY() >= horMinDistance) {
					moveOut();
				} else {
					moveOrig();
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			temp = ty - (int) event.getY();
			if (lockPassOpen && mParent.getScrollY() + temp >= lockPassHeight)
				mParent.scrollTo((int) mParent.getX(), lockPassHeight);
			else if (mParent.getScrollY() + temp >= 0)
				mParent.scrollBy(0, temp);
			else
				mParent.scrollBy(0, -mParent.getScrollY());
		}
		return true;
		//return mGestureDetector.onTouchEvent(event);
	}

	/////imooncat:以下完成上滑以及下滑动画

	//滑动到密码界面
	private void movePass() {
		mFinish = false;
		int moveLength = lockPassHeight - mParent.getScrollY();
		mScroller.startScroll(
			0,
			mParent.getScrollY(),
			0,
			moveLength,
			moveLength);
		layoutRoot.postInvalidate();
	}

	//上滑滑出
	private void moveOut() {
		mFinish = true;
		int moveLength = mParent.getHeight() - mParent.getScrollY();
		mScroller.startScroll(
			0,
			mParent.getScrollY(),
			0,
			moveLength,
			moveLength / 2);
		layoutRoot.postInvalidate();
	}

	//下滑还原
	private void moveOrig() {
		int moveLength = mParent.getScrollY();
		mScroller.startScroll(
			0,
			mParent.getScrollY(),
			0,
			-moveLength,
			moveLength / 2);
		layoutRoot.postInvalidate();
	}

	/////


	/////以下手势识别操作
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		System.out.println("_a v=" + velocityY);
		if (e1.getY() > e2.getY() && velocityY >= minVelocity) {
			//moveOut();
			return true;
		}

		return false;
	}
	/////手势识别结束

	// 获取当前时间
	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String time = format.format(new Date());
		return time;

	}

	// 获取当前日期
	public String getYear() {
		Calendar calendar = Calendar.getInstance();
		String year = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"// 从0计算
			+ calendar.get(Calendar.DAY_OF_MONTH);
		return year;

	}

	// 获取当前week
	public String getWeek() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		String today = null;
		if (day == 2) {
			today = "星期一";
		} else if (day == 3) {
			today = "星期二";
		} else if (day == 4) {
			today = "星期三";
		} else if (day == 5) {
			today = "星期四";
		} else if (day == 6) {
			today = "星期五";
		} else if (day == 7) {
			today = "星期六";
		} else if (day == 1) {
			today = "星期日";
		}
		return today;
	}

	//音乐前一首，暂停，后一首
	void onScreenSkipLeft(View v) {
		Intent i = new Intent("com.android.music.musicservicecommand.previous");
		sendBroadcast(i);
	}

	void onScreenPause(View v) {
		Intent i = new Intent("com.android.music.musicservicecommand.pause");
		sendBroadcast(i);
	}

	void onScreenSkipRight(View v) {
		Intent i = new Intent("com.android.music.musicservicecommand.next");
		sendBroadcast(i);
	}

	@Override
	public void finish() {
		if (mFinish) {
			super.finish();
		}
	}

	private void debug(String s) {
		Log.d("_LiveFace", s);
		Toast.makeText(this, s, 100).show();
	}

	OnClickListener ForgetListener = new OnClickListener() {

		@Override
		public void onClick(View p1) {
			{
				View dialogView = getLayoutInflater().inflate(R.layout.dialog_forget_lock_pass, null);
				final EditText et = (EditText) dialogView.findViewById(R.id.dialog_forget_complex);
				new AlertDialog.Builder(LockScreenActivity.this)
					.setTitle(R.string.dialog_forget_title)
					.setView(dialogView)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							if (("WAY-WAYH" + et.getText().toString() + "ily").equals(SettingsProvider.getString(LockScreenActivity.this, "lockc", ""))) {
								SettingsProvider.putBoolean(LockScreenActivity.this, "settings_lock_pass_open", false);
								Toast.makeText(LockScreenActivity.this, R.string.dialog_forget_reset_success, 1000).show();
								moveOut();
								Intent i = new Intent(LockScreenActivity.this, LockPassSettingActivity.class);
								startActivity(i);
							} else {
								Toast.makeText(LockScreenActivity.this, R.string.dialog_forget_complex_wrong, 1000).show();
							}
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1, int p2) {
							//close
						}
					})
					.show();
			}
		}

	};

	public void onForget(View v) {
		View dialogView = getLayoutInflater().inflate(R.layout.dialog_forget_lock_pass, null);
		final EditText et = (EditText) dialogView.findViewById(R.id.dialog_forget_complex);
		new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_forget_title)
			.setView(dialogView)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					if ("WAY-WAYH" + et.getText().toString() + "ily" == SettingsProvider.getString(LockScreenActivity.this, "lockc", "")) {
						SettingsProvider.putBoolean(LockScreenActivity.this, "settings_lock_pass_open", false);
						Toast.makeText(LockScreenActivity.this, R.string.dialog_forget_reset_success, 1000).show();
						moveOut();
						Intent i = new Intent(LockScreenActivity.this, LockPassSettingActivity.class);
						startActivity(i);
					} else {
						Toast.makeText(LockScreenActivity.this, R.string.dialog_forget_complex_wrong, 1000).show();
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					//close
				}
			})
			.show();
	}

	public class HideLockOnPhoneListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING :
				case TelephonyManager.CALL_STATE_OFFHOOK :
					//Debugger.p(LockScreenActivity.this, "phoning");
					mOnPhone = true;
					moveTaskToBack(true);
					break;
				case TelephonyManager.CALL_STATE_IDLE :
					//Debugger.p(LockScreenActivity.this, "idle");
					mOnPhone = false;
					Intent intent = new Intent("cc.flydev.launcher.lockscreen.pause");
					sendBroadcast(intent);
					break;
				default:
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	};

	@Override
	protected void onStop() {
		TimesDebugger.addTimes(this, "OtherRun");

		super.onStop();
	}

	@Override
	protected void onPause() {
		TimesDebugger.addTimes(this, "LockPause");


		super.onPause();
		if (mNeedRestart) {
			if (SettingsProvider.getBoolean(this, "screen_on", true) && !mOnPhone) {
				//Debugger.p(this, "onPause " + SettingsProvider.getBoolean(this, "screen_on", true));
				mNeedRestart = false;
				Intent i = new Intent("cc.flydev.launcher.lockscreen.pause");
				sendBroadcast(i);
				mCheckLockHandler.postDelayed(mCheckRunnable, 500);
			}
		} else {
			mNeedRestart = true;
		}
	}

	private Runnable mCheckRunnable = new Runnable() {

		@Override
		public void run() {
			mNeedRestart = false;
			Intent intent = new Intent("cc.flydev.launcher.lockscreen.pause");
			sendBroadcast(intent);
		}

	};


	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
		if(mOnPhone) {
			moveTaskToBack(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMusicInfoReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		//Debugger.p(this, "onNewIntent");
		super.onNewIntent(intent);
	}



}
