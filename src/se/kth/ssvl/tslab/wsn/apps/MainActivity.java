package se.kth.ssvl.tslab.wsn.apps;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

   
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	 //Resources res;
    	 TabHost tabHost;
    	 TabHost.TabSpec spec;
    	 //Intent intent;
    	
    	/*TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
    	tabHost=(TabHost)findViewById(R.id.tabHost);
    	tabHost.setup();*/
    	 
    	// res=getResources();
         tabHost=getTabHost();

    	
    	Intent intent1=new Intent().setClass(this,WebData.class);
    	spec=tabHost.newTabSpec("Tab 1");
    	//spec1.setContent(R.id.Gateway);
    	spec.setIndicator("Gateway");
    	spec.setContent(intent1);
    	//startActivity(intent);
    	//Log.d("tabs", tabHost + ";" + spec);
    	tabHost.addTab(spec);
    	
    	Intent intent2=new Intent().setClass(this,DaemonConfig.class);
    	spec=tabHost.newTabSpec("Tab 2");
    	spec.setIndicator("Config");
    	//spec2.setContent(R.id.Config);
    	spec.setContent(intent2);
    	tabHost.addTab(spec);
    	
    	Intent intent3=new Intent().setClass(this,BundleStatistics.class);
    	spec=tabHost.newTabSpec("Tab 3");
    	spec.setIndicator("Statistics");
    	//spec3.setContent(R.id.Statistics);
    	spec.setContent(intent3);
    	tabHost.addTab(spec);
    	}

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/
}
