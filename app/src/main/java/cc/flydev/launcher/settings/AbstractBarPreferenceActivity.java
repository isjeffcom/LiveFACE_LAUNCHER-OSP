package cc.flydev.launcher.settings;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;

import cc.flydev.face.R;
import cc.flydev.launcher.LauncherApplication;

public class AbstractBarPreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		LauncherApplication.TintStatuBarNavigationBar(this);
//		getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(android.R.color.holo_blue_light));
		}else{
			getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(R.drawable.BackBar));
		}*/
    }
}
