package cc.flydev.launcher.utils;

import java.util.ArrayList;
import java.util.List;

import cc.flydev.launcher.entity.Music;

public class DataUtils {
	private static List<Music> mList = new ArrayList<Music>();
	/**
	 * 获取歌曲的List集合
	 * @return List<Music>
	 */
	public static List<Music> getmList() {
		return mList;
	}
	/**
	 * 获取集合中的歌曲
	 * @param position
	 * @return
	 */
	public static Music getMusic(int position){
		return mList.get(position);
	}
}
