package cc.flydev.launcher;

import java.util.ArrayList;
import java.util.List;

import com.android.gallery3d.exif.IfdId;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import cc.flydev.face.R;

public class GuideActivity extends Activity implements OnPageChangeListener{
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private ImageView[] dots;
	private int[] ids = { R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4 , R.id.iv5 };
	private Button start_btn;
	private Button lock_intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.guide);
		initViews();
		initDots();
	}
	
	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.one, null));
		views.add(inflater.inflate(R.layout.two, null));
		views.add(inflater.inflate(R.layout.three, null));
		views.add(inflater.inflate(R.layout.four, null));
		views.add(inflater.inflate(R.layout.five, null));
		vpAdapter = new ViewPagerAdapter(views,this);
		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);
		vp.setPageTransformer(true,new ZoomOutPageTransformer());
		start_btn = (Button) views.get(4).findViewById(R.id.start_btn);
		lock_intent = (Button) views.get(4).findViewById(R.id.lock_intent);
		lock_intent.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT>=19) {
					Toast.makeText(getApplicationContext(), R.string.notice1, Toast.LENGTH_SHORT).show();
					
			}else {
					 Intent intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);  
		               startActivity(intent);
		               
				}
				
			}
		});
		start_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(GuideActivity.this, Launcher.class);
				startActivity(i);
				finish();
			}
		});
		vp.setOnPageChangeListener(this);
	}
	
	private void initDots() {
		dots = new ImageView[views.size()];
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView)findViewById(ids[i]);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < ids.length; i++) {
			if (arg0 == i) {
				dots[i].setImageResource(R.drawable.login_point_selected);
			} else {
				dots[i].setImageResource(R.drawable.login_point);
			}
		}
	}
	
	private class ViewPagerAdapter extends PagerAdapter {
		private List<View> views;
		private Context context;
	
		public ViewPagerAdapter(List<View> views, Context context) {
			this.views = views;
			this.context = context;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}
		@Override
		public int getCount() {
			return views.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

	}
 
}
