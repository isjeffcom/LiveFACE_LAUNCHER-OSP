package cc.flydev.launcher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDBhelper extends SQLiteOpenHelper{
	private static int VERSION = 1;
	public static final String DB_NAME = "music.db";
	public MusicDBhelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table music (" +
				"_id integer primary key autoincrement not null,"+
				"name varchar(50),"+
				"path varchar(50),"+
				"artist varchar(50)"+
				")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}