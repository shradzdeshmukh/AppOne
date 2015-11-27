package com.cyno.reminder.service;


import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder.recievers.AlarmReciever;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.Task;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

public class AlarmService extends IntentService {

	public static final String CREATE = "create";
	public static final String CANCEL = "cancel";
	private IntentFilter matcher;


	public AlarmService() {
		super("some name");
		matcher = new IntentFilter();
		matcher.addAction(CREATE);
		matcher.addAction(CANCEL);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if(matcher.matchAction(action)){
			execute(action, intent.getIntExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, 0));
		}
	}

	private void execute(String action , int category) {
		AlarmManager mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		String sSelection = TasksTable.COL_TASK_DATE  +" > ? AND "+TasksTable.COL_TASK_STATUS + " = ? ";
		String []arSelectionArgs = new String[]{String.valueOf(System.currentTimeMillis()) , String.valueOf(Task.STATUS_PENDING)};
		Cursor mCursor = getContentResolver().query(TasksTable.CONTENT_URI, null , sSelection	, arSelectionArgs, TasksTable.COL_TASK_DATE );
		if(mCursor != null){
			if(mCursor.moveToFirst()){
				Intent mIntent = new Intent(this,AlarmReciever.class);
				mIntent.setAction(StringConstants.NOTIF_ACTION);
				mIntent.putExtra(StringConstants.ALARM_SERVICE_KEY, mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_ID)));
				mIntent.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY)));
				mIntent.putExtra(IntentKeyConstants.KEY_TASK_ID, mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_ID)));
				PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				Long lTime = Long.parseLong(mCursor.getString( mCursor.getColumnIndex(TasksTable.COL_TASK_DATE)));

				if(action.equals(CREATE)){
					mManager.set(AlarmManager.RTC_WAKEUP, lTime, mPendingIntent);
				}else if(action.equals(CANCEL)){
					mManager.cancel(mPendingIntent);
				}
			}
			mCursor.close();
		}
	}

}
