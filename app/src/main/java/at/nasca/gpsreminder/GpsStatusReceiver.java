package at.nasca.gpsreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class GpsStatusReceiver extends BroadcastReceiver {

@Override
public void onReceive(Context context, Intent intent) {

	Intent i = new Intent(context, ReminderService.class);
	context.startService(i);
}
}
