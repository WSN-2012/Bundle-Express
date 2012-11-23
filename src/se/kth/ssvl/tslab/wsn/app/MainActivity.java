package se.kth.ssvl.tslab.wsn.app;

import se.kth.ssvl.tslab.wsn.R;
import se.kth.ssvl.tslab.wsn.app.config.ConfigManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Init configManager
		ConfigManager.init(getApplicationContext(), Environment.getExternalStorageDirectory());
		
		// Resources res;
		TabHost tabHost;
		TabHost.TabSpec spec;

		// res=getResources();
		tabHost = getTabHost();

		Intent intent1 = new Intent().setClass(this, GatewayListActivity.class);
		spec = tabHost.newTabSpec("Tab 1");
		spec.setIndicator("Gateway");
		spec.setContent(intent1);
		tabHost.addTab(spec);

		Intent intent2 = new Intent().setClass(this, ConfigActivity.class);
		spec = tabHost.newTabSpec("Tab 2");
		spec.setIndicator("Config");
		spec.setContent(intent2);
		tabHost.addTab(spec);

		Intent intent3 = new Intent().setClass(this, StatisticsActivity.class);
		spec = tabHost.newTabSpec("Tab 3");
		spec.setIndicator("Statistics");
		spec.setContent(intent3);
		tabHost.addTab(spec);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */
}
