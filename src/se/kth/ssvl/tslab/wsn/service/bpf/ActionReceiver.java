package se.kth.ssvl.tslab.wsn.service.bpf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.bundles.Bundle;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.exception.BundleLockNotHeldByCurrentThread;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ActionReceiver extends Activity implements BPFActionReceiver {
	private static final String TAG = "Actions";
	private static final int mId = 1;  // allows you to update the notification later on
	public ActionReceiver() {
	}

	@Override
	public void bundleReceived(Bundle bundle) {
		Log.i(TAG, "Received bundle! Reading:");
		switch (bundle.payload().location()) {
		case DISK:
			RandomAccessFile f = null;
			try {
				f = new RandomAccessFile(bundle.payload().file(), "r");
				byte[] buffer = new byte[(int) f.length()];
				f.read(buffer);
				Log.i(TAG, new String(buffer));
			} catch (FileNotFoundException e) {
				Log.e(TAG, "Payload should be in file: " + 
						bundle.payload().file().getAbsolutePath() + ". But did not exist!");
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			} finally {
				try {
					f.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			break;
		case MEMORY:
			try {
				Log.i(TAG, new String(bundle.payload().memory_buf()));
			} catch (BundleLockNotHeldByCurrentThread e) {
				e.printStackTrace();
			}
			break;
		default:
			Log.w(TAG, "The bundle was neither stored in disk nor memory");
		}
	}

	@Override
	public void notify(String header, String description)	{ 
		/*Log.d(TAG, "NOTIFICATION :" + header + description);*/		
		NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
		.setSmallIcon(se.kth.ssvl.tslab.wsn.R.drawable.ic_stat_notify) //add notification icon
        .setContentTitle("New bundle: "/*header*/)
        .setContentText(header+" "+description);
// Creates an explicit intent for an Activity in your app
Intent resultIntent = new Intent(this, se.kth.ssvl.tslab.wsn.app.StatisticsActivity.class);
PendingIntent resultPendingIntent= PendingIntent.getActivity( 
        this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
   mBuilder.setContentIntent(resultPendingIntent); //start an activity 
                                               //when the user clicks the notification text 
                                              //in the notification drawer
NotificationManager mNotificationManager =
    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

int numMessages = 0; /*Begin loop if phone receives new bundle*/

mBuilder.setContentText("New bundle received ").setNumber(++numMessages); // Because the ID remains 
                                                                         //unchanged
                                                                        //the existing 
                                                                       //notification is updated.
mNotificationManager.notify(mId, mBuilder.build());

	}

}
