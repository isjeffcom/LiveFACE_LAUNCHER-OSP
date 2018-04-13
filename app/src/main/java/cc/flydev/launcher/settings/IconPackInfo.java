package cc.flydev.launcher.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class IconPackInfo {
    String packageName;
    CharSequence label;
    Drawable icon;
    Drawable preview;

    IconPackInfo(Context con, ResolveInfo r, PackageManager packageManager) {
        int resId;
		Resources res = null;
		packageName = r.activityInfo.packageName;
		try {
			res = con.getPackageManager().getResourcesForApplication(packageName);
		} catch(PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		icon = r.loadIcon(packageManager);
        label = r.loadLabel(packageManager);
        if((resId = res.getIdentifier("preview", "drawable", packageName)) != 0)
            preview = res.getDrawable(resId);
		else if((resId = res.getIdentifier("themepreview", "drawable", packageName)) != 0)
			preview = res.getDrawable(resId);
		else if((resId = res.getIdentifier("theme_preview", "drawable", packageName)) != 0)
			preview = res.getDrawable(resId);
    }

    IconPackInfo() {
    }
	
	public IconPackInfo(String label, Drawable icon, String packageName) {
        this.label = label;
        this.icon = icon;
        this.packageName = packageName;
    }

    public IconPackInfo(String label, Drawable icon, Drawable preview, String packageName) {
        this.label = label;
        this.icon = icon;
        this.preview = preview;
        this.packageName = packageName;
    }
}
