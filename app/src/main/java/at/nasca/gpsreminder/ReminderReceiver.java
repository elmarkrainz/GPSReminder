package at.nasca.gpsreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SettingsHelper settings = new SettingsHelper(context);

		if (settings.isAutoStart()){
			Intent i = new Intent(context, ReminderService.class);
			context.startService(i);
		}
	}

}
