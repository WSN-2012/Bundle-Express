package se.kth.ssvl.tslab.wsn.app;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.general.servlib.config.ConfigurationParser;
import se.kth.ssvl.tslab.wsn.general.servlib.config.exceptions.InvalidDTNConfigurationException;

public class ConfigManager {
	
	private final static String TAG = "ConfigManager";

	private Configuration configuration;
	private Context context;
	
	public ConfigManager(Context context) {
		configuration = new Configuration();
	}
	
	public Configuration readConfig() {
		Configuration conf = null;
		try {
			Log.d(TAG, "Trying to load config from assets");
			conf = ConfigurationParser.parse_config_file(context.getAssets().open("config/dtn.config.xml"));
		} catch (InvalidDTNConfigurationException e) {
			Log.e(TAG, "There was an error in the ");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
