package com.cyno.reminder.recievers;


import com.cyno.reminder.service.AlarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmSetter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, AlarmService.class);
        service.setAction(AlarmService.CREATE);
        context.startService(service);
    
	}

}
