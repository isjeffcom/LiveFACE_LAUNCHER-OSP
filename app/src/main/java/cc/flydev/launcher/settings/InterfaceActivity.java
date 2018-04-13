package cc.flydev.launcher.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.widget.Toast;

import cc.flydev.face.R;
import cc.flydev.launcher.AppsCustomizePagedView;

public class InterfaceActivity extends AbstractBarPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new InterfaceFragment())
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

    class InterfaceFragment extends PreferenceFragment implements OnPreferenceChangeListener {
        private static final String KEY_ICONPACK_CHOOSER = "interface_iconpack_chooser";
        private static final String KEY_ICONPACK_CURRENT = "interface_iconpack_current";
        private static final String ICON_SIZE_VALUE = "null";

        private Preference mCurrent;
        private Preference mChooser;
        private Preference mHideIcon;
        private EditTextPreference mGlobalFontSize;
        private ListPreference mHomescreenIconSize;
        private CheckBoxPreference mEnableDrawer;
        private ListPreference mHotseatIconSize;
        private CheckBoxPreference mAutoRotate;
        private String mSizeValue;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_interface);
            mCurrent = findPreference(KEY_ICONPACK_CURRENT);
            mChooser = findPreference(KEY_ICONPACK_CHOOSER);
            mGlobalFontSize = (EditTextPreference) findPreference(SettingsProvider.KEY_INTERFACE_GLOBAL_FONT_SIZE);
            mHomescreenIconSize = (ListPreference) findPreference(SettingsProvider.KEY_INTERFACE_HOMESCREEN_DRAWER_ICON_SIZE);
            mEnableDrawer = (CheckBoxPreference) findPreference(SettingsProvider.KEY_INTERFACE_HOMESCREEN_DRAWER_ENABLE_DRAWER);
            mHotseatIconSize = (ListPreference) findPreference(SettingsProvider.KEY_INTERFACE_HOTSEAT_ICON_SIZE);
            mSizeValue = ICON_SIZE_VALUE;



            mHideIcon = findPreference("interface_hide_shortcut");
            if (!AppsCustomizePagedView.DISABLE_ALL_APPS) {
                getPreferenceScreen().removePreference(mHideIcon);
            }
            String iconPack = SettingsProvider.getString(getActivity(), SettingsProvider.KEY_INTERFACE_ICONPACK, getResources().getString(R.string.interface_iconpack_current_summary_default));
            try {
                mCurrent.setSummary(getPackageManager().getPackageInfo(iconPack, 0).applicationInfo.loadLabel(getPackageManager()));
            } catch (Exception e) {
            }

            /*int globalFontSize = SettingsProvider.getInt(getActivity(), SettingsProvider.KEY_INTERFACE_GLOBAL_FONT_SIZE, 13);
            mGlobalFontSize.setSummary(globalFontSize + " sp");
            mGlobalFontSize.setText(String.valueOf(globalFontSize));*/

            int homescreenIconSize = SettingsProvider.getInt(getActivity(), SettingsProvider.KEY_INTERFACE_HOMESCREEN_DRAWER_ICON_SIZE, 48);
            switch (homescreenIconSize){
                case 48:
                    mSizeValue = getString(R.string.iconSizeSmall).toString();
                    break;

                case 52:
                    mSizeValue = getString(R.string.iconSizeMid).toString();;
                    break;

                case 58:
                    mSizeValue = getString(R.string.iconSizeLarge).toString();;
                    break;
            }
            mHomescreenIconSize.setSummary(mSizeValue);
            mHomescreenIconSize.setValue(String.valueOf(homescreenIconSize));

            int hotseatIconSize = SettingsProvider.getInt(getActivity(), SettingsProvider.KEY_INTERFACE_HOTSEAT_ICON_SIZE, 48);
            //mHotseatIconSize.setSummary(hotseatIconSize + " dp");
            //mHotseatIconSize.setValue(String.valueOf(hotseatIconSize));

            mEnableDrawer.setChecked(SettingsProvider.getBoolean(getActivity(), SettingsProvider.KEY_INTERFACE_HOMESCREEN_DRAWER_ENABLE_DRAWER, false));

            //mGlobalFontSize.setOnPreferenceChangeListener(this);
            mHomescreenIconSize.setOnPreferenceChangeListener(this);
            mEnableDrawer.setOnPreferenceChangeListener(this);
            //mHotseatIconSize.setOnPreferenceChangeListener(this);
            mAutoRotate = (CheckBoxPreference) findPreference(SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE);

            // Initialize
            boolean checked = SettingsProvider.getBoolean(getActivity(),
                    SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE,
                    false);
            mAutoRotate.setChecked(checked);
            mAutoRotate.setOnPreferenceChangeListener(this);

        }

        @Override
        public void onResume() {
            super.onResume();
            if (mCurrent != null) {
                String iconPack = SettingsProvider.getString(getActivity(), SettingsProvider.KEY_INTERFACE_ICONPACK, getResources().getString(R.string.interface_iconpack_current_summary_default));
                try {
                    mCurrent.setSummary(getPackageManager().getPackageInfo(iconPack, 0).applicationInfo.loadLabel(getPackageManager()));
                } catch (Exception e) {
                    mCurrent.setSummary(iconPack);
                }
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean ret = false;
            boolean needsRestart = false;
            if (preference == mGlobalFontSize) {
                int size = newValue.equals("") ? 13 : Integer.parseInt((String) newValue);
                //mGlobalFontSize.setSummary(size + " sp");
                SettingsProvider.putInt(getActivity(), SettingsProvider.KEY_INTERFACE_GLOBAL_FONT_SIZE, size);
                ret = true;
                needsRestart = true;
            } else if (preference == mHomescreenIconSize) {
                int size = newValue.equals("") ? 48 : Integer.parseInt((String) newValue);
                SettingsProvider.putInt(getActivity(), SettingsProvider.KEY_INTERFACE_HOMESCREEN_DRAWER_ICON_SIZE, size);
                SettingsProvider.putInt(getActivity(), SettingsProvider.KEY_INTERFACE_HOTSEAT_ICON_SIZE, size);
                mHomescreenIconSize.setSummary(mSizeValue);
                ret = true;
                needsRestart = true;
            } else if (preference == mHotseatIconSize) {
                int size = newValue.equals("") ? 48 : Integer.parseInt((String) newValue);
                mHotseatIconSize.setSummary(size + " dp");
                SettingsProvider.putInt(getActivity(), SettingsProvider.KEY_INTERFACE_HOTSEAT_ICON_SIZE, size);
                ret = true;
                needsRestart = true;
            } else if (preference == mEnableDrawer) {
                boolean enable = (Boolean) newValue;
                mEnableDrawer.setChecked(enable);
                SettingsProvider.putBoolean(getActivity(),
                        SettingsProvider.KEY_INTERFACE_HOMESCREEN_DRAWER_ENABLE_DRAWER,
                        enable);
                ret = true;
                needsRestart = true;
            } else if (preference == mAutoRotate) {
                SettingsProvider.putBoolean(getActivity(),
                        SettingsProvider.KEY_EFFECTS_GLOBAL_AUTO_ROTATE,
                        (Boolean) newValue);
                ret = true;
            }

            if (ret) {
            }

            if (needsRestart) {
//            // Show the message
                Toast.makeText(getActivity(), R.string.message_needs_restart, 1000).show();
            }

            return ret;

        }

    }
}
