package com.cyno.reminder.ui;

import java.util.Calendar;

import com.cyno.reminder.calender.CalendarDay;
import com.cyno.reminder.calender.MaterialCalendarView;
import com.cyno.reminder.calender.OnDateChangedListener;
import com.cyno.reminder.ui.IReminder.TrackerName;
import com.cyno.reminder.R;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.Task;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;




import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;


public class FragmentCalender extends DialogFragment implements OnDateChangedListener, OnTimeSetListener {

	private View rootView;
	private MaterialCalendarView mCal;
	private int taskId;
	private boolean isUpdate;

	public FragmentCalender() {
	}

	public FragmentCalender(boolean isUpdate, int taskId) {
		this.isUpdate = isUpdate;
		this.taskId = taskId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Calender fragment");
//		t.send(new HitBuilders.AppViewBuilder().build());

	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		rootView = View.inflate(getActivity(), R.layout.fragment_calender, null);
		Calendar cal = Calendar.getInstance();
		if(isUpdate){
			Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)},null);
			if(cur != null){
				if(cur.moveToLast()){
					cal.setTimeInMillis(Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE))));
				}
				cur.close();
			}
		}else
			cal.setTimeInMillis(System.currentTimeMillis());
		mCal = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);
		mCal.setSelectedDate(new CalendarDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)));
		mCal.setOnDateChangedListener(this);
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setView(rootView)
		.setPositiveButton(getString(R.string.create), null)
		.setNegativeButton(getString(android.R.string.cancel),null);
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getDialog() == null) {
			return;
		}
		getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
	}

	@Override
	public void onResume() {
		super.onResume();
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
		Button mPosButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
		mPosButton.setOnClickListener(new CustomListener(getDialog()));

	}

	@Override
	public void onDateChanged(MaterialCalendarView widget, CalendarDay date) {
	}


	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if(getActivity()== null){
			return;
		}

		CalendarDay cal = mCal.getSelectedDate();
		Calendar mCalender = cal.getCalendar();
		mCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mCalender.set(Calendar.MINUTE, minute);

		if(mCalender.getTimeInMillis() < System.currentTimeMillis()){
			Toast.makeText(getActivity(), R.string.time_back_selected, Toast.LENGTH_LONG).show();
			return;
		}

		Task.getInstance().setTime(mCalender.getTimeInMillis());
		RepeatTaskFragment frag  = new RepeatTaskFragment();
		frag.show(getFragmentManager() , RepeatTaskFragment.class.getSimpleName());

		/*
		TODO move below code
		 */

//		Task.getInstance().setStatus(Task.STATUS_PENDING);
//		if(!isUpdate)
//			Task.getInstance().storeLocally(getActivity(), Task.NOT_IS_UPDATE);
//		else
//			Task.getInstance().storeLocally(getActivity(), taskId);
//
//		Toast.makeText(getActivity(), R.string.task_added, Toast.LENGTH_LONG).show();
//		((MainActivity)getActivity()).refreshNavDrawer();
//		((MainActivity)getActivity()).displayView(Task.getInstance().getiCategory());
//		Task.getInstance().clearObject();
		getDialog().dismiss();
	}


	class CustomListener implements View.OnClickListener {
		private final Dialog dialog;
		public CustomListener(Dialog dialog) {
			this.dialog = dialog;
		}


		@Override
		public void onClick(View v) {
			Calendar cal  = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			TimePickerDialog dialogg = new TimePickerDialog(getActivity(), FragmentCalender.this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
			dialogg.show();			
		}
	}
}
