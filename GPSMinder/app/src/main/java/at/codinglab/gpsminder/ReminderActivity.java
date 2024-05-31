package at.codinglab.gpsminder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import androidx.core.content.ContextCompat;
import android.Manifest;


public class ReminderActivity extends Activity {

    private static final String LOGGING_TAG = "REMINDER ACTIVITY";

    private SettingsHelper settings;
    private Spinner timeSpinner;
    private CheckBox keepRunning, autostart;
    private Button stop;
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        timeSpinner = (Spinner) findViewById(R.id.spinner1);
        keepRunning = (CheckBox) findViewById(R.id.checkBoxRunning1);
        autostart = (CheckBox) findViewById(R.id.checkBoxAuto);

        stop = (Button) findViewById(R.id.buttonStop);
        start = (Button) findViewById(R.id.buttonStart);


        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("10");
        list.add("30");
        list.add("60");
        list.add("90");
        list.add("120");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);

        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSpinner.setAdapter(dataAdapter);


        // load controlls from settings
        settings = new SettingsHelper(this);

        autostart.setChecked(settings.isAutoStart());
        keepRunning.setChecked(settings.isKeepRunnning());

        int spinnerPostion = dataAdapter.getPosition(String.valueOf(settings.getTimePeriod()));
        timeSpinner.setSelection(spinnerPostion);

        verifyNotificationPermission();

    }

    private void verifyNotificationPermission() {
        Log.e(LOGGING_TAG, "check PERMISSION fo notifiocation");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {

                Log.e(LOGGING_TAG, "PERMISSION_GRANTED");
            } else {
                Log.e(LOGGING_TAG, "PERMISSION -- NOT -- GRANTED");
                String[] strings = {Manifest.permission.POST_NOTIFICATIONS};
                requestPermissions(strings, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toogleButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();

    }

    private void saveSettings() {
        // save to settings
        settings.setAutoStart(autostart.isChecked());
        settings.setKeepRunnning(keepRunning.isChecked());
        settings.setTimePeriod(Integer.parseInt(timeSpinner.getSelectedItem().toString()));


    }

    private void toogleButtons() {

/*        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/

        if (ReminderService.isServiceRunning()) {
            stop.setEnabled(true);
            stop.setTextColor(Color.WHITE);

            start.setEnabled(false);
            start.setTextColor(Color.LTGRAY);
        } else {
            stop.setEnabled(false);
            stop.setTextColor(Color.LTGRAY);

            start.setEnabled(true);
            start.setTextColor(Color.WHITE);
        }

    }

    public void startService(View v) {

        saveSettings();

        Log.i(LOGGING_TAG, "Start Service from activity");
        Intent i = new Intent(this, ReminderService.class);
        startService(i);

        toogleButtons();
    }

    public void stopService(View v) {

        Log.i(LOGGING_TAG, "stop Service from activity");

        Intent i = new Intent(this, ReminderService.class);
        stopService(i);
        toogleButtons();
    }

    public void openGpsSettings(View v) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void closeApp(View v) {
        finish();
    }
}
