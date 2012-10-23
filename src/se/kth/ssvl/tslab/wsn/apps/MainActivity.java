package se.kth.ssvl.tslab.wsn.apps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
    	tabHost.setup();

    	TabSpec spec1=tabHost.newTabSpec("Gateway");
    	spec1.setContent(R.id.Gateway);
    	spec1.setIndicator("Gateway");

    	TabSpec spec2=tabHost.newTabSpec("Config");
    	spec2.setIndicator("Config");
    	spec2.setContent(R.id.Config);

    	TabSpec spec3=tabHost.newTabSpec("Statistics");
    	spec3.setIndicator("Statistics");
    	spec3.setContent(R.id.Statistics);

    	tabHost.addTab(spec1);
    	tabHost.addTab(spec2);
    	tabHost.addTab(spec3);
    	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
