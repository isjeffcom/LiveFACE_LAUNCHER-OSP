package cc.flydev.launcher.settings;


import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import cc.flydev.face.R;

public class BlurSetting extends AppCompatActivity {
    private SeekBar seekbar;
    private StackBlurManager _stackBlurManager;
    private Button btn;
    private TextView tv1;
    private int mProgress;
    private static Context sContext;
    private static final String WALLPAPER_NAME = "wall";
    WallpaperManager mWallpaperManager;
    private Bitmap pbm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.blur);
        seekbar = (SeekBar) findViewById(R.id.seekBar1);
        tv1 = (TextView) findViewById(R.id.textview);
        sContext = this;
        mWallpaperManager = WallpaperManager.getInstance(this); // 获取壁纸管理器
        Drawable wallpaperDrawable = mWallpaperManager.getDrawable();
        pbm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        if (wallpaperDrawable instanceof BitmapDrawable) {
            pbm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
            saveBitmap2file(pbm, WALLPAPER_NAME);//保存到本地
        }

	/*	FileInputStream fis = null;
		try {
			fis = openFileInput(WALLPAPER_NAME);
		} catch (FileNotFoundException e1){
		}
		Bitmap pbm = null;
		if(fis != null){
			pbm = BitmapFactory.decodeStream(fis);			
		}else{
		//获取WallpaperManager 壁纸管理器
			// 获取当前壁纸
			Drawable wallpaperDrawable = mWallpaperManager.getDrawable();
			if(wallpaperDrawable instanceof BitmapDrawable){
				pbm = ((BitmapDrawable) wallpaperDrawable).getBitmap();		
				saveBitmap2file(pbm, WALLPAPER_NAME);//保存到本地
			}
		}*/
//		img1.setBackground(new BitmapDrawable(null, pbm));			
        _stackBlurManager = new StackBlurManager(pbm);

        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mProgress = progress * 1;


            }
        });

        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new WallpaperTasker().execute();
                Toast.makeText(getApplicationContext(), "正在为你毛玻璃化当前壁纸~~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class WallpaperTasker extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
			
			
			/*try {
				Log.e("MainAcitivity",System.currentTimeMillis()+"");
				mWallpaperManager.setBitmap(_stackBlurManager.returnBlurredImage());
				img1.setImageBitmap(_stackBlurManager.returnBlurredImage());//模糊壁纸完成后主线程设置壁纸
			} catch (IOException e) {
				e.printStackTrace();
			}*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("MainAcitivity", System.currentTimeMillis() + "");
            _stackBlurManager.processNatively(mProgress);//非常耗时的操作，后台线程处理
            try {
                mWallpaperManager.setBitmap(_stackBlurManager.returnBlurredImage());

            } catch (IOException e) {
                ;
                e.printStackTrace();
            }
            finish();
            return null;

        }


    }


    static boolean saveBitmap2file(Bitmap bmp, String filename) {
        CompressFormat format = CompressFormat.JPEG;
        int quality = 50;
        FileOutputStream stream = null;
        try {
            stream = sContext.openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (Exception e) {
        }
        return bmp.compress(format, quality, stream);
    }

}

