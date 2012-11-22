package se.kth.ssvl.tslab.wsn.service;

import java.io.File;

import se.kth.ssvl.tslab.wsn.service.bpf.ActionReceiver;
import se.kth.ssvl.tslab.wsn.service.bpf.Communication;
import se.kth.ssvl.tslab.wsn.service.bpf.DB;
import se.kth.ssvl.tslab.wsn.service.bpf.Logger;
import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.general.bpf.BPF;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFCommunication;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFDB;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFLogger;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFService;
import se.kth.ssvl.tslab.wsn.general.bpf.exceptions.BPFException;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WSNService extends Service implements BPFService {
//	private static final String TAG = "AndService";
//	MediaPlayer player;
	private static String TAG = "Service";
	
	private Logger logger; 
	private ActionReceiver action;
	private Communication comm;
	private DB db;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
//		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
//		Log.d(TAG, "onCreate");
//		player = MediaPlayer.create(this, R.raw.braincandy);
//		player.setLooping(false); // Set looping
		
		// Init a logger first of all
//		logger = new Logger(Integer.parseInt(args[1])); TODO: take log level from config
		
		// Init the action receiver
		action = new ActionReceiver(logger);
		
		// Init the communications object
		comm = new Communication();
		
		// Init the DB object
		db = new DB(new File("build/database.db"), logger);

		// Try to init the BPF
		try {
//			BPF.init(this, args[0]); TODO: take config path
			BPF.init(this, null);
		} catch (BPFException e) {
			logger.error(TAG, "Couldn't initialize the BPF, exception: " + e.getMessage());
			System.exit(-1);
		}
	}

	@Override
	public void onDestroy() {
//		Log.d(TAG, "onDestroy");
//		player.stop();
		BPF.getInstance().stop();
		logger.info(TAG, "Stopped BPF");
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
//		Log.d(TAG, "onStart");
//		player.start();
		BPF.getInstance().start();
		logger.info(TAG, "Started BPF");
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
	}

	@Override
	public BPFActionReceiver getBPFActionReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BPFCommunication getBPFCommunication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BPFDB getBPFDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BPFLogger getBPFLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}