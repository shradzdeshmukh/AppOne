package com.cyno.reminder.constants;

public class GlobalConstants {
	
	public static final String AUTHORITY = "com.cynozer.ireminder.tasksprovider";
	public static final String DATABASE_NAME = "tasks.db";
	public static final String BASE_PATH = "tasks.db";
	
	public static final int DATABASE_VERSION = 2;
	
	/*
	 * <item>Upcomming</item>
        <item>Next 7 days</item>
        <item>Personal</item>
        <item>Bills</item
        <item>Shopping</item>
        <item>Holidays</item>
        <item>Appointments</item>
	 */
	
	//representing each table
	public static final int ALL_TABLES = 0;
	public static final int TASKS = 1;
	
	public static final int TYPE_UPCOMMING = 0;
	public static final int TYPE_WEEK = 1;
	public static final int TYPE_ALL = 2;
	public static final int TYPE_PERSONAL = 3;
	public static final int TYPE_BIRTHDAY = 4;
	public static final int TYPE_BILLS = 5;
	public static final int TYPE_SHOPPING = 6;
	public static final int TYPE_HOLIDAYS = 7;
	public static final int TYPE_APPOINTMENTS = 8;
	public static final int TYPE_OTHERS = 9;
	public static final int TYPE_SCRIBBLE = 10;
}
