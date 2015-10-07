package at.nasca.gpsreminder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {

	// Prfs Keys
	final static String TIME_PERIOD = "time_period";
	final static String KEEP_RUNNING = "keep_running";
	final static String AUTO_START = "auto_start";

	private static final String SHARED_SETTINGS = "Reminder_Settings";

	private SharedPreferences sPrefs;
	private SharedPreferences.Editor edPrfs;

	private int timePeriod;
	private boolean keepRunnning;
	private boolean autoStart;

	public SettingsHelper(Context context) {
		super();

		sPrefs = context.getSharedPreferences(SHARED_SETTINGS, 0);
		loadSettings();
	}

	

	private void loadSettings() {
		// get from shared Prefs
		
		this.timePeriod = sPrefs.getInt(TIME_PERIOD, 60);
		this.keepRunnning = sPrefs.getBoolean(KEEP_RUNNING, true);
		this.autoStart= sPrefs.getBoolean(AUTO_START, false);
	}

	private void saveSettings() {

		// save to shared Prefs
		edPrfs = sPrefs.edit(); // Prefs Editor

		// values
		edPrfs.putInt(TIME_PERIOD, this.timePeriod);
		edPrfs.putBoolean(KEEP_RUNNING, this.keepRunnning);
		edPrfs.putBoolean(AUTO_START, this.autoStart);

		edPrfs.commit();
	}

	public int getTimePeriod() {
		return timePeriod;

	}

	public void setTimePeriod(int timePeriod) {
		this.timePeriod = timePeriod;
		saveSettings();
	}

	public boolean isKeepRunnning() {
		return keepRunnning;
	}

	public void setKeepRunnning(boolean keepRunnning) {
		this.keepRunnning = keepRunnning;
		saveSettings();
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		saveSettings();
	}

	// Getter/Setter for Accessing the Settings

}