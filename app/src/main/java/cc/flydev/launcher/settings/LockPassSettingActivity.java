package cc.flydev.launcher.settings;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.flydev.face.R;
import cc.flydev.launcher.LauncherApplication;

public class LockPassSettingActivity extends AppCompatActivity {

	private int step = 1, step1pass;
	private LinearLayout mLockPass, mStep3;
	private TextView lockPassNum, mLockPassHint;
	private Button mClearButton;
	private ImageView lockPass1,
			lockPass2,
			lockPass3,
			lockPass4,
			lockPass5,
			lockPass6,
			lockPass7,
			lockPass8,
			lockPass9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		LauncherApplication.TintStatuBarNavigationBar(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		setContentView(R.layout.activity_lock_pass_setting);

		mLockPass = (LinearLayout) findViewById(R.id.setting_lock_screen_pass);
		mStep3 = (LinearLayout) findViewById(R.id.setting_lock_pass_step3);
		mLockPassHint = (TextView) findViewById(R.id.setting_lock_pass_hint);
		lockPassNum = (TextView) findViewById(R.id.setting_lock_pass_num);
		mClearButton = (Button) findViewById(R.id.setting_clear_pass);

		mStep3.setVisibility(View.GONE);

		if (SettingsProvider.getBoolean(this, "settings_lock_pass_open", false)) {
			step = 0;
			mLockPassHint.setText(R.string.settings_lock_pass_verify);
			mClearButton.setVisibility(View.GONE);
		} else {
			step = 1;
			mLockPassHint.setText(R.string.settings_lock_pass_step1);
		}


		lockPass1 = (ImageView) findViewById(R.id.setting_lock_pass_1);
		lockPass2 = (ImageView) findViewById(R.id.setting_lock_pass_2);
		lockPass3 = (ImageView) findViewById(R.id.setting_lock_pass_3);
		lockPass4 = (ImageView) findViewById(R.id.setting_lock_pass_4);
		lockPass5 = (ImageView) findViewById(R.id.setting_lock_pass_5);
		lockPass6 = (ImageView) findViewById(R.id.setting_lock_pass_6);
		lockPass7 = (ImageView) findViewById(R.id.setting_lock_pass_7);
		lockPass8 = (ImageView) findViewById(R.id.setting_lock_pass_8);
		lockPass9 = (ImageView) findViewById(R.id.setting_lock_pass_9);

		lockPass1.setOnClickListener(LockPassListener);
		lockPass2.setOnClickListener(LockPassListener);
		lockPass3.setOnClickListener(LockPassListener);
		lockPass4.setOnClickListener(LockPassListener);
		lockPass5.setOnClickListener(LockPassListener);
		lockPass6.setOnClickListener(LockPassListener);
		lockPass7.setOnClickListener(LockPassListener);
		lockPass8.setOnClickListener(LockPassListener);
		lockPass9.setOnClickListener(LockPassListener);


	}

	OnClickListener LockPassListener = new OnClickListener() {

		int pass = 0, digits = 0;
		String t;

		@Override
		public void onClick(View v) {
			if (digits == 0) {
				lockPassNum.setText("");
			}
			t = "";
			for (int i = 1; i <= digits; ++i) {
				t += " •";
			}
			lockPassNum.setText(t);
			switch (v.getId()) {
				case R.id.setting_lock_pass_1:
					lockPassNum.setText(t + " 1");
					pass *= 10;
					pass += 1;
					digits++;
					break;
				case R.id.setting_lock_pass_2:
					lockPassNum.setText(t + " 2");
					pass *= 10;
					pass += 2;
					digits++;
					break;
				case R.id.setting_lock_pass_3:
					lockPassNum.setText(t + " 3");
					pass *= 10;
					pass += 3;
					digits++;
					break;
				case R.id.setting_lock_pass_4:
					lockPassNum.setText(t + " 4");
					pass *= 10;
					pass += 4;
					digits++;
					break;
				case R.id.setting_lock_pass_5:
					lockPassNum.setText(t + " 5");
					pass *= 10;
					pass += 5;
					digits++;
					break;
				case R.id.setting_lock_pass_6:
					lockPassNum.setText(t + " 6");
					pass *= 10;
					pass += 6;
					digits++;
					break;
				case R.id.setting_lock_pass_7:
					lockPassNum.setText(lockPassNum.getText() + " 7");
					pass *= 10;
					pass += 7;
					digits++;
					break;
				case R.id.setting_lock_pass_8:
					lockPassNum.setText(t + " 8");
					pass *= 10;
					pass += 8;
					digits++;
					break;
				case R.id.setting_lock_pass_9:
					lockPassNum.setText(t + " 9");
					pass *= 10;
					pass += 9;
					digits++;
					break;
			}
			if (digits >= 4) {
				if (step == 0) {
					if (pass * 10007 == SettingsProvider.getInt(LockPassSettingActivity.this, "lockp", -1)) {
						step = 1;
						mLockPassHint.setText(R.string.settings_lock_pass_step1);
						mClearButton.setVisibility(View.VISIBLE);
					} else {
						Toast.makeText(LockPassSettingActivity.this, R.string.lock_pass_wrong, 1000).show();
					}

				} else if (step == 1) {
					step1pass = pass;
					step = 2;
					mLockPassHint.setText(R.string.settings_lock_pass_step2);

					//mClearButton.setVisibility(View.GONE);
				} else if (step == 2) {
					if (pass == step1pass) {
						mLockPass.setVisibility(View.GONE);
						mStep3.setVisibility(View.VISIBLE);
						step = 3;
					} else {
						Toast.makeText(LockPassSettingActivity.this, R.string.settings_lock_pass_mismatch, 1000).show();
						step = 1;
						mLockPassHint.setText(R.string.settings_lock_pass_step1);
						mClearButton.setVisibility(View.VISIBLE);
					}
				}
				pass = digits = 0;
				lockPassNum.setText("");
			}
		}

	};

	public void onClearPass(View v) {
		SettingsProvider.putBoolean(this, "settings_lock_pass_open", false);
		//Toast.makeText(this, R.string.message_needs_restart, 1000).show();
		finish();
	}

	public void onOkButton(View v) {
		EditText et = (EditText) findViewById(R.id.setting_lock_pass_complex);
		SettingsProvider.putBoolean(this, "settings_lock_pass_open", true);
		SettingsProvider.putInt(this, "lockp", step1pass * 10007);
		SettingsProvider.putString(this, "lockc", "WAY-WAYH" + et.getText().toString() + "ily");
		//Toast.makeText(this, R.string.message_needs_restart, 1000).show();
		finish();
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

}
