package com.cyno.reminder.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.cyno.reminder.constants.GlobalConstants;
import com.cyno.reminder.fab.ButtonFloat;
import com.cyno.reminder.ui.FragmentCalender.CustomListener;
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
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentDetails extends DialogFragment implements OnClickListener, android.view.View.OnClickListener{

	private  boolean isScribble;
	private int taskId;
	private View rootView;
	private final String dateformat = "dd MMMM , HH:mm ";
	private ButtonFloat mFab;
	private boolean isDone;
	private boolean tvDescription;
	private Integer category;

	public FragmentDetails() {
	}

	public FragmentDetails(String id){
		this.taskId = Integer.valueOf(id);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Cursor mCursor = getActivity().getContentResolver().
				query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_CATEGORY},
						TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(taskId)}, null);
		if(mCursor!= null){
			if(mCursor.moveToNext()) {
				int cat = mCursor.getInt(mCursor.getColumnIndex(TasksTable.COL_TASK_CATEGORY));
				this.isScribble = (cat == GlobalConstants.TYPE_SCRIBBLE);
			}
			mCursor.close();
		}
		AlertDialog.Builder builder = new Builder(getActivity());
		if(!isScribble)
			rootView = View.inflate(getActivity(), R.layout.fragment_detail_view, null);
		else
			rootView = View.inflate(getActivity(), R.layout.fragment_detail_view_scribble, null);


		mFab = (ButtonFloat) rootView.findViewById(R.id.fab);
		if(!isScribble)
			mFab.setOnClickListener(this);
		builder.setView(rootView);
		if(!isScribble)
			builder.setPositiveButton(getActivity().getString(R.string.edit), this);
		else
			builder.setNeutralButton(getActivity().getString(R.string.cancel), this);

		builder.setNegativeButton(getActivity().getString(R.string.delete), this);
