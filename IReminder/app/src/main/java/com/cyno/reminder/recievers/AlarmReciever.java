package com.cyno.reminder.recievers;


import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder.service.AlarmService;
import com.cyno.reminder.ui.AlarmActivity;
import com.cyno.reminder.ui.MainActivity;
import com.cyno.reminder.R;
import com.cynozer.reminder.contentproviders.TasksTable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class AlarmReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int id = intent.getIntExtra(StringConstants.ALARM_SERVICE_KEY, 0);
		Intent oIntent = new Intent(context,MainActivity.class);
		oIntent.setAction(StringConstants.NOTIF_ACTION);
		oIntent.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, intent.getIntExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, 0));

		PendingIntent mPendingIntent = PendingIntent.getActivity(context, id, oIntent, 0);
		
	
		Cursor mCursor = context.getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ?", new String[]{String.valueOf(id)}, null);
		if(mCursor != null){
			if(mCursor.moveToNext()){
				Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle(context.getString(R.string.notification_title))
				.setContentText(mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_NAME)))
				.setSmallIcon(R.drawable.ic_notification)
				.setAutoCancel(true)
				.setContentIntent(mPendingIntent)
				.build();

				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_sound), true))
				noti.defaults |= Notification.DEFAULT_SOUND;
				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_vibrate), true))
				noti.defaults |= Notification.DEFAULT_VIBRATE;
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_alarm), true)
						&& PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_notification), true))
					notificationManager.notify((int)System.currentTimeMillis(), noti); 
			}
			mCursor.close();
		}

		Intent service = new Intent(context, AlarmService.class);
		service.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, 1);//TODO change later
		service.setAction(AlarmService.CREATE);
		context.startService(service);
		Intent mIntent =  new Intent(context , AlarmActivity.class) ;
		mIntent.putExtra(IntentKeyConstants.KEY_TASK_ID, id);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);

	}

}
