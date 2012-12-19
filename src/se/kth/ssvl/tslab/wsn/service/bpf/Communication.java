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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

import se.kth.ssvl.tslab.wsn.general.bpf.BPF;
import se.kth.ssvl.tslab.wsn.general.bpf.BPFCommunication;

public class Communication implements BPFCommunication {

	private static final String TAG = "Communication";
	
	@Override
	public InetAddress getBroadcastAddress(String interfaceName) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			Enumeration<NetworkInterface> niEnum = NetworkInterface
					.getNetworkInterfaces();
			while (niEnum.hasMoreElements()) {
				NetworkInterface ni = niEnum.nextElement();
				if (!ni.isLoopback()) {
					for (InterfaceAddress interfaceAddress : ni
							.getInterfaceAddresses()) {
						if (interfaceAddress.getBroadcast() != null) {
							Log.d("Broadcasr Address", ">"+interfaceAddress.getBroadcast());
							return interfaceAddress.getBroadcast();
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public InetAddress getDeviceIP(String interfaceName) {
		try {
			Enumeration<NetworkInterface> ifaces = NetworkInterface
					.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface iface = ifaces.nextElement();
				Enumeration<InetAddress> addresses = iface.getInetAddresses();

				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					if (addr instanceof Inet4Address
							&& !addr.isLoopbackAddress()) {
						Log.d(TAG,
								"getDeviceIP returning " + addr);
						return addr;
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG, "Exception while getting device address.");
		}
		return null;
	}
}
