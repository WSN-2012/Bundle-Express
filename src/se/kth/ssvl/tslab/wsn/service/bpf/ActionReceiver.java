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

public class ActionReceiver implements BPFActionReceiver {
	private static final String TAG = "Actions";
	private static final int mId = 19;  // allows you to update the notification later on
	private NotificationManager mNotificationManager=null;
	private Context mContext;
	public ActionReceiver(Context context) {
		mContext = context;
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
	public void notify(String header, String description) {
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		/* Log.d(TAG, "NOTIFICATION :" + header + description); */
		int numMessages = 0; /* Begin loop if phone receives new bundle */
		PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0,
				new Intent(mContext,
						se.kth.ssvl.tslab.wsn.app.StatisticsActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext)
				.setSmallIcon(se.kth.ssvl.tslab.wsn.R.drawable.ic_stat_notify)
				// add notification icon
				.setContentTitle(header)
				.setContentText(description).setNumber(++numMessages)
				.setContentIntent(resultPendingIntent);
		// Creates an intent for an Activity

		mNotificationManager.notify(mId, mBuilder.build());

	}

}
