package se.kth.ssvl.tslab.wsn.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.app.net.ConnectionDetector;
import se.kth.ssvl.tslab.wsn.app.util.AlertDialogManager;
import se.kth.ssvl.tslab.wsn.app.util.JSONParser;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GatewayListActivity extends ListActivity {

	// Connection detector
	ConnectionDetector cd;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> gatewayslist;

	// gateways JSONArray
	JSONArray gateways = null;

	private static String URL_GATEWAYS;

	// ALL JSON node names
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_data);

		cd = new ConnectionDetector(getApplicationContext());

		// Check for Internet connection
		if (!cd.isConnectingToInternet()) {
		//if (true) {
			// Internet Connection is not present
			alert.showAlertDialog(GatewayListActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Hashmap for ListView

		gatewayslist = new ArrayList<HashMap<String, String>>();

		Log.d("gatewaylist: ", "> ");

		// Load URL from settings
		URL_GATEWAYS = this.getPreferences(MODE_WORLD_READABLE).getString("server.url", 
				getResources().getString(R.string.defaultWebServerUrl));
		
		// Loading Gateways JSON in Background Thread
		new LoadGateways().execute();

		
		// get listview
		ListView lv = getListView();

		/**
		 * Listview item click listener SensorListActivity will be lauched by
		 * passing gateway id
		 * */
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// on selecting a single gateway
				// SensorListActivity will be launched to show Data inside the gateway
				Intent i = new Intent(getApplicationContext(), SensorListActivity.class);

				String gateway_id = ((TextView) view
						.findViewById(R.id.gateway_id)).getText().toString();
				// String gateway_name = ((TextView)
				// view.findViewById(R.id.gateway_name)).getText().toString();
				i.putExtra("gateway_id", gateway_id);
				// i.putExtra("gateway_name", gateway_name);

				startActivity(i);

			}
		});

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.activity_web_data);
	}
	

	/*Background Async Task to Load all Albums by making http request*/
	class LoadGateways extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GatewayListActivity.this);
			pDialog.setMessage("Listing Gateways ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			timerDelayRemoveDialog(10000,pDialog);
			pDialog.show();
		}

		public void timerDelayRemoveDialog(long time, final Dialog d){
		Handler handler = new Handler(); 
		handler.postDelayed(new Runnable() {           
        @Override
		public void run() {                
            d.dismiss();         
        		}
			}, time); 
		}

		/*getting Gateways JSON*/
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair() {

				@Override
				public String getValue() {
					return "";
				}

				@Override
				public String getName() {
					return "getGateways";
				}
			});

			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_GATEWAYS, "GET",
					params);
			
			// Check your log cat for JSON reponse
			Log.d("Gateways JSON: ", "> " + json);

			try {
				gateways = new JSONArray(json);

				if (gateways != null) {
					// looping through All gateways
					for (int i = 0; i < gateways.length(); i++) {
						JSONObject c = gateways.getJSONObject(i);

						// Storing each json item values in variable
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, name);

						// adding HashList to ArrayList
						gatewayslist.add(map);
					}
				}

				else {

					Log.d("Gateways: ", "null");
				}
			}

			catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all gateways
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(GatewayListActivity.this,
							gatewayslist, R.layout.list_item_gateways,
							new String[] { TAG_ID, TAG_NAME }, new int[] {
									R.id.gateway_id, R.id.gateway_name });

					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}
