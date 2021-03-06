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

package se.kth.ssvl.tslab.wsn.app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.kth.ssvl.tslab.wsn.app.util.FileUtil;
import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.general.servlib.config.ConfigurationParser;
import se.kth.ssvl.tslab.wsn.general.servlib.config.exceptions.InvalidDTNConfigurationException;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.DiscoveriesSetting.AnnounceEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.DiscoveriesSetting.DiscoveryEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.InterfacesSetting.InterfaceEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.LinksSetting.LinkEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.RoutesSetting.RouteEntry;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class ConfigManager implements Serializable {

	private static final long serialVersionUID = -6827434347784136141L;

	private final static String TAG = "ConfigManager";
	public final static String FILENAME = "dtn.config.xml";
	public final static String PATH = "/Bundle-Express/" + FILENAME;

	private static ConfigManager mInstance;
	private static Context mContext = null;
	private static File mConfigurationFile = null;

	public static void init(Context context) {
		mContext = context;

		// Set the preference path to the correct memory
		Editor e = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		e.putString("storage.path", PreferenceManager.getDefaultSharedPreferences(mContext).getString(
				"storage.path", FileUtil.phoneStoragePath(mContext).getAbsolutePath()));
		e.commit();

		// Create a configuration file based on the preference set above
		mConfigurationFile = new File(PreferenceManager.
				getDefaultSharedPreferences(mContext).getString("storage.path", "") + "/" + FILENAME);

		// Run the constructor
		getInstance();
	}

	public static ConfigManager getInstance() {
		if (mInstance == null) {
			mInstance = new ConfigManager();
		}
		
		return mInstance;
	}
	
	public void destruct() {
		mInstance = null;
	}
	
	private ConfigManager() {
		if (!mConfigurationFile.exists()) {
			InputStream in = null;
			OutputStream out = null;
			try {
				// First create the folder on the sdcard if it doesn't exist
				if (!mConfigurationFile.getParentFile().mkdirs()) {
					Log.e(TAG, "Couldn't create the directory for storage");
					return;
				}

				// In and output streams
				in = mContext.getAssets().open("config/dtn.config.xml");
				out = new FileOutputStream(mConfigurationFile);

				// Do the actual copying
				FileUtil.proxyStream(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
				// When done, edit the config and write the path in the
				// configuration
				Configuration c = readConfig();
				c.storage_setting().set_storage_path(
						mConfigurationFile.getParentFile().getAbsolutePath());
				writeConfig(c);
			} catch (IOException e) {
				Log.e(TAG, "Failed to copy config file", e);
			}
		}
	}

	/**
	 * Reads the configuration from the config file in the assets folder
	 * 
	 * @return A Configurations object containing all configuration
	 */
	public Configuration readConfig() {
		try {
			Log.d(TAG, "Trying to read configuration from file: " + mConfigurationFile.getAbsolutePath());
			return ConfigurationParser.parse_config_file(new FileInputStream(mConfigurationFile));
		} catch (InvalidDTNConfigurationException e) {
			Log.e(TAG,
					"There was an error in the configuration: "
							+ e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e(TAG, "There was an IO exception when reading the config");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e(TAG, "There was a parser error when parsing the config");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Write the configuration file from a configuration object
	 * @param config The configurations object to save
	 * @return Returns true if the writing went well, otherwise false
	 */
	public boolean writeConfig(Configuration config) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement(ConfigurationParser.DTNConfigurationTagName);
			
			// Get all the configuration and add it
			root.appendChild(storageSettings(doc, config));
			root.appendChild(interfaceSettings(doc, config));
			root.appendChild(linkSettings(doc, config));
			root.appendChild(routeSettings(doc, config));
			root.appendChild(discoverSettings(doc, config));
			root.appendChild(securitySettings(doc, config));
			
			doc.appendChild(root);
			
			// Write the configuration
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(mConfigurationFile);

			transformer.transform(source, result);			
			return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private Element securitySettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element security = doc.createElement(ConfigurationParser.SecuritySettingTagName);
		
		// First add all discover entries
		security.setAttribute("ks_path", config.security_setting().ks_path());
		security.setAttribute("ks_password", config.security_setting().ks_password());
		security.setAttribute("use_pcb", Boolean.toString(config.security_setting().use_pcb()));
		security.setAttribute("use_pib", Boolean.toString(config.security_setting().use_pib()));
		
		return security;
	}

	private Element discoverSettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element discovery = doc.createElement(ConfigurationParser.DiscoveriesSettingTagName);

		// Fill all config values
		Iterator<DiscoveryEntry> d = config.discoveries_setting().discovery_entries().iterator();
		Iterator<AnnounceEntry> a = config.discoveries_setting().announce_entries().iterator();
		
		// First add all discover entries
		Element discover;
		DiscoveryEntry de;
		while (d.hasNext()) {
			de = d.next();
			discover = doc.createElement(ConfigurationParser.DiscoveryTagName);
			discover.setAttribute("id", de.id());
			discover.setAttribute("address_family", de.address_family().getCaption());
			discover.setAttribute("port", Integer.toString(de.port()));
			discovery.appendChild(discover);
		}
		
		// Then add all announce entries
		Element announce;
		AnnounceEntry ae;
		while (a.hasNext()) {
			ae = a.next();
			announce = doc.createElement(ConfigurationParser.AnnounceTagName);
			announce.setAttribute("interface_id", ae.interface_id());
			announce.setAttribute("discovery_id", ae.discovery_id());
			announce.setAttribute("conv_layer_type", ae.conv_layer_type().getCaption());
			announce.setAttribute("interval", Integer.toString(ae.interval()));
			discovery.appendChild(announce);
		}
		
		return discovery;
	}

	private Element routeSettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element routes = doc.createElement(ConfigurationParser.RoutesSettingTagName);

		// Fill all config values
		routes.setAttribute("router_type", config.routes_setting().router_type().getCaption());
		routes.setAttribute("queuing", config.routes_setting().getQueuing_policy().getCaption());
		routes.setAttribute("local_eid", config.routes_setting().local_eid());
		
		Iterator<RouteEntry> i = config.routes_setting().route_entries().iterator();
		Element route;
		RouteEntry re;
		while (i.hasNext()) {
			re = i.next();
			route = doc.createElement(ConfigurationParser.RouteTagName);
			route.setAttribute("dest", re.dest());
			route.setAttribute("link_id", re.link_id());
			routes.appendChild(route);
		}

		return routes;
	}

	private Element linkSettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element links = doc.createElement(ConfigurationParser.LinksSettingTagName);

		// Fill all config values
		links.setAttribute("proactive_fragmentation", 
				Boolean.toString(config.links_setting().proactive_fragmentation()));
		links.setAttribute("fragmentation_mtu",
				Integer.toString(config.links_setting().fragmentation_mtu()));
		
		Iterator<LinkEntry> i = config.links_setting().link_entries().iterator();
		Element link;
		LinkEntry le;
		while (i.hasNext()) {
			le = i.next();
			link = doc.createElement(ConfigurationParser.LinkTagName);
			link.setAttribute("id", le.id());
			link.setAttribute("conv_layer_type", le.conv_layer_type().getCaption());
			link.setAttribute("dest", le.dest());
			link.setAttribute("type", le.type().toString());
			link.setAttribute("interval", Integer.toString(le.retryInterval()));
			links.appendChild(link);
		}

		return links;
	}

	private Element interfaceSettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element interfaces = doc.createElement(ConfigurationParser.InterfacesSettingTagName);
		
		// Fill all config values
		Iterator<InterfaceEntry> i = config.interfaces_setting().interface_entries().iterator();
		Element inter;
		InterfaceEntry ie;
		while (i.hasNext()) {
			ie = i.next();
			inter = doc.createElement(ConfigurationParser.InterfaceTagName);
			inter.setAttribute("conv_layer_type", ie.conv_layer_type().getCaption());
			inter.setAttribute("id", ie.id());
			inter.setAttribute("local_port", Integer.toString(ie.local_port()));
			interfaces.appendChild(inter);
		}
		
		return interfaces;
	}

	private Element storageSettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element storage = doc.createElement(ConfigurationParser.StorageSettingTagName);
		
		// Fill all config values
		storage.setAttribute("quota", Integer.toString(config.storage_setting().quota()));
		storage.setAttribute("storage_path", config.storage_setting().storage_path());
		storage.setAttribute("test_data_log", Boolean.toString(config.storage_setting().test_data_log()));
		storage.setAttribute("keep_copy", Boolean.toString(config.storage_setting().keep_copy()));
		
		return storage;
	}

	//getter for configuration file
	public File getConfigurationFile() {
		return mConfigurationFile;
	}
}
