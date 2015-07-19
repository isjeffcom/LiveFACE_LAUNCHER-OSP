package cc.flydev.launcher.debug;
import android.content.Context;
import android.content.SharedPreferences;

public class TimesDebugger {
	
	public static boolean addTimes(Context context, String tag) {
		SharedPreferences msp = context.getSharedPreferences("times_debugger", Context.MODE_PRIVATE);
		int mNowValue = msp.getInt(tag, 0);
		return msp.edit().putInt(tag, mNowValue + 1).commit();
	}
	
	public static int getTimes(Context context, String tag) {
		SharedPreferences msp = context.getSharedPreferences("times_debugger", Context.MODE_PRIVATE);
		int mNowValue = msp.getInt(tag, 0);
		return mNowValue;
	}
	
	public static boolean clearTimes(Context context, String tag) {
		SharedPreferences msp = context.getSharedPreferences("times_debugger", Context.MODE_PRIVATE);
		return msp.edit().putInt(tag, 0).commit();
	}
	
	
	
}
