package se.kth.ssvl.tslab.wsn.app;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.service.WSNService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class DaemonConfig extends Activity {
	private CheckBox chkIos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daemon_config);
		addListenerOnChkIos();
	}

	private void addListenerOnChkIos() {
		chkIos = (CheckBox) findViewById(R.id.checkBox1);

		chkIos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					Toast.makeText(DaemonConfig.this, "Activate Service",
							Toast.LENGTH_LONG).show();
					startService(new Intent(DaemonConfig.this, WSNService.class));/*
																				 * Remember
																				 * as
																				 * a
																				 * flag
																				 */
				} else
					stopService(new Intent(DaemonConfig.this, WSNService.class)); /*
																				 * Remember
																				 * as
																				 * a
																				 * flag
																				 */
			}
		});

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_daemon_config, menu); return
	 * true; }
	 */

	/* Called when the user clicks the save button */
	/*
	 * public void UpdateEID(View view) { /Do something in response to button }
	 */
}