//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Task details fragment");
//		t.send(new HitBuilders.AppViewBuilder().build());

		return builder.show();
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

		populateFields();
	}

	private void populateFields() {
		TextView tvCategory = (TextView) rootView.findViewById(R.id.details_tv_category);
		TextView tvTitle = (TextView) rootView.findViewById(R.id.details_tv_title);
		TextView tvDate = (TextView) rootView.findViewById(R.id.details_tv_date);
		TextView tvDaysLeftKey = (TextView) rootView.findViewById(R.id.details_tv_days_left_key);
		TextView tvDaysLeftValue = (TextView) rootView.findViewById(R.id.details_tv_days_left_value);
		TextView tvDescKey = (TextView) rootView.findViewById(R.id.details_tv_description_key);
		TextView tvDescValue = (TextView) rootView.findViewById(R.id.details_tv_description_value);
		ImageView ivScribble = (ImageView) rootView.findViewById(R.id.iv_details_scribble);

		Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ?"
				, new String[]{String.valueOf(this.taskId)}, null);

		if(cur != null){
			if(cur.moveToNext()) {
				this.category = Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_CATEGORY)));
				if (!isScribble)
					tvCategory.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_LABEL)));
				else
					ivScribble.setImageBitmap(setImage(cur.getBlob(cur.getColumnIndex(TasksTable.COL_SCRIBBLE))));
				tvTitle.setText(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NAME)));
				isDone = Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_STATUS))) == Task.STATUS_DONE;
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE))));
				SimpleDateFormat format = new SimpleDateFormat(dateformat, Locale.getDefault());
				long date = Long.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_DATE)));
				long diff = date - System.currentTimeMillis();
				tvDate.setText(format.format(cal.getTime()));

				if (Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_STATUS))) == Task.STATUS_DONE) {
					tvDaysLeftValue.setTextColor(getActivity().getResources().getColor(R.color.green));
					tvDaysLeftKey.setTextColor(getActivity().getResources().getColor(R.color.green));
					tvDaysLeftValue.setVisibility(View.GONE);
					tvDaysLeftKey.setText(R.string.done);
					tvDaysLeftKey.setGravity(Gravity.CENTER_HORIZONTAL);
					if(!isScribble)
						mFab.setDrawableIcon(getActivity().getResources().getDrawable(R.drawable.ic_content_clear));
				} else if (diff < 0) {
					if(!isScribble) {
						tvDaysLeftValue.setTextColor(getActivity().getResources().getColor(R.color.warn));
						tvDaysLeftKey.setTextColor(getActivity().getResources().getColor(R.color.warn));
					}
					tvDaysLeftValue.setVisibility(View.GONE);
					tvDaysLeftKey.setText(R.string.overdue);
					tvDaysLeftKey.setGravity(Gravity.CENTER_HORIZONTAL);
					if (!isScribble)
						mFab.setDrawableIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_done));
				} else {
					if(!isScribble) {
						tvDaysLeftValue.setTextColor(getActivity().getResources().getColor(R.color.primary_dark));
						tvDaysLeftKey.setTextColor(getActivity().getResources().getColor(R.color.black));
					}
					tvDaysLeftValue.setVisibility(View.VISIBLE);
					tvDate.setText(format.format(cal.getTime()));
					tvDaysLeftValue.setText(String.valueOf(getDaysLeft(diff, tvDaysLeftKey)));
					if(!isScribble)
						mFab.setDrawableIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_done));
				}

				if (!isScribble) {
					String desc = cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_NOTE));
					if (desc != null) {
						if (desc.length() == 0) {
							tvDescKey.setVisibility(View.GONE);
							tvDescValue.setVisibility(View.GONE);
						} else {
							tvDescValue.setText(desc);
							tvDescKey.setVisibility(View.VISIBLE);
						}
					} else {
						tvDescKey.setVisibility(View.GONE);
						tvDescValue.setVisibility(View.GONE);
					}

				}
			}
			cur.close();
		}

	}

	private long getDaysLeft(long diff , TextView tv) {
		long diffr = diff/(1000*3600*24);
		if(diffr != 0){
			tv.setText(R.string.days_left);
			return diffr;
		}else{
			tv.setText(R.string.hours_left_details);
			return diff/(1000*3600);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isScribble)
			((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accent));
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case AlertDialog.BUTTON_POSITIVE:
				FragmentTaskList frag = new FragmentTaskList(true, taskId);
				frag.show(getFragmentManager(), FragmentTaskList.class.getSimpleName());
				break;
			case AlertDialog.BUTTON_NEGATIVE:
				getActivity().getContentResolver().
						delete(TasksTable.CONTENT_URI,TasksTable.COL_TASK_ID + " = ? ",
								new String[]{String.valueOf(this.taskId)});
				Toast.makeText(getActivity(), getActivity().getString(R.string.task_delete),Toast.LENGTH_LONG).show();
				Task.getInstance().setAlarm(getActivity());
				((MainActivity)getActivity()).refreshNavDrawer();
				((MainActivity)getActivity()).displayView(category);
				break;
			case AlertDialog.BUTTON_NEUTRAL:
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		ContentValues values = new ContentValues();
		if(!isDone)
			values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_DONE);
		else
			values.put(TasksTable.COL_TASK_STATUS, Task.STATUS_PENDING);

		getActivity().getContentResolver().
				update(TasksTable.CONTENT_URI, values,
						TasksTable.COL_TASK_ID + " = ? ", new String[]{String.valueOf(this.taskId)});
		if(!isDone)
			Toast.makeText(getActivity(), getActivity().getString(R.string.task_completed),Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getActivity(), getActivity().getString(R.string.task_pending),Toast.LENGTH_LONG).show();

		Task.getInstance().setAlarm(getActivity());
		getDialog().dismiss();
		getActivity().sendBroadcast(new Intent(TaskListFragment.ACTION_REFRESH));
	}

	private Bitmap setImage(byte[] data){
		Bitmap bmp = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		if(Build.VERSION.SDK_INT >= 11)
			options.inMutable = false;
		bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return bmp;
	}

}
