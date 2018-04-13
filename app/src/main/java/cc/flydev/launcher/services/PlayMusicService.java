package cc.flydev.launcher.services;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import cc.flydev.launcher.entity.Music;
import cc.flydev.launcher.utils.DataUtils;

public class PlayMusicService extends Service{
	public static MediaPlayer player;
	public static TelephonyManager telephonyManager;
	/*音乐播放的状态：
	 * 0：初始状态
	 * 1：正在播放状态
	 *:2：停止状态
	 */
	public static final int INITIAL_STATE = 0;
	public static final int PLAYING_STATE = 1;
	public static final int PAUSE_STATE = 2;
	
	//当前播放音乐在音乐集合中的下标
	public static int musicIndex = 0;
	//记录播放的变量,默认是停止状态
	public static int playstate = INITIAL_STATE;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	//初始化
	@Override
	public void onCreate() {
		Log.i("Tag", "Service ---> onCreate");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if(player == null){
			player = new MediaPlayer();
			player.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					playNext();
				}
				
			});			
		}
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Tag", "Service --> onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	//重新播放
	public static void playMusic() {
			player.reset();
			List<Music> mlist = DataUtils.getmList();
			String path = mlist.get(musicIndex).getPath();
			try {
				player.setDataSource(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			player.prepareAsync();
			player.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					if(player != null){
						playstate = PLAYING_STATE;
						player.start();
					}
				}
			});
	}
	
	//暂停歌曲
	public static void pause() {
		if (playstate == PLAYING_STATE) {
			player.pause();
			playstate = PAUSE_STATE ;
		}
	}
	
	/**
	 * 继续播放
	 */
	public static void goOn() {
		if (playstate == PAUSE_STATE) {
			player.start();
			playstate = PLAYING_STATE;
		}
	}

	/**
	 * 停止播放
	 */
	public static void stop() {
		if (playstate != INITIAL_STATE) {
			player.stop();
			playstate = INITIAL_STATE;
		}
	}
	
	//播放下一首
	public static void playNext() {
		player.stop();
		musicIndex ++;
		playMusic();
	}
	
	@Override
	public void onDestroy() {
		Log.i("Tag","Service --->onDestroy");
		super.onDestroy();
	}
}
