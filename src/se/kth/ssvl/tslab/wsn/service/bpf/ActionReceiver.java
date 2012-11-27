package se.kth.ssvl.tslab.wsn.service.bpf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.bundles.Bundle;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.exception.BundleLockNotHeldByCurrentThread;
import android.util.Log;

public class ActionReceiver implements BPFActionReceiver {
	private static final String TAG = "Actions";

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
	public void notify(String header, String description) {
		Log.d(TAG, "NOTIFICATION :" + header + description);
	}

}
