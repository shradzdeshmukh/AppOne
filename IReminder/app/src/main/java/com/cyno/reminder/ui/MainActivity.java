package com.cyno.reminder.ui;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.cyno.reminder.adapters.NavDrawerListAdapter;
import com.cyno.reminder.calender.CalendarDay;
import com.cyno.reminder.calender.MaterialCalendarView;
import com.cyno.reminder.constants.GlobalConstants;
import com.cyno.reminder.constants.IntentKeyConstants;
import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder.fab.ButtonFloat;
import com.cyno.reminder.models.NavDrawerItem;
import com.cyno.reminder.tooltip.ToolTip;
import com.cyno.reminder.tooltip.ToolTipRelativeLayout;
import com.cyno.reminder.tooltip.ToolTipView;
import com.cyno.reminder.tooltip.ToolTipView.OnToolTipViewClickedListener;
import com.cyno.reminder.ui.IReminder.TrackerName;
import com.cyno.reminder.R;
import com.cynozer.reminder.contentproviders.TasksTable;
import com.cynozer.reminder.utils.AppUtils;
//import com.cynozer.reminder.utils.LoginActivity;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import com.cynozer.reminder.utils.Task;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {

	protected static final int SETTINGS = 1000;
	protected static final int EMPTY_FRAGMENT = 200;
	protected static final int ADD_TASK = 20;
	private static final int NAV_LIST_COUNT = 10;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ButtonFloat mFab;
	private ArrayList<Object> alCount;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private NavDrawerListAdapter adapter;
	//	private ImageView ivProfilePic;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Task.getInstance().test(getContentResolver());
		if(Build.VERSION.SDK_INT >= 21){
//			Window window = getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
//			// inside your activity (if you did not enable transitions in your theme)
//			window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//			window.setExitTransition(new Explode());
//			window.setEnterTransition(new Fade());
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFab =  (ButtonFloat) findViewById(R.id.fab);
		mFab.setOnClickListener(this);

		getCountList();
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		try{
			View mPremiumView = findViewById(R.id.go_premium);
			if(Build.VERSION.SDK_INT >= 11) {
				mPremiumView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String appPackageName = "com.cyno.reminder_premium";// getPackageName() from Context or Activity object
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
						}
					}
				});
			}else{
				mPremiumView.setVisibility(View.GONE);
			}
		}catch(Exception ex){
		}
		//		mDrawerList.addHeaderView(mPremiumView);


		adapter = new NavDrawerListAdapter(getApplicationContext(),getNavList() );
		navMenuIcons.recycle();
		mDrawerList.setAdapter(adapter);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				toolbar, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
			}

			public void onDrawerOpened(View drawerView) {
			}
		};

		mDrawerToggle.syncState();


		mDrawerLayout.setDrawerListener(mDrawerToggle);

		//		ivProfilePic = (ImageView) findViewById(R.id.profile_img);
		displayView(0);


		TextView tvDate = (TextView) findViewById(R.id.tv_date);
		TextView tvMonth = (TextView) findViewById(R.id.tv_month);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat mFormatDate = new SimpleDateFormat("dd" , Locale.getDefault());
		SimpleDateFormat mFormatMonth = new SimpleDateFormat("MMMM" , Locale.getDefault());

		tvDate.setText(mFormatDate.format(cal.getTime()));
		tvMonth.setText(mFormatMonth.format(cal.getTime()));

		Intent mIntent = getIntent();
		if(mIntent != null){
			if(mIntent.getAction() != null){
				if(mIntent.getAction().equals(StringConstants.NOTIF_ACTION)){
					int cat = Integer.valueOf(mIntent.getIntExtra(IntentKeyConstants.KEY_TYPE_OF_ALARM , 0));
					displayView(cat);
				}
			}
		}

		MaterialCalendarView mCal = (MaterialCalendarView) findViewById(R.id.calendarView);
		if(mCal != null)
			mCal.setSelectedDate(new CalendarDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)));

		//		GoogleAnalytics.getInstance(getBaseContext()).dispatchLocalHits();
		//		Tracker t = ((IReminder)getApplication()).getTracker(TrackerName.APP_TRACKER);
		//		t.setScreenName("Main activity");
		//		t.send(new HitBuilders.AppViewBuilder().build());
	}


	@Override
	public void onStart() {
		super.onStart();
		//		registerReceiver(LogoutReciever, new IntentFilter(LoginActivity.LOGOUT_BROADCAST));
		SharedPreferences pref = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE);
		//		ImageLoader.getInstance().displayImage(pref.getString(StringConstants.KEY_USER_IMAGE, null), ivProfilePic);
	}

	@Override
	public void onStop() {
		super.onStop();
		//		unregisterReceiver(LogoutReciever);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	protected void displayView(int type) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (type) {
		case ADD_TASK:
//			startActivity(new Intent(this , ScribbleActivity.class));
			FragmentTaskList list = new FragmentTaskList();
			list.show(fragmentManager, FragmentTaskList.class.getSimpleName());
			return;
		case SETTINGS:
			return;
		case EMPTY_FRAGMENT:
			fragment =	new EmptListFragment();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment, EmptListFragment.class.getSimpleName()).commit();
			return;

		default:
			fragment =	new TaskListFragment(type , navMenuTitles[type] );
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment, TaskListFragment.class.getSimpleName()).commit();
			break;
		}
		selectNavPosition(type);

	}

	private void selectNavPosition(int position) {
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(Gravity.START);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}



	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		//		case R.id.action_login:
		//			Intent mIntentLogin = new Intent(this , SplashActivity.class);
		////			mIntentLogin.setAction(LoginActivity.ACTION_SIGNIN);
		//			startActivity(mIntentLogin);
		//			break;
		//		case R.id.action_logout:
		////			Intent mIntentLogout = new Intent(this , LoginActivity.class);
		////			mIntentLogout.setAction(LoginActivity.ACTION_SIGNOUT);
		////			startActivity(mIntentLogout);
		//			break;
		case R.id.action_settings:
			getSupportFragmentManager().beginTransaction().addToBackStack(SettingsFragment.class.getSimpleName()).replace(R.id.frame_container, new SettingsFragment()).commit();
			break;


		default:
			break;
		}

		//TODO support
		invalidateOptionsMenu();
		return true;

	}

	@SuppressLint("NewApi")
	@Override
	protected void onRestart() {
		super.onRestart();
        showFab();
		invalidateOptionsMenu();
	}

	private ArrayList<NavDrawerItem> getNavList(){
		ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
		for(int i = 0 ; i <= navMenuTitles.length -1 ; ++i){
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
					navMenuIcons.getResourceId(i, -1) ,  !String.valueOf(alCount.get(i)).equals("0") , String.valueOf(alCount.get(i))));
		}
		return navDrawerItems;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(AppUtils.isSignedIn(this))
			getMenuInflater().inflate(R.menu.main_signed_in, menu);
		else
			getMenuInflater().inflate(R.menu.main_signed_out, menu);
		return true;
	}




	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.START))
			mDrawerLayout.closeDrawer(Gravity.START);
		else
			super.onBackPressed();
	}


	public void refreshNavDrawer(){
		getCountList();
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		adapter = new NavDrawerListAdapter(getApplicationContext(),getNavList() );
		mDrawerList.setAdapter(adapter);
	}


	//	@SuppressLint("NewApi")
	//	BroadcastReceiver LogoutReciever = new  BroadcastReceiver(){
	//
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//			if(intent.getAction().equals(LoginActivity.LOGOUT_BROADCAST)){
	////				ivProfilePic.setImageDrawable(getDrawable(R.drawable.ic_profile_pic));
	//				invalidateOptionsMenu();
	//			}
	//		}
	//	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fab:
			displayView(ADD_TASK);
			break;

		default:
			break;
		}
	}

	private void getCountList(){
		alCount = new ArrayList<>();
		Cursor cursor = null;
		Cursor cursor1 = null;
		Cursor cur = null;
		for(int i = 0 ; i <= NAV_LIST_COUNT  ; ++i){

			switch (i) {
			case 0:
				cursor = getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_ID},
						TasksTable.COL_TASK_DATE +  " > ? AND "+TasksTable.COL_TASK_DATE +  " < ? AND " +
								TasksTable.COL_TASK_CATEGORY + " != ? ",
						new String[]{String.valueOf(System.currentTimeMillis()) ,
								String.valueOf(System.currentTimeMillis() + 2*24*3600*1000) ,
								String.valueOf(GlobalConstants.TYPE_SCRIBBLE)},
						null);
				if(cursor != null)
					alCount.add(cursor.getCount());

				break;
			case 1:
				cursor1 = getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_ID},
						TasksTable.COL_TASK_DATE +  " > ? AND "+TasksTable.COL_TASK_DATE +  " < ? AND " + TasksTable.COL_TASK_CATEGORY + " != ? ", new String[]{String.valueOf(System.currentTimeMillis()) , String.valueOf(System.currentTimeMillis() + 7*24*3600*1000) , String.valueOf(GlobalConstants.TYPE_SCRIBBLE)}, null);
				if(cursor1 != null)
					alCount.add(cursor1.getCount());

				break;
			case 2:
				cursor = getContentResolver().query(TasksTable.CONTENT_URI,new String[]{TasksTable.COL_TASK_ID}, TasksTable.COL_TASK_CATEGORY + " != ? ",new String[]{String.valueOf(GlobalConstants.TYPE_SCRIBBLE)}, null);
				if(cursor != null)
					alCount.add(cursor.getCount());

				break;

			default:
				cur = getContentResolver().query(TasksTable.CONTENT_URI, new String[]{TasksTable.COL_TASK_ID},
						TasksTable.COL_TASK_CATEGORY +  " = ? ", new String[]{String.valueOf(i)}, null);
				if(cur != null)
					alCount.add(cur.getCount());

				break;
			}

		}
		if(cursor != null)
			cursor.close();
		if(cursor1 != null)
			cursor1.close();
		if(cur != null)
			cur.close();
	}

	public void hideFab(){
		mFab.setVisibility(View.GONE);
	}

	public void showFab(){
		mFab.setVisibility(View.VISIBLE);
	}
}
