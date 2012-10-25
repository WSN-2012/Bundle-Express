package se.kth.ssvl.tslab.wsn.apps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends FragmentActivity {

   
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	 //Resources res;
    	 TabHost tabHost;
    	 //TabHost.TabSpec spec;
    	 Intent intent;
    	
    	/*TabHost tabHost=(TabHost)findViewById(R.id.tabHost);*/
    	tabHost=(TabHost)findViewById(R.id.tabHost);
    	tabHost.setup();
    	 
    	// res=getResources();
         //tabHost=getTabHost();

    	
    	intent=new Intent().setClass(this,WebData.class);
    	TabSpec spec1=tabHost.newTabSpec("Tab 1");
    	//spec1.setContent(R.id.Gateway);
    	spec1.setIndicator("Gateway");
    	spec1.setContent(intent);
    	//startActivity(intent);
    	tabHost.addTab(spec1);
    	
    	intent=new Intent().setClass(this,DaemonConfig.class);
    	TabSpec spec2=tabHost.newTabSpec("Tab 2");
    	spec2.setIndicator("Config");
    	//spec2.setContent(R.id.Config);
    	spec2.setContent(intent);
    	tabHost.addTab(spec2);
    	
    	intent=new Intent().setClass(this,BundleStatistics.class);
    	TabSpec spec3=tabHost.newTabSpec("Tab 3");
    	spec3.setIndicator("Statistics");
    	//spec3.setContent(R.id.Statistics);
    	spec3.setContent(intent);
    	tabHost.addTab(spec3);
    	}

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/
}
