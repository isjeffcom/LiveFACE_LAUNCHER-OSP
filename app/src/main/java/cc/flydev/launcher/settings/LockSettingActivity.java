package cc.flydev.launcher.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cc.flydev.face.R;
import cc.flydev.launcher.LauncherAppState;
import cc.flydev.launcher.LauncherApplication;

public class LockSettingActivity extends AbstractBarPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new LockSettingFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
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

    class LockSettingFragment extends PreferenceFragment implements OnPreferenceClickListener, OnPreferenceChangeListener {

        private Preference mLockHint;
        private CheckBoxPreference mLock, mLockPass;

        private final String lock_open = "settings_lock_open";
        private final String lock_pass_open = "settings_lock_pass_open";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            addPreferencesFromResource(R.xml.settings_lock);
            mLockHint = findPreference("settings_lock_hint");
            mLockHint.setOnPreferenceClickListener(this);

            mLock = (CheckBoxPreference) findPreference(lock_open);
            mLock.setChecked(SettingsProvider.getBoolean(getActivity(), lock_open, false));
            mLock.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mLockHint) {
                final View dialogView = getLayoutInflater().inflate(R.layout.settings_lock_hint_dialog, null);
                final EditText hint = (EditText) dialogView.findViewById(R.id.lock_hint_edit);

                final SharedPreferences msp = getSharedPreferences("LockScreen", Context.MODE_PRIVATE);

                hint.setText(msp.getString("lock_hint", "我是自定义文字"));

                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.lock_hint_settings_dialog_title)
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                msp.edit().putString("lock_hint", hint.getText().toString()).commit();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                //close
                            }
                        })
                        .show();

                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean ret = false, needsRestart = false;

            if (preference == mLock) {
                if (SettingsProvider.getBoolean(getActivity(), lock_pass_open, false)) {
                    Toast.makeText(getActivity(), R.string.settings_need_close_lock_pass, 1000).show();
                    ret = false;
                } else {
                    SettingsProvider.putBoolean(getActivity(), lock_open, (Boolean) newValue);
                    ret = true;
                    needsRestart = true;
                }
            } else {
                ret = false;
            }

            if (needsRestart) {
//			Toast.makeText(this, R.string.message_needs_restart, 1000).show();
                if (SettingsProvider.getBoolean(getActivity(), "settings_lock_open", true)) {
                    Boolean isRunning = LauncherApplication.getInstance().isServiceRunning("cc.flydev.launcher.lockscreen.LockScreenService");
                    if (isRunning) {
                        stopService(LauncherAppState.getInstance().getLockScreenService());
                    }
                    startService(LauncherAppState.getInstance().getLockScreenService());
                } else {
                    stopService(LauncherAppState.getInstance().getLockScreenService());
                }
            }

            return ret;
        }
    }

}
