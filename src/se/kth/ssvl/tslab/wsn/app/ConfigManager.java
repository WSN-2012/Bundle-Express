package se.kth.ssvl.tslab.wsn.app;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

import se.kth.ssvl.tslab.wsn.general.servlib.config.Configuration;
import se.kth.ssvl.tslab.wsn.general.servlib.config.ConfigurationParser;
import se.kth.ssvl.tslab.wsn.general.servlib.config.exceptions.InvalidDTNConfigurationException;
import android.content.Context;
import android.util.Log;

public class ConfigManager {

	private final static String TAG = "ConfigManager";

	private Context context;

	public ConfigManager(Context context) {
		this.context = context;
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
	 * 
	 * @param config
	 * @return
	 */
	public boolean writeConfig(Configuration config) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element storage = storageSettings(doc, config);
			Element interfaces = interfaceSettings(doc, config);
			Element links = linkSettings(doc, config);
			Element routes = routeSettings(doc, config);
			Element discover = discoverSettings(doc, config);
			Element security = securitySettings(doc, config);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult();
			
			// TODO: Do the actual writing here
			
			return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	private Element securitySettings(Document doc, Configuration config) {
		// TODO Auto-generated method stub
		return null;
	}

	private Element discoverSettings(Document doc, Configuration config) {
		// TODO Auto-generated method stub
		return null;
	}

	private Element routeSettings(Document doc, Configuration config) {
		// TODO Auto-generated method stub
		return null;
	}

	private Element linkSettings(Document doc, Configuration config) {
		// TODO Auto-generated method stub
		return null;
	}

	private Element interfaceSettings(Document doc, Configuration config) {
		// TODO Auto-generated method stub
		return null;
	}

	private Element storageSettings(Document doc, Configuration config) {
		// Create the storage object and fill it
		Element storage = doc.createElement(ConfigurationParser.StorageSettingTagName);
		
		// Fill all config
		storage.setAttribute("quota", Integer.toString(config.storage_setting().quota()));
		storage.setAttribute("storage_path", config.storage_setting().storage_path());
		storage.setAttribute("test_data_log", Boolean.toString(config.storage_setting().test_data_log()));
		storage.setAttribute("keep_copy", Boolean.toString(config.storage_setting().keep_copy()));
		
		return storage;
	}
	
	
	
}
