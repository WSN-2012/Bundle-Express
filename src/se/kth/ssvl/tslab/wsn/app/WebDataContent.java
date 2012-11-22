package se.kth.ssvl.tslab.wsn.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.kth.ssvl.tslab.wsn.app.helper.AlertDialogManager;
import se.kth.ssvl.tslab.wsn.app.helper.ConnectionDetector;
import se.kth.ssvl.tslab.wsn.app.helper.JSONParser;
import se.kth.ssvl.tslab.wsn.apps.R;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WebDataContent extends ListActivity {

	// Connection detector
	ConnectionDetector cd;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> datalist;

	JSONArray data = null;

	String sensor_id = null;

	private static final String URL_DATA = "http://130.229.130.122:8081/WSN-web/HTTPServlet";

	// private static final String TAG_ID = "id";
	private static final String TAG_UTIMESTAMP = "utimestamp";
	private static final String TAG_UT = "ut";
	private static final String TAG_T = "t";
	private static final String TAG_PS = "ps";
	private static final String TAG_T_MCU = "t_mcu";
	private static final String TAG_V_MCU = "v_mcu";
	private static final String TAG_UP = "up";
	private static final String TAG_RH = "rh";
	private static final String TAG_V_IN = "v_in";
	// private static final String TAG_V_A1 = "v_a1";
	private static final String TAG_SENSOR_NAME = "sensorName";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_data_content);

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(WebDataContent.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		Intent i = getIntent();
		// getting attached intent data;
		sensor_id = i.getStringExtra("sensor_id");

		datalist = new ArrayList<HashMap<String, String>>();
		
		new GatewayData().execute();

		Log.d("data: ", "> ");

		ListView lv = getListView();

		/**
		 * Listview on item click listener SingleTrackActivity will be lauched
		 * by passing album id, song id
		 * */
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
	class GatewayData extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(WebDataContent.this);
			pDialog.setMessage("Loading data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting song json and parsing
		 * */
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
				data = new JSONArray(json);

				if (data != null) {

					// looping through All data
					for (int i = 0; i < data.length(); i++) {
						JSONObject jObj = data.getJSONObject(i);

						String utimestamp = jObj.getString(TAG_UTIMESTAMP);
						String ut = jObj.getString(TAG_UT);
						String t = jObj.getString(TAG_T);
						String ps = jObj.getString(TAG_PS);
						String t_mcu = jObj.getString(TAG_T_MCU);
						String v_mcu = jObj.getString(TAG_V_MCU);
						String up = jObj.getString(TAG_UP);
						String rh = jObj.getString(TAG_RH);
						String v_in = jObj.getString(TAG_V_IN);
						String sensorName = jObj.getString(TAG_SENSOR_NAME);

						Log.d("Up time: ", ut);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_UTIMESTAMP, utimestamp);
						map.put(TAG_UT, ut);
						map.put(TAG_T, t);
						map.put(TAG_PS, ps);
						map.put(TAG_T_MCU, t_mcu);
						map.put(TAG_V_MCU, v_mcu);
						map.put(TAG_UP, up);
						map.put(TAG_RH, rh);
						map.put(TAG_V_IN, v_in);
						map.put(TAG_SENSOR_NAME, sensorName);

						// adding HashList to ArrayList
						datalist.add(map);
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
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all albums
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {

					/**
					 * Updating parsed JSON data into ListView
					 * */
					/*
					 * ListAdapter adapter = new SimpleAdapter(
					 * WebDataContent.this, datalist, R.layout.list_item_data,
					 * new String[] { TAG_UTIMESTAMP, TAG_UT, TAG_T, TAG_PS,
					 * TAG_T_MCU, TAG_V_MCU,TAG_UP, TAG_RH,
					 * TAG_V_IN,TAG_SENSOR_NAME}, new int[] {R.id.unixTimestamp,
					 * R.id.unixTime,R.id.temp,
					 * R.id.powerSaveIndicator,R.id.mTemp, R.id.mVolt,
					 * R.id.upTime, R.id.rhumidity, R.id.vIn,
					 * R.id.sensor_name});
					 */

					ListAdapter adapter = new SimpleAdapter(
							WebDataContent.this, datalist,
							R.layout.list_item_data, new String[] {
									TAG_UTIMESTAMP, TAG_T, TAG_UT, TAG_PS, TAG_V_IN }, new int[] {
									R.id.unixTimestamp, R.id.temp, R.id.unixTime, R.id.powerSaveIndicator, R.id.vIn });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}
	}
}