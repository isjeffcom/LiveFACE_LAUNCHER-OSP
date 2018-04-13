package cc.flydev.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.flydev.face.R;
import cc.flydev.launcher.HideShortcutActivity.HiedShortcutAdapter.ViewHolder;

public class HideShortcutActivity extends AppCompatActivity implements OnClickListener {
	private Context mContext;
	private TextView mTvCancel;
	private TextView mTvSave;
	private ProgressBar mPb;
	private ListView mListView;
	private HiedShortcutAdapter mAdapter;
	private List<ShortcutInfo> mData;
	private Map<Integer, ShortcutInfo> mHideData = new HashMap<Integer, ShortcutInfo>();
	private Map<Integer, ShortcutInfo> mRawData = new HashMap<Integer, ShortcutInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_hide_shortcut);
//		LauncherApplication.TintStatuBarNavigationBar(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mContext = this;
		initView();
	}

	private void initView() {
		mTvCancel = (TextView) findViewById(R.id.tv_hide_shortcut_cancel);
		mTvCancel.setOnClickListener(this);
		mTvSave = (TextView) findViewById(R.id.tv_hide_shortcut_save);
		mTvSave.setOnClickListener(this);
		mPb = (ProgressBar) findViewById(R.id.pb_hide_shortcut);
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				CheckBox db = ((ViewHolder) view.getTag()).cbIsHide;
				boolean isChecked = db.isChecked();
				if (isChecked) {
					((ViewHolder) view.getTag()).cbIsHide.setChecked(false);
					((ShortcutInfo) view.getTag(R.id.ll_item)).isHide = false;
					mHideData.remove(position);
				} else {
					((ViewHolder) view.getTag()).cbIsHide.setChecked(true);
					ShortcutInfo appInfo = ((ShortcutInfo) view.getTag(R.id.ll_item));
					appInfo.isHide = true;
					mHideData.put(position, appInfo);
				}
			}
		});
		mData = new ArrayList<ShortcutInfo>();
		mAdapter = new HiedShortcutAdapter(mData);
		mListView.setAdapter(mAdapter);
		initData();
	}

	private void initData() {
		new LoadAppInfo().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private class LoadAppInfo extends AsyncTask<Void, Void, List<ShortcutInfo>> {

		@Override
		protected List<ShortcutInfo> doInBackground(Void... params) {
			mHideData.clear();
			SharedPreferences hideShortCutsharedPrefs = LauncherAppState.getInstance().getHideShortCutSharedPrefs();
			String hide = hideShortCutsharedPrefs.getString("hide", "");
			List<String> packageNames = null;
			if (!TextUtils.isEmpty(hide)) {
				String[] packageNameFromSharePrefs = hide.split("u007a");
				packageNames = Arrays.asList(packageNameFromSharePrefs);
			}
			List<ShortcutInfo> appInfos = LauncherAppState.getInstance().getAppInfos();
			for (int index = 0; appInfos != null && index < appInfos.size(); index++) {
				ShortcutInfo appInfo = appInfos.get(index);
				String packageName = appInfo.intent.getComponent().getPackageName();
				if (packageNames != null && packageNames.contains(packageName)) {
					appInfo.isHide = true;
					mHideData.put(index, appInfo);
					mRawData.put(index, appInfo);
				}
			}
			return appInfos;
		}

		@Override
		protected void onPreExecute() {
			mPb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(List<ShortcutInfo> result) {
			mPb.setVisibility(View.GONE);
			mAdapter.setAppInfos(result);
			mAdapter.notifyDataSetChanged();
		}
	}

	class HiedShortcutAdapter extends BaseAdapter {
		private List<ShortcutInfo> appInfos;

		public void setAppInfos(List<ShortcutInfo> appInfos) {
			this.appInfos = appInfos;
		}

		public HiedShortcutAdapter(List<ShortcutInfo> appInfos) {
			this.appInfos = appInfos;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		@Override
		public int getCount() {
			return appInfos != null && appInfos.size() != 0 ? appInfos.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.setting_hide_shortcut_listview_item, parent, false);
				viewHolder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_hide_shortcut_icon);
				viewHolder.tvAppLabel = (TextView) convertView.findViewById(R.id.tv_hied_shortcut_title);
				viewHolder.cbIsHide = (CheckBox) convertView.findViewById(R.id.cb_hide_shortcut_ishide);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ShortcutInfo appInfo = appInfos.get(position);
			viewHolder.ivAppIcon.setImageBitmap(appInfo.getIcon(LauncherAppState.getInstance().getIconCache()));
			PackageManager pm = getPackageManager();
			String appName = (String) appInfo.intent.resolveActivityInfo(pm, 0).loadLabel(pm);
			viewHolder.tvAppLabel.setText(appName);
			viewHolder.cbIsHide.setChecked(appInfo.isHide);

			convertView.setTag(R.id.ll_item, appInfo);
			return convertView;
		}

		final class ViewHolder {
			ImageView ivAppIcon;
			TextView tvAppLabel;
			CheckBox cbIsHide;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_hide_shortcut_cancel:
				finish();
				break;
			case R.id.tv_hide_shortcut_save:
				StringBuffer sb = new StringBuffer();
				SharedPreferences hideShortCutsharedPrefs = LauncherAppState.getInstance().getHideShortCutSharedPrefs();
				ArrayList<Long> workspaceScreens = new ArrayList<Long>();
				final ContentResolver contentResolver = mContext.getContentResolver();
				final Uri screensUri = LauncherSettings.WorkspaceScreens.CONTENT_URI;
				final Cursor sc = contentResolver.query(screensUri, null, null, null, null);
				try {
					final int idIndex = sc.getColumnIndexOrThrow(
							LauncherSettings.WorkspaceScreens._ID);
					while (sc.moveToNext()) {
						try {
							long screenId = sc.getLong(idIndex);
							workspaceScreens.add(screenId);
						} catch (Exception e) {
						}
					}
				} finally {
					sc.close();
				}
				int startSearchPageIndex = workspaceScreens.isEmpty() ? 0 : 1;
				if (!mRawData.equals(mHideData)) {
					for (Map.Entry<Integer, ShortcutInfo> entry : mHideData.entrySet()) {
						if (entry.getValue().isHide) {
							String formatPackagerName = String.format("%su007a", entry.getValue().intent.getComponent().getPackageName());
							sb.append(formatPackagerName);
							LauncherModel.modifyItemInDatabase(mContext, entry.getValue(), LauncherSettings.Favorites.CONTAINER_DESKTOP,
									entry.getValue().screenId, -1, -1, 1, 1);
						}
					}
					Editor editor = hideShortCutsharedPrefs.edit();
					editor.putString("hide", sb.toString()).commit();
				}
				for (Map.Entry<Integer, ShortcutInfo> entry : mRawData.entrySet()) {
					if (mHideData.size() == 0) {
						Pair<Long, int[]> coords = LauncherModel.findNextAvailableIconSpace(mContext, entry.getValue().title.toString(),
								entry.getValue().intent, startSearchPageIndex, workspaceScreens);
						LauncherModel.modifyItemInDatabase(mContext, entry.getValue(),
								LauncherSettings.Favorites.CONTAINER_DESKTOP,
								coords.first, coords.second[0], coords.second[1], 1, 1);
					} else if (!mHideData.containsValue(entry.getValue())) {
						Pair<Long, int[]> coords = LauncherModel.findNextAvailableIconSpace(mContext, entry.getValue().title.toString(),
								entry.getValue().intent, startSearchPageIndex, workspaceScreens);
						LauncherModel.modifyItemInDatabase(mContext, entry.getValue(),
								LauncherSettings.Favorites.CONTAINER_DESKTOP,
								coords.first, coords.second[0], coords.second[1], 1, 1);
					}
				}
				if (!mRawData.equals(mHideData)) {
					LauncherAppState.getInstance().getModel().resetLoadedState(false, true);
					LauncherAppState.getInstance().getModel().startLoaderFromBackground();
				}
				finish();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
