package com.cyno.reminder.ui;

import java.util.ArrayList;

import com.cyno.reminder.adapters.TaskListAdapter;
import com.cyno.reminder.constants.GlobalConstants;
import com.cyno.reminder.ui.IReminder.TrackerName;
import com.cyno.reminder.R;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.DividerHelper;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TaskListFragment extends Fragment implements LoaderCallbacks<Cursor> , OnClickListener , OnLongClickListener{

	protected static final int SHOW_EMPTY_VIEW = 0;
	protected static final String ACTION_REFRESH = "REFRESH";
	private final long lTwoDays = 1000*3600*24*2;
	private final long lOneWeek = 1000*3600*24*7;
	private boolean isMultiSelect;
	private ArrayList<Integer> selectedIds;

	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private TaskListAdapter mAdapter;
	private int LOADER_ID = 10;
	private int type;
	private TextView mSubHeader;
	private String tite;
	private Handler mEmptyListHandler;
	private RefreshLisner listner;

	public TaskListFragment(){
		super();
	}


	public TaskListFragment(int type , String title) {
		this.type = type;
		this.tite = title;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.selectedIds = new ArrayList<>();
		mEmptyListHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_EMPTY_VIEW:
					if(getActivity() != null)
						((MainActivity)getActivity()).displayView(MainActivity.EMPTY_FRAGMENT);
					break;

				default:
					break;
				}
			}
		};
		
//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Task list fragment");
//		t.send(new HitBuilders.AppViewBuilder().build());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_task_list, null);
		mSubHeader =(TextView) rootView.findViewById(R.id.fragment_task_list_subheader);


		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_tasks);
		final RecyclerView.ItemDecoration itemDecoration = new DividerHelper(getActivity());
		mRecyclerView.addItemDecoration(itemDecoration);
		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);
		// use a linear layout manager
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);
		// specify an adapter (see also next example)
		mAdapter = new TaskListAdapter(getActivity(), null , this , this);
		mRecyclerView.setAdapter(mAdapter);
		getLoaderManager().initLoader(LOADER_ID , null, this);

		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (type) {
		case GlobalConstants.TYPE_UPCOMMING:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  TasksTable.COL_TASK_DATE + " > ? AND " + TasksTable.COL_TASK_DATE + " < ? And " + TasksTable.COL_TASK_CATEGORY + " != ? ",
					new String[]{String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()+lTwoDays),String.valueOf(GlobalConstants.TYPE_SCRIBBLE)}, null);
		case GlobalConstants.TYPE_WEEK:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  
					TasksTable.COL_TASK_DATE + " > ? AND " + TasksTable.COL_TASK_DATE + " < ? And " + TasksTable.COL_TASK_CATEGORY + " != ? ",
					new String[]{String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()+lOneWeek),String.valueOf(GlobalConstants.TYPE_SCRIBBLE)}, null);
		case GlobalConstants.TYPE_ALL:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  TasksTable.COL_TASK_CATEGORY + " != ? ",new String[]{String.valueOf(GlobalConstants.TYPE_SCRIBBLE)}, null);
	
		case GlobalConstants.TYPE_SCRIBBLE:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,  TasksTable.COL_TASK_CATEGORY + " = ? ", new String[]{String.valueOf(type)}, null);

		case GlobalConstants.TYPE_BILLS:
		case GlobalConstants.TYPE_BIRTHDAY:
		case GlobalConstants.TYPE_SHOPPING:
		case GlobalConstants.TYPE_HOLIDAYS:
		case GlobalConstants.TYPE_APPOINTMENTS:
		case GlobalConstants.TYPE_OTHERS:
			return new CursorLoader(getActivity(),TasksTable.CONTENT_URI,null,
					TasksTable.COL_TASK_CATEGORY + " = ? AND " +
							TasksTable.COL_TASK_CATEGORY + " != ?",
					new String[]{String.valueOf(type) ,
							String.valueOf(GlobalConstants.TYPE_SCRIBBLE)}, null);

		default:
			break;
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		if(cursor.getCount() == 0)
			this.mEmptyListHandler.sendEmptyMessage(SHOW_EMPTY_VIEW);
		else
			mSubHeader.setText(this.tite);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	@Override
	public boolean onLongClick(View v) {
		return true;
	}

	@Override
	public void onClick(View v) {
		FragmentDetails frag = new FragmentDetails((String)v.getTag());
		frag.show(getFragmentManager(), FragmentDetails.class.getSimpleName());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		listner = new RefreshLisner();
		getActivity().registerReceiver(listner, new IntentFilter(ACTION_REFRESH));
	}

	
	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(listner);
	}
	
	private class RefreshLisner extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			getLoaderManager().restartLoader(LOADER_ID, null, TaskListFragment.this);
		}
	}

}
