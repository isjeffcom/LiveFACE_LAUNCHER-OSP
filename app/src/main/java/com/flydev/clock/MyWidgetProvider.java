package com.flydev.clock;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.widget.RemoteViews;
import cc.flydev.face.R;

public class MyWidgetProvider extends AppWidgetProvider {
	private final static SimpleDateFormat TIMEFORMAT = new SimpleDateFormat(
			"HH:mm");
	private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
			"dd");
	private final static SimpleDateFormat MONTHFORMAT = new SimpleDateFormat(
			"M");
	// private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
	// "M��dd�� E a");
	private final static SimpleDateFormat weekFORMAT = new SimpleDateFormat("E");

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, ClockService.class));

	}

	public static RemoteViews updateTime(Context context,int width) {
		RemoteViews views ;
		if(width>=1080){
			 views = new RemoteViews(context.getPackageName(),
					R.layout.time_frame_1080);
		}else if(width>=640){
			 views = new RemoteViews(context.getPackageName(),
					R.layout.time_frame_720);
		}else{
			 views = new RemoteViews(context.getPackageName(),
					R.layout.time_frame_480);
		}
		Intent intent = new Intent("android.intent.action.SET_ALARM");
		String time = TIMEFORMAT.format(new Date());
		String date = DATEFORMAT.format(new Date());
		System.out.println("date" + date);
		switch (Integer.parseInt(date) % 10) {
		case 1:
			date = date + "st";
			break;
		case 2:
			date = date + "nd";
			break;
		case 3:
			date = date + "rd";
			break;
		default:
			date = date + "th";
			break;
		}
		String week = weekFORMAT.format(new Date());
		if (week.equals("周一") || week.equals("星期一")) {
			week = "MONDAY";
		} else if (week.equals("周二") || week.equals("星期二")) {
			week = "TUESDAY";
		} else if (week.equals("周三") || week.equals("星期三")) {
			week = "WEDNESDAY";
		} else if (week.equals("周四") || week.equals("星期四")) {
			week = "THURSDAY";
		} else if (week.equals("周五") || week.equals("星期五")) {
			week = "FRIDAY";
		} else if (week.equals("周六") || week.equals("星期六")) {
			week = "SATURDAY";
		} else if (week.equals("周日") || week.equals("星期日")) {
			week = "SUNDAY";
		}
		String month = MONTHFORMAT.format(new Date());
		switch (Integer.parseInt(month)) {
		case 1:
			month = "January";
			break;
		case 2:
			month = "February";
			break;
		case 3:
			month = "March";
			break;
		case 4:
			month = "April";
			break;
		case 5:
			month = "May";
			break;
		case 6:
			month = "June";
			break;
		case 7:
			month = "July";
			break;
		case 8:
			month = "August";
			break;
		case 9:
			month = "September";
			break;
		case 10:
			month = "October";
			break;
		case 11:
			month = "November";
			break;
		case 12:
			month = "December";
			break;
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
//		views.setOnClickPendingIntent(R.id.txtview, pendingIntent);
		// 2.�Ĵ�����ȥ3.�Ĵ�����ȥ
		if(width>=1080){
			views.setImageViewBitmap(R.id.time_1080,
					buildUpdate(context, time, 140, 60, 130, 450, 150));
			views.setImageViewBitmap(R.id.month_1080,
					buildUpdate(context, month + " " + date, 50, 15, 45, 500, 55));
			views.setImageViewBitmap(R.id.week_1080,
					buildUpdate(context, week + "   ", 50, 15, 38, 500, 50));
		}else if(width>=640){
			views.setImageViewBitmap(R.id.time_720,
					buildUpdate(context, time, 140, 60, 130, 450, 150));
			views.setImageViewBitmap(R.id.month_720,
					buildUpdate(context, month + " " + date, 50, 15, 38, 500, 55));
			views.setImageViewBitmap(R.id.week_720,
					buildUpdate(context, week + "   ", 50, 15, 45, 500, 50));
		}else{
			views.setImageViewBitmap(R.id.time_480,
					buildUpdate(context, time, 140, 60, 130, 450, 150));
			views.setImageViewBitmap(R.id.month_480,
					buildUpdate(context, month + " " + date, 50, 15, 38, 500, 60));
			views.setImageViewBitmap(R.id.week_480,
					buildUpdate(context, week + "   ", 50, 15, 45, 500, 60));
		}
		return views;
	}

	public static Bitmap buildUpdate(Context context, String time,
			int textSize, int x, int y, int w, int h) {
		Bitmap myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		Canvas myCanvas = new Canvas(myBitmap);
		Paint paint = new Paint();
		Typeface clock = Typeface.createFromAsset(context.getAssets(),
				"fonts/iuni.ttf");
		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setTypeface(clock);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTextSize(textSize);
		paint.setTextAlign(Align.LEFT);
		myCanvas.drawText(time, x, y, paint);
		return myBitmap;
	}
}
