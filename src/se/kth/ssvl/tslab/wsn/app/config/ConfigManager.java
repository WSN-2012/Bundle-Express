package se.kth.ssvl.tslab.wsn.app.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.general.servlib.config.ConfigurationParser;
import se.kth.ssvl.tslab.wsn.general.servlib.config.exceptions.InvalidDTNConfigurationException;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.DiscoveriesSetting.AnnounceEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.DiscoveriesSetting.DiscoveryEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.InterfacesSetting.InterfaceEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.LinksSetting.LinkEntry;
import se.kth.ssvl.tslab.wsn.general.servlib.config.settings.RoutesSetting.RouteEntry;
import android.content.Context;
import android.util.Log;

public class ConfigManager {

	private final static String TAG = "ConfigManager";

	private Context context;
	private String configurationPath;

	public ConfigManager(Context context, String configurationPath) {
		this.context = context;
		this.configurationPath = configurationPath;

		if (!new File(configurationPath).exists()) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = context.getAssets().open("config/dtn.config.xml");
				out = new FileOutputStream(configurationPath);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
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
			Log.d(TAG, "Trying to load config from assets");
			return ConfigurationParser.parse_config_file(context.getAssets()
					.open("config/dtn.config.xml"));
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
			
			// Get all the configuration and add it
			doc.appendChild(storageSettings(doc, config));
			doc.appendChild(interfaceSettings(doc, config));
			doc.appendChild(linkSettings(doc, config));
			doc.appendChild(routeSettings(doc, config));
			doc.appendChild(discoverSettings(doc, config));
			doc.appendChild(securitySettings(doc, config));
			
			// Write the configuration
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(configurationPath));

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
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
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
			link.setAttribute("dest", le.des());
			link.setAttribute("type", le.type().toString());
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
	
	
	
}
