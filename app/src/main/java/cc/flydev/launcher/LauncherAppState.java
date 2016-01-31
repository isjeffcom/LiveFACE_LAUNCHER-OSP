/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.flydev.launcher;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import cc.flydev.face.R;

public class LauncherAppState {
	private static final String TAG = "LauncherAppState";
    private static final String SHARED_PREFERENCES_KEY = "cc.flydev.launcher.prefs";

    private LauncherModel mModel;
    private IconCache mIconCache;
    private AppFilter mAppFilter;
    private WidgetPreviewLoader.CacheDb mWidgetPreviewCacheDb;
    private boolean mIsScreenLarge;
    private float mScreenDensity;
    private int mLongPressTimeout = 300;
    
    
    private static WeakReference<LauncherProvider> sLauncherProvider;
    private static Context sContext;

    private static LauncherAppState INSTANCE;

    private DynamicGrid mDynamicGrid;
    private List<ShortcutInfo> mAppInfos;
    private SharedPreferences mHideShortCutSharedPrefs;
    private static final String HIDESHORTCUT_SHAREDPREFS_NAME = "hideshortcut";
    private boolean mIsSaveShortCut = true;
    private Intent mLockScreenIntent;
    private Intent mUpMenuIntent;
    
    private boolean mIsInitGrid;
    
    public boolean isInitGrid() {
    	return mIsInitGrid;
    }
    
    public void setIsInitGrid(boolean mIsInitGrid) {
    	this.mIsInitGrid = mIsInitGrid;
    }
    
    
    public boolean isSaveshortcut() {
		return mIsSaveShortCut;
	}
    
    public void setIsSaveShorcut(boolean isSaveShortCut){
    	mIsSaveShortCut = isSaveShortCut;
    }
    
    public List<ShortcutInfo> getAppInfos() {
		return mAppInfos;
	}

	public void setAppInfos(List<ShortcutInfo> appInfos) {
		this.mAppInfos = appInfos;
	}

	public void setLockScreenService(Intent lockScreenIntent){
		mLockScreenIntent = lockScreenIntent;
	}
	
	public Intent getLockScreenService(){
		return mLockScreenIntent;
	}
	
	public void setUpMenuService(Intent upMenuIntent){
		mUpMenuIntent = upMenuIntent;
	}
	
	public Intent getUpMenuService(){
		return mUpMenuIntent;
	}
	public static LauncherAppState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LauncherAppState();
        }
        return INSTANCE;
    }

	public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return sContext;
    }

    public static void setApplicationContext(Context context) {
        if (sContext != null) {
            Log.w(Launcher.TAG, "setApplicationContext called twice! old=" + sContext + " new=" + context);
        }
        sContext = context.getApplicationContext();
    }

    private LauncherAppState() {
        if (sContext == null) {
            throw new IllegalStateException("LauncherAppState inited before app context set");
        }

        Log.v(Launcher.TAG, "LauncherAppState inited");

        if (sContext.getResources().getBoolean(R.bool.debug_memory_enabled)) {
            MemoryTracker.startTrackingMe(sContext, "L");
        }

        // set sIsScreenXLarge and mScreenDensity *before* creating icon cache
        mIsScreenLarge = isScreenLarge(sContext.getResources());
        mScreenDensity = sContext.getResources().getDisplayMetrics().density;

        mWidgetPreviewCacheDb = new WidgetPreviewLoader.CacheDb(sContext);
        mIconCache = new IconCache(sContext);

        mAppFilter = AppFilter.loadByName(sContext.getString(R.string.app_filter_class));
        mModel = new LauncherModel(this, mIconCache, mAppFilter);

        // Register intent receivers
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);
        sContext.registerReceiver(mModel, filter);

        // Register for changes to the favorites
        ContentResolver resolver = sContext.getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true,
                mFavoritesObserver);
        
        mHideShortCutSharedPrefs = sContext.getSharedPreferences(HIDESHORTCUT_SHAREDPREFS_NAME, Context.MODE_PRIVATE);
        if(mHideShortCutSharedPrefs.getBoolean("is_new_install", true)){
        	Editor editor = mHideShortCutSharedPrefs.edit();
        	editor.putBoolean("is_new_install", false);
        	editor.putString("hide", "cc.flydev.faceu007a");
        	editor.commit();        	
        }
    }
    
    public SharedPreferences getHideShortCutSharedPrefs() {
		return mHideShortCutSharedPrefs;
	}

	/**
     * Call from Application.onTerminate(), which is not guaranteed to ever be called.
     */
    public void onTerminate() {
        sContext.unregisterReceiver(mModel);

        ContentResolver resolver = sContext.getContentResolver();
        resolver.unregisterContentObserver(mFavoritesObserver);
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // If the database has ever changed, then we really need to force a reload of the
            // workspace on the next load
            mModel.resetLoadedState(false, true);
            mModel.startLoaderFromBackground();
        }
    };

    LauncherModel setLauncher(Launcher launcher) {
        if (mModel == null) {
            throw new IllegalStateException("setLauncher() called before init()");
        }
        mModel.initialize(launcher);
        return mModel;
    }

    public IconCache getIconCache() {
        return mIconCache;
    }

    public LauncherModel getModel() {
        return mModel;
    }

    boolean shouldShowAppOrWidgetProvider(ComponentName componentName) {
        return mAppFilter == null || mAppFilter.shouldShowApp(componentName);
    }

    WidgetPreviewLoader.CacheDb getWidgetPreviewCacheDb() {
        return mWidgetPreviewCacheDb;
    }

    static void setLauncherProvider(LauncherProvider provider) {
        sLauncherProvider = new WeakReference<LauncherProvider>(provider);
    }

    static LauncherProvider getLauncherProvider() {
        return sLauncherProvider.get();
    }

    public static String getSharedPreferencesKey() {
        return SHARED_PREFERENCES_KEY;
    }

    DeviceProfile initDynamicGrid(Context context, int minWidth, int minHeight,
                                  int width, int height,
                                  int availableWidth, int availableHeight) {
        if (mDynamicGrid == null) {
            mDynamicGrid = new DynamicGrid(context,
                    context.getResources(),
                    minWidth, minHeight, width, height,
                    availableWidth, availableHeight);
        }

        // Update the icon size
        DeviceProfile grid = mDynamicGrid.getDeviceProfile();
        Utilities.setIconSize(grid.iconSizePx);
        grid.updateFromConfiguration(context.getResources(), width, height,
                availableWidth, availableHeight);
        return grid;
    }
    public DynamicGrid getDynamicGrid() {
        return mDynamicGrid;
    }

    public boolean isScreenLarge() {
        return mIsScreenLarge;
    }

    // Need a version that doesn't require an instance of LauncherAppState for the wallpaper picker
    public static boolean isScreenLarge(Resources res) {
        return res.getBoolean(R.bool.is_large_tablet);
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public float getScreenDensity() {
        return mScreenDensity;
    }

    public int getLongPressTimeout() {
        return mLongPressTimeout;
    }
       
    public Typeface getIuniTypeface(){
    	return Typeface.createFromAsset(LauncherApplication.getInstance().getAssets(),"fonts/iuni.ttf"); 	 			
    }
    
    public Typeface getFlyUIenTypeface(){
    	return Typeface.createFromAsset(LauncherApplication.getInstance().getAssets(), "fonts/flyui_en.ttf");
    }
    
    public Typeface getAvanGardeTwoBQTypeface(){
    	return Typeface.createFromAsset(LauncherApplication.getInstance().getAssets(), "fonts/AvantGardeTwoBQ-Book.ttf");
    }
}
