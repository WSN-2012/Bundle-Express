package se.kth.ssvl.tslab.wsn.service.bpf;

import android.util.Log;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFLogger;

public class Logger implements BPFLogger {

	@Override
	public String debug(String TAG, String message) {
		Log.d(TAG, message);
		return TAG + message;
	}

	@Override
	public String error(String TAG, String message) {
		Log.e(TAG, message);
		return TAG + message;
	}

	@Override
	public String info(String TAG, String message) {
		Log.i(TAG, message);
		return TAG + message;
	}

	@Override
	public String warning(String TAG, String message) {
		Log.w(TAG, message);
		return TAG + message;
	}

}
