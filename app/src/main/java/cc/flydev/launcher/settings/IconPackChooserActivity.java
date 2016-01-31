package cc.flydev.launcher.settings;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.flydev.face.R;
import cc.flydev.launcher.LauncherAppState;
import cc.flydev.launcher.LauncherApplication;

public class IconPackChooserActivity extends Activity implements OnItemClickListener
{
    private IconPackAdapter mAdapter;
    
    private ListView mList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_iconpack_chooser);
//        LauncherApplication.TintStatuBarNavigationBar(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(android.R.color.holo_blue_light));
		}else{
			getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(R.drawable.BackBar));
		}
        // Initialize the list
        mList = (ListView) findViewById(R.id.iconpack_list);
		IconPackHelper iconPack = new IconPackHelper(this);
        mAdapter = new IconPackAdapter(this, iconPack.getSupportedPackages(this));
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_theme_chooser, menu);
		return true;
	}
	
	

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		switch(item.getItemId()) {
			case android.R.id.home :
				finish();
				break;
			case R.id.iconpack_downloadmore :
				Uri down = Uri.parse("http://www.coolapk.com/apk/tag/%E5%9B%BE%E6%A0%87/");
				Intent i = new Intent();
				i.setAction("android.intent.action.VIEW");
				i.setData(down);
				startActivity(i);
				break;
			default :
				return super.onOptionsItemSelected(item);
		}
		return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mAdapter.getCount()) {
            return;
        } else {
            SettingsProvider.putString(this, SettingsProvider.KEY_INTERFACE_ICONPACK, mAdapter.getItem(position).toString());
        }
        
        // Reload launcher after changing icon pack
        LauncherAppState.getInstance().getIconCache().flush();
        LauncherAppState.getInstance().getModel().forceReload();
        
        finish();
    }
    
    private class IconPackAdapter extends BaseAdapter
    {
        private ArrayList<IconPackInfo> mPackages;
        private LayoutInflater mInflater;
        
        public IconPackAdapter(Context context, Map<String, IconPackInfo> pkgs) {
            mPackages = new ArrayList<IconPackInfo>(pkgs.values());
            //mPackages.add(0, new IconPackInfo(context.getResources().getString(R.string.application_name), context.getResources().getDrawable(R.mipmap.ic_launcher_home), context.getResources().getDrawable(R.drawable.preview),context.getResources().getString(R.string.app_package_name)));
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        @Override
        public int getCount() {
            return mPackages.size();
        }

        @Override
        public Object getItem(int position) {
            return mPackages.get(position).packageName;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (position >= mPackages.size()) return convertView;
            IconPackInfo info = mPackages.get(position);
            View ret = mInflater.inflate(R.layout.interface_iconpack_chooser_item_preview, null);
            ImageView icon = (ImageView) ret.findViewById(R.id.iconpack_preview_icon);
            icon.setImageDrawable(info.preview);
            TextView label = (TextView) ret.findViewById(R.id.iconpack_preview_name);
            label.setText(info.label);
            return ret;
        }

        
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
