package at.nasca.gpsreminder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;


public class ReminderActivity extends Activity {

	SettingsHelper settings;
	
	private Spinner spinner1;
	private CheckBox keepRunnning, autostart;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		keepRunnning = (CheckBox) findViewById(R.id.checkBoxRunning1);
		autostart = (CheckBox) findViewById(R.id.checkBoxAuto);

		
		
		List<String> list = new ArrayList<String>();
		list.add("30");
		list.add("60");
		list.add("90");
		list.add("120");
		list.add("10");
		list.add("5");
		list.add("1");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);

		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner1.setAdapter(dataAdapter);
		
		
		// load controlls from settings
		 settings = new SettingsHelper(this);
		 		 
		 autostart.setChecked(settings.isAutoStart());
		 keepRunnning.setChecked(settings.isKeepRunnning());
		
 		 int spinnerPostion = dataAdapter.getPosition(String.valueOf(settings.getTimePeriod()));
         spinner1.setSelection(spinnerPostion);
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
		settings.setKeepRunnning(keepRunnning.isChecked());
		settings.setTimePeriod(Integer.parseInt(spinner1.getSelectedItem().toString()));
	
		
	}

	private void toogleButtons() {

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Button stop= (Button) findViewById(R.id.buttonStop);
		Button start= (Button) findViewById(R.id.buttonStart);
		
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
		
		
		Intent i = new Intent(this, ReminderService.class);
		startService(i);

		toogleButtons();
	}

	public void stopService(View v) {

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
