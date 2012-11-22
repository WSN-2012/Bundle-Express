package se.kth.ssvl.tslab.wsn.app;

import se.kth.ssvl.tslab.wsn.apps.R;
import android.app.Activity;
import android.os.Bundle;

public class BundleStatistics extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle_statistics);
       /* TextView tv=new TextView(this);
        tv.setText("Bundle Statisrics");
        setContentView(tv);*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bundle_statistics, menu);
        return true;
    }*/
}
