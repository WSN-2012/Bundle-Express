package se.kth.ssvl.tslab.wsn.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.app.net.ConnectionDetector;
import se.kth.ssvl.tslab.wsn.app.util.AlertDialogManager;
import se.kth.ssvl.tslab.wsn.app.util.JSONHelper;
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

public class DataListActivity extends ListActivity {

	// Connection detector
	ConnectionDetector cd;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, Object>> datalist;

	JSONArray data = null;
	JSONObject wsdata = null;

	String sensor_id = null;
	String sensor_name = null;

	private static final String TAG_WSDATA = "wsdata";
	private static final String TAG_UTIMESTAMP = "utimestamp";
	private static final String TAG_UT = "ut";
	private static final String TAG_T = "t";
	private static final String TAG_PS = "ps";
	private static final String TAG_T_MCU = "t_mcu";
	private static final String TAG_V_MCU = "v_mcu";
	private static final String TAG_UP = "up";
	private static final String TAG_RH = "rh";
	private static final String TAG_V_IN = "v_in";
	private static final String TAG_SENSOR_NAME = "sensorName";

	private static String URL_DATA;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_data_content);

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(DataListActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		Intent i = getIntent();
		// getting attached intent data;
		sensor_id = i.getStringExtra("sensor_id");
		sensor_name = i.getStringExtra("sensor_name");

		datalist = new ArrayList<HashMap<String, Object>>();

		// Get the URL from settings
		URL_DATA = this.getPreferences(MODE_WORLD_READABLE).getString(
				"server.url",
				getResources().getString(R.string.defaultWebServerUrl));

		new Data().execute();

		Log.d("data: ", "> ");

		ListView lv = getListView();

		/* Listview on item click listener */
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// on click method
			}
		});
	}

	/**
	 * Background Async Task to get single song information
	 * */
	class Data extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DataListActivity.this);
			pDialog.setMessage("Loading data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			timerDelayRemoveDialog(10000, pDialog);
			pDialog.show();
		}

		public void timerDelayRemoveDialog(long time, final Dialog d) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					d.dismiss();
				}
			}, time);
		}

		/**
		 * getting song json and parsing
		 * */
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// post gateway id as GET parameters
			params.add(new BasicNameValuePair("sensor", sensor_id));

			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_DATA, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("Gateway Data JSON: ", json);

			try {
				// JSONObject jObj = new JSONObject(json);
				data = new JSONArray(json);
				if (data != null) {

					for (int i = 0; i < data.length(); i++) {
						JSONObject jObj = data.getJSONObject(i);
						// if (jObj != null) {
						String utimestamp = jObj.getString(TAG_UTIMESTAMP);
						String sensorName = jObj.getString(TAG_SENSOR_NAME);
						Log.d("Sensor Name: ", sensorName);

						wsdata = jObj.getJSONObject(TAG_WSDATA);

						if (wsdata != null) {
							// Create a map out of the json objects
							HashMap<String, Object> map = JSONHelper
									.toMap(wsdata);
							
							// Add the sensor name and timestamp
							map.put(TAG_UTIMESTAMP, utimestamp);
							map.put(TAG_SENSOR_NAME, sensorName);
							
							// adding HashList to ArrayList
							datalist.add(map);
						} else {

							Log.d("Data: ", "null");
						}

					}
				}

				else {

					Log.d("Data: ", "null");
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
			// dismiss the dialog after getting all albums
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					/**
					 * Updating parsed JSON data into ListView
					 * */
					

					ListAdapter adapter = new SimpleAdapter(
							DataListActivity.this, datalist,
							R.layout.list_item_data, new String[] {
									TAG_UTIMESTAMP, TAG_T, TAG_UT, TAG_PS,
									TAG_V_IN, TAG_RH, TAG_UP,TAG_T_MCU, TAG_V_MCU }, new int[] { R.id.unixTimestamp,
									R.id.temp, R.id.unixTime,
									R.id.powerSaveIndicator, R.id.vIn, R.id.rhumidity, R.id.upTime,R.id.mTemp, R.id.mVolt });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}
	}
}