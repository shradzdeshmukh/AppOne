package com.cyno.reminder.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyno.reminder.R;
import com.cyno.reminder.fab.ButtonFloat;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.Task;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

public class RepeatTaskFragment extends DialogFragment {

	private View rootView;

	private  int category;

	public RepeatTaskFragment() {
		this.category = Task.getInstance().getiCategory();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new Builder(getActivity());
		rootView = View.inflate(getActivity(), R.layout.repeat_task_fragment, null);
		builder.setView(rootView);
//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Task details fragment");
//		t.send(new HitBuilders.AppViewBuilder().build());

		return builder.show();
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		final ListView mListView = (ListView) rootView.findViewById(R.id.list_repeat_tasks);
		String []arrRepeat = getResources().getStringArray(R.array.repeat_items);
		RepeatAdapter mAdapter = new RepeatAdapter(getActivity() , R.id.tv_repeat_task_item
		 , Arrays.asList(arrRepeat));
		mListView.setAdapter(mAdapter);
		mListView.setDividerHeight(0);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mListView.setItemChecked(position, true);
				new RepeatTask().execute(new Integer[]{position});


			}
		});
	}

	private class RepeatAdapter extends ArrayAdapter<String>{

		public RepeatAdapter(Context context, int resource, List<String> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder = null;
			if(convertView == null){
				mHolder = new ViewHolder();
				convertView = View.inflate(getActivity() , R.layout.repeat_task_item , null);
				mHolder.mText = (TextView) convertView.findViewById(R.id.tv_repeat_task_item);
				convertView.setTag(mHolder);
			}else{
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.mText.setText(getItem(position));
			return convertView;
		}
	}


	private static class ViewHolder{
		private TextView mText;
	}

	private class RepeatTask extends  AsyncTask<Integer , Void, Void>{

		private ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = ProgressDialog.show(getActivity(), null, getString(R.string.wait_msg));

		}

		@Override
		protected Void doInBackground(Integer... params) {
			Task.getInstance().setStatus(Task.STATUS_PENDING);
			Task.getInstance().storeLocally(getActivity(), Task.NOT_IS_UPDATE, params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			mDialog.dismiss();
			((MainActivity)getActivity()).displayView(category);
			((MainActivity)getActivity()).refreshNavDrawer();
			getDialog().dismiss();
		}
	}

}
