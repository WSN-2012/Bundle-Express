package se.kth.ssvl.tslab.wsn.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import se.kth.ssvl.tslab.wsn.service.bpf.ActionReceiver;
import se.kth.ssvl.tslab.wsn.service.bpf.Communication;
import se.kth.ssvl.tslab.wsn.service.bpf.DB;
import se.kth.ssvl.tslab.wsn.service.bpf.Logger;
import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.service.WSNServiceInterface;
import se.kth.ssvl.tslab.wsn.app.ConfigActivity;
import se.kth.ssvl.tslab.wsn.general.bpf.BPF;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFCommunication;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFDB;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFLogger;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFService;
import se.kth.ssvl.tslab.wsn.general.bpf.exceptions.BPFException;
import se.kth.ssvl.tslab.wsn.general.servlib.storage.Stats;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class WSNService extends Service implements BPFService {
	
	private static String TAG = "Service";
	private Logger logger;
	private ActionReceiver action;
	private Communication comm;
	private DB db;

	private final WSNServiceInterface.Stub mBinder = new WSNServiceInterface.Stub() {

		@Override
		public void start() throws RemoteException {
			logger.debug(TAG, "Start method in Service called by ConfigActivity");
			//start BPF
//			BPF.getInstance().start();
		}
		
		@Override
		public Map<String, Integer> getStats() throws RemoteException {
			Stats stats = BPF.getInstance().getStats();
			Map<String, Integer> s = new HashMap<String, Integer>();
			s.put("stored", stats.storedBundles());
			s.put("transmitted", stats.transmittedBundles());
			s.put("received", stats.receivedBundles());
			s.put("usage", stats.totalSize());
			return s;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		logger.debug(TAG, "Binding...");
		return mBinder;
	}

	@Override
	public void onCreate() {
		// Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

		// Init a logger first of all
		logger = new Logger();

		// Init the action receiver
		action = new ActionReceiver(logger);

		// Init the communications object
		comm = new Communication();

		// Init the DB object
//		db = new DB(new File("build/database.db"), logger); // TODO: this is not
															// working!
		logger.debug(TAG, "Creating Service");
		
		// Try to init the BPF
//		try {
//			BPF.init(this, Environment.getExternalStorageDirectory()
//					.getAbsolutePath() + "/dtn.config.xml");
//		} catch (BPFException e) {
//			logger.error(TAG,
//					"Couldn't initialize the BPF, exception: " + e.getMessage());
//			System.exit(-1);
//		}
	}

	@Override
	public void onDestroy() {
//		BPF.getInstance().stop();
		logger.info(TAG, "Stopped BPF");
		Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		// this method is not called when the service is started with bindService().
		// we use the start() method defined in WSNServiceInterface
//		Toast.makeText(this, "Service Started (onStart)", Toast.LENGTH_LONG).show();
	}

	@Override
	public BPFActionReceiver getBPFActionReceiver() {
		return action;
	}

	@Override
	public BPFCommunication getBPFCommunication() {
		return comm;
	}

	@Override
	public BPFDB getBPFDB() {
		return db;
	}

	@Override
	public BPFLogger getBPFLogger() {
		return logger;
	}

}