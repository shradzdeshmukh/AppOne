package com.cyno.reminder.ui;

import java.util.ArrayList;

import com.cyno.reminder.adapters.CategoryListAdapter;
import com.cyno.reminder.models.CategoryItem;
import com.cyno.reminder.ui.IReminder.TrackerName;
import com.cyno.reminder.R;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.Task;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;


import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class FragmentTaskList extends DialogFragment implements OnItemClickListener {

	private String[] mCatTitles;
	private TypedArray mCatIcons;
	private ListView mCatList;
	private CategoryListAdapter adapter;
	private View rootView;
	private boolean isUpdate;
	private int taskId;

	public FragmentTaskList(){

	}
	public FragmentTaskList(boolean isUpdate , int id){
		this.isUpdate = isUpdate;
		this.taskId = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Dialog);
//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Category list fragment");
//		t.send(new HitBuilders.AppViewBuilder().build());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_category_dialog, null);
		mCatTitles = getResources().getStringArray(R.array.category_list_titles);
		mCatIcons = getResources().obtainTypedArray(R.array.category_list_icons);
		mCatList = (ListView) rootView.findViewById(R.id.list_task);
		mCatList.setOnItemClickListener(this);
		adapter = new CategoryListAdapter(getActivity(),getCatList() );
		mCatIcons.recycle();
		mCatList.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		// safety check
		if (getDialog() == null) {
			return;
		}
		getDialog().getWindow().setWindowAnimations(
				R.style.dialog_animation_fade);
	}

	private ArrayList<CategoryItem> getCatList(){
		ArrayList<CategoryItem> items = new ArrayList<CategoryItem>();
		for(int i = 0 ; i <= mCatTitles.length -1 ; ++i){
			items .add(new CategoryItem(mCatTitles[i], mCatIcons.getResourceId(i, -1) ));
		}
		return items ;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isUpdate){
			Cursor cur = getActivity().getContentResolver().query(TasksTable.CONTENT_URI, null, TasksTable.COL_TASK_ID + " = ? ",new String[]{String.valueOf(this.taskId)}, null);
			if(cur != null){
				if(cur.moveToNext()){
					int category = Integer.valueOf(cur.getString(cur.getColumnIndex(TasksTable.COL_TASK_CATEGORY)));
					selectListPosition(category -3);
				}else{
					selectListPosition(0);
				}
				cur.close();
			}
		}else
			selectListPosition(0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {

		selectListPosition(position);
		getDialog().dismiss();
		Task.getInstance().setCategory(mCatTitles[position]);
		Task.getInstance().setiCategory(position + 3);

		if (position == mCatTitles.length-1) {
			ScribbleFragment frag = new ScribbleFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_container , frag).commit();
		} else {
			FragmentAddTask frag = new FragmentAddTask(isUpdate, taskId);
			frag.show(getFragmentManager(), FragmentAddTask.class.getSimpleName());
		}
	}

	private void selectListPosition(int position) {
		mCatList.setItemChecked(position, true);
		mCatList.setSelection(position);
	}

}
