package at.codinglab.gpsminder;

import android.R.bool;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ReminderService extends Service {

    private static final String SERVICE_TAG = "REMINDER SERVICE";

    private LocationManager locManager;
    private CountDownTimer cdt = null;
    private int counter;
    private static boolean serviceIsRunning;
    private SettingsHelper settings;
    private int timePeriod = 60 * 60 * 1000;

    private boolean isGPSon;

    public static boolean isServiceRunning() {
        return serviceIsRunning;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, getResources().getString(R.string.service_started), Toast.LENGTH_LONG)
                .show();



        // ---------log
        // Log.i(SERVICE_TAG, "Service created ");
        //Log.i(SERVICE_TAG, getServiceInfo());

        settings = new SettingsHelper(this);
        //   timePeriod = settings.getTimePeriod() * 60 * 1000;
        timePeriod = settings.getTimePeriod() * 60 * 50;
        counter = 0;

        serviceIsRunning = true;

        // ----log
        Log.i(SERVICE_TAG, getServiceInfo());

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // first check
        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.i(SERVICE_TAG, "start Checking");

            // Start countdown
            cdt = new CountDownTimer(timePeriod, 60000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Log.i(SERVICE_TAG, "Countdown seconds remaining: "
                            + millisUntilFinished / 1000);
                    Log.i(SERVICE_TAG, "Countdown seconds remaining: "
                            + millisUntilFinished / 60000);
                    Log.i(SERVICE_TAG, getServiceInfo());
                }

                public void onFinish() {

                    // ---------log
                    //	Log.i(SERVICE_TAG, "Timer Finished ");
                    //	Log.i(SERVICE_TAG, getServiceInfo());

                    String title = getResources().getString(R.string.app_name);
                    if (locManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Toast.makeText(ReminderService.this, "GPS is active ...",
                                Toast.LENGTH_LONG).show();

                        isGPSon = true;

                        notifyGPSStatus(title, getResources().getString(R.string.gps_is_active));

                        // ---------log
                        //		Log.i(SERVICE_TAG, "Notification ");
                        //		Log.i(SERVICE_TAG, getServiceInfo());

                    } else {

                        Toast.makeText(ReminderService.this, "GPS is NOT active ...",
                                Toast.LENGTH_LONG).show();
                        isGPSon = false;
                        notifyGPSStatus(title, getResources().getString(R.string.gps_isnot_active));
                        ReminderService.this.stopSelf();

                    }

                    // Repeat 3 times
                    if (counter < 3) {
                        // only restart if gps is active
                        if (isGPSon) {
                            // ---------log
                            Log.i(SERVICE_TAG, "restart loop");
                            Log.i(SERVICE_TAG, getServiceInfo());

                            this.start();
                        } else {
                            Log.i(SERVICE_TAG, "no loop");
                            Log.i(SERVICE_TAG, getServiceInfo());
                        }

                    } else {


                        // if not KeepRunning, stop service and reopen App
                        if (!settings.isKeepRunnning()) {
                            // stop service
                            ReminderService.this.stopSelf();

                            // restart activity
                            Intent dialogIntent = new Intent(
                                    ReminderService.this,
                                    ReminderActivity.class);
                            dialogIntent
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);
                        }
                    }
                    counter++;

                }

                private void notifyGPSStatus(String strTitle, String strText) {

                    Log.i(SERVICE_TAG, "now notification");

                    String CHANNEL_ID = "1001";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "GPS Reminder"; //getString(R.string.channel_name);
                        String description = "GPS ON Notification";//getString(R.string.channel_description);
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                        channel.setDescription(description);
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this.
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);

                        ///----

                        Intent intent = new Intent(ReminderService.this,
                                ReminderActivity.class);
                        PendingIntent pIntent = PendingIntent.getService(
                                ReminderService.this, 0, intent, PendingIntent.FLAG_MUTABLE);
                        Uri soundUri = RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                       /* Notification n = new Notification.Builder(
                                ReminderService.this).setContentTitle(strTitle)
                                .setContentText(strText)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentIntent(pIntent).setAutoCancel(true) // GEHT
                                // NED?
                                .setSound(soundUri).build();
                        */
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ReminderService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(strTitle)
                                .setContentText(strText)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.notify(0, builder.build());


                        ///---

                    }
                }
            };

            cdt.start();
        } else {
            Log.i(SERVICE_TAG, "NOT start Checking");

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIsRunning = false;

        Toast.makeText(this, "GPS Reminder service closed.", Toast.LENGTH_LONG)
                .show();

        // ---------log
        Log.i(SERVICE_TAG, "Service stopped");
        Log.i(SERVICE_TAG, getServiceInfo());
    }

    private String getServiceInfo() {
        return "Service info: GpsOn:" + isGPSon + ", Running: "
                + serviceIsRunning + " time: " + timePeriod + ", counter:"
                + counter + ", hashCode:" + this.hashCode();
    }
}
