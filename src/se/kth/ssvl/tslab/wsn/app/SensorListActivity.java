package se.kth.ssvl.tslab.wsn.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.Toast;

public class SensorListActivity extends ListActivity {

	// Connection detector
	ConnectionDetector cd;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> sensorslist;

	// gateways JSONArray
	JSONArray sensors = null;
	String gateway_id, sensor_id;

	private static String URL_SENSOR;

	// ALL JSON node names
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_list);

		cd = new ConnectionDetector(getApplicationContext());

		// Check for Internet connection
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(SensorListActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		Intent i = getIntent();
		gateway_id = i.getStringExtra("gateway_id");

		// Hashmap for ListView

		sensorslist = new ArrayList<HashMap<String, String>>();

		Log.d("sensorlist: ", "> ");
		
		// Load URL from settings
		URL_SENSOR = this.getPreferences(MODE_WORLD_READABLE).getString("server.url", 
				getResources().getString(R.string.defaultWebServerUrl));

		// Loading Sensors JSON in Background Thread
		new LoadSensors().execute();

		ListView lv = getListView();

		/**
		 * Listview on item click listener DataListActivity will be lauched
		 * by passing sensor id
		 * */
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// On selecting single sensor to get sensor data
				Intent i = new Intent(getApplicationContext(),
						DataListActivity.class);

				// to get sensors data, sensor id is needed
				String sensor_name = ((TextView)
				view.findViewById(R.id.sensor_name)).getText().toString();
				String sensor_id = ((TextView) view
						.findViewById(R.id.sensor_id)).getText().toString();

				Toast.makeText(
						getApplicationContext(),
						"gateway Id: " + gateway_id + ", Sensor Id: "
								+ sensor_id, Toast.LENGTH_SHORT).show();

				i.putExtra("sensor_id", sensor_id);
				i.putExtra("sensor_name", sensor_name);

				startActivity(i);
			}
		});

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_sensor_list, menu); return
	 * true; }
	 */

	/**
	 * Background Async Task to Load all tracks under one album
	 * */
	class LoadSensors extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SensorListActivity.this);
			pDialog.setMessage("Loading Sensor list ...");
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

		/**
		 * getting tracks json and parsing
		 * */
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// post sensor id  as GET parameter
			params.add(new BasicNameValuePair("gateway", gateway_id));

			params.add(new NameValuePair() {

				@Override
				public String getValue() {
					return "";
				}

				@Override
				public String getName() {
					return "getSensors";
				}
			});

			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_SENSOR, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("Sensor List JSON: ", json);

			try {
				sensors = new JSONArray(json);

				if (sensors != null) {
					// looping through All Sensors
					for (int i = 0; i < sensors.length(); i++) {
						JSONObject c = sensors.getJSONObject(i);

						// Storing each json item values in variable
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);

						Log.d("Name: ", name);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, name);

						// adding HashList to ArrayList
						sensorslist.add(map);
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

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all sensors
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(SensorListActivity.this,
							sensorslist, R.layout.list_item_sensors,
							new String[] { TAG_ID, TAG_NAME }, new int[] {
									R.id.sensor_id, R.id.sensor_name });

					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}
