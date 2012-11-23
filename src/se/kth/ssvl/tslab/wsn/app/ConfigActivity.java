package se.kth.ssvl.tslab.wsn.app;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.WSNServiceInterface;
import se.kth.ssvl.tslab.wsn.service.WSNService;
import android.app.Activity;
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
import android.widget.Toast;

public class ConfigActivity extends Activity {
	private CheckBox chkIos;
	
	private final String TAG = "ConfigActivity";
	private WSNServiceInterface serviceInterface;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daemon_config);
		addListenerOnChkIos();
		updateUI();
	}
	
	private void updateUI() {
		
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
	

	public void addListenerOnChkIos() {
		chkIos = (CheckBox) findViewById(R.id.checkBox1);

		chkIos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
//					startService(new Intent(ConfigActivity.this, WSNService.class));
					Intent i = new Intent(ConfigActivity.this, WSNService.class);
//					i.setClassName("se.kth.ssvl.tslab.wsn.service",
//							"se.kth.ssvl.tslab.wsn.service.WSNService");
//					startService(i);
					boolean ok = getApplicationContext().bindService(
							i, mConnection, BIND_AUTO_CREATE);
					Log.i("ConfigActivity","bindService: " + ok);
					if (serviceInterface != null) {
						try {
							int ret = serviceInterface.start();
							Toast.makeText(ConfigActivity.this, "start method returned: " + ret,
									Toast.LENGTH_LONG).show();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						Log.e(TAG, "Cound not call method in Service. serviceInterface is null.");
						Toast.makeText(ConfigActivity.this, "Error communicating with service!",
								Toast.LENGTH_LONG).show();
					}
					
					
				} else {
					Intent i = new Intent();
					i.setClassName("se.kth.ssvl.tslab.wsn.service", "se.kth.ssvl.tslab.wsn.service.WSNService");
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
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceInterface = null;
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
