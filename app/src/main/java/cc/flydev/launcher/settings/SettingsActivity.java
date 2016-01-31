package cc.flydev.launcher.settings;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cc.flydev.face.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.listview_noline);
        setContentView(R.layout.activity_settting);
//		 Initialize the preferences
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingPreferencesFragment())
                .commit();


        Drawable wrappedDrawable = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_share));
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(0xffffffff));
        FloatingActionButton fab_share = (FloatingActionButton) findViewById(R.id.fab_share);
        fab_share.setImageDrawable(wrappedDrawable);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I recommend an excited APP: LiveFACE");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        // TODO: Implement this method
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO: Implement this method
        super.onResume();
    }

    class SettingPreferencesFragment extends PreferenceFragment implements OnPreferenceClickListener {

        private Preference mRestart;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            mRestart = findPreference(SettingsProvider.KEY_SETTINGS_RESTART);
            mRestart.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mRestart) {
                // I am Launcher, so I will restart after killing
                System.exit(0);
                return true;
            } else {
                return false;
            }
        }
    }

}
