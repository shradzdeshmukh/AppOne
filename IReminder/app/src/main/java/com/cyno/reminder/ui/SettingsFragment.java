package com.cyno.reminder.ui;

import android.content.res.Resources;
import android.os.Bundle;

import com.cyno.reminder.preference.PreferenceFragment;
import com.cyno.reminder.ui.IReminder.TrackerName;
import com.cyno.reminder.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
//		GoogleAnalytics.getInstance(getActivity().getBaseContext()).dispatchLocalHits();
//		Tracker t = ((IReminder)getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Settings fragment");
//		t.send(new HitBuilders.AppViewBuilder().build());
		addPreferencesFromResource(R.xml.settings);
	}
	
}
