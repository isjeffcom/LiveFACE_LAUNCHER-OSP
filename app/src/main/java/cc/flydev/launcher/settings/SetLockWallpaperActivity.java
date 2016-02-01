package cc.flydev.launcher.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cc.flydev.face.R;
import cc.flydev.launcher.LauncherApplication;


public class SetLockWallpaperActivity extends AppCompatActivity implements OnItemClickListener {

	GridView mGridView;
	LockWallpaperAdapter mAdapter;
	SharedPreferences msp;
	Resources r;
	private DisplayMetrics mDisplayMetrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_lock_wallpaper);
//		LauncherApplication.TintStatuBarNavigationBar(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

		r = getResources();

		msp = getSharedPreferences("LockScreen", Context.MODE_PRIVATE);

		ArrayList<LockWallpaperInfo> mInfo = new ArrayList<LockWallpaperInfo>();
		String prefix = r.getString(R.string.lock_wallpaper_prefix);
		int i = 1, resId;
		String resName = prefix + i, s = "wallpaper_" + i;

		while ((resId = r.getIdentifier(s, "drawable", this.getApplicationInfo().packageName)) > 0) {
			LockWallpaperInfo temp = new LockWallpaperInfo(resName, resId);
			mInfo.add(temp);
			++i;
			resName = prefix + i;
			s = "wallpaper_" + i;
		}

		mAdapter = new LockWallpaperAdapter(this, mInfo);
		mGridView = (GridView) findViewById(R.id.wallpaper_gridview);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);

		View v = findViewById(R.id.pick_wallpaper);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(v.getLayoutParams());
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		v.setLayoutParams(layoutParams);
		RelativeLayout.LayoutParams layoutParamsGrid = new RelativeLayout.LayoutParams(mGridView.getLayoutParams());
		layoutParamsGrid.addRule(RelativeLayout.ABOVE, R.id.pick_wallpaper);
		mGridView.setLayoutParams(layoutParamsGrid);
		Button btn = (Button) v.findViewById(R.id.lock_wallpaper_more_button);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						"image/*");
				startActivityForResult(intent, 0);
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			default:
				return false;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int resId = (Integer) mAdapter.getItem(position);
		if (resId != 0) {
			msp.edit()
					.putBoolean("isDrawable", true)
					.putInt("wallId", resId)
					.commit();
			Toast.makeText(this, r.getString(R.string.set_lock_wallpaper_success), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (data != null)
				startWallpaperZooom(data.getData());
		} else if (requestCode == 1) {
			if (data != null) {
				Uri uri = data.getData();

				msp.edit()
						.putBoolean("isDrawable", false)
						.putString("wallPath", getAbsoluteImagePath(uri))
						.commit();
				Toast.makeText(this, r.getString(R.string.set_lock_wallpaper_success), Toast.LENGTH_LONG).show();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	protected String getAbsoluteImagePath(Uri uri) {
		// can post image  
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery(uri,
				proj,// Which columns to return  
				null,// WHERE clause; which rows to return (all rows)  
				null,// WHERE clause selection arguments (none)  
				null);// Order-by clause (ascending by name)  

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	private void startWallpaperZooom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");

		intent.putExtra("crop", "true");

		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 3);

		intent.putExtra("outputX", 1080);
		intent.putExtra("outputY", 1701);

		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);

		intent.putExtra("return-data", false);

		startActivityForResult(intent, 1);
	}

	private class LockWallpaperInfo {
		public int wallId;

		public LockWallpaperInfo(String mName, int id) {
			wallId = id;
		}
	}

	private class LockWallpaperAdapter extends BaseAdapter {

		ArrayList<LockWallpaperInfo> mInfo;
		private Context mContext;

		public LockWallpaperAdapter(Context context, ArrayList<LockWallpaperInfo> mInfoList) {
			mInfo = mInfoList;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mInfo.size();
		}

		@Override
		public Object getItem(int position) {
			return mInfo.get(position).wallId;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LockWallpaperInfo info = mInfo.get(position);
			ImageView imageView = null;

			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(180 * mDisplayMetrics.densityDpi / 160, 180 * mDisplayMetrics.densityDpi / 160));
				int pading = 3 * mDisplayMetrics.densityDpi / 160;
				imageView.setPadding(pading, pading, pading, pading);
			} else {
				imageView = (ImageView) convertView;
			}
			new WallpaperAsyncTask(imageView).execute(info.wallId);
			return imageView;
		}

	}

	public static int calculateInSampleSize(
			Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	class WallpaperAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
		private ImageView iv;

		public WallpaperAsyncTask(ImageView iv) {
			this.iv = iv;
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			int resid = params[0];
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(), resid, opts);
			opts.inSampleSize = calculateInSampleSize(opts, 180 * mDisplayMetrics.densityDpi / 160, 180 * mDisplayMetrics.densityDpi / 160);
			opts.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(getResources(), resid, opts);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
		}

	}

}
