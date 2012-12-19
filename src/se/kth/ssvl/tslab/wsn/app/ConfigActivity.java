/*
 * Copyright 2012 KTH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package se.kth.ssvl.tslab.wsn.app;

import java.io.File;
import java.util.Collection;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.app.config.ConfigManager;
import se.kth.ssvl.tslab.wsn.app.util.AlertDialogManager;
import se.kth.ssvl.tslab.wsn.app.util.FileUtil;
import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.LinksSetting.LinkEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.RoutesSetting.RouteEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.routing.routers.BundleRouter.router_type_t;
import se.kth.ssvl.tslab.wsn.service.WSNService;
import se.kth.ssvl.tslab.wsn.service.WSNServiceInterface;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	private final String TAG = "ConfigActivity";
	private WSNServiceInterface serviceInterface;

	private CheckBox checkService;
	private EditText quota;
	private Spinner spinnerRouting;
	private Spinner spinnerStorage;
	private EditText serverEid;
	private EditText localEid;
	private EditText serverAddress;
	private EditText webServerUrl;
	
	private Configuration config;
 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daemon_config);
		getUIComponents();
		addListenerOnChk();
		updateUI();
	}
	
	private void getUIComponents() {
		checkService = (CheckBox) findViewById(R.id.checkService);
		quota = (EditText) findViewById(R.id.txtQuota);
		spinnerRouting = (Spinner) findViewById(R.id.spinnerRouting);
		spinnerStorage = (Spinner) findViewById(R.id.spinnerStorage);
		serverEid = (EditText) findViewById(R.id.txtServerEid);
		localEid = (EditText) findViewById(R.id.txtLocalEid);
		serverAddress = (EditText) findViewById(R.id.txtServerAddress);
		webServerUrl = (EditText) findViewById(R.id.txtWebServerUrl);
	}
	
	private void updateUI() {
		// Read the config and show it in the UI
		config = ConfigManager.getInstance().readConfig();
		if (config == null) {
			return;
		}
		
		// Give only the option for the sd-card as storage if it is mounted
		String[] storageOptions;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			storageOptions = new String[] {"Phone", "SD-Card"}; 
		} else {
			storageOptions = new String[] {"Phone"};
		}
		spinnerStorage.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, storageOptions));
		
		// Set the storage spinner
		if (PreferenceManager.getDefaultSharedPreferences(this).getString("storage.path", "").contains(
				getFilesDir().getAbsolutePath())) {
			spinnerStorage.setSelection(0);
		} else if (PreferenceManager.getDefaultSharedPreferences(this).getString("storage.path", "").contains(
				Environment.getExternalStorageDirectory().getAbsolutePath())) {
			spinnerStorage.setSelection(1);
		}
		
		
		// Set the checkbox depending on if the service is running
		if (isServiceRunning()) {
			checkService.setChecked(true);
		} else {
			checkService.setChecked(false);
		}
		
		// Set the quota
		quota.setText(Integer.toString(config.storage_setting().quota()));
		
		// Set the routing type
		switch (config.routes_setting().router_type()) {
		case STATIC_BUNDLE_ROUTER:
			spinnerRouting.setSelection(0);
			break;
		case EPIDEMIC_BUNDLE_ROUTER:
			spinnerRouting.setSelection(1);
			break;
		case PROPHET_BUNDLE_ROUTER:
			spinnerRouting.setSelection(2);
			break;
		default:
			Log.e(TAG, "Don't know what to specify in the routing spinner. Is the router type undefined?");
			break;
		}
		
		// Set the server eid
		RouteEntry server = find(config.routes_setting().route_entries(), new RoutesChecker(), "server");
		if (server != null) {
			serverEid.setText(server.dest());
		} else {
			serverEid.setText(getResources().getString(R.string.defaultServerEid));
		}
		
		// Set the local eid
		localEid.setText(config.routes_setting().local_eid());
		
		// Set the server address
		LinkEntry link = find(config.links_setting().link_entries(), new LinksChecker(), "server");
		if (link != null) {
			serverAddress.setText(link.dest());
		} else {
			serverAddress.setText(getResources().getString(R.string.defaultServerAddress));
		}
				
		// Set the web service address
		webServerUrl.setText(this.getPreferences(MODE_WORLD_READABLE).getString("server.url", 
				getResources().getString(R.string.defaultWebServerUrl)));
		
	}

	private boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (WSNService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private void showSaveError(String msg) {
		AlertDialogManager adm = new AlertDialogManager();
		adm.showAlertDialog(this, "Save error", msg, false);
	}

	public void preSave(View v) {
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            save();
		            break;
		        case DialogInterface.BUTTON_NEGATIVE:
		        	
		            break;
		        }
		    }
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("If you change storage type all your storage is going to be deleted. Do you want to continue?")
			.setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener);
		
		boolean inPhone = PreferenceManager.getDefaultSharedPreferences(this).
				getString("storage.path", "").contains(getFilesDir().getAbsolutePath());
		switch (spinnerStorage.getSelectedItemPosition()) {
		case 0: // Phone
			if (!inPhone) { // SD-Card -> Phone
				builder.show();
			} else {
				save();
			}
			break;
		case 1: // SD-Card
			if (inPhone) { // Phone -> SD-Card
				builder.show();
			} else {
				save();
			}
			break;
		default:
			showSaveError("The storage selector is not working properly");
			return;
		}
	}

	/**
	 * Method for saving the configuration 
	 * (only to be used when save-btn is pressed)
	 */
	public void save() {
		// Check that the service is not running before saving
		if (isServiceRunning()) {
			showSaveError("You have to stop the service first!");
			return;
		}
		
		// Start by saving the shared prefs values
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		editor = settings.edit();
		editor.putString("server.url", webServerUrl.getText().toString());
		editor.commit();
		
		// Now fill in the values and save to config file
		try {
			config.storage_setting().set_quota(Integer.parseInt(quota.getText().toString()));
		} catch (NumberFormatException e) {
			showSaveError("The storage limit must be a number");
			return;
		}
		
		int routeId = getId(config.routes_setting().route_entries(), new RoutesChecker(), "server");
		if (routeId > -1 && routeId < config.routes_setting().route_entries().size()) {
			config.routes_setting().route_entries().get(routeId).set_dest(serverEid.getText().toString());
		} else {
			showSaveError("Couldn't find the route entry in the config");
			return;
		}
		
		config.routes_setting().set_local_eid(localEid.getText().toString());
		
		int linkId = getId(config.links_setting().link_entries(), new LinksChecker(), "server");
		if (linkId > -1 && linkId < config.links_setting().link_entries().size()) {
			config.links_setting().link_entries().get(linkId).set_dest(serverAddress.getText().toString());
		} else {
			showSaveError("Couldn't find the link entry in the config");
			return;
		}

		switch (spinnerRouting.getSelectedItemPosition()) {
		case 0: // static
			config.routes_setting().set_router_type(
					router_type_t.STATIC_BUNDLE_ROUTER);
			break;
		case 1: // epidemic
			config.routes_setting().set_router_type(
					router_type_t.EPIDEMIC_BUNDLE_ROUTER);
			break;
		case 2: // prohphet
			config.routes_setting().set_router_type(
					router_type_t.PROPHET_BUNDLE_ROUTER);
			break;
		default:
			showSaveError("There was a problem in understanding the routing type");
			return;
		}

		// Last thing is to check whether we should move storage path
		// Destruct the object and reinit it with the new position
		boolean inPhone = settings.getString("storage.path", "").contains(
				getFilesDir().getAbsolutePath());
		switch (spinnerStorage.getSelectedItemPosition()) {
		case 0: // Phone
			if (!inPhone) { // SD-Card -> Phone
				moveStorage(FileUtil.phoneStoragePath(this));
			}
			break;
		case 1: // SD-Card
			if (inPhone) { // Phone -> SD-Card
				moveStorage(FileUtil.sdCardStoragePath(this));
			}
			break;
		default:
			showSaveError("The storage selector is not working properly");
			return;
		}
		
		// By now we should be ok, let go on and write the file
		if (ConfigManager.getInstance().writeConfig(config)) {
			Log.d(TAG , "Saved successfully");
			Toast.makeText(ConfigActivity.this, "Configuration Saved Successfully",
					Toast.LENGTH_LONG).show();
		} else {
			Log.e(TAG, "Saving configuration file didn't succeed");
			showSaveError("Couldn't write the configuration");
		}
	}
	
	/**
	 * Method for canceling the changes made
	 * (only to be used when cancel-btn is pressed)
	 */
	public void cancel(View v) {
		updateUI();
	}
	
	public void addListenerOnChk() {
		checkService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(ConfigActivity.this, WSNService.class);

				if (((CheckBox) v).isChecked()) {
					
					getApplicationContext().startService(i);
					boolean ok = getApplicationContext().bindService(
							i, mConnection, 0);
					Log.d(TAG, "bindService: " + ok);
					
				} else {
					
					Log.d(TAG, "Box unchecked. Going to stop service.");
					getApplicationContext().stopService(i);
					try{
						getApplicationContext().unbindService(mConnection);
					} catch (IllegalArgumentException e){
					    Log.w(TAG, "Service not bound. Only stopping it.");
					}
					
				}
					
			}
		});

	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected has been called");
			serviceInterface = WSNServiceInterface.Stub.asInterface(service);
			if (serviceInterface != null) {
				try {
					//start the BPF
					serviceInterface.start(ConfigManager.getInstance().getConfigurationFile().getParentFile().getAbsolutePath());
					Toast.makeText(ConfigActivity.this, "Service started",
							Toast.LENGTH_LONG).show();
				} catch (RemoteException e) {
					Log.e(TAG, "Error communicating with service!");
					Toast.makeText(ConfigActivity.this, "Error communicating with service!",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Log.e(TAG, "Cound not call method in Service. serviceInterface is null.");
				Toast.makeText(ConfigActivity.this, "Error communicating with service!",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceInterface = null;
			Log.d(TAG, "Service disconnected");
		}
	};
	
	private void moveStorage(File to) {
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		editor = settings.edit();
		
		// Move the config file
		if (!FileUtil.moveFile(
				new File(settings.getString("storage.path", "") + "/" + ConfigManager.FILENAME),
				new File(to.getAbsoluteFile() + "/" + ConfigManager.FILENAME))) {
			showSaveError("There was a problem when moving the files");
			Log.e(TAG, "There were problems in moving the folder");
			return;
		}
		
		// Delete the old folder
		FileUtil.deleteFolder(new File(settings.getString("storage.path", "")));
		
		// Set the new storage path
		editor = settings.edit();
		editor.putString("storage.path", to.getAbsolutePath());
		editor.commit();
		
		// Reconstruct the configmanager with the new path
		ConfigManager.getInstance().destruct();
		ConfigManager.init(getApplicationContext());
		
		// Set the storage path in the config
		config.storage_setting().set_storage_path(
				settings.getString("storage.path", ""));
	}
	
	interface Checker<T> {
	    public boolean check(T object, String value);
	}

	class RoutesChecker implements Checker<RouteEntry> {
	    @Override
		public boolean check(RouteEntry re, String id) {
	        return re.link_id().equals(id);
	    }
	}
	
	class LinksChecker implements Checker<LinkEntry> {
		@Override
		public boolean check(LinkEntry le, String id) {
			return le.id().equals(id);
		}
	}

	static <T> T find(Collection<T> coll, Checker<T> chk, String value) {
	    for (T obj : coll) {
	         if (chk.check(obj, value)) {
	        	 return obj;
	         }
	    }
	    return null;
	}
	
	static <T> int getId(Collection<T> coll, Checker<T> chk, String value) {
		int i = 0;
		for (T obj : coll) {
	         if (chk.check(obj, value)) {
	        	 return i;
	         }
	         i++;
	    }
	    return -1;
	}
}
