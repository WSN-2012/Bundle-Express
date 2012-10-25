package se.kth.ssvl.tslab.wsn.apps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DaemonConfig extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_daemon_config);
        TextView tv=new TextView(this);
        tv.setText("Daemon Configuration");
        setContentView(tv);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_daemon_config, menu);
        return true;
    }*/
}
