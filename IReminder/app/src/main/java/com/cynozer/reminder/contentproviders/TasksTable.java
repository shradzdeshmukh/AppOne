package com.cynozer.reminder.contentproviders;

import com.cyno.reminder.constants.GlobalConstants;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TasksTable 
{
	public static final String TABLE_TASKS = "Tasks";

	public static final Uri CONTENT_URI = Uri.parse("content://" + GlobalConstants.AUTHORITY
			+ "/" + TABLE_TASKS);

	public static final String COL_TASK_ID = "_id";
	public static final String COL_TASK_LABEL = "Label";
	public static final String COL_TASK_NAME = "Name";
	public static final String COL_TASK_DATE = "Date";
	public static final String COL_TASK_EMAIL = "Email";
	public static final String COL_TASK_PHONE = "Phone";
	public static final String COL_TASK_STATUS = "Status";
	public static final String COL_TASK_NOTE = "Note";
	public static final String COL_TASK_CATEGORY = "Cat";
	public static final String COL_SCRIBBLE = "scribble";

	/*private static final String DATABASE_CREATE = "create table "
			+ TABLE_TASKS
			+ "(" 
			+ COL_TASK_ID + " integer primary key autoincrement, "
			+ COL_TASK_LABEL + " text not null, " 
			+ COL_TASK_CATEGORY + " integer not null, "
			+ COL_TASK_NAME + " text , "
			+ COL_TASK_DATE + " integer not null,"
			+ COL_TASK_STATUS + " integer not null," 
			+ COL_TASK_EMAIL + " text , " 
			+ COL_TASK_PHONE + " text , " 
			+ COL_TASK_NOTE + " text  " 
			+ ");";
*/
	private static final String DATABASE_CREATE_NEW = "create table "
			+ TABLE_TASKS
			+ "("
			+ COL_TASK_ID + " integer primary key autoincrement, "
			+ COL_TASK_LABEL + " text not null, "
			+ COL_TASK_CATEGORY + " integer not null, "
			+ COL_TASK_NAME + " text not null, "
			+ COL_TASK_DATE + " integer not null,"
			+ COL_TASK_STATUS + " integer not null,"
			+ COL_TASK_EMAIL + " text , "
			+ COL_TASK_PHONE + " text , "
			+ COL_TASK_NOTE + " text,  "
			+ COL_SCRIBBLE + " blob  "
			+ ");";

	private static final java.lang.String DATABASE_UPGRADE_V2 = " ALTER TABLE "+TABLE_TASKS+
			" ADD COLUMN "+COL_SCRIBBLE+" blob default '' ;";

	public static void onCreate(SQLiteDatabase mDatabase)
	{
		mDatabase.execSQL(DATABASE_CREATE_NEW);
	}
	public static void onUpdate(SQLiteDatabase mDatabase , int oldVer, int newVer){
				mDatabase.execSQL(DATABASE_UPGRADE_V2);
	}


}
