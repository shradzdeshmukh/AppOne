package com.cynozer.reminder.utils;

import com.cyno.reminder.constants.GlobalConstants;
import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.service.AlarmService;
import com.cynozer.reminder.contentproviders.TasksTable;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class Task {

    public static enum RepeatTypes{REPEAT_NEVER ,REPEAT_FIVE_MIN,REPEAT_ONE_HOUR,REPEAT_EVERYDAY,
        REPEAT_WEEKLY,REPEAT_MONTHLY,REPEAT_YEARLY};

    public static final int STATUS_PENDING = 100;
    public static final int STATUS_DONE = 200;

    private String title;
    private int status;
    private String category;
    private String note;
    private long time;
    private int iCategory;
    private byte[] scribbleData;

    public static int NOT_IS_UPDATE = -1;

    private static Task instance = new Task();

    private Task(){

    }

    public static Task getInstance(){
        return instance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }



    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public byte[] getScribbleData() {
        return scribbleData;
    }

    public void setScribbleData(byte[] scribbleData) {
        this.scribbleData = scribbleData;
    }

    public int getiCategory() {
        return iCategory;
    }

    public void setiCategory(int iCategory) {
        this.iCategory = iCategory;
    }

    public void storeLocally(Context context , int id , int repeatType){
//        if(this.title == null)
//            return;
        ArrayList<Task> alTasks = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.time);
        int daysToAdd = 0;
        int monthsToAdd = 0;
        int yearToAdd = 0;
        int hoursToAdd = 0;
        int minToAdd = 0;
        RepeatTypes types = RepeatTypes.values()[repeatType];
        int repeatCount = 0;
        switch (types){
            case REPEAT_NEVER:
                repeatCount = 0;
                break;
            case REPEAT_FIVE_MIN:
                repeatCount = getRemainingMin();
                minToAdd = 5;
                break;
            case REPEAT_ONE_HOUR:
                repeatCount = getRemainingHours();
                hoursToAdd = 1;
                break;
            case REPEAT_EVERYDAY:
                repeatCount = 365;
                daysToAdd = 1;
                break;
            case REPEAT_WEEKLY:
                repeatCount = 50;
                daysToAdd = 7;
                break;
            case REPEAT_MONTHLY:
                repeatCount = 12;
                monthsToAdd = 1;
                break;
            case REPEAT_YEARLY:
                repeatCount = 10;
                yearToAdd = 1;
                break;
        }

        for(int i = 1 ; i <= repeatCount +1; ++i){
            Task mTask = new Task();
            mTask.setTitle(this.getTitle());
            mTask.setNote(this.getNote());
            mTask.setStatus(this.getStatus());
            mTask.setCategory(this.getCategory());
            mTask.setiCategory(this.getiCategory());
            mTask.setScribbleData(this.getScribbleData());

            if(i >1) {
                switch (types) {
                    case REPEAT_FIVE_MIN:
                        cal.add(Calendar.MINUTE, minToAdd);
                        break;
                    case REPEAT_ONE_HOUR:
                        cal.add(Calendar.HOUR_OF_DAY, hoursToAdd);
                        break;
                    case REPEAT_EVERYDAY:
                    case REPEAT_WEEKLY:
                        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
                        break;
                    case REPEAT_MONTHLY:
                        cal.add(Calendar.MONTH, monthsToAdd);
                        break;
                    case REPEAT_YEARLY:
                        cal.add(Calendar.YEAR, yearToAdd);
                        break;
                }
            }

//            if(minToAdd != 0)
//                cal.add(Calendar.MINUTE, minToAdd);
//            else if(hoursToAdd != 0)
//                cal.add(Calendar.HOUR_OF_DAY, 1);
//            else if(yearToAdd != 0)
//                cal.add(Calendar.YEAR, 1);
//            else if(monthsToAdd != 0)
//                cal.add(Calendar.MONTH, 1);
//            else
//                cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
            mTask.setTime(cal.getTimeInMillis());
            alTasks.add(mTask);
        }

        final ArrayList<ContentProviderOperation> ops =new ArrayList<ContentProviderOperation>();
        for(Task mTask : alTasks){
            ops.add(ContentProviderOperation.newInsert(TasksTable.CONTENT_URI)
                    .withValue(TasksTable.COL_TASK_STATUS, mTask.getStatus())
                    .withValue(TasksTable.COL_TASK_NAME, mTask.getTitle())
                    .withValue(TasksTable.COL_TASK_NOTE, mTask.getNote())
                    .withValue(TasksTable.COL_TASK_DATE, mTask.getTime())
                    .withValue(TasksTable.COL_TASK_LABEL, mTask.getCategory())
                    .withValue(TasksTable.COL_TASK_CATEGORY, mTask.getiCategory())
                    .withValue(TasksTable.COL_SCRIBBLE, mTask.getScribbleData())
                    .build());
        }
        try {

            context.getContentResolver().applyBatch(GlobalConstants.AUTHORITY, ops);
            setAlarm(context);
            return;
        } catch (RemoteException e) {
        } catch (OperationApplicationException e) {
        }



        ContentValues values = new ContentValues();
        values.put(TasksTable.COL_TASK_NAME, this.title);
        values.put(TasksTable.COL_TASK_DATE, this.time);
        values.put(TasksTable.COL_TASK_STATUS, this.status);
        values.put(TasksTable.COL_TASK_NOTE, this.note);
        values.put(TasksTable.COL_TASK_LABEL, this.category);
        values.put(TasksTable.COL_SCRIBBLE, this.scribbleData);

        if(id == NOT_IS_UPDATE)
            context.getContentResolver().insert(TasksTable.CONTENT_URI, values);
        else
            context.getContentResolver().update(TasksTable.CONTENT_URI, values, TasksTable.COL_TASK_ID + " = ?",
                    new String[]{String.valueOf(id)});

        clearObject();
        setAlarm(context);
    }

    private int getRemainingHours() {
        Calendar mCal1 = Calendar.getInstance();
        mCal1.setTimeInMillis(this.getTime());
        Calendar mCal2 = Calendar.getInstance();
        mCal2.setTimeInMillis(this.getTime());
        mCal2.set(Calendar.HOUR_OF_DAY, 23);
        mCal2.set(Calendar.MINUTE, 59);
        mCal2.set(Calendar.SECOND, 59);
        mCal2.set(Calendar.MILLISECOND, 999);
        Log.d("hour", "cal 2 " + mCal2.get(Calendar.HOUR_OF_DAY));
        Log.d("hour", "cal 1 " + mCal1.get(Calendar.HOUR_OF_DAY));
        Log.d("hour", "diff" + (mCal2.get(Calendar.HOUR_OF_DAY) - mCal1.get(Calendar.HOUR_OF_DAY)));
        return mCal2.get(Calendar.HOUR_OF_DAY) - mCal1.get(Calendar.HOUR_OF_DAY);
    }

    private int getRemainingMin() {
        Calendar mCal1 = Calendar.getInstance();
        mCal1.setTimeInMillis(this.getTime());
        Calendar mCal2 = Calendar.getInstance();
        mCal2.setTimeInMillis(this.getTime());
        mCal2.set(Calendar.HOUR_OF_DAY, 23);
        mCal2.set(Calendar.MINUTE, 59);
        mCal2.set(Calendar.SECOND, 59);
        mCal2.set(Calendar.MILLISECOND, 999);

        long seconds = mCal2.getTimeInMillis() - mCal1.getTimeInMillis();
        return (int) (seconds/(1000*60*5));
    }

    public void clearObject() {
        this.title  = null;
        this.category = null;
        this.status = 0;
        this.time = 0;
        this.note = null;
    }

    public void updateTask(int taskId , ContentValues values , Context context){
        context.getContentResolver().update(TasksTable.CONTENT_URI, values,
                TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)});
        setAlarm(context);
    }

    public void test(ContentResolver mResolver){
        Cursor mCursor = mResolver.query(TasksTable.CONTENT_URI , null , null , null , null);
        while(mCursor.moveToNext()){
            Log.d("test" , "------------------------");
            Log.d("test" , "name = " + mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_NAME)));
            Log.d("test" , "time = " + mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_DATE)));
            Log.d("test" , "status = " + mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_STATUS)));
            Log.d("test" , "id = " + mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_ID)));
            Log.d("test" , "label = " + mCursor.getString(mCursor.getColumnIndex(TasksTable.COL_TASK_LABEL)));
        }
    }

    public void setAlarm(Context context){
        Intent service = new Intent(context, AlarmService.class);
        service.putExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM, category);
        service.setAction(AlarmService.CREATE);
        context.startService(service);
        test(context.getContentResolver());
    }
}
