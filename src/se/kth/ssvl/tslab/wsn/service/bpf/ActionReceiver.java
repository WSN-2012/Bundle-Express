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

package se.kth.ssvl.tslab.wsn.service.bpf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.bundles.Bundle;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.exception.BundleLockNotHeldByCurrentThread;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ActionReceiver implements BPFActionReceiver {
	private static final String TAG = "Actions";

	private int appId = 0;
	private NotificationManager mNotificationManager;
	private Context mContext;

	public ActionReceiver(Context context) {
		mContext = context;
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
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
				Log.e(TAG, "Payload should be in file: "
						+ bundle.payload().file().getAbsolutePath()
						+ ". But did not exist!");
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
		PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
				0, new Intent(mContext,
						se.kth.ssvl.tslab.wsn.app.StatisticsActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext)
				.setSmallIcon(se.kth.ssvl.tslab.wsn.R.drawable.ic_stat_notify)
				.setContentTitle(header).setContentText(description)
				.setContentIntent(resultPendingIntent).setTicker(header);

		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify((appId++ % 5), notification);

	}

}
