package cc.flydev.launcher.settings;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import cc.flydev.face.R;
import cc.flydev.launcher.LauncherApplication;

public class AbstractBarPreferenceActivity extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		LauncherApplication.TintStatuBarNavigationBar(this);
		getActionBar();
		/*getActionBar().setDisplayHomeAsUpEnabled(true);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(android.R.color.holo_blue_light));
		}else{
			getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(R.drawable.BackBar));
		}*/
	}
}
