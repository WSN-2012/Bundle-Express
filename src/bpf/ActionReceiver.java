package bpf;

import se.kth.ssvl.tslab.wsn.general.bpf.BPFActionReceiver;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.bundles.Bundle;

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

}
