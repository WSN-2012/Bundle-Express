package se.kth.ssvl.tslab.wsn.app;

import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.general.bpf.BPF;
import se.kth.ssvl.tslab.wsn.general.servlib.storage.Stats;
import se.kth.ssvl.tslab.wsn.service.WSNService;
import se.kth.ssvl.tslab.wsn.service.WSNServiceInterface;
import se.kth.ssvl.tslab.wsn.service.WSNServiceInterfaceCallBack;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

public class StatisticsActivity extends Activity {

	private final String TAG = "StatisticsActivity";
	private WSNServiceInterface serviceInterface;
	private TextView stored;
	private TextView transmitted;
	private TextView received;
	private TextView usage;
	private boolean boundToService = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_statistics);
		getUIComponents();

		if (isServiceRunning()) {
			Intent i = new Intent(StatisticsActivity.this, WSNService.class);
			boolean ok = getApplicationContext().bindService(
					i, mConnection, 0);
			Log.d(TAG, "bindService: " + ok);
			if (ok) {
				boundToService = true;
			}
		} else {
			// for now statistics are not available if service is down
			stored.setText("N/A");
			transmitted.setText("N/A");
			received.setText("N/A");
			usage.setText("N/A");
		}
	}

	@Override
    public void onResume() {
		super.onResume();
		if (isServiceRunning()) {
			if (boundToService) {
				getApplicationContext().unbindService(mConnection);
			}
			Intent i = new Intent(StatisticsActivity.this, WSNService.class);
			boolean ok = getApplicationContext().bindService(
					i, mConnection, 0);
			Log.d(TAG, "bindService: " + ok);
		}
	}
	
	
	@Override
    public void onStop() {
        super.onStop();
        try{
        	// if we are bound to the service we unregister the callback so that
        	// the service will not trying to contact us
        	if (serviceInterface != null) {
        		serviceInterface.unregisterCallBack(mCallback);
        	}
			getApplicationContext().unbindService(mConnection);
			boundToService = false;
		} catch (IllegalArgumentException e){
		    Log.w(TAG, "Service not bound.");
		} catch (RemoteException e) {
			Log.e(TAG, "Exception while unregistering callback");
		}
    }
	
	private void getUIComponents() {
		stored = (TextView) findViewById(R.id.storedCounter);
		transmitted = (TextView) findViewById(R.id.TransmittedCounter);
		received = (TextView) findViewById(R.id.ReceivedCounter);
		usage = (TextView) findViewById(R.id.storageUsage);
	}
	
	private void updateUI(Map<String, Integer> stats) {
		stored.setText(Integer.toString(stats.get("stored")));
		transmitted.setText(Integer.toString(stats.get("transmitted")));
		received.setText(Integer.toString(stats.get("received")));
		usage.setText(Integer.toString(stats.get("usage")));
	}
	
	Handler mHandler = new Handler() {
        public void update(Message msg) {
        	updateUI((Map<String, Integer>) msg.obj);
        }
};
	
	private WSNServiceInterfaceCallBack mCallback = new WSNServiceInterfaceCallBack.Stub() {

		@Override
		public void updateStats(int s, int t,
				int r, int u) throws RemoteException {
			
			Map<String, Integer> stats = new HashMap<String, Integer>();
			stats.put("stored", s);
			stats.put("transmitted", t);
			stats.put("received", r);
			stats.put("usage", u);
			
			Message msg = new Message();
			msg.obj = stats;
			mHandler.sendMessage(msg);
			
		}
	};
	
	
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected has been called");
			
			serviceInterface = WSNServiceInterface.Stub.asInterface(service);
			if (serviceInterface != null) {
				try {
					updateUI(serviceInterface.getStats());
					serviceInterface.registerCallBack(mCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				Log.e(TAG, "Could not call method in Service. serviceInterface is null.");
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceInterface = null;
			Log.d(TAG, "Service disconnected");
			boundToService = false;
		}
	};
	
	private boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (WSNService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
