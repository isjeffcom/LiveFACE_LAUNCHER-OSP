package cc.flydev.launcher.debug;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Debugger {
	
	static boolean isDebuging = true;
	static String TAG = "aLiveFACE";
	
	public static void p(Context context, String s) {
		Log.d(TAG, s);
		if(isDebuging) {
			Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
		}
	}
	
}
