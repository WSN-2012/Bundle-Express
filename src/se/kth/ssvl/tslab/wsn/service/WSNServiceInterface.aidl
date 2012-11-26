package se.kth.ssvl.tslab.wsn.service;
import se.kth.ssvl.tslab.wsn.service.WSNServiceInterfaceCallBack;

interface WSNServiceInterface {
    	void start(String path);
		Map getStats();
		void registerCallBack(WSNServiceInterfaceCallBack cb);
		void unregisterCallBack(WSNServiceInterfaceCallBack cb);
     }