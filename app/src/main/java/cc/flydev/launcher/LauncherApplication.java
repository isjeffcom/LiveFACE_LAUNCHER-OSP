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


import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;



public class LauncherApplication extends Application {
	private static LauncherApplication mInstance;
	
	public static LauncherApplication getInstance(){
		return mInstance;
	}
	
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Context appContext =  this.getApplicationContext();
        LauncherAppState.setApplicationContext(this);
        LauncherAppState.getInstance();
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        LauncherAppState.getInstance().onTerminate();
    }
    
    public boolean isServiceRunning(String serviceClassName){
  	  final ActivityManager activityManager = (ActivityManager)mInstance.getSystemService(Context.ACTIVITY_SERVICE);
  	  final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

	  	  for (RunningServiceInfo runningServiceInfo : services) {
		  	   if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
		  		   return true;
		  	   }
	  	  }
	  	  return false;
  	  }
    
//    public static void TintStatuBarNavigationBar(Activity activity){
//    	Window window = activity.getWindow();
//    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//			tintManager.setStatusBarTintEnabled(true);
//			tintManager.setNavigationBarTintEnabled(true);
//			tintManager.setStatusBarTintResource(android.R.color.holo_blue_light);
//			tintManager.setNavigationBarTintResource(android.R.color.transparent);
//		}
//    }
//
//    public static void TintStatuBarNavigation(Activity activity){
//    	Window window = activity.getWindow();
//    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//			tintManager.setStatusBarTintEnabled(true);
//			tintManager.setNavigationBarTintEnabled(true);
//			tintManager.setStatusBarTintResource(android.R.color.transparent);
//			tintManager.setNavigationBarTintResource(android.R.color.transparent);
//		}
//    }
}