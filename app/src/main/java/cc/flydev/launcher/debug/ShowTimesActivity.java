package cc.flydev.launcher.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cc.flydev.face.R;

public class ShowTimesActivity extends Activity {

	private EditText mShowTimes;
	private final String
		lock_screen = "LockScreen",
		home_click = "HomeClick",
		other_run = "OtherRun",
		lock_restart = "LockRestart",
		lock_pause = "LockPause";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_times);
		
		mShowTimes = (EditText) findViewById(R.id.et_show_times);
		
		String s = "";
		s += lock_screen + " : " + TimesDebugger.getTimes(this, lock_screen) + "\n";
		s += home_click + " : " + TimesDebugger.getTimes(this, home_click) + "\n";
		s += other_run + " : " + TimesDebugger.getTimes(this, other_run) + "\n";
		s += lock_restart + " : " + TimesDebugger.getTimes(this, lock_restart) + "\n";
		s += lock_pause + " : " + TimesDebugger.getTimes(this, lock_pause) + "\n";
		
		mShowTimes.setText(s);
	}
	
	
	public void onStClearButton(View v) {
		TimesDebugger.clearTimes(this, lock_screen);
		TimesDebugger.clearTimes(this, home_click);
		TimesDebugger.clearTimes(this, other_run);
		TimesDebugger.clearTimes(this, lock_restart);
		TimesDebugger.clearTimes(this, lock_pause);
	}
	
	
}
