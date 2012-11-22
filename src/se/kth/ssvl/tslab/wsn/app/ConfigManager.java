package se.kth.ssvl.tslab.wsn.app;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.general.servlib.config.ConfigurationParser;
import se.kth.ssvl.tslab.wsn.general.servlib.config.exceptions.InvalidDTNConfigurationException;

public class ConfigManager {

	private final static String TAG = "ConfigManager";

	private Context context;

	public ConfigManager(Context context) {
		this.context = context;
	}

	/**
	 * Reads the configuration from the config file in the assets folder
	 * 
	 * @return A Configurations object containing all configuration
	 */
	public Configuration readConfig() {
		try {
			Log.d(TAG, "Trying to load config from assets");
			return ConfigurationParser.parse_config_file(context.getAssets()
					.open("config/dtn.config.xml"));
		} catch (InvalidDTNConfigurationException e) {
			Log.e(TAG,
					"There was an error in the configuration: "
							+ e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e(TAG, "There was an IO exception when reading the config");
			e.printStackTrace();
			return null;
		}
	}

	public boolean writeConfig(Configuration config) {
		return true;
	}

}
