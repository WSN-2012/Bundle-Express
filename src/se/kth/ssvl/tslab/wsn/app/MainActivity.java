package se.kth.ssvl.tslab.wsn.app;

import java.io.File;

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
		ConfigManager.init(getApplicationContext(), 
				new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WSN-Android/dtn.config.xml"));
		
		// Resources res;
		TabHost tabHost;
		TabHost.TabSpec spec;

		//Getting different Tabs
		// res=getResources();
		tabHost = getTabHost();
		
		//init gatway tab
		Intent intent1 = new Intent().setClass(this, GatewayListActivity.class);
		spec = tabHost.newTabSpec("Tab 1");
		spec.setIndicator("Gateway", getResources().getDrawable(R.drawable.webdata));
		spec.setContent(intent1);
		tabHost.addTab(spec);
		
		//init config tab
		Intent intent2 = new Intent().setClass(this, ConfigActivity.class);
		spec = tabHost.newTabSpec("Tab 2");
		spec.setIndicator("Config", getResources().getDrawable(R.drawable.config));
		spec.setContent(intent2);
		tabHost.addTab(spec);
		
		//init Statistics tab
		Intent intent3 = new Intent().setClass(this, StatisticsActivity.class);
		spec = tabHost.newTabSpec("Tab 3");
		spec.setIndicator("Statistics", getResources().getDrawable(R.drawable.statistics));
		spec.setContent(intent3);
		tabHost.addTab(spec);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */
}
