package se.kth.ssvl.tslab.wsn.service;

import se.kth.ssvl.tslab.wsn.apps.R;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BPFService extends Service  {
/*String tag="TestService";*/
	private static final String TAG = "AndService";
	MediaPlayer player;

    	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		player = MediaPlayer.create(this, R.raw.braincandy);
		player.setLooping(false); // Set looping
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		player.stop();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		player.start();
	}
}