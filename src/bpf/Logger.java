package bpf;

import android.util.Log;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFLogger;

public class Logger implements BPFLogger {

	@Override
	public String debug(String TAG, String message) {
		Log.d(TAG, "Debug: "+Thread.currentThread().getName()+message);
		return null;
	}

	@Override
	public String error(String TAG, String message) {
		Log.e(TAG, "Error: "+Thread.currentThread().getName()+message);
		return null;
	}

	@Override
	public String info(String TAG, String message) {
		Log.i(TAG, "Info: "+Thread.currentThread().getName()+message);
		return null;
	}

	@Override
	public String warning(String TAG, String message) {
		Log.w(TAG, "WARN: "+Thread.currentThread().getName()+message);
		return null;
	}

}
