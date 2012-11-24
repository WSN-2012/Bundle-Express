package se.kth.ssvl.tslab.wsn.app;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.WSNServiceInterface;
import se.kth.ssvl.tslab.wsn.app.config.ConfigManager;
import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.service.WSNService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	private final String TAG = "ConfigActivity";
	private WSNServiceInterface serviceInterface;
	
	private CheckBox checkService;
	private EditText txtQuota;
	private Spinner spinnerRouting;
	private EditText serverEid;
	private EditText serverAddress;
 	
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
		txtQuota = (EditText) findViewById(R.id.txtQuota);
		spinnerRouting = (Spinner) findViewById(R.id.spinnerRouting);
		serverEid = (EditText) findViewById(R.id.txtServerEid);
		serverAddress = (EditText) findViewById(R.id.txtServerAddress);
	}
	
	private void updateUI() {
		// Read the config and show it in the UI
		Configuration config = ConfigManager.getInstance().readConfig();
		
		// Set the checkbox depending on if the service is running
		if (isServiceRunning()) {
			checkService.setChecked(true);
		} else {
			checkService.setChecked(false);
		}
		
		// Set the quota
		txtQuota.setText(config.storage_setting().quota());
		
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
		
		// Set the server eid from shared prefs
		
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
	
	/**
	 * Method for saving the configuration 
	 * (only to be used when save-btn is pressed)
	 */
	public void save(View v) {
		
	}
	
	/**
	 * Method for canceling the changes made
	 * (only to be used when cancel-btn is pressed)
	 */
	public void cancel(View v) {
		
	}
	
	public void addListenerOnChk() {
		checkService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ConfigActivity.this, WSNService.class);
				getApplicationContext().startService(i);
				boolean ok = getApplicationContext().bindService(
						i, mConnection, BIND_AUTO_CREATE);
				Log.d(TAG, "bindService: " + ok);

				if (((CheckBox) v).isChecked()) {
//					startService(new Intent(ConfigActivity.this, WSNService.class));
//					i.setClassName("se.kth.ssvl.tslab.wsn.service",
//							"se.kth.ssvl.tslab.wsn.service.WSNService");
//					startService(i);
					
				} else {
					Log.d(TAG, "Box unchecked");
//					Intent i = new Intent(ConfigActivity.this, WSNService.class);
//					i.setClassName("se.kth.ssvl.tslab.wsn.service", "se.kth.ssvl.tslab.wsn.service.WSNService");
					getApplicationContext().unbindService(mConnection);
					stopService(i);
				}
					
			}
		});

	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected has been called");
			serviceInterface = WSNServiceInterface.Stub.asInterface(service);
			if (serviceInterface != null) {
				try {
					//start the BPF
					serviceInterface.start();
					Toast.makeText(ConfigActivity.this, "BPF started",
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
	
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_daemon_config, menu); return
	 * true; }
	 */

	/* Called when the user clicks the save button */
	/*
	 * public void UpdateEID(View view) { /Do something in response to button }
	 */
}
