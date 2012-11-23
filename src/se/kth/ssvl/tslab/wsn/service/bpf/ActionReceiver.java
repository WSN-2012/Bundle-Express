package se.kth.ssvl.tslab.wsn.service.bpf;

import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.bundles.Bundle;
import se.kth.ssvl.tslab.wsn.general.servlib.storage.Stats;

public class ActionReceiver implements BPFActionReceiver {
	private static final String TAG = "Actions";

	private Logger logger;

	public ActionReceiver(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void bundleReceived(Bundle bundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(String header, String description) {
		// TODO Auto-generated method stub

	}

	//TODO: this method needs to notify the app
	@Override
	public void updateStats(Stats stats) {
		logger.debug(TAG, "New Stats object received:" +
				"\nTotal size: " + stats.totalSize() + 
				"\nStored bundles: " + stats.storedBundles() +
				"\nTransmitted bundles: " + stats.transmittedBundles() +
				"\nReceived bundles: " + stats.receivedBundles() );
	}

}
